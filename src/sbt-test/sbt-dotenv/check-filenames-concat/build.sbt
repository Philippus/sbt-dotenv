version                  := "0.1"
ThisBuild / envFileNames := Seq(".env", ".moreEnv")

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  val contains      = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  if (sys.env.get("LINE_ONE").isEmpty || sys.env.get("LINE_TWO").isEmpty || sys.env.get("LINE_THREE").isEmpty)
    sys.error("environment variables are not set")
  if (sys.env.get("LINE_TWO") != Some("efg"))
    sys.error("environment variables are not overridden from the second file")
}
