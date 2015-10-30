#!/bin/bash
if [ ! $# -eq 5 ]
      then
              echo "Usage: upgrade <server> <username> <password> <database> <sql.script.list>"
              exit 0
fi
server=$1
username=$2
password=$3
database=$4
filelist=$5
while IFS='' read -r line || [[ -n "$line" ]]; do
    echo sqlcmd -S $server -U $username -P $password -d $database -i "$line"
done < "$5"
