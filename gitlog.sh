today=$(date +"%d %b %Y")
to_default=$(date -d "$today" +"01 %b %Y")
from_default=$(date -d "$to_default -1 month" +"%d %b %Y")


read -p "from? ($from_default): " from
read -p "to  ? ($to_default): " to

if [ -z $from ]; then from=$from_default; fi
if [ -z $to ]; then to=$to_default; fi

echo "" >&2
echo "from: $from" >&2
echo "to  : $to" >&2
echo "" >&2

rm /tmp/$$ 2>/dev/null >/dev/null

for a in `cat gitlog.repos`
do  
	repo=`echo $a | cut -d: -f1`
	repodir=`echo $a | cut -d: -f2`

	pushd $repodir >/dev/null

	git log --since="$from" --until="$to" --pretty=format:'%cn,%ai,%aD,%s' >/tmp/$$.$repo.1
	cat /tmp/$$.$repo.1 | sed "s/^/${repo},/" > /tmp/$$.$repo.2

	cat /tmp/$$.$repo.2 >> /tmp/$$

	popd >/dev/null
done



cat /tmp/$$ | sort -k3

for a in `cat gitlog.repos`
do  
	repo=`echo $a | cut -d: -f1`
	rm /tmp/$$.$repo.1
	rm /tmp/$$.$repo.2
done

rm /tmp/$$



