#!/bin/bash
if [ ! $# -eq 5 ]
      then
              echo "Usage: upgrade <server> <username> <password> <database> <version>"
              exit 0
fi
server=$1
username=$2
password=$3
database=$4
version=$5
find $version/ -name "*.sql" -exec sqlcmd -S $server -U $username -P $password -d $version -i {} \;
