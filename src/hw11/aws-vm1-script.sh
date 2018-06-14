#!/bin/bash

readarray keys < mKeys.txt
access_key="${keys[0]}"
secret_access_key="${keys[1]}"

# configure aws
aws configure set aws_access_key_id $access_key
aws configure set aws_secret_access_key $secret_access_key
aws configure set default.region eu-west-2

fallocate -l 1K F1.dat
fallocate -l 10K F2.dat
fallocate -l 100k F3.dat
fallocate -l 1M F4.dat
fallocate -l 100M F5.dat
ls
echo "--------------------------"
/usr/bin/time -f "%e" aws s3 cp F1.dat s3://doetlingerlukas-test-bucket
echo "$(command) seconds for F1 to S3" >> time.txt
/usr/bin/time -f "%e" aws s3 cp F2.dat s3://doetlingerlukas-test-bucket
echo "seconds for F2 to S3"
/usr/bin/time -f "%e" aws s3 cp F3.dat s3://doetlingerlukas-test-bucket
echo "seconds for F3 to S3"
/usr/bin/time -f "%e" aws s3 cp F4.dat s3://doetlingerlukas-test-bucket
echo "seconds for F4 to S3"
/usr/bin/time -f "%e" aws s3 cp F5.dat s3://doetlingerlukas-test-bucket
echo "seconds for F5 to S3"
aws s3 ls s3://doetlingerlukas-test-bucket
echo "Done!"