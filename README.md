Estatio: an open source estate management system.
=================================================

Estatio is modern and flexible property management software. It offers real estate professionals and service providers the power and flexibility to manage their business in a superior, flexible and cost-effective manner.


## Screenshots ##


## Building Estatio ##

### Prereqs ###

Estatio runs on Java and is built with [Maven](http://maven.apache.org).  The source code is managed using [git](https://help.github.com/articles/set-up-git), and is held on [github](http://github.com).

If you don't already have them installed, install Java (JDK 6 or later), Maven (3.0.4 or later), and git.

### Download and install RFC2445 ###

This open source library is not available via Maven central repo, so must be installed manually.

Download from the command line using:

    curl https://google-rfc-2445.googlecode.com/files/rfc2445-4Mar2011.jar > rfc2445-4Mar2011.jar

Install into your local Maven repo using:

    mvn install:install-file \
                 -Dfile=rfc2445-4Mar2011.jar \
                 -DgroupId=com.google \
                 -DartifactId=rfc-2445 \
                 -Dversion=0.0.20110304 \
                 -Dpackaging=jar

### Download and build Apache Isis ###

Estatio currently uses the snapshot version of [Apache Isis](http://isis.apache.org), and so Isis must be built from source.

Estatio also maintains its [own copy of the Isis codebase](http://github.com/estatio/isis), and so this is what you should download.  (Normally there is very little, if any, difference between the Estatio's copy of Isis and the [official Isis codebase](http://github.com/apache/isis))

Download using git:

    mkdir -p github/estatio
    cd github/estatio
    git clone https://github.com/estatio/isis.git
    cd isis

and build using maven:

    mvn clean install

The clone is approx 83Mb, and takes approx 10 minutes to build.

### Download and build Estatio ###

Estatio itself is also built using maven.

Download using git:
 
    cd ..
    git clone https://github.com/estatio/estatio.git
   cd estatio

and build using maven:

    mvn clean install -Pjetty-console

The clone is approx 3Mb, and takes approximately 1 minute to build.

## Run Estatio ##

By default Estatio is configured to use an in-memory HSQLDB database.  (This can be changed by editing the JDBC properties in `webapp/src/main/webapp/WEB-INF/persistor.properties` properties file).

You can run Estatio either using the standalone (self-hosting) version of the WAR, or using mvn jetty plugin.

### Running as a self-hosting JAR

Run using:

    java -jar webapp/target/estatio-webapp-0.0.1-SNAPSHOT-jetty-console.war

and press the 'start button'.

Then browse to:
 
    http://localhost:8080/wicket/

### Running through Maven

Run using:

    cd webapp
    mvn jetty:run

Then browse to:

    http://localhost:8080/estatio-webapp/wicket/

## Use Estatio ##

* Login using root/root.  (Other user/passwords can be found in `webapp/sc/main/webapp/WEB-INF/shiro.ini`).

* Install some demo fixtures:

    Administration > Install Demo Fixtures

* Run a script to setup invoices:

    Administration > Run script: Generate invoices



## Implementation

This product uses [Apache Isis](http://isis.apache.org), a software framework 
developed at [The Apache Software Foundation](http://www.apache.org/).

## Support

For more information, go to [www.estatio.org](http://www.estatio.org).

## Legal Stuff ##

Copyright 2012-2013 [Eurocommercial Properties NV](http://www.eurocommercialproperties.com) 

Licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

