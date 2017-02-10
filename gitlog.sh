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

echo "repo,who,yyyy-mm-dd,day,date,descr" > /tmp/$$

for a in `cat gitlog.txt`
do  
	repo=`echo $a | cut -d: -f1`
	repodir=`echo $a | cut -d: -f2`

	pushd $repodir >/dev/null

	git log --since="$from" --until="$to" --pretty=format:'%cn,%ai,%aD,"%s"' | sed "s/^/${repo},/" >>/tmp/$$

	popd >/dev/null
	echo "" >>/tmp/$$
done



cat /tmp/$$ | sort -k3 | grep -v "^$"

#for a in `cat gitlog.txt`
#do  
	#repo=`echo $a | cut -d: -f1`
	#rm /tmp/$$.$repo
#done
#
rm /tmp/$$



