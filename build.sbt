sbtPlugin := true

name := "sbt-dotenv"
organization := "au.com.onegeek"

description := "An sbt plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
    "net.java.dev.jna" % "jna" % "5.1.0",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

enablePlugins(ScriptedPlugin)
scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

publishMavenStyle := false

bintrayOrganization := None
bintrayRepository := "sbt-plugins"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
