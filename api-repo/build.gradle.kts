tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

plugins {
    /** jooq */
    id("org.jooq.jooq-codegen-gradle") version DependencyVersion.JOOQ
}

sourceSets {
    main {
        java {
            val mainDir = "src/main/kotlin"
            val jooqDir = "src/generated"
            srcDirs(mainDir, jooqDir)
        }
    }
}

dependencies {
    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.mysql:mysql-connector-j")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")

    /** jooq */
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:${DependencyVersion.JOOQ}")
    implementation("org.jooq:jooq-meta:${DependencyVersion.JOOQ}")
    implementation("org.jooq:jooq-codegen:${DependencyVersion.JOOQ}")
    jooqCodegen("org.jooq:jooq-meta-extensions:${DependencyVersion.JOOQ}")
}

/** copy data migration */
tasks.create("copyDataMigration") {
    doLast {
        val root = rootDir
        val flyWayResourceDir = "/db/migration/entity"
        val dataMigrationDir = "$root/data/$flyWayResourceDir"
        File(dataMigrationDir).walkTopDown().forEach {
            if (it.isFile) {
                it.copyTo(
                    File("${project.projectDir}/src/main/resources$flyWayResourceDir/${it.name}"),
                    true
                )
            }
        }
    }
}

/** copy data migration before compile kotlin */
tasks.getByName("compileKotlin") {
    dependsOn("copyDataMigration")
}
