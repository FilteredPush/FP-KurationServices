# FP-KurationServices

Actor internals for biodiversity data quality control used in FP-Akka and in Kurator-FP-Validation.  

See: http://wiki.datakurator.org/wiki/FP-Akka_User_Documentation 

doi:10.5281/zenodo.3533267

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.3533267.svg)](https://doi.org/10.5281/zenodo.3533267)


# To Build

    mvn clean install -DskipTests

Note: Tests are currently failing with errors if run with maven from the command line, but green bar for all tests if run from within Eclipse Kepler. 

# Include using maven

Available in Maven Central.

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>FP-KurationServices</artifactId>
        <version>1.1.8</version>
    </dependency>

# Developer deployment: 

To deploy a snapshot to the snapshotRepository:

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, then deploy with the release profile (which adds package signing and deployment to release staging:

    mvn clean deploy -P release

After this, you will need to login to the sonatype oss repository hosting nexus instance (https://oss.sonatype.org/index.html#welcome), find the staged release in the staging repositories, and perform the release.


