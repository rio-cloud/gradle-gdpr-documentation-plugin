plugins {
    kotlin("jvm") version "2.2.10"
    id("cloud.rio.gdprdoc")  // no version needed (resolved from includeBuild)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    // thanks to dependencySubstitution above, we can depend on core without a version:
    compileOnly("cloud.rio.gdprdoc:core")
    testImplementation("com.diffplug.selfie:selfie-runner-junit5:2.5.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.AMAZON
    }
}

tasks {
    generateGdprDocumentation {
        additionalGdprDataFiles.setFrom(
            fileTree("src/main/resources") { include("**/gdpr-documentation.yaml") }
        )
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    dependsOn("generateGdprDocumentation")
}