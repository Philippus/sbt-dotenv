version := "0.1"

TaskKey[Unit]("check") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  if (sys.env.get("LINE_ONE") != Some("TEST-ONE*") || sys.env.get("LINE_TWO") != Some("TEST TWO"))
    sys.error("environment variables not set correctly")
}
