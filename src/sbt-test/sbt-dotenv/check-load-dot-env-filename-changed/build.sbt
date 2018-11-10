version := "0.1"

envFileName in ThisBuild := "dotenv"

TaskKey[Unit]("check") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  if (sys.env.get("LINE_ONE").isEmpty || sys.env.get("LINE_TWO").isEmpty)
    sys.error("environment variables not set")
}
