// Требования задачи: имя проекта app (задаётся в settings.gradle.kts),
// группа hexlet.code и версия 1.0-SNAPSHOT должны быть явно объявлены здесь.
group = "hexlet.code"
version = "1.0-SNAPSHOT"

plugins {
    application
    checkstyle
    jacoco
    id("org.sonarqube") version "7.3.1.8318"
    id("com.gradleup.shadow") version "9.5.0"
}

// Явно закрепляем версию Java 21 для исходников и байткода, чтобы сборка
// давала результат, совместимый с JDK 21, независимо от того, каким JDK
// запускается Gradle (на локальной машине по умолчанию может стоять новее).
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.guava:guava:33.6.0-jre")
    implementation("io.javalin:javalin:7.2.2")
    implementation("org.slf4j:slf4j-api:2.0.18")
}

application {
    mainClass.set("hexlet.code.App")
}

tasks {
    jacocoTestReport {
        reports {
            xml.required = false
            csv.required = false
            html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        }
    }
    
    shadowJar {
        manifest {
            attributes["Main-Class"] = "hexlet.code.App"
        }
    }
}

jacoco {
    applyTo(tasks.run.get())
}

tasks.register<JacocoReport>("applicationCodeCoverageReport") {
    executionData(tasks.run.get())
    sourceSets(sourceSets.main.get())
}

sonar {
    properties {
        property("sonar.projectKey", "stronty_java-project-72")
        property("sonar.organization", "stronty")
    }
}
