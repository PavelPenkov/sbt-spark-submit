package me.penkov.sbt.spark_submit

import sbt.{File, ModuleID, Resolver}

case class SparkSubmitSettings(
                                appJar: File,
                                mainClass: String,
                                user: String,
                                host: Option[String],
                                args: Seq[String],
                                master: Master,
                                deployMode: DeployMode,
                                appName: String,
                                jars: Seq[String],
                                files: Seq[File],
                                conf: Map[String, Any],
                                driverMemory: Option[Long],
                                driverJavaOptions: Option[String],
                                driverLibraryPath: Option[String],
                                driverClassPath: Option[String],
                                executorMemory: Option[Long],
                                proxyUser: Option[String],
                                verbose: Boolean,
                                packages: Seq[ModuleID],
                                resolvers: Seq[Resolver],
                                scalaVersion: String
                              )
