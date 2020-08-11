package de.beiertu.kafka.protobuf.example

import com.google.protobuf.Message
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory


interface Producer {
    fun publish(topic: String, key: String, message: Message): RecordMetadata?
}

class DefaultProducer(config: Config) : Producer {
    companion object {
        private val log = LoggerFactory.getLogger(DefaultProducer::class.java)
    }

    private val producer = KafkaProducer<String, Message>(config.toProperties(ConfigType.PRODUCER))

    override fun publish(topic: String, key: String, message: Message): RecordMetadata? = try {
        producer
            .send(ProducerRecord(topic, key, message))
            .get()
    } catch (e: Exception) {
        null
            .also { log.error("failed to published event $message", e) }
    }
}
