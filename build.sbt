name := """sbt-spark-submit"""
organization := "me.penkov"
version := "0.1.15"

sbtPlugin := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

publishMavenStyle := false

bintrayRepository := "sbt-plugins"
bintrayPackageLabels := Seq("sbt","plugin")
bintrayVcsUrl := Some("""git@github.com:PavelPenkov/sbt-spark-submit.git""")

initialCommands in console := """import me.penkov.sbt._"""

enablePlugins(ScriptedPlugin)
// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
