#!/bin/bash

readarray keys < mKeys.txt
access_key="${keys[0]}"
secret_access_key="${keys[1]}"

# settings
imageid="ami-a36f8dc4" # Amazon Linux 64-Bit
instance_type="t2.micro"
key_name="mKey"
key_location="mKey.pem"
security_group_id="sg-9486e1ff"
# end of settings

# configure aws
aws configure set aws_access_key_id $access_key
aws configure set aws_secret_access_key $secret_access_key
aws configure set default.region eu-west-2
chmod 400 $key_location

# create local files 
echo "Creating files ..."
fallocate -l 1K F1.dat
fallocate -l 10K F2.dat
fallocate -l 100k F3.dat
fallocate -l 1M F4.dat
fallocate -l 10M F5.dat
ls *.dat
echo "--------------------------"

# write files to S3 bucket and calculate time
echo "Copying files to S3 ..."
echo "$({ TIMEFORMAT=%E; time aws s3 cp F1.dat s3://doetlingerlukas-test-bucket &>logfile; } 2>&1) seconds for F1 to S3 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp F2.dat s3://doetlingerlukas-test-bucket &>logfile; } 2>&1) seconds for F2 to S3 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp F3.dat s3://doetlingerlukas-test-bucket &>logfile; } 2>&1) seconds for F3 to S3 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp F4.dat s3://doetlingerlukas-test-bucket &>logfile; } 2>&1) seconds for F4 to S3 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time aws s3 cp F5.dat s3://doetlingerlukas-test-bucket &>logfile; } 2>&1) seconds for F5 to S3 from VM1" >> time.txt
aws s3 ls s3://doetlingerlukas-test-bucket
echo " " >> time.txt
echo "Done!"

# start second instance and get ip address
echo "Starting instance 2 ..."
instance_id=$(aws ec2 run-instances --image-id $imageid --security-group-ids $security_group_id --count 1 --instance-type $instance_type --key-name $key_name --query 'Instances[0].InstanceId')
correct_id=$(echo $instance_id | sed 's/[^a-zA-Z0-9-]//g')

instance_ip=$(aws ec2 describe-instances --instance-ids $correct_id --query 'Reservations[0].Instances[0].PublicIpAddress')
correct_ip=$(echo $instance_ip | sed 's/[^0-9.]//g')

# wait until instance is running completely
echo "Waiting for instance 2 to get up and running ..."
aws ec2 wait instance-running --instance-ids $correct_id

echo "Instance 2 running, waiting for ssh port to be open ..."
while ! ssh -o StrictHostKeyChecking=no -i $key_location ec2-user@$correct_ip 'exit' &>logfile
do
    echo "Not available, trying again..."
	sleep 5
done

# copy key files to instance
echo "Copying key-files to instance 2 at $correct_ip ..."
scp -o StrictHostKeyChecking=no -i $key_location mKeys.txt ec2-user@$correct_ip:/home/ec2-user
scp -o StrictHostKeyChecking=no -i $key_location $key_location ec2-user@$correct_ip:/home/ec2-user

# copy files to vm2
echo "---------------------"
echo "Copying local files to instance 2 ..."
echo "$({ TIMEFORMAT=%E; time scp -o StrictHostKeyChecking=no -i $key_location F1.dat ec2-user@$correct_ip:/home/ec2-user &>logfile; } 2>&1) seconds for F1 to VM2 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time scp -o StrictHostKeyChecking=no -i $key_location F2.dat ec2-user@$correct_ip:/home/ec2-user &>logfile; } 2>&1) seconds for F2 to VM2 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time scp -o StrictHostKeyChecking=no -i $key_location F3.dat ec2-user@$correct_ip:/home/ec2-user &>logfile; } 2>&1) seconds for F3 to VM2 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time scp -o StrictHostKeyChecking=no -i $key_location F4.dat ec2-user@$correct_ip:/home/ec2-user &>logfile; } 2>&1) seconds for F4 to VM2 from VM1" >> time.txt
echo "$({ TIMEFORMAT=%E; time scp -o StrictHostKeyChecking=no -i $key_location F5.dat ec2-user@$correct_ip:/home/ec2-user &>logfile; } 2>&1) seconds for F5 to VM2 from VM1" >> time.txt
echo " " >> time.txt
echo "Done!"

# inside the instance 2
echo "Connecting to instance at $correct_ip ..."
ssh -o StrictHostKeyChecking=no -i $key_location ec2-user@$correct_ip 'bash -s' < aws-vm2-script.sh

# recieve time2 file and merge it into local textfile
echo "Recieving time file ..."
scp -o StrictHostKeyChecking=no -i $key_location ec2-user@$correct_ip:/home/ec2-user/time2.txt .
cat time2.txt >> time.txt

# terminate the instance and delete the bucket 
echo "Terminating instance 2 ..."
aws ec2 terminate-instances --instance-ids $correct_id --query 'TerminatingInstances[0].CurrentState.Name'