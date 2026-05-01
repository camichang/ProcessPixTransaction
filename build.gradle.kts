plugins {
	java
	id("org.springframework.boot") version "3.5.14"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.camilachangperes"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.flywaydb:flyway-core")
	implementation("org.springframework:spring-jdbc")
	implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation ("io.micrometer:micrometer-tracing")
    implementation ("io.micrometer:micrometer-tracing-bridge-otel")
    implementation ("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation ("org.apache.commons:commons-lang3:3.14.0")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.mockito:mockito-core:5.11.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
    testImplementation("io.github.hakky54:logcaptor:2.9.0")
    testImplementation("io.rest-assured:rest-assured:5.5.7")
    testImplementation("io.rest-assured:json-path:5.4.0")
    testImplementation("io.rest-assured:json-schema-validator:6.0.0")
    testImplementation("org.hamcrest:hamcrest:2.2")

    runtimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
