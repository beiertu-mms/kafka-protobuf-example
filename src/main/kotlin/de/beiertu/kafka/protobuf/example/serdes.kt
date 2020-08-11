package de.beiertu.kafka.protobuf.example

import com.google.protobuf.Message
import de.beiertu.protobuf.Data
import de.beiertu.protobuf.Input
import de.beiertu.protobuf.Output
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde
import java.util.Properties
import kotlin.reflect.KClass

/** Base class for the protobuf's serde */
open class ProtobufSerde<T : Message>(
    specificProtobufClass: KClass<T>
) : KafkaProtobufSerde<T>(specificProtobufClass.java) {

    fun configure(config: Config) {
        configure(
            config.schemaRegistryProperties
                .mapKeys { entry -> entry.key.toString() },
            false
        )
    }
}

/** Returns schema registry related properties */
val Config.schemaRegistryProperties: Properties
    get() = Properties().apply {
        this[AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl
        if (schemaRegistryApiKey.isNotBlank() && schemaRegistryApiSecret.isNotBlank()) {
            this["basic.auth.credentials.source"] = "USER_INFO"
            this["schema.registry.basic.auth.user.info"] = "$schemaRegistryApiKey:$schemaRegistryApiSecret"
        }
    }

class InputSerde : ProtobufSerde<Input>(Input::class)
class DataSerde : ProtobufSerde<Data>(Data::class)
class OutputSerde : ProtobufSerde<Output>(Output::class)
