#!/usr/bin/env bash -e
# See: https://docs.aws.amazon.com/codeartifact/latest/ug/maven-mvn.html
# export CODEARTIFACT_AUTH_TOKEN=`aws codeartifact get-authorization-token --domain pn-codeartifact-domain --domain-owner 911845998067 --query authorizationToken --output text --profile cicd`
# export GITHUB_TOKEN=<mettere il proprio token>
# File settings.xml

if [ -z $CODEARTIFACT_AUTH_TOKEN ]; then
  echo "Run this command:"
  echo "export CODEARTIFACT_AUTH_TOKEN=\`aws codeartifact get-authorization-token --domain pn-codeartifact-domain --domain-owner 911845998067 --query authorizationToken --output text --profile cicd\`"
  exit 1
fi

if [ -z $GITHUB_TOKEN ]; then
  echo "Run this command:"
  echo "export GITHUB_TOKEN=<token with package read access>"
  exit 1
fi

ARTIFACTS=(
  it.pagopa.tech.lollipop-consumer-java-sdk:core:1.0.0-RC2
  it.pagopa.tech.lollipop-consumer-java-sdk:http-verifier:1.0.0-RC2
  it.pagopa.tech.lollipop-consumer-java-sdk:assertion-rest-client-native:1.0.0-RC2
  it.pagopa.tech.lollipop-consumer-java-sdk:identity-service-rest-client-native:1.0.0-RC2
#  it.pagopa.tech:http-signatures:1.1.4
)

REPOS=(
  ["lollipop-consumer-java-sdk"]="https://maven.pkg.github.com/pagopa/eng-http-signatures"
  ["tech"]="https://maven.pkg.github.com/pagopa/eng-http-signatures"
)

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

for ARTIFACT in ${ARTIFACTS[@]}; do
  GRP=$(echo $ARTIFACT| cut -d ':' -f 1)
  ART=$(echo $ARTIFACT| cut -d ':' -f 2)
  VER=$(echo $ARTIFACT| cut -d ':' -f 3)
  REPO=${REPOS[${GRP##*.}]}
  mvn -s $SCRIPT_DIR/settings.xml -P github org.apache.maven.plugins:maven-dependency-plugin:2.8:get \
      -DrepoUrl=$REPO \
      -Dartifact=$ARTIFACT

  cp "$HOME/.m2/repository/${GRP//.//}/${ART}/${VER}/${ART}-${VER}.jar" /tmp
  cp $HOME/.m2/repository/${GRP//.//}/${ART}/${VER}/${ART}-${VER}.pom /tmp
  mvn -s $SCRIPT_DIR/settings.xml deploy:deploy-file \
   -DgroupId=$GRP \
   -DartifactId=$ART \
   -Dversion=$VER \
   -Dfile=/tmp/${ART}-${VER}.jar \
   -DpomFile=/tmp/${ART}-${VER}.pom \
   -Dpackaging=jar \
   -DrepositoryId=pn-codeartifact-domain-pn-codeartifact-repo \
   -Durl=https://pn-codeartifact-domain-911845998067.d.codeartifact.eu-central-1.amazonaws.com/maven/pn-codeartifact-repo/
done


