version := "0.1"

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  val contains      = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  if (sys.env.get("INVALID_LINE").isDefined || sys.env.get("VALID_LINE").isEmpty)
    sys.error(
      "environment variables not set correctly, INVALID_LINE should be skipped and VALID_LINE should be defined"
    )
}
