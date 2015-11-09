#!/bin/bash

URL='http://localhost:8080/internal/count'

for i in `seq 1 60`;
do
    curl $URL
    echo ''
    sleep 1
done
