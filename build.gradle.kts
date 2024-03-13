plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("App")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
}

sourceSets.main {
    java.srcDir("src/main/java")
    resources.srcDir("src/main/resources")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes["Main-Class"] = "App"
        }
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
