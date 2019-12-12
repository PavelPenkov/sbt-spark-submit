import me.penkov.sbt._

version := "0.1"
scalaVersion := "2.12.9"

mainClass in sparkSubmit := Some("simple.Program")

sparkName := Some("simple")

sshHost := Some("10.20.80.62")

sshUser := "papenkov"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.1"
