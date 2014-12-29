#!/bin/bash

sudo iptables -A INPUT -p tcp --destination-port 50070 -j DROP
sudo iptables -A INPUT -p tcp --destination-port 50075 -j DROP

cd /home/hadoop/hadoop/

bin/hadoop fs -mkdir /user/hadoop/$1
bin/hadoop fs -mkdir /user/hadoop/$1/input

bin/hadoop fs -copyFromLocal /var/www/tce/upload/$3 /user/hadoop/$1/input
bin/hadoop fs -copyFromLocal /var/www/tce/$2 /user/hadoop/$1

rm /var/www/tce/upload/$3
rm /var/www/tce/$2

bin/hadoop jar aes1.jar aesenc /user/hadoop/$1/input /user/hadoop/$1/$3
bin/hadoop fs -rmr /user/hadoop/$1/key.txt
bin/hadoop fs -rmr /user/hadoop/$1/input

sudo iptables -D INPUT 1
sudo iptables -D INPUT 1

