organization := "se.citerus"

name := "backend"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % "2.0.4",
  "org.scalatra" %% "scalatra-scalate" % "2.0.4",
  "org.scalatra" %% "scalatra-specs2" % "2.0.4" % "test",
  "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "7.6.0.v20120127" % "container",
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "com.mongodb.casbah" % "casbah-core_2.9.0-1" % "2.1.5.0",
  "cc.spray" %%  "spray-json" % "1.1.1"
)

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Spray repository" at "http://repo.spray.cc/"
