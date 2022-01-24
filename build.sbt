sbtPlugin := true

name := "sbt-dotenv"

description := "An sbt plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."

scalaVersion := "2.12.8"

publishMavenStyle := true

inThisBuild(List(
  organization := "au.com.onegeek",
  homepage := Some(url("https://github.com/mefellows/sbt-dotenv")),
  licenses := List("MIT" -> url("https://raw.githubusercontent.com/mefellows/sbt-dotenv/master/LICENSE")),
  developers := List(
    Developer(
        id    = "Philippus",
        name  = "Philippus Baalman",
        email = "",
        url = url("http://wehkamp.nl")
    ),
    Developer(
        id    = "mefellows",
        name  = "Matt Fellows",
        email = "",
        url = url("http://www.onegeek.com.au")
    )
  )
))

// For all Sonatype accounts created on or after February 2021
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
sonatypeProfileName := "au.com.onegeek"

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

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
