import com.github.gradle.node.yarn.task.YarnTask
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("jacoco")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.github.node-gradle.node")
}

// change me to your group name //
group = "org.snowjak"
version = "0.0.1" //if (version != "unspecified") version else "0.0.0"
description = "A Software-Development CV"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.clojars.org")
        name = "Clojars"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        modularity.inferModulePath.set(true)
    }
}

node {
    download.set(true)
    version.set("16.14.0")
    yarnVersion.set("1.22.17")
}

tasks {

    test {
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        }
    }

    val install = create<YarnTask>("install-dependencies") {
        workingDir.set(file("${project.projectDir}/src/main/frontend"))
        args.set(listOf("install"))
    }

    val build = create<YarnTask>("build-frontend") {
        dependsOn(install)
        workingDir.set(file("${project.projectDir}/src/main/frontend"))
        args.set(listOf("build"))
    }

    val cleanup = create<Delete>("cleanup-frontend") {
        delete("${project.projectDir}/src/main/frontend/build")
    }

    val copy = create<Copy>("copy-frontend") {
        dependsOn(build)
        from("${project.projectDir}/src/main/frontend/build")
        into("${rootDir}/build/resources/main/frontend/.")
    }

    compileJava {
        dependsOn(copy)
    }

    clean {
        dependsOn(cleanup)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.auth0:java-jwt:3.19.1")

    implementation("com.h2database:h2:2.1.210")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}