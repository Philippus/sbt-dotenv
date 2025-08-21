package au.com.onegeek.sbtdotenv

import sbt.Def
import sbt.librarymanagement.Configurations.IntegrationTest

private[sbtdotenv] object Compat {
  def additionalProjectSettings(baseEnvFileSettings: Seq[Def.Setting[_]]) =
    sbt.Project.inConfig(IntegrationTest)(baseEnvFileSettings)

  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
