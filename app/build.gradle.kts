plugins {
	application
	checkstyle
	jacoco
	id("org.sonarqube") version "7.3.1.8318"
}

tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
	html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
   }
}
jacoco {
    applyTo(tasks.run.get())
}

tasks.register<JacocoReport>("applicationCodeCoverageReport") {
    executionData(tasks.run.get())
    sourceSets(sourceSets.main.get())
}
repositories {
    mavenCentral()
}

dependencies { 
testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation("com.google.guava:guava:33.6.0-jre")   
}


application { 
    // Define the main class for the application.
    mainClass = "hexlet.code.App"
}

sonar {
  properties {
    property("sonar.projectKey", "stronty_java-project-72")
    property("sonar.organization", "stronty")
  }
}

