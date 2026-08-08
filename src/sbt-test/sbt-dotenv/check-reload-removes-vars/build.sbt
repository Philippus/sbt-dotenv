version := "0.1"

TaskKey[Unit]("checkVarsSet") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  val contains      = last.contains("Configured .env environment")
  if (!contains)
    sys.error("expected log message")
  val varA = sys.env.get("DOTENV_VAR_A")
  val varB = sys.env.get("DOTENV_VAR_B")
  if (varA.isEmpty || varA.get != "initial_value")
    sys.error(s"DOTENV_VAR_A not set correctly, got: $varA")
  if (varB.isEmpty || varB.get != "second_value")
    sys.error(s"DOTENV_VAR_B not set correctly, got: $varB")
}

TaskKey[Unit]("removeVarFromFile") := {
  val envFile = baseDirectory.value / ".env"
  val content = IO.read(envFile)
  // Remove DOTENV_VAR_A line by commenting it out
  val updated = content.replace("DOTENV_VAR_A=initial_value", "# DOTENV_VAR_A=initial_value")
  IO.write(envFile, updated)
}

TaskKey[Unit]("checkVarARemovedAfterReload") := {
  val varA = sys.env.get("DOTENV_VAR_A")
  val varB = sys.env.get("DOTENV_VAR_B")
  if (varA.isDefined)
    sys.error(s"DOTENV_VAR_A should be unset after reload, but got: ${varA.get}")
  if (varB.isEmpty || varB.get != "second_value")
    sys.error(s"DOTENV_VAR_B should still be set, got: $varB")
}
