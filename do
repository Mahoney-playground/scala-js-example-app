#!/usr/bin/env bash

set -ueo pipefail
IFS=$'\n\t'

action=${1:-}; shift

if [ -f .env ]; then
  . .env
fi

now=$(date +%Y-%m-%dT%H:%M)

function doBuild {
    local outputFile="/tmp/scala-js-$now.txt"
    set +e
    time "$@" | tee $outputFile
    result=$?
    set -e
    if [ $result -eq 0 ]
    then echo "********* BUILD SUCCESSFUL *********"
    else echo "!!!!!!!!! BUILD FAILED !!!!!!!!!"
    fi
    echo "Output can be found at $outputFile"
    exit $result
}

case $action in
     "develop")
        sbt '~ ;appJVM/reStart;fastOptJS' shell
        ;;

    "build")
        doBuild sbt fullOptJS
        ;;

    *)
        echo "Sorry, no idea what you mean by '$action'"
        echo "Usage:"
        echo "$0 build | compile | run"
        exit 1
    ;;
esac
