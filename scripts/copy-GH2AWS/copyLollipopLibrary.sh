#!/usr/bin/env bash
#
# export CODEARTIFACT_AUTH_TOKEN=`aws codeartifact get-authorization-token --domain pn-codeartifact-domain --domain-owner 911845998067 --query authorizationToken --output text --profile cicd`
#
ARTIFACTS=(
  it.pagopa.tech.lollipop-consumer-java-sdk:core:1.0.0-RC1
  it.pagopa.tech.lollipop-consumer-java-sdk:http-verifier:1.0.0-RC1
  it.pagopa.tech.lollipop-consumer-java-sdk:assertion-rest-client-native:1.0.0-RC1
  it.pagopa.tech.lollipop-consumer-java-sdk:identity-service-rest-client-native:1.0.0-RC1
  it.pagopa.tech:http-signatures:1.1.4
)

REPOS=(
  ["lollipop-consumer-java-sdk"]="https://maven.pkg.github.com/pagopa/eng-http-signatures"
  ["tech"]="https://maven.pkg.github.com/pagopa/eng-http-signatures"
)
# https://maven.pkg.github.com/pagopa/eng-http-signatures
# https://maven.pkg.github.com/pagopa/eng-lollipop-consumer-java-sdk

for ARTIFACT in ${ARTIFACTS[@]}; do
  GRP=$(echo $ARTIFACT| cut -d ':' -f 1)
  ART=$(echo $ARTIFACT| cut -d ':' -f 2)
  VER=$(echo $ARTIFACT| cut -d ':' -f 3)
  REPO=${REPOS[${GRP##*.}]}
  mvn -P github org.apache.maven.plugins:maven-dependency-plugin:2.8:get \
       -DrepoUrl=$REPO \
       -Dartifact=$ARTIFACT

  cp "$HOME/.m2/repository/${GRP//.//}/${ART}/${VER}/${ART}-${VER}.jar" /tmp
  cp $HOME/.m2/repository/${GRP//.//}/${ART}/${VER}/${ART}-${VER}.pom /tmp
  mvn deploy:deploy-file -DgroupId=$GRP \
   -DartifactId=$ART \
   -Dversion=$VER \
   -Dfile=/tmp/${ART}-${VER}.jar \
   -DpomFile=/tmp/${ART}-${VER}.pom \
   -Dpackaging=jar \
   -DrepositoryId=pn-codeartifact-domain-pn-codeartifact-repo \
   -Durl=https://pn-codeartifact-domain-911845998067.d.codeartifact.eu-central-1.amazonaws.com/maven/pn-codeartifact-repo/
done


