#!/usr/bin/env bash 

# base path
basepath=$(cd `dirname $0`; pwd)
# enable debug
VERBOSE=true
PSWD=`echo "$1" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

log() {
	if [[ -n "$VERBOSE" ]]; then echo -e "$@"; else test 1; fi
}

if [ ! -n "${PSWD}" ];
then
    log "Error: exec command like : sh install.sh macOS_password "
    exit -1
fi

function init() {
	PRIVATE_KEY_PATH="$basepath/$1/privateKey.key"
	if [ ! -f "$PRIVATE_KEY_PATH" ]; then
		log "Fail : $1 ,Private Key not found !"
		return
	fi
	
	CER_PATH="$basepath/$1/ios_development.cer"
	if [ ! -f "$CER_PATH" ]; then
		log "Fail : $1 ,Cer file not found !"
		return
	fi

	KEYCHAIN_PATH="$2"
	KEYCHAIN_PASSWORD="$3"
	# if [[ ! -n "$KEYCHAIN_PATH" ]] ; then
	# 	if [ ! -f "$KEYCHAIN_PATH" ]; then
	# 		echo "create new keychain"
	# 		security create-keychain -p $KEYCHAIN_PASSWORD $KEYCHAIN_PATH &>/dev/null || exit -1
	# 	fi
	# fi

	# unlock keychain
	security unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH} &>/dev/null || return

	# import private key
	security import ${PRIVATE_KEY_PATH} -A -k ${KEYCHAIN_PATH} -P ${KEYCHAIN_PASSWORD} &>/dev/null
	if [ ! $? -eq 0 ]
	then
		log "Warn : $1 , privateKey.key is exist , ignore ~"
	fi

	# import cer file
	security import ${CER_PATH} -A -k ${KEYCHAIN_PATH} -P ${KEYCHAIN_PASSWORD} &>/dev/null
	if [ ! $? -eq 0 ]
	then
		log "Warn : $1 , cert is exist , ignore ~"
		echo -e "\n"
		return
	fi

	log "Account: $1 ,imported Success !"
	echo -e "\n"
}

# accounts
accounts=$(ls -l | awk '/^d/{print $NF}')
# foreach accounts
for account in ${accounts[@]};  
do  
    init $account "$HOME/Library/Keychains/login.keychain-db" "$PSWD"
done

# fix UI-prompts warning
security set-key-partition-list -S apple-tool:,apple: -s -k ${KEYCHAIN_PASSWORD} ${KEYCHAIN_PATH} &>/dev/null || return




