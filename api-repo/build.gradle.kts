tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    /** spring starter */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.mysql:mysql-connector-j")

    /** flyway */
    implementation("org.flywaydb:flyway-core:${DependencyVersion.FLYWAY}")
    implementation("org.flywaydb:flyway-mysql")
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
