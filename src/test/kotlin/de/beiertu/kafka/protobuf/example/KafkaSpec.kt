package de.beiertu.kafka.protobuf.example

//import io.kotest.core.spec.Spec
//import io.kotest.core.spec.autoClose
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.matchers.collections.shouldBeIn
//import io.kotest.matchers.should
//import org.apache.kafka.clients.admin.AdminClient
//import org.apache.kafka.clients.admin.NewTopic
//import org.slf4j.LoggerFactory
//import org.testcontainers.containers.Network
//import org.testcontainers.containers.output.Slf4jLogConsumer
//
//internal class KafkaSpec : StringSpec() {
//    private val log = LoggerFactory.getLogger(KafkaSpec::class.java)
//
//    private val topic = "input-events"
//
//    private val kafka = autoClose(
//        SingleNodeKafkaContainer()
//            .withNetwork(Network.newNetwork())
//            .withLogConsumer(Slf4jLogConsumer(log))
//    )
//    private lateinit var schemaRegistry: SchemaRegistryContainer
//
//    override fun beforeSpec(spec: Spec) {
//        kafka.start()
//
//        schemaRegistry = autoClose(
//            SchemaRegistryContainer(kafka = kafka)
//                .withLogConsumer(Slf4jLogConsumer(log))
//        )
//        schemaRegistry.start()
//
//        // create input topic
//        val adminClient: AdminClient = AdminClient.create(
//            mapOf(
//                "bootstrap.servers" to kafka.bootstrapServers
//            )
//        )
//        adminClient.createTopics(
//            listOf(
//                NewTopic(topic, 1, 1)
//            )
//        ).all().get()
//
//        // ensure the topic does exist
//        val topics = adminClient.listTopics().names().get()
//        topic.shouldBeIn(topics)
//    }
//
//    init {
//        "Producer" should {
//        }
//    }
//}
