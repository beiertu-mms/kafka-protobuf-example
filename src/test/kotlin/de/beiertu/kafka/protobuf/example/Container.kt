package de.beiertu.kafka.protobuf.example

//import org.testcontainers.containers.GenericContainer
//import org.testcontainers.containers.KafkaContainer
//
//val cpVersion = "5.5.1"
///** A kafka container with single node */
//class SingleNodeKafkaContainer(
//    version: String = cpVersion
//) : KafkaContainer(version) {
//    init {
//        addEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
//        addEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
//    }
//}
//
///** Schema registry container */
//class SchemaRegistryContainer(
//    version: String = cpVersion,
//    kafka: KafkaContainer
//) : GenericContainer<SchemaRegistryContainer>("confluentinc/cp-schema-registry:$version") {
//    init {
//        withNetwork(kafka.network)
//        withExposedPorts(8081)
//        withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
//        withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
//        withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://${kafka.networkAliases[0]}:9092")
//    }
//
//    val url: String get() = "http://" + containerIpAddress + ":" + getMappedPort(8081)
//}
