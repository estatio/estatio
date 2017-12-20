#!/usr/bin/env bash
mvn datanucleus:enhance -pl estatioapp/app -DmetadataDirectory="target/classes" -o
mvn datanucleus:enhance -pl estatioapp/app -DmetadataDirectory="target/test-classes" -o
mvn datanucleus:enhance -pl estatioapp/dom/alias/impl -DmetadataDirectory="target/classes" -o
mvn datanucleus:enhance -pl estatioapp/dom/alias/integtests -DmetadataDirectory="target/test-classes" -o
