package me.penkov.sbt.spark_submit

import sbt.Keys._
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Def, File, Keys, Resolver, settingKey, taskKey, Compile}

object SparkSubmitPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport {
    // SSH settings
    val sshUser = settingKey[String]("SSH user")
    val sshHost = settingKey[Option[String]]("SSH host")
    val sparkArgs = settingKey[Seq[String]]("Application arguments")

    // Spark submit settings
    val sparkMaster = settingKey[Master]("Spark master")
    val sparkDeployMode = settingKey[DeployMode]("Whether to launch the driver program locally ('client') or on one of the worker machines inside the cluster ('cluster')")
    val sparkName = settingKey[String]("A name of your application")
    val sparkJars = settingKey[Seq[String]]("List of jars to include on the driver and executor classpaths")
    // val sparkExcludePackages = settingKey[Seq[ModuleID]]("List of excluded packages")
    val sparkRepositories = settingKey[Seq[Resolver]]("Additional resolvers")
    val sparkFiles = settingKey[Seq[File]]("List of files to be placed in the working directory of each executor")
    val sparkConf = settingKey[Map[String, Any]]("Arbitrary Spark configuration property")
    val sparkDriverMemory = settingKey[Option[Long]]("Memory for driver in bytes")
    val sparkDriverJavaOptions = settingKey[Option[String]]("Extra Java options to pass to the driver")
    val sparkDriverLibraryPath = settingKey[Option[String]]("Extra library path entries to pass to the driver")
    val sparkDriverClassPath = settingKey[Option[String]]("Extra class path entries to pass to the driver")
    val sparkExecutorMemory = settingKey[Option[Long]]("Memory per executor")
    val sparkProxyUser = settingKey[Option[String]]("User to impersonate when submitting the application.")
    val sparkVerbose = settingKey[Boolean]("Print additional debug output")

    val sparkSubmit = taskKey[Unit]("Submit Spark application")
  }

  import autoImport._

  lazy val submitTask = Def.task {
    val f = (Keys.`package` in Compile).value
    val mc = (mainClass in sparkSubmit).value


    val settings = SparkSubmitSettings(f,
      (Compile / selectMainClass).value.get,
      user = (sshUser in sparkSubmit).value,
      host = (sshHost in sparkSubmit).value,
      args = (sparkArgs in sparkSubmit).value,
      master = (sparkMaster in sparkSubmit).value,
      deployMode = (sparkDeployMode in sparkSubmit).value,
      appName = (sparkName in sparkSubmit).value,
      jars = (sparkJars in sparkSubmit).value,
      files = (sparkFiles in sparkSubmit).value,
      conf = (sparkConf in sparkSubmit).value,
      driverMemory = (sparkDriverMemory in sparkSubmit).value,
      driverJavaOptions = (sparkDriverJavaOptions in sparkSubmit).value,
      driverLibraryPath = (sparkDriverLibraryPath in sparkSubmit).value,
      driverClassPath = (sparkDriverLibraryPath in sparkSubmit).value,
      executorMemory = (sparkExecutorMemory in sparkSubmit).value,
      proxyUser = (sparkProxyUser in sparkSubmit).value,
      verbose = (sparkVerbose in sparkSubmit).value,
      packages = libraryDependencies.value,
      scalaVersion = scalaVersion.value,
      resolvers = (sparkRepositories in sparkSubmit).value
    )

    val submit = new SparkSubmitter(settings, streams.value.log)

    submit.submit()
  }

  override lazy val projectSettings = Seq(
    sparkArgs := Nil,
    sparkMaster := Yarn(),
    sparkDeployMode := Client,
    sparkName := name.value,
    sshHost := None,
    sshUser := sys.props("user.name"),
    sparkJars := Nil,
    sparkFiles := Nil,
    sparkConf := Map.empty[String, Any],
    sparkDriverMemory := None,
    sparkDriverJavaOptions := None,
    sparkDriverLibraryPath := None,
    sparkDriverClassPath := None,
    sparkExecutorMemory := None,
    sparkProxyUser := None,
    sparkVerbose := false,
    sparkRepositories := resolvers.value,
    sparkSubmit := submitTask.value,
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}
