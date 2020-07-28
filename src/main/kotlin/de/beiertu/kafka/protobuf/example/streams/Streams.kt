package de.beiertu.kafka.protobuf.example.streams

import com.google.protobuf.DynamicMessage
import de.beiertu.kafka.protobuf.example.config.Config
import de.beiertu.kafka.protobuf.example.config.ConfigType
import de.beiertu.kafka.protobuf.example.config.toProperties
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import java.time.Duration

/** Kafka streams application */
interface Streams : AutoCloseable {
    fun setUncaughtExceptionHandler(handler: (thread: Thread, t: Throwable) -> Unit)
    val state: KafkaStreams.State
    fun start()
}

/**
 * Default implementation of the [Streams].
 */
class DefaultStreams(config: Config) : Streams {
    private val kafkaStreams: KafkaStreams

    init {
        val streamsBuilder = StreamsBuilder()

        streamsBuilder.stream<String, DynamicMessage>(config.inputTopic)
            .peek { key, event ->
                println("about to process an ${event::class.simpleName} event with key=$key")
            }
            .foreach { _, event ->
                println("Event = $event")
            }

        val topology = streamsBuilder.build()
        kafkaStreams = KafkaStreams(topology, config.toProperties(ConfigType.STREAMS))
    }

    override fun setUncaughtExceptionHandler(handler: (thread: Thread, t: Throwable) -> Unit) =
        kafkaStreams.setUncaughtExceptionHandler { t, e -> handler(t, e) }

    override val state: KafkaStreams.State
        get() = kafkaStreams.state()

    override fun start() = kafkaStreams.start()

    override fun close() {
        kafkaStreams.close(Duration.ofSeconds(5))
    }
}
