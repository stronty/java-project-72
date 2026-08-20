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
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.javalin:javalin-testtools:7.2.2")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    implementation("io.javalin:javalin:7.2.2")
    // Реализация логирования для slf4j — без неё javalin пишет предупреждение
    // "No SLF4J providers were found" и логи не выводятся вообще.
    implementation("org.slf4j:slf4j-simple:2.0.18")
    // Пул соединений с базой данных.
    implementation("com.zaxxer:HikariCP:7.1.0")
    // Драйвер H2 — используется при локальной разработке и в тестах.
    implementation("com.h2database:h2:2.4.240")
    // Драйвер PostgreSQL — используется в продакшене на render.com.
    implementation("org.postgresql:postgresql:42.7.13")
    // Шаблонизатор Jte и его интеграция с Javalin.
    implementation("gg.jte:jte:3.2.4")
    implementation("io.javalin:javalin-rendering-jte:7.2.2")
    // Парсер HTML для извлечения h1, title и description при проверке url.
    implementation("org.jsoup:jsoup:1.19.1")
    // HTTP-клиент для выполнения запросов к проверяемым сайтам.
    implementation("com.konghq:unirest-java:3.14.5")
}

application {
    mainClass.set("hexlet.code.App")
}

tasks {
    test {
        useJUnitPlatform()
        // Запуск тестов также формирует отчёт о покрытии кода.
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
        // Сливаем META-INF/services из всех зависимостей, чтобы в fat-jar
        // регистрировались оба JDBC-драйвера (H2 и PostgreSQL).
        mergeServiceFiles {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
        manifest {
            attributes["Main-Class"] = "hexlet.code.App"
        }
    }
}

// SonarCloud анализирует покрытие по XML-отчёту jacoco, поэтому sonar
// должен выполняться после генерации отчёта.
tasks.named("sonar") {
    dependsOn(tasks.named("jacocoTestReport"))
}

sonar {
    properties {
        property("sonar.projectKey", "stronty_java-project-72")
        property("sonar.organization", "stronty")
    }
}
