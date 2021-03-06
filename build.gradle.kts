import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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

val testContainersVersion = "1.14.3"
val kotestVersion = "4.0.6"
val junitPlatformVersion = "1.6.2"
val junitJupiterVersion = "5.6.2"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.protobuf:protobuf-java:$protoVersion")
    implementation("com.typesafe:config:1.4.0")
    implementation("org.apache.kafka:kafka-clients:$cpVersion-ccs")
    implementation("org.apache.kafka:kafka-streams:$cpVersion-ccs")
    implementation("io.confluent:kafka-protobuf-serializer:$cpVersion")
    implementation("io.confluent:kafka-streams-protobuf-serde:$cpVersion")
    implementation("ch.qos.logback:logback-classic:1.0.13")
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()

        eachDependency {
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion("1.3.72")
                "org.junit.platform" -> useVersion(junitPlatformVersion)
            }
        }

        force("com.google.protobuf:protobuf-java:$protoVersion")
        force("org.slf4j:slf4j-api:1.7.30")
        force("com.google.guava:guava:20.0")
        force("com.google.errorprone:error_prone_annotations:2.3.4")
        force("org.apache.commons:commons-compress:1.20")
        force("org.jetbrains:annotations:17.0.0")
        force("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        force("org.junit:junit-bom:$junitJupiterVersion")
    }
}

configurations.forEach {
    // Fix 'Cannot choose between the following variants' error
    // See https://github.com/google/protobuf-gradle-plugin/issues/391#issuecomment-609958243
    if (it.name.toLowerCase().contains("proto")) {
        it.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "java-runtime"))
    }
}

application {
    mainClassName = "de.beiertu.kafka.protobuf.example.App"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protoVersion"
    }
}

