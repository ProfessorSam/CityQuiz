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
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("gg.jte:jte:3.1.1")
    implementation("mysql:mysql-connector-java:5.1.6")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("io.minio:minio:8.5.6")
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.4.1.jre11")
}

tasks.jar {
    dependsOn(tasks.precompileJte)
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
    options.encoding = "UTF-8"
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