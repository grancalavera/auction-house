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
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "works.quiet.AuctionHouse"
}