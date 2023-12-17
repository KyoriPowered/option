plugins {
  alias(libs.plugins.indra)
  alias(libs.plugins.indra.checkstyle)
  alias(libs.plugins.indra.crossdoc)
  alias(libs.plugins.indra.licenser.spotless)
  alias(libs.plugins.indra.publishing.sonatype)
  alias(libs.plugins.nexusPublish)
  alias(libs.plugins.spotless)
}

dependencies {
  compileOnlyApi(libs.jetbrainsAnnotations)
  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.api)
  testRuntimeOnly(libs.junit.engine)
  testRuntimeOnly(libs.junit.launcher)
  checkstyle(libs.stylecheck)
}

spotless {
  ratchetFrom("origin/trunk")
  java {
    endWithNewline()
    indentWithSpaces(2)
    importOrderFile(rootProject.file(".spotless/kyori.importorder"))
    trimTrailingWhitespace()
  }
}

sourceSets {
  main {
    multirelease {
      alternateVersions(9)
      moduleName("net.kyori.option")
      requireAllPackagesExported()
    }
  }
}

indraSonatype {
  useAlternateSonatypeOSSHost("s01")
}

indra {
  github("KyoriPowered", "option") {
    ci(true)
  }
  mitLicense()
  checkstyle(libs.versions.checkstyle.get())

  javaVersions {
    minimumToolchain(17)
    testWith(11, 17, 21)
  }

  signWithKeyFromPrefixedProperties("kyori")
  configurePublications {
    pom {
      url = "https://option.kyori.net"
      developers {
        developer {
          id = "kashike"
          timezone = "America/Vancouver"
        }

        developer {
          id = "zml"
          name = "zml"
          timezone = "America/Vancouver"
        }
      }
    }
  }
}

indraCrossdoc {
  baseUrl().set(providers.gradleProperty("javadocPublishRoot"))
}

tasks.jar {
  indraGit.applyVcsInformationToManifest(manifest)
}
