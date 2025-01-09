import org.gradle.internal.impldep.org.bouncycastle.oer.OERDefinition

plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.google.protobuf") version "0.9.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")

    // Protobuf and gRPC dependencies
    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("io.grpc:grpc-netty-shaded:1.68.1")
    implementation("io.grpc:grpc-protobuf:1.68.1")
    implementation("io.grpc:grpc-stub:1.68.1")

    // javax.annotation for @Generated
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    //Jackson for Json Value
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.0")

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.assertj:assertj-core:3.18.1")
    // https://mvnrepository.com/artifact/org.mockito/mockito-core
    testImplementation("org.mockito:mockito-core:2.1.0")
    // https://mvnrepository.com/artifact/org.mockito/mockito-junit-jupiter
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")




}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.3"

    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.68.1"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }

}
