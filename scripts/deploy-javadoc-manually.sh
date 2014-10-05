# Generated via docs-deploy:
# https://github.com/CodeBlock/docs-deploy
git_project="CodeBlock/pureio-java"
git_url="git@github.com:CodeBlock/pureio-java.git"
cwd="$( cd "${BASH_SOURCE[0]%/*}" && pwd )"
cd "$cwd/.."
function generate {
    PATH=/usr/lib/jvm/java-1.8.0-openjdk/bin/:$PATH
    sbt -java-home /usr/lib/jvm/java-1.8.0-openjdk/ doc
}

function deploy {
  rand_dir=`mktemp -d`
  git clone "$git_url" "$rand_dir/repo"
  pushd "$rand_dir/repo"
  git checkout gh-pages
  git rm -rf docs
  popd
  cp -rv ./pureio-core/target/scala-*/api "$rand_dir/repo/docs"
  pushd "$rand_dir/repo"
  git add -A
  git commit -m '[scripted] documentation deployment'
  git push origin gh-pages
  popd
  rm -rf "$rand_dir"
}
generate
deploy
