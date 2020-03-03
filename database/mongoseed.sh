#!/usr/bin/env bash

SEED_DB=${MONGO_DB:-dev}
echo Dropping DB $SEED_DB
mongo $SEED_DB --eval "db.dropDatabase()"
for file in $(dirname $BASH_SOURCE)/seed/*.json; do
  echo Seeding $(basename "$file" ".json") from $file in DB $SEED_DB
  mongoimport --db=$SEED_DB --collection=$(basename "$file" ".json") --file=$file --jsonArray
done
