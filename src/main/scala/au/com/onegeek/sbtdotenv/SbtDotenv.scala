/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Matt Fellows (OneGeek)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package au.com.onegeek.sbtdotenv

import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._
import scala.io.Source

/**
 * sbt-dotenv - a dotenv (https://github.com/bkeepers/dotenv) implementation for Scala sbt.
 *
 * Reads a file
 *
 */
object SbtDotenv extends AutoPlugin {

  object autoImport {
    val dotEnv = (s: State) => configureEnvironment(s)
  }

  import au.com.onegeek.sbtdotenv.SbtDotenv.autoImport._

  override def trigger = allRequirements

  // Automatically configure environment on load
  override lazy val buildSettings =
    Seq(onLoad in Global := dotEnv compose (onLoad in Global).value)

  /**
   * Configures the sbt environment from a dotfile (.env) if one exists.
   *
   * @param state
   * @return
   */
  def configureEnvironment(state: State): State = {
    state.log.debug(s"Base directory: ${state.configuration.baseDirectory}")
    state.log.debug(s"looking for .env file: ${state.configuration.baseDirectory}/.env")
    val dotEnvFile: File = new File(state.configuration.baseDirectory + "/.env")
    parseFile(dotEnvFile) match {
      case Some(environment) =>
        state.log.debug(s".env detected. About to configure JVM System Environment with new map: $environment")
        NativeEnvironmentManager.setEnv(environment.asJava)
        DirtyEnvironmentHack.setEnv((sys.env ++ environment).asJava)
        state.log.info("Configured .env environment")
      case None =>
        state.log.debug(s".env file not found, no .env environment configured.")
    }
    state
  }

  /**
   * Parse provided file in .env format and return an immutable environment Map[String, String]
   *
   * @param file .env file to read
   * @return
   */
  def parseFile(file: File): Option[Map[String, String]] = {
    if (!file.exists) {
      None
    } else {
      val source = Source.fromFile(file)
      val result = source.getLines.foldLeft(Map.empty[String, String]) { (env, line) =>
        parseLine(line).map { env + _ } getOrElse env
      }
      source.close
      Some(result)
    }
  }

  def isValidLine(line: String): Boolean = line.matches("^[a-zA-Z_]+[a-zA-Z0-9_]*=.*")

  /**
   * Extract k/v pairs from each line as an environment Key -> Value.
   *
   * @param line A line of text to convert into a Tuple2
   * @return
   */
  def parseLine(line: String): Option[(String, String)] = {
    if (isValidLine(line)) {
      val splitted = line.split("=", 2)
      Some(splitted(0) -> splitted(1).split(" #")(0).trim)
    } else {
      None
    }
  }
}
