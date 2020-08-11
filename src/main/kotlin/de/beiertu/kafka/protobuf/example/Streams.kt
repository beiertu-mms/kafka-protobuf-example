package de.beiertu.kafka.protobuf.example

import de.beiertu.protobuf.Data
import de.beiertu.protobuf.Input
import de.beiertu.protobuf.Output
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.Produced
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
    private val kafkaStreams: KafkaStreams

    companion object {
        private val log = LoggerFactory.getLogger(DefaultStreams::class.java)
    }

    init {
        val dataSerde = DataSerde().apply { configure(config) }
        val inputSerde = InputSerde().apply { configure(config) }
        val outputSerde = OutputSerde().apply { configure(config) }

        StreamsBuilder().let { streamsBuilder ->
            val dataTable = streamsBuilder.table<String, Data>(
                config.dataTopic, Consumed.with(Serdes.String(), dataSerde)
            ).filter { _, event -> event != null }

            streamsBuilder.stream<String, Input>(
                config.inputTopic, Consumed.with(Serdes.String(), inputSerde)
            ).filter { _, event ->
                (event != null)
                    .also { log.info("got event {}", event) }
            }.join(
                dataTable,
                { input, data ->
                    Output
                        .newBuilder()
                        .setId(input.id)
                        .setMessage("${input.message} ${data.message}")
                        .build()
                        .also { log.info("join input $input and data $data") }
                },
                Joined.with(Serdes.String(), InputSerde(), DataSerde())
            ).to(config.outputTopic, Produced.with(Serdes.String(), outputSerde))

            val topology = streamsBuilder.build()
            kafkaStreams = KafkaStreams(topology, config.toProperties(ConfigType.STREAMS))
        }
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
