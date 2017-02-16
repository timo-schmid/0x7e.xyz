import sbt.{SettingKey, settingKey}

object Versions {

  val http4sVersion: SettingKey[String] = settingKey[String]("The version of http4s")
  val doobieVersion: SettingKey[String] = settingKey[String]("The version of doobie")
  val slf4jVersion: SettingKey[String] = settingKey[String]("The version of SLF4J")
  val mdlVersion: SettingKey[String] = settingKey[String]("The version of Material Design Lite")
  val jqueryVersion: SettingKey[String] = settingKey[String]("The version of jQuery")

}
