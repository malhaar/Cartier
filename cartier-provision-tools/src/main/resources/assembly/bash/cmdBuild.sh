#!/bin/bash
set -x

USAGE() {
cat << EOF
Usage: ${0##*/} <-i ident.p12> [-p password] <-m profile.mobileprovision> [-a com.example.app] [-n NewName] [-I Info.plist]
     -i ident.p12                  The signing identity file.
     -p password                   The password of signing identity file.
     -m profile.mobileprovision    Signing provision profile
     -a com.example.app            Override CFBundleIdentifier
     -n NewName                    Override CFBundleName
     -I Info.plist                 Override plist file target
     -h help
EOF
exit 1
}

while getopts "i:p::m:a::n::I::h" opt ; do
    case "$opt" in
        i) SIGNFILE=$OPTARG ;;
        p) SIGNPASS=$OPTARG ;;
        m) PROVFILE=$OPTARG ;;
        a) OVER_APPID=$OPTARG ;;
        n) OVER_NAME=$OPTARG ;;
        I) OVER_FILE=$OPTARG ;;
        h|*) USAGE ;;
    esac
done

if [ ! -f "$SIGNFILE" ]
then
    echo "signing identity file not exists"
    exit 1
fi
if [ ! -f "$PROVFILE" ]
then
    echo "signing provision profile not exists"
    exit 1
fi

if [ "$OVER_APPID" != "" ]
then
    if [ ! -f "$OVER_FILE" ]
    then
        echo "Use -I Info.plist to find plist"
        exit 7
    fi
    /usr/libexec/PlistBuddy -c "set :CFBundleIdentifier $OVER_APPID" "$OVER_FILE"
fi
if [ "$OVER_NAME" != "" ]
then
    if [ ! -f "$OVER_FILE" ]
    then
        echo "Use -I Info.plist to find plist"
        exit 7
    fi
    /usr/libexec/PlistBuddy -c "set :CFBundleName $OVER_NAME" "$OVER_FILE"
fi

RAND_KEYCHAIN=cmdbuild$$.keychain
security create-keychain -p $$ $RAND_KEYCHAIN || exit 2
security default-keychain -s $RAND_KEYCHAIN || exit 2
security list-keychains -s $RAND_KEYCHAIN || exit 2

trap "{
    security delete-keychain $RAND_KEYCHAIN || echo Cannot delete keychain $RAND_KEYCHAIN
}" EXIT

if [ "$OVER_NAME" != "" ]
then
    if [ -f "$OVER_FILE" ]
    then
        trap "{
            git checkout \"$OVER_FILE\"
            security delete-keychain $RAND_KEYCHAIN || echo Cannot delete keychain $RAND_KEYCHAIN
        }" EXIT
    fi
fi

if [ "$SIGNPASS" == "" ]
then
security import "$SIGNFILE" -k $RAND_KEYCHAIN -T /usr/bin/codesign || exit 3
else
security import "$SIGNFILE" -k $RAND_KEYCHAIN -P "$SIGNPASS" -T /usr/bin/codesign || exit 3
fi
security unlock-keychain -p $$ $HOME/Library/Keychains/$RAND_KEYCHAIN || exit 4
security set-keychain-settings -u $RAND_KEYCHAIN || exit 5
#security show-keychain-info $RAND_KEYCHAIN

SIGN_IDEN=$(security find-identity -p codesigning -v $RAND_KEYCHAIN|head -n1|cut -d "\"" -f 2)
if [ "$SIGN_IDEN" == "" ]
then
    exit 5
fi
#echo $SIGN_IDEN

# For macOS 10.12+, new security enforcement need following command.
security set-key-partition-list -S apple: -k $$ -D "$SIGN_IDEN" -t private

PROV_UUID=$(/usr/libexec/PlistBuddy -c "Print UUID" /dev/stdin <<< `security cms -D -i "$PROVFILE"`)
if [ "$PROV_UUID" == "" ]
then
    exit 6
fi
if [ ! -d "$HOME/Library/MobileDevice/Provisioning Profiles/" ]
then
    mkdir -p "$HOME/Library/MobileDevice/Provisioning Profiles/"
fi
cp "$PROVFILE" "$HOME/Library/MobileDevice/Provisioning Profiles/$PROV_UUID.mobileprovision"
#echo $PROV_UUID

xcodebuild $CMDBUILD_ARGS CODE_SIGN_IDENTITY="$SIGN_IDEN" PROVISIONING_PROFILE="$PROV_UUID" OTHER_CODE_SIGN_FLAGS="--keychain '$HOME/Library/Keychains/$RAND_KEYCHAIN'"

exit 0
