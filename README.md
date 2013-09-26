Estatio: an open source estate management system.
=================================================

tests [![Tests](https://estatio.ci.cloudbees.com/job/github=estatio=estatio-01-tests/badge/icon)](https://estatio.ci.cloudbees.com/job/github=estatio=estatio-01-tests/) -*- analysis [![Code Analysis](https://estatio.ci.cloudbees.com/job/github=estatio=estatio-02-analysis/badge/icon)](https://estatio.ci.cloudbees.com/job/github=estatio=estatio-02-analysis/) -*- packaging [![Packaging](https://estatio.ci.cloudbees.com/job/github=estatio=estatio-03-packaging/badge/icon)](https://estatio.ci.cloudbees.com/job/github=estatio=estatio-03-package/)

Estatio is modern and flexible property management software. It offers real estate professionals and service providers the power and flexibility to manage their business in a superior, flexible and cost-effective manner.

## Screenshots ##

The following screenshots (taken 15 jul 2013) correspond to the business logic in Estatio's [domain object model](
https://github.com/estatio/estatio/tree/master/dom/src/main/java/org/estatio/dom).

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/1.png" width=600"/>

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/2.png" width=600"/>

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/3.png" width=600"/>

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/4.png" width=600"/>

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/5.png" width=600"/>

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/6.png" width=600"/>

<img src="https://raw.github.com/estatio/estatio/master/docs/20130715/7.png" width=600"/>


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

## Configure Estatio (JDBC URL) ##

Before Estatio can be run, you must configure its JDBC URL; typically this lives in the `webapp/src/main/webapp/WEB-INF/persistor.properties` properties file.

You can do this most easily by copying a set of property entries from `webapp/src/main/webapp/WEB-INF/persistor-SAMPLE.properties`.

For example, to run against an in-memory HSQLDB, the `persistor.properties` file should consist of:

    isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName=org.hsqldb.jdbcDriver
    isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL=jdbc:hsqldb:mem:test
    isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionUserName=sa
    isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionPassword=
 
The JDBC driver for HSQLDB is on the classpath.  If you want to connect to some other database, be sure to update the `pom.xml` to add the driver as a `<dependency>`.

## Run Estatio ##

You can run Estatio either using the standalone (self-hosting) version of the WAR, or using `mvn jetty plugin`.

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

    Administration > Run script: GenerateTopModelInvoice

* Take a look around :-)

This is still alpha software, but if you encounter any bugs, do [let us know](https://github.com/estatio/estatio/blob/master/pom.xml#L52).

## Implementation

This product uses [Apache Isis](http://isis.apache.org), a software framework 
developed at [The Apache Software Foundation](http://www.apache.org/).

## Support

You are free to adapt or extend Estatio to your needs.  If you would like assistance in doing so, go to [www.estatio.org](http://www.estatio.org).

You can find plenty of help on using Apache Isis at the [Isis mailing lists](http://isis.apache.org/support.html).  There is also extensive [online documentation](http://isis.apache.org/documentation.html).

## Legal Stuff ##

Copyright 2012-2013 [Eurocommercial Properties NV](http://www.eurocommercialproperties.com) 

Licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

