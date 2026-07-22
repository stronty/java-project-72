plugins {
	application
	checkstyle
}



repositories {
    mavenCentral()
}

dependencies {  (2)
testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  implementation("com.google.guava:guava:33.6.0-jre")   
}


application {   (3)
    // Define the main class for the application.
    mainClass = "hexlet.code.App"
}

