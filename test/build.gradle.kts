plugins {
    kotlin("jvm") version "2.2.21"
    id("cloud.rio.gdprdoc")  // no version needed (resolved from includeBuild)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    // thanks to dependencySubstitution above, we can depend on core without a version:
    compileOnly("cloud.rio.gdprdoc:core")
    testImplementation("com.diffplug.selfie:selfie-runner-junit5:2.5.5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.3")
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
