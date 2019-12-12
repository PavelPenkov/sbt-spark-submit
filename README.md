# sbt-spark-submit

Conviniently submit Spark jobs. This plugin makes it easier to submit Spark jobs to remote servers, it requires `ssh`
and `scp` command line tools installed.

## Usage

Add `addSbtPlugin("me.penkov" % "sbt-spark-submit" % "0.1.15")` to `project/spark-submit.sbt`.

Configure Spark submit settings in `build.sbt`

```
mainClass in sparkSubmit := Some("MyClass")

sparkMaster in sparkSubmit := Yarn(
  numExecutors = Some(100),
  executorCores = Some(1),
  queue = Some("production")
)

sparkConf := Map("spark.dynamicAllocation.enabled" -> "false")

sparkArgs := Seq("--date", "2019-12-12")

sshHost in sparkSubmit := Some("somehost")
sshUser in sparkSubmit := "ppenkov" // Default is current user
```

Run `sbt sparkSubmit`. The plugin will build a thin JAR (fat JARs not supported yet), copy it to remote host and run
`spark-submit` with appropriate arguments.
