#!/bin/bash

# example command
# sh import.sh

# private key path
PRIVATE_KEY_PATH=`echo "$1" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

# cer path
CER_PATH=`echo "$2" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

# password
USER_PASSWORD=`echo "$3" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
KEYCHAIN_PATH=`echo "$4" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
KEYCHAIN_PASSWORD=`echo "$5" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

if [ ! -n "${PRIVATE_KEY_PATH}" -o ! -n "${CER_PATH}" -o ! -n "${KEYCHAIN_PATH}" -o ! -n "${USER_PASSWORD}" -o ! -n ${KEYCHAIN_PASSWORD} ]
then
    echo "WARN: Please enter a correct parameters.";
    echo "Usage :\r\n\tsh initAndImportKeychain.sh \"private.key\" \"xxx.cer\" \"xxxx\" \"/path/keychain.keychain\" \"xxxx\""
    echo "END!"
    exit 1
fi

# unlock keychain
security unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH} || exit 2

# import private key
security import ${PRIVATE_KEY_PATH} -A -k ${KEYCHAIN_PATH} -P ${KEYCHAIN_PASSWORD} || exit 2

# import cer file
security import ${CER_PATH} -A -k ${KEYCHAIN_PATH} -P ${KEYCHAIN_PASSWORD} || exit 2

# fix UI-prompts warning
security set-key-partition-list -S apple-tool:,apple: -s -k ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH} &>/dev/null

# find-identity
# security find-identity -v -p codesigning ${KEYCHAIN_PATH}