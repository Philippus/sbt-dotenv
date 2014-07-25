import bintray.AttrMap
import bintray._

sbtPlugin := true

name := "sbt-dotenv"

description := "An SBT Plugin to load environment variables from .env into the JVM System Environment for local development. Assists with 'Twelve Factor App' development principle 3 'Store config in the environment'."

organization := "au.com.onegeek"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"

scalaVersion := "2.10.4"

version := "1.0"

publishMavenStyle := false

bintrayPublishSettings

bintray.Keys.repository in bintray.Keys.bintray := "sbt-plugins"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintray.Keys.bintrayOrganization in bintray.Keys.bintray := None