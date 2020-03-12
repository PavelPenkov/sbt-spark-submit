package me.penkov.sbt.spark_submit

import sbt._

import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

class SparkSubmitter(settings: SparkSubmitSettings, logger: Logger) {
  def commandLine: Seq[String] = {
    val cmd = ArrayBuffer("spark-submit", "--class", settings.mainClass)

    cmd++=settings.master.args

    cmd++=settings.deployMode.args

    val singleValArgs = Seq(
      ("--name", settings.appName),
      ("--driver-memory", settings.driverMemory),
      ("--driver-library-path", settings.driverLibraryPath),
      ("--driver-class-path", settings.driverClassPath),
      ("--executor-memory", settings.executorMemory),
      ("--proxy-user", settings.proxyUser),
      ("--verbose", settings.verbose),
      ("--files", settings.files.mkString(",")),
      ("--jars", settings.jars.mkString(","))

    ).collect { case (arg, Some(value)) => Seq(arg, value.toString)}
      .flatten

    cmd++=singleValArgs

    settings.packages.foreach { pkg =>
      logger.info(pkg.toString())
      logger.info(s"org=${pkg.organization}, name=${pkg.name}, revision=${pkg.revision}, crossVersion=${pkg.crossVersion}")
    }

    val packageArgs = finalDependencies.map(_.toString)

    if (packageArgs.nonEmpty) cmd++=Seq("--packages", packageArgs mkString ",")

    val confArgs = settings.conf.flatMap { case (key, value) => Seq("--conf", s"$key=$value") }

    cmd++=confArgs

    if (settings.resolvers.nonEmpty) {
      cmd ++= Seq("--repositories", settings.resolvers.mkString(","))
    }

    cmd += uploadPath

    cmd ++= settings.args

    cmd
  }

  def copy(): Unit = settings.master match {
    case _: Local =>
    case _ =>
      val cmd = Seq("scp", settings.appJar.toString, s"${settings.user}@${settings.host.get}:$uploadPath")
      logger.info(s"Copying JAR using $cmd")
      if (cmd.! != 0) throw new RuntimeException("Error while copying application JAR")
  }

  def run(): Unit = {
    val cmd: Seq[String] = settings.master match {
      case _: Local => commandLine
      case _ => Seq("ssh", s"${settings.user}@${settings.host.get}", commandLine mkString " ")
    }

    logger.info(s"Running using $cmd")

    if (cmd.! != 0) throw new RuntimeException("Error while running application")
  }

  def submit(): Unit = {
    logger.info("*****************************")
    logger.info(settings.toString)
    logger.info("*****************************")
    copy()
    run()
  }

  def finalDependencies = {
    val crossVersion = CrossVersion(settings.scalaVersion, CrossVersion.binaryScalaVersion(settings.scalaVersion))
    settings.packages
      .filterNot(_.configurations.contains("provided"))
      .filterNot(_.configurations.contains("test"))
      .filterNot(_.configurations.contains("compile"))
      .map(crossVersion)
  }

  val uploadPath = s"/tmp/${settings.appJar.getName}"
}
