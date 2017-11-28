#!/usr/bin/env bash
mvn datanucleus:enhance -pl estatioapp/app -DmetadataDirectory="target/classes"
mvn datanucleus:enhance -pl estatioapp/app -DmetadataDirectory="target/test-classes"
