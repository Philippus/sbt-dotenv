version := "0.1"

TaskKey[Unit]("check") :=  {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val contains = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  if (!(sys.env.get("LINE_ONE").exists(_ == "") && sys.env.get("LINE_TWO").exists(_ == "abc=def") &&
    sys.env.get("LINE_THREE").exists(_ == "xyz") && sys.env.get("LINE_FOUR").exists(_ == "a b c") &&
    sys.env.get("LINE_FIVE").exists(_ == "'xyz'" && sys.env.get("LINE_SIX").exists(_ == "abc#xyz"))))
    sys.error("environment variables have unexpected values")
}
