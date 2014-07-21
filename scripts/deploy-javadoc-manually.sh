#!/usr/bin/env bash

cwd="$( cd "${BASH_SOURCE[0]%/*}" && pwd )"
cd "$cwd/.."

PATH=/usr/lib/jvm/java-1.8.0-openjdk.x86_64/bin/:$PATH sbt -java-home /usr/lib/jvm/java-1.8.0-openjdk.x86_64/ doc

f=`mktemp -d`
git clone git@github.com:CodeBlock/pureio-java.git "$f/pureio-java.git"
pushd "$f/pureio-java.git"
  git checkout gh-pages
  git rm -rf api
popd

mkdir "$f/pureio-java.git/api"
cp -rv ./pureio-core/target/scala-*/api "$f/pureio-java.git/api/pureio-core"
cp -rv ./pureio-examples/target/scala-*/api "$f/pureio-java.git/api/pureio-examples"

pushd "$f/pureio-java.git"
  git add -A
  git commit -m "[scripted] Manual docs deploy."
  git push origin gh-pages
popd
rm -rf "$f"

if [ $? == 0 ]; then
  echo "*** Done: https://codeblock.github.io/pureio-java"
  exit 0
else
  echo "*** ERROR!!! Fix the above and try again."
  exit 1
fi
