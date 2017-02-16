import Versions._
import de.heikoseeberger.sbtheader.license.Apache2_0

enablePlugins(BuildInfoPlugin, SbtTwirl, JavaAppPackaging)

organization := "xyz.0x7e"

name := "web"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.1"

http4sVersion := "0.15.3a"
doobieVersion := "0.4.1"
slf4jVersion  := "1.6.4"
mdlVersion    := "1.3.0"
jqueryVersion := "3.1.1-1"

libraryDependencies ++= Seq(

  // web server
  "org.http4s"    %% "http4s-blaze-server"  % http4sVersion.value,
  "org.http4s"    %% "http4s-dsl"           % http4sVersion.value,
  "org.http4s"    %% "http4s-argonaut"      % http4sVersion.value,
  "org.http4s"    %% "http4s-twirl"         % http4sVersion.value,

  // database access
  "org.tpolecat"  %% "doobie-core"          % doobieVersion.value,
  "org.tpolecat"  %% "doobie-postgres"      % doobieVersion.value,

  // barcode
  "com.google.zxing"  % "core"              % "3.3.0",

  // logging
  "org.slf4j"     %  "slf4j-simple"         % slf4jVersion.value,

  // frontend
  "org.webjars"   %  "material-design-lite" % mdlVersion.value,
  "org.webjars"   %  "jquery"               % jqueryVersion.value

)

buildInfoPackage := "xyz._0x7e"

buildInfoKeys := Seq[BuildInfoKey](
  organization,
  name,
  version,
  scalaVersion,
  sbtVersion,
  http4sVersion,
  doobieVersion,
  slf4jVersion,
  mdlVersion,
  jqueryVersion
)

//
// Publishing settings
//

publishArtifact in Test := false

publishTo := {
  val nexus = "https://nexus.timo-schmid.ch/repository/maven-"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "snapshots")
  else
    Some("releases" at nexus + "releases")
}

//
// Credentials
//

buildCredentials

lazy val buildCredentials: sbt.SettingsDefinition = {
  (for {
    username <- Option(System.getenv().get("DEPLOY_USERNAME"))
    password <- Option(System.getenv().get("DEPLOY_PASSWORD"))
  } yield {
    credentials += Credentials("Sonatype Nexus", "nexus.timo-schmid.ch", username, password)
  }).getOrElse(credentials ++= Seq())
}

//
// Docker package configuration 
// 

packageName in Docker := "0x7e.xyz"

maintainer in Docker := "Timo Schmid <timo.schmid@gmail.com>"

dockerRepository := Some("docker.timo-schmid.ch")

defaultLinuxInstallLocation in Docker := "/opt/0x7e.xyz"

daemonUser in Docker := "daemon"

headers := Map(
  "scala" -> Apache2_0("2016", "Timo Schmid")
)

HeaderPlugin.settingsFor(Compile, Test)
