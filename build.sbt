name         := "sbt-dotenv"
organization := "nl.gn0s1s"
description  := "An sbt plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."
startYear    := Some(2014)
homepage     := Some(url("https://github.com/philippus/sbt-dotenv"))
licenses += ("MIT" -> url("http://opensource.org/licenses/MIT"))

developers := List(
  Developer(
    id = "Philippus",
    name = "Philippus Baalman",
    email = "",
    url = url("https://github.com/philippus")
  ),
  Developer(
    id = "mefellows",
    name = "Matt Fellows",
    email = "",
    url = url("http://www.onegeek.com.au")
  )
)

enablePlugins(SbtPlugin)

scalaVersion := "2.12.20"
crossScalaVersions += "3.7.2"

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.5.8"
    case _      => "2.0.0-RC3"
  }
}

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

enablePlugins(ScriptedPlugin)

scriptedLaunchOpts := {
  if (System.getProperty("java.version").startsWith("1.")) {
    scriptedLaunchOpts.value ++ Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value
    )
  } else {
    scriptedLaunchOpts.value ++ Seq(
      "--illegal-access=deny",
      "--add-opens",
      "java.base/java.util=ALL-UNNAMED",
      "--add-opens",
      "java.base/java.lang=ALL-UNNAMED",
      "-Xmx1024M",
      "-Dplugin.version=" + version.value
    )
  }
}

scriptedBufferLog := false
