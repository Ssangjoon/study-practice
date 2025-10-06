plugins {
    java
    kotlin("jvm") version "1.9.23" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("java")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
    }
}

project(":study:object") {
    dependencies {
    }
}