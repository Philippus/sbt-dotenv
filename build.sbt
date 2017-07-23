sbtPlugin := true

name := "sbt-dotenv"

description := "An SBT Plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."

organization := "au.com.onegeek"

libraryDependencies ++= Seq(
    "net.java.dev.jna" % "jna" % "4.4.0",
    "org.scalatest" %% "scalatest" % "2.1.0" % "test"
)

ScriptedPlugin.scriptedSettings
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

scalaVersion := "2.10.6"

version := "1.1.38-SNAPSHOT"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
publishMavenStyle := false
bintrayRepository in bintray := "sbt-plugins"
bintrayOrganization in bintray := None
