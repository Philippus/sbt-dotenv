version := "0.1"

TaskKey[Unit]("check") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains(".env file not found, no .env environment configured.")
  if (!contains)
    sys.error("expected log message")
}
