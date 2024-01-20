plugins {
    id("java")
    // https://docs.gradle.org/current/userguide/application_plugin.html
    // I think this is doing nothing
    application
    // https://imperceptiblethoughts.com/shadow/getting-started/#default-java-groovy-tasks
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "works.quiet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://picocli.info/
    // https://picocli.info/quick-guide.html
    implementation("info.picocli:picocli:4.7.5")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "works.quiet.AuctionHouse"
}

// https://imperceptiblethoughts.com/shadow/introduction/
// https://docs.gradle.org/current/userguide/working_with_files.html#sec:creating_uber_jar_example
tasks.register<Jar>("uberJar") {
    archiveClassifier = "uber"
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter{ it.name.endsWith("jar")}.map { zipTree(it) }
    })
}