#!/bin/bash

# settings
imageid="ami-a36f8dc4" # Amazon Linux 64-Bit
instance_type="t2.micro"
key_name="windowsKey"
key_location="windowsKey.pem"
security_group_id="sg-c72188ae"

cpp_file="hello-world.cpp"
makefile="Makefile"
# end of settings

# measure time
START_TIME=$SECONDS

# start instance and get ip address
echo "Starting instance ..."
instance_id=$(aws ec2 run-instances --image-id $imageid --security-group-ids $security_group_id --count 1 --instance-type $instance_type --key-name $key_name --query 'Instances[0].InstanceId')
correct_id=$(echo $instance_id | sed 's/[^a-zA-Z0-9-]//g')

instance_ip=$(aws ec2 describe-instances --instance-ids $correct_id --query 'Reservations[0].Instances[0].PublicIpAddress')
correct_ip=$(echo $instance_ip | sed 's/[^0-9.]//g')

# wait until instance is running completely
echo "Waiting for instance to get up and running ..."
aws ec2 wait instance-running --instance-ids $correct_id

echo "Instance running, waiting for ssh port to be open ..."
while ! ssh -o StrictHostKeyChecking=no -i $key_location ec2-user@$correct_ip 'exit'
do
    echo "Trying again..."
	sleep 5
done

# echo elapsed time 
echo "Starting the instance took $(($SECONDS-$START_TIME)) seconds!"

# inside the instance
echo "Connecting to instance at $correct_ip ..."
ssh -o StrictHostKeyChecking=no -i $key_location ec2-user@$correct_ip << EOF
	sudo yum -y update
	sudo yum -y groupinstall "Development Tools"
	fallocate -l 1K F1.dat
  	fallocate -l 10K F2.dat
  	fallocate -l 100k F3.dat
  	fallocate -l 1M F4.dat
  	fallocate -l 10M F5.dat
  	ls
	echo "Done!"
EOF

# terminate the instance
echo "Terminating instance ..."
aws ec2 terminate-instances --instance-ids $correct_id --query 'TerminatingInstances[0].CurrentState.Name'
