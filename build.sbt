name := "sbt-dotenv"
organization := "au.com.onegeek"
description := "An sbt plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."
startYear := Some(2014)
homepage := Some(url("https://github.com/mefellows/sbt-dotenv"))
licenses += ("MIT" -> url("https://raw.githubusercontent.com/mefellows/sbt-dotenv/master/LICENSE"))

developers := List(
  Developer(
    id    = "Philippus",
    name  = "Philippus Baalman",
    email = "",
    url = url("https://github.com/philippus")
  ),
  Developer(
    id    = "mefellows",
    name  = "Matt Fellows",
    email = "",
    url = url("http://www.onegeek.com.au")
  )
)

enablePlugins(SbtPlugin)
sbtPlugin := true
pluginCrossBuild / sbtVersion := "1.3.9" // minimum version we target

libraryDependencies ++= Seq(
    "net.java.dev.jna" % "jna" % "5.10.0",
    "org.scalatest" %% "scalatest" % "3.2.11" % Test
)

enablePlugins(ScriptedPlugin)

scriptedLaunchOpts := {
    if (System.getProperty("java.version").startsWith("1.")) {
        scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    }
    else {
        scriptedLaunchOpts.value ++ Seq("--illegal-access=deny", "--add-opens", "java.base/java.util=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-Xmx1024M", "-Dplugin.version=" + version.value)
    }
}

scriptedBufferLog := false
