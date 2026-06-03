plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
    `maven-publish`
    jacoco
    id("org.sonarqube") version "5.1.0.4882"
}

group = "id.ac.ui.cs.advprog.yomu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

val grpcVersion = "1.63.0"
val protobufVersion = "3.25.3"
val jjwtVersion = "0.12.6"
val grpcSpringBootVersion = "3.1.0.RELEASE"
val javaxAnnotationVersion = "1.3.2"
val jakartaAnnotationVersion = "3.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("net.devh:grpc-server-spring-boot-starter:${grpcSpringBootVersion}")
    implementation("net.devh:grpc-client-spring-boot-starter:${grpcSpringBootVersion}")
    implementation("javax.annotation:javax.annotation-api:${javaxAnnotationVersion}")

    compileOnly("org.projectlombok:lombok")
    compileOnly("jakarta.annotation:jakarta.annotation-api:${jakartaAnnotationVersion}")
    annotationProcessor("org.projectlombok:lombok")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyLocking {
    lockAllConfigurations()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("id/ac/ui/cs/advprog/yomu/shared/grpc/**")
                exclude("**/event/**")
            }
        })
    )
}

sonar {
    properties {
        property("sonar.projectKey", "advprog-2026-A15-project_yomu-shared-lib")
        property("sonar.organization", "advprog-2026-a15-project")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "https://sonarcloud.io")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}
