lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    version := "0.1",
    envFileName in ThisBuild := "build.env",
    envFileName in Test := "test.env",
    envFileName in IntegrationTest := "integration.env",
    fork in test := true,
    fork in IntegrationTest := true
  )

TaskKey[Unit]("checkGlobal") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains(".env file not found (fileName=build.env), no .env environment configured.")
  if (!contains)
    sys.error("expected log message")
}

TaskKey[Unit]("checkTest") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains(".env detected (fileName=test.env)")
  if (!contains)
    sys.error("expected log message")
}

TaskKey[Unit]("checkIntegrationTest") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains(".env detected (fileName=integration.env)")
  if (!contains)
    sys.error("expected log message")
}