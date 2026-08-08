version := "0.1"

TaskKey[Unit]("checkEnvVarsLoaded") := {
  val appEnv = sys.env.get("APP_ENV")
  val debugMode = sys.env.get("DEBUG_MODE")
  
  if (appEnv.isEmpty || appEnv.get != "development")
    sys.error(s"APP_ENV should be loaded from .env, got: $appEnv")
  
  if (debugMode.isEmpty || debugMode.get != "true")
    sys.error(s"DEBUG_MODE should be loaded from .env, got: $debugMode")
  
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("Configured .env environment"))
    sys.error("expected log message 'Configured .env environment'")
}
