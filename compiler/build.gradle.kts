plugins {
    alias(libs.plugins.kotlin)
    antlr
}

dependencies {
    repositories {
        implementation("org.antlr:antlr4:4.13.1")
        implementation("org.ow2.asm:asm:7.3.1")
        implementation(project(":lexer"))
    }
}

configurations {
    antlr {
        version = "4.13.1"
    }
}