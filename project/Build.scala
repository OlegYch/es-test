import sbt.Keys._
import sbt._

object Build extends Build {
  val esVersion = settingKey[String]("elasticsearch version")
  val root = project.in(file(".")).settings(
    esVersion := "1.2.1"
    , fork in run := true
    , javaOptions := Seq("-Xmx500m")
    , libraryDependencies ++= Seq(
      "com.sksamuel.elastic4s" %% "elastic4s" % "1.2.0.0" exclude("org.elasticsearch", "elasticsearch")
      , "org.elasticsearch" % "elasticsearch" % esVersion.value force())
    , libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.7"
      , "ch.qos.logback" % "logback-classic" % "1.1.2" % Runtime exclude("org.slf4j", "slf4j-api"))
    , libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2"
  )
}
