group = "hexlet.code"
version = "1.0-SNAPSHOT"

plugins {
    application
    checkstyle
    jacoco
    id("org.sonarqube") version "7.3.1.8318"
    id("com.gradleup.shadow") version "9.5.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.javalin:javalin-testtools:7.2.2")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("io.javalin:javalin:7.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.18")
    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("com.h2database:h2:2.4.240")
    implementation("org.postgresql:postgresql:42.7.13")
    implementation("gg.jte:jte:3.2.4")
    implementation("io.javalin:javalin-rendering-jte:7.2.2")
    implementation("org.jsoup:jsoup:1.19.1")
    implementation("com.konghq:unirest-java:3.14.5")
}

application {
    mainClass.set("hexlet.code.App")
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    jacocoTestReport {
        reports {
            xml.required = true
            csv.required = false
            html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        }
    }

    shadowJar {
        mergeServiceFiles {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
        manifest {
            attributes["Main-Class"] = "hexlet.code.App"
        }
    }
}

tasks.named("sonar") {
    dependsOn(tasks.named("jacocoTestReport"))
}

sonar {
    properties {
        property("sonar.projectKey", "stronty_java-project-72")
        property("sonar.organization", "stronty")
    }
}
