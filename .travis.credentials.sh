#!/bin/bash
mkdir ~/.bintray/
cat <<EOF >~/.bintray/.credentials
    realm = Bintray API Realm
    host = api.bintray.com
    user = $BINTRAY_USER
    password = $BINTRAY_API_KEY
EOF