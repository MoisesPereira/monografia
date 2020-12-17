name := "Flink_Stream"

version := "0.1"

scalaVersion := "2.11.12"

val flink = "1.11.1"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.apache.flink" % "flink-core" % flink
libraryDependencies += "org.apache.flink" %% "flink-scala" % flink
libraryDependencies += "org.apache.flink" %% "flink-clients" % flink
libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % flink
libraryDependencies += "org.apache.flink" %% "flink-runtime-web" % flink
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka-0.11" % flink

assemblyJarName in assembly := "flink-assembly-fatjar-1.0.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}