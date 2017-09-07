#!/usr/bin/env bash
PROJECT_NAME="apple-account-tools"
echo "Begin to package:"
rm .temp.log &>/dev/null

mvn clean package &> .temp.log
if [ "$?" -ne 0 ];
then
    echo "Package fail ,press enter show log:"
    cat .temp.log
    exit -1
fi

echo "Package success!"

# parser project version
version=`awk '/<project_version>[^<]+<\/project_version>/{gsub(/<project_version>|<\/project_version>/,"",$1);print $1;exit;}' pom.xml`
echo "Version : ${version}"

RELEASE_PATH="releases/$version/$PROJECT_NAME"
if [ -d "$RELEASE_PATH" ]
then
    rm -rf ${RELEASE_PATH}
fi

mkdir -p ${RELEASE_PATH}
mkdir "$RELEASE_PATH/bin/" &>/dev/null
mkdir "$RELEASE_PATH/macOS/" &>/dev/null
mkdir "$RELEASE_PATH/shell/" &>/dev/null
mkdir "$RELEASE_PATH/apple-account-cers-output/" &>/dev/null
mkdir "$RELEASE_PATH/help/" &>/dev/null

# copy resources
cp -r target/sbin/assembly/bash/*.sh $RELEASE_PATH/shell/ &>/dev/null
cp -r src/main/resources/META-INF/sbin/*.sh $RELEASE_PATH/bin/ &>/dev/null
mv $RELEASE_PATH/bin/install.sh $RELEASE_PATH/shell/install.sh &>/dev/null
cp src/main/resources/META-INF/json/account.json $RELEASE_PATH/help/demo_account.json &>/dev/null
cp -r target/*.jar $RELEASE_PATH/macOS/ &>/dev/null

cp README.md $RELEASE_PATH/ &>/dev/null
cp LICENSE $RELEASE_PATH/ &>/dev/null
cp ../favicon.png $RELEASE_PATH/ &>/dev/null

# tar
cd "releases/$version" && tar -zcvf "$PROJECT_NAME-$version.tar.gz" ${PROJECT_NAME} &>/dev/null

cd ../../
rm .temp.log &>/dev/null
rm -rf ${RELEASE_PATH} &>/dev/null
echo "done!"