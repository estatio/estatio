#!/bin/bash
VERSION=$1

if [ ! "$VERSION" ]; then
    echo "usage: $(basename $0) [version]"
    exit 1
fi

# set version
echo "setting version"
mvn versions:set -DnewVersion=$VERSION -DgenerateBackupPoms=false  > /dev/null

echo "Committing changes"
git commit -am "bumping to $VERSION"

# tag if not a snapshot
echo $VERSION | grep -v SNAPSHOT > /dev/null
if [ $? = 0 ]; then
    echo "tagging (not a snapshot version)"
    git tag $VERSION
fi
