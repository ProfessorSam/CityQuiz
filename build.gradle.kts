plugins {
    id("java")
}

group = "com.github.professorSam"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:5.6.1")
    implementation("gg.jte:jte:3.1.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("io.javalin.javalin-rendering:5.6.1")
}