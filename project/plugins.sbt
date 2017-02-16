//
// sbt build info
//
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

//
// sbt revolver to reload on file system changes
//
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

//
// twirl for templates
//
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.0")

//
// Generate file headers using `sbt createHeaders`
//
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "1.5.1-2-g8b57b53")

//
// Native-Packager - to build the docker image
//
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0-RC1")

