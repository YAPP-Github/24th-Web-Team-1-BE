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
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
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

    /** test container */
    implementation(platform("org.testcontainers:testcontainers-bom:${DependencyVersion.TEST_CONTAINER}"))
    testImplementation("org.testcontainers:mysql")
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

/** jooq codegen after copy data migration */
tasks.getByName("jooqCodegen") {
    dependsOn("copyDataMigration")
}

jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    // Specify the location of your SQL script.
                    // You may use ant-style file matching, e.g. /path/**/to/*.sql
                    //
                    // Where:
                    // - ** matches any directory subtree
                    // - * matches any number of characters in a directory / file name
                    // - ? matches a single character in a directory / file name
                    property {
                        key = "scripts"
                        value = "src/main/resources/db/migration/**/*.sql"
                    }

                    // The sort order of the scripts within a directory, where:
                    //
                    // - semantic: sorts versions, e.g. v-3.10.0 is after v-3.9.0 (default)
                    // - alphanumeric: sorts strings, e.g. v-3.10.0 is before v-3.9.0
                    // - flyway: sorts files the same way as flyway does
                    // - none: doesn't sort directory contents after fetching them from the directory
                    property {
                        key = "sort"
                        value = "flyway"
                    }

                    // The default schema for unqualified objects:
                    //
                    // - public: all unqualified objects are located in the PUBLIC (upper case) schema
                    // - none: all unqualified objects are located in the default schema (default)
                    //
                    // This configuration can be overridden with the schema mapping feature
                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }

                    // The default name case for unquoted objects:
                    //
                    // - as_is: unquoted object names are kept unquoted
                    // - upper: unquoted object names are turned into upper case (most databases)
                    // - lower: unquoted object names are turned into lower case (e.g. PostgreSQL)
                    property {
                        key = "defaultNameCase"
                        value = "as_is"
                    }
                }
            }

            generate {
                isDeprecated = false
                isRecords = true
                isImmutablePojos = true
                isFluentSetters = true
                isJavaTimeTypes = true
            }

            target {
                packageName = "jooq.jooq_dsl"
                directory = "src/generated"
                encoding = "UTF-8"
            }
        }
    }
}