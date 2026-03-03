package au.com.onegeek.sbtdotenv

import sbt.Def
import sbt.librarymanagement.Configurations.IntegrationTest

private[sbtdotenv] object Compat {
  def additionalProjectSettings(baseEnvFileSettings: Seq[Def.Setting[_]]) =
    sbt.Project.inConfig(IntegrationTest)(baseEnvFileSettings)
}
