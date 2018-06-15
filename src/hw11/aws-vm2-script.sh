#!/bin/bash

readarray keys < mKeys.txt
access_key="${keys[0]}"
secret_access_key="${keys[1]}"

# configure aws
aws configure set aws_access_key_id $access_key
aws configure set aws_secret_access_key $secret_access_key
aws configure set default.region eu-west-2

echo "This is vm2"