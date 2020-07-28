package de.beiertu.kafka.protobuf.example.kafka

import com.google.protobuf.GeneratedMessageV3
import de.beiertu.kafka.protobuf.example.config.Config
import de.beiertu.kafka.protobuf.example.config.ConfigType
import de.beiertu.kafka.protobuf.example.config.toProperties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata


interface EventProducer {
    fun publish(topic: String, key: String, message: GeneratedMessageV3): RecordMetadata?
}

class DefaultEventProducer(config: Config) : EventProducer {
    private val producer = KafkaProducer<String, GeneratedMessageV3>(config.toProperties(ConfigType.PRODUCER))

    override fun publish(topic: String, key: String, message: GeneratedMessageV3): RecordMetadata? = try {
        producer
            .send(ProducerRecord(topic, key, message))
            .get()
    } catch (e: Exception) {
        null
            .also { e.printStackTrace() }
    }
}
