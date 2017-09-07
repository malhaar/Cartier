#!/bin/bash

# example command
# sh buildCSR.sh "/Users/ive/git-pyw-repo/cartier/cartier-provision-tools/src/main/resources/assembly/bash" "private.key" "csr.certSigningRequest" "xuhw@yyft.com" "ElveXu" "false"

# 获取文件输出跟路径
BASE_DIR=`echo "$1" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`
BASE_DIR_PATH_END=`echo ${BASE_DIR: -1}`

if [ "${BASE_DIR_PATH_END}" != "/" ];
then
    BASE_DIR=${BASE_DIR}"/"
fi

# 私钥文件名称
PRIVATE_KEY_NAME=`echo "$2" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

# CSR文件名称
CSR_FILE_NAME=`echo "$3" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

# CSR PARAMS : EMAIL
CSR_P_EMAIL=`echo "$4" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`


# CSR PARAMS : USER NAME
CSR_P_USER_NAME=`echo "$5" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

if [ ! -n "${BASE_DIR}" -o ! -n "${PRIVATE_KEY_NAME}" -o ! -n "${CSR_FILE_NAME}" -o ! -n "${CSR_P_EMAIL}" -o ! -n ${CSR_P_USER_NAME} ]
then
    echo "WARN: Please enter a correct parameters."
    echo "Usage :\r\n\tsh buildCSR.sh \"/tmp\" \"private.key\" \"csr.certSigningRequest\" \"xuhw@yyft.com\" \"ElveXu\" \"false\""
    echo "END!"
    exit 1
fi

# Force
FORCE_FLAG=`echo "$6" | grep -o "[^ ]\+\( \+[^ ]\+\)*"`

if [ ! -n "${FORCE_FLAG}" ];
then
    FORCE_FLAG="false"
elif [ "${FORCE_FLAG}" != true -a "${FORCE_FLAG}" != false  ]
then
    FORCE_FLAG="false"
fi

# 判断文件夹是否存在
if [ ! -d "${BASE_DIR}" ];
then
    mkdir -p "${BASE_DIR}"
    echo "INFO: base directory is not exist ,auto create dir : ${BASE_DIR}"
fi

if [ ${FORCE_FLAG} ];
then
    rm ${BASE_DIR}${PRIVATE_KEY_NAME}
    rm ${BASE_DIR}${CSR_FILE_NAME}
    echo "INFO: Remove already exist files."
fi

# 执行openssl genrsa
openssl genrsa -out ${BASE_DIR}${PRIVATE_KEY_NAME} 2048 >/dev/null 2>&1

# 判断私钥文件存在不存在
# if [ ! -f "${BASE_DIR}${PRIVATE_KEY_NAME}" ]
if [ "$?" -ne 0 ];
then
    echo "ERROR: Create PrivateKey file fail."
    exit
else
    echo "INFO: Private Key already created."
    echo "INFO: Private Key full Path :[${BASE_DIR}${PRIVATE_KEY_NAME}]"
fi

# 创建CSR文件
openssl req -new -key ${BASE_DIR}${PRIVATE_KEY_NAME} -out ${BASE_DIR}${CSR_FILE_NAME} -subj "/emailAddress=${CSR_P_EMAIL}, CN=${CSR_P_USER_NAME}, C=ZH" >/dev/null 2>&1

# if [ ! -f "${BASE_DIR}${CSR_FILE_NAME}" ]
if [ "$?" -ne 0 ];
then
    echo "ERROR: Create CSR file fail."
    exit
else
    echo "INFO: CSR already created."
    echo "INFO: CSR's full Path :[${BASE_DIR}${CSR_FILE_NAME}]"
fi



echo "Done!"
