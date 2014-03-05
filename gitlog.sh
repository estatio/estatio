today=$(date +"%d %b %Y")
to_default=$(date -d "$today" +"01 %b %Y")
from_default=$(date -d "$to_default -1 month" +"%d %b %Y")

who_default="Jeroen"
host=$(hostname)
if [ "$host" == "halyogatp" ]; then who_default="Dan"; fi

read -p "from? ($from_default): " from
read -p "to  ? ($to_default): " to
read -p "who ? ($who_default): " who

if [ -z $from ]; then from=$from_default; fi
if [ -z $to ]; then to=$to_default; fi
if [ -z $who ]; then who=$who_default; fi

echo "" >&2
echo "from: $from" >&2
echo "to  : $to" >&2
echo "who : $who" >&2
echo "" >&2


git log --since="$from" --until="$to" --pretty=format:'%cn,%ai,%aD,%s' >/tmp/$$.est.1
awk '{ printf "EST,"; print }' /tmp/$$.est.1 > /tmp/$$.est.2



pushd /c/apache/isis-git-rw >/dev/null

git log --since="$from" --until="$to" --pretty=format:'%cn,%ai,%aD,%s' >/tmp/$$.isis.1
awk '{ printf "ISIS,"; print }' /tmp/$$.isis.1 > /tmp/$$.isis.2

popd >/dev/null

cat /tmp/$$.est.2 > /tmp/$$
cat /tmp/$$.isis.2 >> /tmp/$$


cat /tmp/$$ | egrep "^(EST|ISIS),$who" | sort -k3
rm /tmp/$$.est.1
rm /tmp/$$.est.2
rm /tmp/$$.isis.1
rm /tmp/$$.isis.2
rm /tmp/$$



