import gg.jte.ContentType
import java.nio.file.Paths

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("gg.jte.gradle") version "3.1.1"
}

group = "com.github.professorSam"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("io.javalin:javalin:5.6.1")
    implementation("io.javalin:javalin-rendering:5.6.1")
    implementation("gg.jte:jte:3.1.1")
}

tasks.jar {
    dependsOn(tasks.precompileJte)
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

application {
    mainClass = "com.github.professorSam.Main"
}

tasks.withType(JavaExec::class) {
    args = listOf("--dev")
}

tasks.shadowJar{
    dependsOn(tasks.precompileJte)
    from(jte.targetDirectory)
}

jte {
    sourceDirectory = Paths.get(project.projectDir.absolutePath, "src", "main", "jte")
    targetDirectory = Paths.get(project.projectDir.absolutePath, "build", "generated", "jte")
    contentType = ContentType.Html
    precompile()
}