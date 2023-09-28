plugins {
    id("java")
}

dependencies {
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}