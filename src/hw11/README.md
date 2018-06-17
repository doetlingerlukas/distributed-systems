# Hw11 by Lukas Dötlinger

These scripts start an EC2 instance and an S3 bucket. The first instance copies testfiles to the S3 bucket. Afterwards it starts another second instance and copies those files to it. The second instance copies the files from S3 to it's local storage. All those copy-times are recorded and saved into a textfile. After all the copying is finished, the times are printet out on the console.\\
Those times will also be saved as a local file, `time.txt` .

## How to run

In order to run the scripts, you need to change code in the files `aws-script-hw11.sh` and `aws-vm1-script.sh` .\\
You need to add a file called `mKeys.txt` to this directory, in which you write your AWS API keys. Copy the access-key in the first line and the secret-key in the second. Only add the keys, nothing else.\\
You also need to add a `mKey.pem` file that you create via the AWS Console.\\
In both files, there is a settings section, where you need to specify the `instanceId` and the `security_group_id` , which you get from the AWS Console.\\
You may also change the S3 bucket-name.\\