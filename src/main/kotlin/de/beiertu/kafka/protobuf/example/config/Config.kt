package de.beiertu.kafka.protobuf.example.config

import com.typesafe.config.ConfigFactory
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig
import io.confluent.kafka.serializers.subject.TopicNameStrategy
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsConfig
import java.util.Properties

enum class ConfigType {
    PRODUCER, STREAMS
}

object Config {
    private val config by lazy { ConfigFactory.load() }

    val bootstrapServers: String by lazy { config.getString("kafka.bootstrap-servers") }
    val clusterApiKey: String by lazy { config.getString("kafka.cluster-api-key") }
    val clusterApiSecret: String by lazy { config.getString("kafka.cluster-api-secret") }

    val schemaRegistryUrl: String by lazy { config.getString("kafka.schema-registry-url") }
    val schemaRegistryApiKey: String by lazy { config.getString("kafka.schema-registry-api-key") }
    val schemaRegistryApiSecret: String by lazy { config.getString("kafka.schema-registry-api-secret") }

    val applicationId: String by lazy { config.getString("kafka.application-id") }
    val inputTopic: String by lazy { config.getString("kafka.input-topic") }
    val replicationFactor: Int by lazy { config.getInt("kafka.replication-factor") }
}

fun Config.toProperties(type: ConfigType) = Properties().apply {
    this[CommonClientConfigs.REQUEST_TIMEOUT_MS_CONFIG] = 20000
    this[CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG] = 500

    this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    if (clusterApiKey.isNotBlank() && clusterApiSecret.isNotBlank()) {
        this[SaslConfigs.SASL_JAAS_CONFIG] =
            """org.apache.kafka.common.security.plain.PlainLoginModule required username="$clusterApiKey" password="$clusterApiSecret";"""
        this[SaslConfigs.SASL_MECHANISM] = "PLAIN"
        this[CommonClientConfigs.SECURITY_PROTOCOL_CONFIG] = "SASL_SSL"
        this["ssl.endpoint.identification.algorithm"] = "https"
    }

    this[KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
    if (schemaRegistryApiKey.isNotBlank() && schemaRegistryApiSecret.isNotBlank()) {
        this["basic.auth.credentials.source"] = "USER_INFO"
        this["schema.registry.basic.auth.user.info"] = "$schemaRegistryApiKey:$schemaRegistryApiSecret"
    }

    when (type) {
        ConfigType.PRODUCER -> {
            this[CommonClientConfigs.CLIENT_ID_CONFIG] = applicationId
            this[ProducerConfig.ACKS_CONFIG] = "all"
            this[ProducerConfig.RETRIES_CONFIG] = "1"
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaProtobufSerializer::class.java
        }

        ConfigType.STREAMS -> {
            this[StreamsConfig.APPLICATION_ID_CONFIG] = applicationId
            this[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE
            this[StreamsConfig.REPLICATION_FACTOR_CONFIG] = replicationFactor
            this[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
            this[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = KafkaProtobufSerde::class.java.name
        }
    }

    // https://docs.confluent.io/current/schema-registry/serdes-develop/serdes-protobuf.html#multiple-event-types-in-the-same-topic
    this[AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY] = TopicNameStrategy::class.java.name
}
