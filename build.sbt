name := "library"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVersion = "2.5.12"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % akkaVersion
