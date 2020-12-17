package br.com.kafkaconsumer

import java.sql.Timestamp
import java.util.Properties
import java.util.Calendar

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object SparkConsumer extends App {

  /// Configurações do Spark
  val calendar = Calendar.getInstance
  val now = new Timestamp(calendar.getTime.getTime)
  println(now)

  val conf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("Spark_Consumer_Kafka")

  val ssc = new StreamingContext(conf, Seconds(1))

  ///////////// KAFKA PARAMETROS CONSUMER
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "group.id" -> "grupo_kafika", "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  ///////////////// TOPICOS /////////////////////////
  val topics = Array("transacoes_1000mb") // Consumer
  val TPCBOLETO = "boleto" // Producer
  val TPCCARTAO = "cartaocredito" // Producer
  val TPCTRANSF = "transferencia" // Producer

  //////////////////  KAFKA PARAMETROS PRODUCER ///////////////////////////////////////////////
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](props)

  val stream: InputDStream[ConsumerRecord[String, String]] =
    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

  val boleto = stream.filter(record => record.value().contains("Boleto"))
  val transf = stream.filter(record => record.value().contains("EletronicTransfer"))
  val cartao = stream.filter(record => record.value().contains("CreditCard"))

  boleto.foreachRDD(msg => msg.foreach(record =>
    producer.send(new ProducerRecord(TPCBOLETO, record.key(), record.value()))
  ))

  transf.foreachRDD(msg => msg.foreach(record =>
    producer.send(new ProducerRecord(TPCTRANSF, record.key(), record.value()))
  ))
  cartao.foreachRDD(msg => msg.foreach(record =>
    producer.send(new ProducerRecord(TPCCARTAO, record.key(), record.value()))
  ))

  ssc.start() // Start the computation
  ssc.awaitTermination() // Wait for the computation to terminate

}

//  stream.foreachRDD { rdd =>
//    rdd.foreach { record =>
//      envio(record.value() )
//    }
//  }

//  def trataOrigem(tipo: String, msg: Any): Unit = {
////    print(msg)
//    if (tipo == "Some(Boleto)"){
//      producer.send(new ProducerRecord(TPCBOLETO, "1", s"${msg}"))
//    }
//    if (tipo == "Some(EletronicTransfer)"){
//      producer.send(new ProducerRecord(TPCTRANSF, "1", s"${msg}"))
//    }
//    if (tipo == "Some(CreditCard)"){
//      producer.send(new ProducerRecord(TPCCARTAO, "1", s"${msg}"))
//    }
//  }
//
//  def envio(msg: String): Unit = {
//    val json:Option[Any] = JSON.parseFull(msg)
//    val map:Map[String,Any] = json.get.asInstanceOf[Map[String, Any]]
//    val pagment:Map[String,Any] = map.get("Payment").get.asInstanceOf[Map[String,Any]]
//    val tipo:String = pagment.get("Type").toString
//    trataOrigem(tipo, msg)
//
//  }

  //// NAO FUNCIONOU IGUAL NO FLINK ---- APARENTEMENTE O FLINK PODE SER UMA MELHOR OPÇAO
  //// NAO FUNCIONOU IGUAL NO FLINK ---- APARENTEMENTE O FLINK PODE SER UMA MELHOR OPÇAO

  //  var boleto = stream.filter(msg => msg.value().contains("Boleto")).toString
  //  var transferencia = stream.filter(msg => msg.value().contains("EletronicTransfer"))
  //  var cartaocredito = stream.filter(msg => msg.value().contains("CreditCard"))
  //
  //  var teste = stream.foreachRDD { rdd =>
  //    rdd.filter(msg => msg.value().contains("Boleto") )
  //  }
  //  producer.send(new ProducerRecord( TPCBOLETO, "", teste.toString))
  //  producer.send(new ProducerRecord( TPCTRANSF, "", "${transferencia}"))
  //  producer.send(new ProducerRecord( TPCCARTAO, "", "${cartaocredito}"))
