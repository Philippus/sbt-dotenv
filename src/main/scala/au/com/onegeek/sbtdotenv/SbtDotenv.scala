/** The MIT License (MIT)
  *
  * Copyright (c) 2014 Matt Fellows (OneGeek)
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
  * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
  * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
  * permit persons to whom the Software is furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
  * Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
  * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
  * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
  * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  */
package au.com.onegeek.sbtdotenv

import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._
import scala.io.Source

/** sbt-dotenv - a dotenv (https://github.com/bkeepers/dotenv) implementation for Scala sbt.
  *
  * Reads a file
  */
object SbtDotenv extends AutoPlugin with SlashSyntax {

  object autoImport {
    lazy val envFileName         =
      settingKey[String]("The file name to define variables.")
    lazy val envFromFile         =
      taskKey[Map[String, String]]("Loads env configuration from file.")
    def dotEnv(fileName: String) = (s: State) =>
      configureEnvironment(s, fileName)
  }

  import autoImport._

  override def trigger = allRequirements

  lazy val baseEnvFileSettings: Seq[Def.Setting[_]] = Seq(
    envFromFile := envFromFileTask.value
  )

  // Automatically configure environment on load
  override lazy val buildSettings =
    Seq(
      envFileName     := ".env",
      Global / onLoad := dotEnv((ThisBuild / envFileName).value)
        .compose((Global / onLoad).value)
    )

  override lazy val projectSettings = inConfig(Test)(
    baseEnvFileSettings
  ) ++ inConfig(IntegrationTest)(baseEnvFileSettings)

  def envFromFileTask = Def.task {
    val fileName = envFileName.value
    loadAndExpand(state.value, fileName).getOrElse(Map.empty[String, String])
  }

  /** Configures the sbt environment from a dotfile (.env) if one exists.
    *
    * @param state
    * @return
    */
  def configureEnvironment(state: State, fileName: String): State =
    loadAndExpand(state, fileName).fold(logNoFile(state, fileName))(
      applyEnvironment(state)
    )

  def loadAndExpand(
      state: State,
      fileName: String
  ): Option[Map[String, String]] = {
    val baseDirectory    = state.configuration.baseDirectory
    val filePath         =
      if (fileName.startsWith("/")) fileName
      else s"${baseDirectory}/${fileName}"
    state.log.debug(s"Base directory: ${baseDirectory}")
    state.log.debug(s"looking for .env file: ${filePath}")
    val dotEnvFile: File = new File(filePath)
    parseFile(dotEnvFile).map { environment =>
      state.log.info(
        s".env detected (fileName=${fileName}). About to configure JVM System Environment with new map"
      )
      // Given the fact that the new environment might have sensitive information, we only print
      // the new environment when debugging the build.
      state.log.debug(s"New map: $environment")
      VariableExpansion.expandAllVars(sys.env ++ environment, environment)
    }
  }

  def applyEnvironment(
      state: State
  )(expandedEnvironment: Map[String, String]) = {
    NativeEnvironmentManager.setEnv(expandedEnvironment.asJava)
    DirtyEnvironmentHack.setEnv((sys.env ++ expandedEnvironment).asJava)
    state.log.info("Configured .env environment")
    state
  }

  def logNoFile(state: State, fileName: String) = {
    state.log.warn(
      s".env file not found (fileName=${fileName}), no .env environment configured."
    )
    state
  }

  /** Parse provided file in .env format and return an immutable environment Map[String, String]
    *
    * @param file
    *   .env file to read
    * @return
    */
  def parseFile(file: File): Option[Map[String, String]] = {
    if (!file.exists) {
      None
    } else {
      val source = Source.fromFile(file)
      val result = parse(source)
      source.close
      Some(result)
    }
  }

  private val LINE_REGEX =
    """(?xms)
       (?:^|\A)           # start of line
       \s*                # leading whitespace
       (?:export\s+)?     # export (optional)
       (                  # start variable name (captured)
         [a-zA-Z_]          # single alphabetic or underscore character
         [a-zA-Z0-9_.-]*    # zero or more alphnumeric, underscore, period or hyphen
       )                  # end variable name (captured)
       (?:\s*=\s*?)       # assignment with whitespace
       (                  # start variable value (captured)
         '(?:\\'|[^'])*'    # single quoted variable
         |                  # or
         "(?:\\"|[^"])*"    # double quoted variable
         |                  # or
         [^\#\r\n]*         # unquoted variable
       )                  # end variable value (captured)
       \s*                # trailing whitespace
       (?:                # start trailing comment (optional)
         \#                 # begin comment
         (?:(?!$).)*        # any character up to end-of-line
       )?                 # end trailing comment (optional)
       (?:$|\z)           # end of line
    """.r

  def parse(source: Source): Map[String, String] = parse(source.mkString)

  def parse(source: String): Map[String, String] = LINE_REGEX
    .findAllMatchIn(source)
    .map(keyValue =>
      (keyValue.group(1), unescapeCharacters(removeQuotes(keyValue.group(2))))
    )
    .toMap

  private def removeQuotes(value: String): String = {
    value.trim match {
      case quoted if quoted.startsWith("'") && quoted.endsWith("'")   =>
        quoted.substring(1, quoted.length - 1)
      case quoted if quoted.startsWith("\"") && quoted.endsWith("\"") =>
        quoted.substring(1, quoted.length - 1)
      case unquoted                                                   => unquoted
    }
  }

  private def unescapeCharacters(value: String): String = {
    value.replaceAll("""\\([^$])""", "$1")
  }
}
