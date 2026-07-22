version                  := "0.1"
ThisBuild / envFileNames := Seq(".env2")

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  val contains      = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  if (!sys.env.get("LINE_ONE").isEmpty || !sys.env.get("LINE_TWO").isEmpty)
    sys.error("environment variables are set, but should not")
  if (sys.env.get("LINE_THREE").isEmpty || sys.env.get("LINE_FOUR").isEmpty)
    sys.error("environment variables are not set")
}
