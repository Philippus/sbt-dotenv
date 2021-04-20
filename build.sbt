sbtPlugin := true

name := "sbt-dotenv"
organization := "au.com.onegeek"

description := "An sbt plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
    "net.java.dev.jna" % "jna" % "5.8.0",
    "org.scalatest" %% "scalatest" % "3.2.8" % Test
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

publishMavenStyle := false

bintrayOrganization := None
bintrayRepository := "sbt-plugins"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
