package de.beiertu.kafka.protobuf.example.kafka

import com.google.protobuf.GeneratedMessageV3
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import java.util.UUID


interface EventProducer {
    fun publish(message: GeneratedMessageV3): RecordMetadata?
}

class DefaultEventProducer : EventProducer {
    private val producer = KafkaProducer<String, GeneratedMessageV3>(properties)

    override fun publish(message: GeneratedMessageV3): RecordMetadata? {
        return producer.send(
            ProducerRecord(
                "person-events",
                UUID.randomUUID().toString(),
                message
            )
        ).get()
    }
}

val properties = Properties().apply {
    this[CommonClientConfigs.CLIENT_ID_CONFIG] = "kafka-protobuf-example"
    this[CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG] = 20000
    this[CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG] = 500

    this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    this[ProducerConfig.ACKS_CONFIG] = "all"
    this[ProducerConfig.RETRIES_CONFIG] = "1"
    this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaProtobufSerializer::class.java

    this[KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = "http://localhost:8081"

    this[AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY] = TopicRecordNameStrategy::class.java.name
}
