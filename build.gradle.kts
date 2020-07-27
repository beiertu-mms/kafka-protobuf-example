import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.google.protobuf") version "0.8.12"

    application
}

repositories {
    maven("https://plugins.gradle.org/m2/")
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
dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.protobuf:protobuf-java:$protoVersion")
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
    }
}

application {
    mainClassName = "de.beiertu.kafka.protobuf.example.AppKt"
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protoVersion"
    }
}

