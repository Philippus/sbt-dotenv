version := "0.1"

TaskKey[Unit]("check") :=  {
  val log = sLog.value
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String = IO.read(lastLog)
  val expected = "Configured .env environment"

  log.info(s"checking for $expected in log output:\n$last")
  if (!last.contains(expected)) {
    sys.error(s"couldn't find $expected in log output")
  }

  def checkEnv(name: String, expected: String): Unit = {
    val actual = sys.env(name)
    log.info(s"""checking that $$$name is equal to $expected""")
    if (actual.contains(expected)) {
      log.debug(s"""$$$name, value $actual == $expected""")
    } else {
      sys.error(s"""sys.env("$name") $actual != $expected""")
    }
  }

  checkEnv("LINE_ONE", "")
  checkEnv("LINE_TWO", "abc=def")
  checkEnv("LINE_THREE", "xyz")
  checkEnv("LINE_FOUR", "a b c")
  checkEnv("LINE_FIVE", "xyz")
  checkEnv("LINE_SIX", "abc#xyz")
}
