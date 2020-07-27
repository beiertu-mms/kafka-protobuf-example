import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import java.net.URI

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.google.protobuf") version "0.8.12"

    application
}

repositories {
    jcenter()
    maven {
        url = URI("http://packages.confluent.io/maven/")
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                file("$buildDir/generated/source/proto/main/java")
            )
        }
    }
}

val protoVersion = "3.12.2"
val cpVersion = "5.5.1" // confluent platform

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.protobuf:protobuf-java:$protoVersion")
    implementation("com.typesafe:config:1.4.0")
    implementation("org.apache.kafka:kafka-clients:$cpVersion-ccs")
    implementation("io.confluent:kafka-protobuf-serializer:$cpVersion")
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()

        eachDependency {
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion("1.3.72")
            }
        }

        force("com.google.protobuf:protobuf-java:$protoVersion")
        force("org.slf4j:slf4j-api:1.7.30")
        force("com.google.guava:guava:20.0")
        force("com.google.errorprone:error_prone_annotations:2.3.4")
    }
}

application {
    mainClassName = "de.beiertu.kafka.protobuf.example.App"
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protoVersion"
    }
}

