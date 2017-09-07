#!/usr/bin/env bash
#
#   ver: 1.0.1
#   Info: macOS's certs command
#
IFS_OLD=$IFS
IFS=$'\n'

TARGET_FILE=`echo "$1" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

if [ -e ${TARGET_FILE} ]; then
    echo "Usage :\r\n  Example: certs.sh /result/path/file.yaml"
    exit -1
fi

if [ -f "$TARGET_FILE" ]; then
    rm ${TARGET_FILE}
fi

# final all keychain list
declare arrayKeychains=($(security list-keychains -d user | sed 's/\"//g' | grep -o "[^ ]\+\( \+[^ ]\+\)*"))
NUM=${#arrayKeychains[@]}
if [[ ${NUM} -gt 0 ]];then
    echo "keyChains:" >> ${TARGET_FILE}
    for var in ${arrayKeychains[@]};
    do
        # find all codesigning items
        arrayKeychainItems=($(security find-identity -p codesigning -v $var | grep -o '\".*\"' | sed 's/\"//g'))
        NUM2=${#arrayKeychainItems[@]}
        echo " - keys: '$var'" >> ${TARGET_FILE}
        if [[ ${NUM2} -gt 0 ]];then
            echo "   items:" >> ${TARGET_FILE}
            for item in ${arrayKeychainItems[@]};
            do
                echo "    - '$item'" >> ${TARGET_FILE}
            done
        fi
    done
fi
IFS=${IFS_OLD}

