// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)
enablePlugins(WorkbenchPlugin)

name := "Scala Js React Guide"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.2"

scalaJSUseMainModuleInitializer := true

testFrameworks += new TestFramework("utest.runner.Framework")

libraryDependencies ++= Seq(
  "com.github.japgolly.scalajs-react" %%% "core" % "1.0.1",
  "com.github.japgolly.scalajs-react" %%% "extra" % "1.0.1"
//  scalareact("core"),
//  scalareact("extra")
)

jsDependencies ++= Seq(
  jsreact("react-with-addons") commonJSName "React",
  jsreact("react-dom") commonJSName "ReactDOM" dependsOn "react-with-addons.js",
  jsreact("react-dom-server") commonJSName "ReactDOMServer" dependsOn "react-dom.js"
)

//def scalareact(artifact: String) = "com.github.japgolly.scalajs-react".%%%(artifact).%("1.0.1")

def jsreact(file: String) = ("org.webjars.bower" % "react" % "15.5.4")./(file+".js").minified(file+".min.js")
