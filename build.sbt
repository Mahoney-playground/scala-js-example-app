//import sbt.Keys.libraryDependencies
// Turn this project into a Scala.js project by importing these settings

enablePlugins(WorkbenchPlugin)

name := "Scala Js React Guide"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.2"

val app = crossProject.settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.5",
    "com.lihaoyi" %%% "upickle" % "0.4.4",
    "fr.hmil" %%% "roshttp" % "2.0.2"
  ),
  scalaVersion := "2.12.2",
  unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "main" / "scala"
).jsSettings(
  jsDependencies ++= Seq(
    jsreact("react-with-addons") commonJSName "React",
    jsreact("react-dom") commonJSName "ReactDOM" dependsOn "react-with-addons.js",
    jsreact("react-dom-server") commonJSName "ReactDOMServer" dependsOn "react-dom.js"
  ),
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % "1.0.1",
    "com.github.japgolly.scalajs-react" %%% "extra" % "1.0.1"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.8",
    "com.typesafe.akka" %% "akka-http-core" % "10.0.8",
    "com.typesafe.akka" %% "akka-actor" % "2.4.19",
    "org.webjars" % "bootstrap" % "3.2.0"
  )
)

lazy val appJS = app.js
lazy val jsFile = fastOptJS in(appJS, Compile)
lazy val appJVM = app.jvm.settings(
  (resources in Compile) += jsFile.value.data,
  (resources in Compile) += jsFile.value.data.toPath.resolveSibling(jsFile.value.data.name+".map").toFile,
  (resources in Compile) += (packageJSDependencies in(appJS, Compile)).value
)

//def scalareact(artifact: String) = "com.github.japgolly.scalajs-react".%%%(artifact).%("1.0.1")

def jsreact(file: String) = ("org.webjars.bower" % "react" % "15.5.4")./(file+".js").minified(file+".min.js")
