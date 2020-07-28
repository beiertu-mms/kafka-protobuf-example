package de.beiertu.kafka.protobuf.example.streams

import de.beiertu.kafka.protobuf.example.config.Config
import de.beiertu.kafka.protobuf.example.config.ConfigType
import de.beiertu.kafka.protobuf.example.config.toProperties
import de.beiertu.protobuf.AllTypes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.slf4j.LoggerFactory
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
    companion object {
        private val log = LoggerFactory.getLogger(DefaultStreams::class.java)
    }

    private val kafkaStreams: KafkaStreams

    init {
        val streamsBuilder = StreamsBuilder()

        streamsBuilder.stream<String, AllTypes.OrderEvents>(config.inputTopic)
            .peek { key, event ->
                log.info("about to process an ${event::class.simpleName} event with key=$key")
            }
            .foreach { _, event ->
                when {
                    event.hasOrder() -> log.info("got order ${event.order}")
                    event.hasPerson() -> log.info("got person ${event.person}")
                }
                println("---")
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
