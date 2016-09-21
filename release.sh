# fail fast
#set -e


NO_SANITY_CHECK=0

while getopts "xj:r:s:" OPT; do
    case "$OPT" in
        x) NO_SANITY_CHECK=1
            ;;
        j) JIRA=$OPTARG
            ;;
        r) RELEASE_VERSION=$OPTARG
            ;;
        s) SNAPSHOT_VERSION=$OPTARG
            ;;
    esac
done
shift $((OPTIND-1))

if [ ! "$JIRA" -o ! "$RELEASE_VERSION" -o ! "$SNAPSHOT_VERSION" ]; then
    echo "usage: $(basename $0) [-x] -j [jira] -r [release_version] -s [snapshot_version]" >&2
    echo "   eg: $(basename $0) -j EST-1234 -r 1.6.0 -s 1.7.0-SNAPSHOT" >&2
    exit 1
fi

echo "\$JIRA             = $JIRA"
echo "\$RELEASE_VERSION  = $RELEASE_VERSION"
echo "\$SNAPSHOT_VERSION = $SNAPSHOT_VERSION"
echo "\$NO_SANITY_CHECK  = $NO_SANITY_CHECK"



echo ""
echo "sanity check (mvn clean install -T1C -o)"
echo ""
if [ $NO_SANITY_CHECK == 0 ]; then
    mvn clean install -T1C -o  || exit 1 >/dev/null
else
    echo "... skipped"
fi



echo ""
echo "bumping version to $RELEASE_VERSION"
echo ""
mvn versions:set -DnewVersion=$RELEASE_VERSION || exit 1  > /dev/null

echo "Committing changes"
git commit -am "$JIRA: bumping to release $RELEASE_VERSION" || exit 1 

echo "Tagging"
git tag $RELEASE_VERSION || exit 1 





echo ""
echo "double-check (mvn clean install -T1C -o)"
echo ""
if [ $NO_SANITY_CHECK == 0 ]; then
    mvn clean install -T1C -o || exit 1  >/dev/null
else
    echo "... skipped"
fi






echo ""
echo "bumping version to $SNAPSHOT_VERSION"
echo ""
mvn versions:set -DnewVersion=$SNAPSHOT_VERSION || exit 1  > /dev/null

echo "Committing changes"
git commit -am "$JIRA: bumping to next snapshot $SNAPSHOT_VERSION" || exit 1 

echo "Tagging"
git tag $SNAPSHOT_VERSION || exit 1 

