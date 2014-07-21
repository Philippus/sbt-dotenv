import bintray.Keys._

sbtPlugin := true

name := "sbt-dotenv"

organization := "au.com.onegeek"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"

scalaVersion := "2.10.3"

version := "1.0.1"

publishMavenStyle := false

bintrayPublishSettings

repository in bintray := "sbt-plugins"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization in bintray := None