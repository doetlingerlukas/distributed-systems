#!/bin/bash

readarray keys < mKeys.txt
access_key="${keys[0]}"
secret_access_key="${keys[1]}"

# configure aws
aws configure set aws_access_key_id $access_key
aws configure set aws_secret_access_key $secret_access_key
aws configure set default.region eu-west-2

echo "-----------------------"
echo "This is vm2!"
echo "-----------------------"
echo "Received files from vm1:"
ls *.dat
rm -f *.dat
echo "Copying files from S3 ..."
echo "$({ TIMEFORMAT=%E; time aws s3 cp s3://doetlingerlukas-test-bucket/F1.dat . &>logfile; } 2>&1) seconds for F1 to VM2 from S3" >> time2.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp s3://doetlingerlukas-test-bucket/F2.dat . &>logfile; } 2>&1) seconds for F2 to VM2 from S3" >> time2.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp s3://doetlingerlukas-test-bucket/F3.dat . &>logfile; } 2>&1) seconds for F3 to VM2 from S3" >> time2.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp s3://doetlingerlukas-test-bucket/F4.dat . &>logfile; } 2>&1) seconds for F4 to VM2 from S3" >> time2.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp s3://doetlingerlukas-test-bucket/F5.dat . &>logfile; } 2>&1) seconds for F5 to VM2 from S3" >> time2.txt
ls *.dat
echo "-----------------------"