package au.com.onegeek.sbtdotenv

import sbt.Def

private[sbtdotenv] object Compat:
  def additionalProjectSettings(baseEnvFileSettings: Seq[Def.Setting[?]]) =
    Seq.empty
end Compat
