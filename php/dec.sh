#! /bin/bash



mkdir /var/www/tce/$1
mkdir /var/www/tce/$1/$3

cd /home/hadoop/hadoop

bin/hadoop fs -copyToLocal /user/hadoop/$1/$3/part-r-00000.gz  /var/www/tce/$1/$3
gunzip /var/www/tce/$1/$3/part-r-00000.gz
bin/hadoop fs -copyFromLocal /var/www/tce/$1/$3/part-r-00000 /user/hadoop/$1/$3

#mv /var/www/tce/key.txt /home/hadoop/hadoop/$1/$3
bin/hadoop fs -copyFromLocal /var/www/tce/$2 /user/hadoop/$1
rm /var/www/tce/$2

bin/hadoop jar aesd.jar aesdec /user/hadoop/$1/$3 /user/hadoop/$1/$3/output

bin/hadoop fs -copyToLocal /user/hadoop/$1/$3/output/part-r-00000 /var/www/tce/$1/


bin/hadoop fs -rmr /user/hadoop/$1/$3
bin/hadoop fs -rmr /user/hadoop/$1/$2

mv /var/www/tce/$1/part-r-00000 /var/www/tce/$1/$3
