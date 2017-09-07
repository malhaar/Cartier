#!/usr/bin/env bash

# 工作目录
BASE_DIR=`echo "$1" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
if [ -e ${BASE_DIR} ]; then
    BASE_DIR=$HOME
fi

# 源IPA文件
SOURCE_IPA_FILE=`echo "$2" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# 描述文件
MOBILE_PROV_FILE=`echo "$3" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# IDENTITY
IDENTITY_NAME=`echo "$4" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# 输出 IPA 路径
DEST_IPA_FILE=`echo "$5" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# 指定钥匙串
KEYCHAIN_FILE=`echo "$6" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# 钥匙串密码
KEYCHAIN_PASSWORD=`echo "$7" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# UDID
UDID=`echo "$8" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
# DEBUG
DEBUG=`echo "$9" | grep -o "[^ ]\+\( \+[^ ]\+\)*"` || false

if [ ! -n "${SOURCE_IPA_FILE}" -o ! -n "${MOBILE_PROV_FILE}" -o ! -n "${IDENTITY_NAME}" -o ! -n "${DEST_IPA_FILE}" -o ! -n "${KEYCHAIN_FILE}" -o ! -n "${KEYCHAIN_PASSWORD}" -o ! -n "${UDID}" ]; then
        
        echo "WARN: Please enter a correct parameters."
        echo "Usage :\r"
        echo "\tsh resign.sh /you_base_dir \\"
        echo "\t\t/your_source_ipa_path_file.ipa \\"
        echo "\t\t/your_mobile_provision_file.mobileprovision \\"
        echo "\t\t/iPhone Developer: Mingjun Lee (XGBQNMQ39P) \\"
        echo "\t\t/the_target_resigned_ipa_file_path.ipa \\"
        echo "\t\t/which_key_chain_to_used_with_resign.keychain \\"
        echo "\t\t/keyChain's password \\"
        echo "\t\t/The_device_udid \\"
        echo "\t\t/debug_flag[true|false] \\"
        exit
fi

# 解压路径
IPA_EXT_DIR="${BASE_DIR}${UDID}/$(date +%s)-$RANDOM"
IPA_EXT_PAYLOAD_DIR="${IPA_EXT_DIR}/Payload"

# 描述文件全内容文件存放路径
EMBEDDED_FULL_PLIST_PATH="${IPA_EXT_DIR}/entitlements_full.plist"
# 提取权限内容文件存放路径
ENTITLEMENTS_PLIST_PATH="${IPA_EXT_DIR}/entitlements.plist"


# unzip
unzip -qo ${SOURCE_IPA_FILE} -d ${IPA_EXT_DIR} || exit 1

# Payload文件夹下面的app名称
APPLICATION_NAME="$(ls -l ${IPA_EXT_PAYLOAD_DIR} | grep '.app' | awk '/^d/{print $NF}')"

# cp
cp "${MOBILE_PROV_FILE}" "${IPA_EXT_PAYLOAD_DIR}/${APPLICATION_NAME}/embedded.mobileprovision" || exit 2

# cms
sudo security cms -D -i "${IPA_EXT_PAYLOAD_DIR}/${APPLICATION_NAME}/embedded.mobileprovision" > ${EMBEDDED_FULL_PLIST_PATH} || exit 3

# plist buddy
/usr/libexec/PlistBuddy -x -c 'Print:Entitlements' ${EMBEDDED_FULL_PLIST_PATH} > ${ENTITLEMENTS_PLIST_PATH} || exit 4

# unlock
sudo security unlock-keychain -p ${KEYCHAIN_PASSWORD} ${KEYCHAIN_FILE} || exit 5

# find targets
arrayA=($(find -d "${IPA_EXT_PAYLOAD_DIR}" \( -name "*.app" -o -name "*.appex" -o -name "*.framework" -o -name "*.dylib" \) | awk '{print $1}'))
for var in ${arrayA[@]};  
do
    # codesign
    /usr/bin/codesign --continue -f -s ${IDENTITY_NAME} --entitlements ${ENTITLEMENTS_PLIST_PATH} ${var} || exit 6
done

# zip
cd ${BASE_DIR}${UDID} && zip -qry ${DEST_IPA_FILE} *

# invoke jar upload to cdn
java -jar pyw-ks3-upload-1.0.1.jar ${DEST_IPA_FILE} ${UDID}

# invoke jar update db
java -jar pyw-cartier-resign-processor-1.0.1.jar ${DEST_IPA_FILE} ${UDID}


# clean work dir
if !${DEBUG}; then
    rm -rf ${IPA_EXT_DIR}    
fi

