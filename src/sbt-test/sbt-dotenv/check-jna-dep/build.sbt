TaskKey[Unit]("check") := {
  assert(
    sys.env.get("SBT_DOTENV_JNA_REGRESSION").contains("loaded"),
    "Expected .env to be loaded using the plugin's own JNA dependency"
  )
}
