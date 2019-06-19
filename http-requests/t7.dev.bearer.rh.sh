#!/usr/bin/env bash

# testuser7
# client app: rsthrtapp key: k258KfoXvHLor0TyF310QNbria0a  secret: iGG8JW1m3i1ev22k265qvj65Ndwa
# forever token: a5ea10c768d3ad8d612bebf2405cfd

# export tok=a5ea10c768d3ad8d612bebf2405cfd
# export host=https://dev.tenants.aloedev.tacc.cloud
# export base=rh/v2

# simple list of metadata collection
# curl -X GET -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/metadata | jq # > result.json

# simple list of api db
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$base"/api | jq # > result.json


###################################################

# admin
# client app: test key: v3tiR9dgNUJOPHDyCVBN3zzQAc8a  secret: UqJ1uGuwYk1qkfYRiTk6pf_a_Xoa
# tmp token: feccf059b8072fe542b8e765c1d3f9

export tok=26bad0f3806052ad2bde638a73423cc
export host=https://dev.tenants.aloedev.tacc.cloud
export base=rh/v2

# create our apim app for testing
# curl -sku "admin:Strangerinastrangel@nd" -X POST -d "clientName=rsthrtapp" -d "description=admin app used for testing rh admin functions" "$host"/clients/v2

# add subscription for our apim app for testing
# curl -sku "admin:Strangerinastrangel@nd" -X POST  -d "apiName=RestHeart&apiVersion=v2&apiProvider=admin" "$host"/clients/v2/rsthrtapp/subscriptions

# simple list of api db
# curl -v -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api

# create a collection for large files
# curl -X PUT -H "Content-Type: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files

# add a large file to collection defaulting to MongoDB naming
# curl -X POST -H "Authorization : Bearer $tok" -F "file=@binary-data/jenkins-the-definitive-guide.pdf" "$host/$base"/api/mybucket.files

# simple list of api/mybucket.files
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files | jq

# using PUT to give large file a meaningful name
# curl -v -X PUT -H "Authorization : Bearer $tok" -F "file=@binary-data/jenkins-the-definitive-guide.pdf" "$host/$base"/api/mybucket.files/jenkins-the-definitive-guide.pdf | jq

# simple list of api/mybucket.files
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files/jenkins-the-definitive-guide.pdf | jq

# get the binary of a file /api/mybucket.files/jenkins-the-definitive-guide.pdf
# curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/mybucket.files/jenkins-the-definitive-guide.pdf/binary > tmp.pdf

# create a document in metadata collection   # s
curl -v -X POST -H "Authorization : Bearer $tok" -H "Content-Type: application/json" -d "@document1.json" "$host/$base"/api/metadata

# get the document created in metadata
curl -H "Accept: application/json" -H "Authorization : Bearer $tok" "$host/$base"/api/metadata/5d095783028ae24619924ea0 | jq



