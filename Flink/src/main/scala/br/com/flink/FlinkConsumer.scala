package br.com.flink

import java.sql.Timestamp
import java.util.{Calendar, Properties}

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer010, FlinkKafkaProducer010}

object FlinkConsumer extends App {

  val calendar = Calendar.getInstance
  val now = new Timestamp(calendar.getTime.getTime)
  println(now)

  val conf = new Configuration()
  val env = StreamExecutionEnvironment.getExecutionEnvironment

  // prepare Kafka consumer properties
  val kafkaConsumerProperties = new Properties()
  kafkaConsumerProperties.setProperty("zookeeper.connect", "localhost:2181")
  kafkaConsumerProperties.setProperty("group.id", "fkil_kafka_grou_02")
  kafkaConsumerProperties.setProperty("bootstrap.servers", "localhost:9092")
  kafkaConsumerProperties.setProperty("enable.auto.commit", "false")
  kafkaConsumerProperties.setProperty("auto.offset.reset", "earliest")

  ///////////////// TOPICOS /////////////////////////
  val origem = "transacoes_1000mb" // Consumer
  val TPCBOLETO = "boleto" // Producer
  val TPCCARTAO = "cartaocredito" // Producer
  val TPCTRANSF = "transferencia" // Producer

  // CONSUMER
  val stream = env
    .addSource(new FlinkKafkaConsumer010[String](origem, new SimpleStringSchema(),
      kafkaConsumerProperties))

  val properties = new Properties()
  properties.setProperty("bootstrap.servers", "localhost:9092")

  //// CREATE SINK TOPIC KAFKA
  val sink_boleto = new FlinkKafkaProducer010[String](
    TPCBOLETO, // target topic
    new SimpleStringSchema(), // serialization schema
    properties
  )

  //// CREATE SINK TOPIC KAFKA
  val sink_transferencia = new FlinkKafkaProducer010[String](
    TPCTRANSF, // target topic
    new SimpleStringSchema(), // serialization schema
    properties
  )

  //// CREATE SINK TOPIC KAFKA
  val sink_cartaocredito = new FlinkKafkaProducer010[String](
    TPCCARTAO, // target topic
    new SimpleStringSchema(), // serialization schema
    properties
  )

  //// PRODUCER
  stream.filter(msg => msg.contains("Boleto"))
    .addSink(sink_boleto)

  //// PRODUCER
  stream.filter(msg => msg.contains("EletronicTransfer"))
    .addSink(sink_transferencia)

  //// PRODUCER
  stream.filter(msg => msg.contains("CreditCard"))
    .addSink(sink_cartaocredito)

  env.execute("Flink Streaming")

}
