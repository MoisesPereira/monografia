name := "Spark_Stream"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.0"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.11.0.0"

assemblyJarName in assembly := "spark-stream-assembly-fatjar-1.0.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}