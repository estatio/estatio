#!/usr/bin/env bash
mvn datanucleus:enhance@process-classes datanucleus:enhance@process-test-classes -pl estatioapp/app -o $*
