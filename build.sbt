// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)
enablePlugins(WorkbenchPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.2"

scalaJSUseMainModuleInitializer := true

testFrameworks += new TestFramework("utest.runner.Framework")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  "com.lihaoyi" %%% "utest" % "0.4.5" % "test",
  "com.lihaoyi" %%% "scalatags" % "0.6.5",
  "com.lihaoyi" %%% "scalarx" % "0.3.2"
)
