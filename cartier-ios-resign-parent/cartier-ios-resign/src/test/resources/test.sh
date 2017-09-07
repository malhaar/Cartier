#!/bin/bash

DIR="/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent"
# find /Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent -d -mindepth 1 \( -name "*.mobileprovision" \)
# find /Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent -d \( -name "*.mobileprovision" \)

#arrayA=($(find "$DIR" -d -mindepth 1 \( -name "*.mobileprovision" \)))
#
#indices=( ${!arrayA[@]} )
#for ((i=${#indices[@]} - 1; i >= 0; i--)) ; do
#    echo "'${arrayA[indices[i]]}'"
#done


#'/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/test4dev.mobileprovision'
#'/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/CSQiYe.mobileprovision'
#'/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/test/testinner/testinner.mobileprovision'
#'/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/test/test.mobileprovision'
#'/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/qiye.mobileprovision'
#'/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/parent.mobileprovision'

while IFS= read -r -d '' app;
do
    echo "'$app'"
done < <(find "$DIR" -d -mindepth 1 \( -name "*test*" \) -print0)
