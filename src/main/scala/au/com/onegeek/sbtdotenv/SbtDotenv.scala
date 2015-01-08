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

import sbt._
import sbt.Keys._

import scala.io.Source

/**
 * SBT dotenv - a dotenv (https://github.com/bkeepers/dotenv) implementation for Scala SBT.
 *
 * Reads a file
 *
 */
object SbtDotenv extends AutoPlugin {
  object autoImport {
    val dotEnvFile = SettingKey[String]("dot-env-file", "Path to .env file to be loaded")
    val dotEnvOverride = SettingKey[Boolean]("dot-env-override", "Whether the .env file should override system variables")
  }

  import autoImport._

  override def trigger = allRequirements

  override lazy val buildSettings = Seq(
    dotEnvFile := (baseDirectory.value / ".env").toString,
    dotEnvOverride := true,
    onLoad in Global ~= (_ andThen { state =>
      val project = Project.extract(state)
      configureEnvironment(project.get(dotEnvFile), state.log, project.get(dotEnvOverride))
      state
    })
  )

  /**
   * Configures the SBT environment from a dotfile (.env) if one exists.
   *
   * @param file Path to the file to load
   * @return
   */
  def configureEnvironment(file: String, logger: Logger, overrideSystem: Boolean): Unit = {
    import scala.collection.JavaConverters._

    logger.debug(s"Looking for .env file: $file")

    val openFile = new File(file)
    parseFile(openFile).map { environment =>
      val filteredEnv = if(!overrideSystem) {
        environment -- sys.env.keySet
      } else {
        environment
      }

      logger.debug(s".env detected. About to configure JVM System Environment with new map: $environment")
      NativeEnvironmentManager.setEnv(filteredEnv.asJava)
      DirtyEnvironmentHack.setEnv((sys.env ++ filteredEnv).asJava)
      logger.info(s"Configured environment from file: $file")
    }.getOrElse {
      logger.debug(s"Environment file not found, skipping: $file")
    }
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
    }
    else {
      val source = Source.fromFile(file)
      val result = source.getLines().filter(isValidLine).foldLeft(Map[String, String]())((env, line) => {
        parseLine(line) match {
          case Some(e) => env + (e._1 -> e._2)
        }
      })
      source.close()
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
    isValidLine(line) match {
      case true =>
        val split = line.split("=", 2)
        Some(split(0) -> split(1))
      case false => None
    }
  }
}
