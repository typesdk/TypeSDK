#!/bin/sh
echo "####################show node version"&&echo "####################show node version">>env.log
node -v&&node -v>>env.log
echo "####################show selinux"&&echo "####################show selinux">>env.log
sestatus -v &&sestatus -v >>env.log
echo "####################show sysctl"&&echo "####################show sysctl">>env.log
sysctl -p&&sysctl -p>>env.log
echo "####################show iptables"&&echo "####################show iptables">>env.log
iptables -L&&iptables -L>>env.log
echo "####################show netstat"&&echo "####################show netstat">>env.log
netstat -ntlp&&netstat -ntlp>>env.log
echo "####################show top"&&echo "####################show top">>env.log
top -n 1 &&top -n 1 >>env.log
echo "####################show systemversion"&&echo "####################show systemversion">>env.log
cat /etc/issue&&cat /etc/issue>>env.log
cat /proc/version&&cat /proc/version>>env.log
echo "####################show ulimit"&&echo "####################show ulimit">>env.log
ulimit -a&&ulimit -a>>env.log
echo "####################show profile"&&echo "####################show profile">>env.log
cat /etc/profile&&cat /etc/profile>>env.log
echo "####################show cpuinfo"&&echo "####################show cpuinfo">>env.log
cat /proc/cpuinfo&&cat /proc/cpuinfo>>env.log
echo "####################show meminfo"&&echo "####################show meminfo">>env.log
cat /proc/meminfo&&cat /proc/meminfo>>env.log
echo "####################show disk"&&echo "####################show disk">>env.log
df -h&&df -h>>env.log
echo "####################show diskrate"&&echo "####################show diskrate">>env.log
du -sh&&du -sh>>env.log
echo "####################show date"&&echo "####################show date">>env.log
date -R&&date -R>>env.log
echo "####################show locale"&&echo "####################show locale">>env.log
locale&&locale>>env.log
echo "####################show pm2 diskrate"&&echo "####################show pm2 diskrate">>env.log
du -sh `find / -name .pm2`&&du -sh `find / -name .pm2`>>env.log
echo "####################show pm2 property"&&echo "####################show pm2 property">>env.log
ls -l `find  / -type f -name pm2`&&ls -l `find  / -type f -name pm2`>>env.log
echo "####################show pm2log"
echo "please wait............"
tar czf pm2log.tar.gz `find /  -name pm2.log` >> /dev/null
echo "pm2log tar complete"
echo "####################show mysqldir"&&echo "####################show mysqldir">>env.log
dataname=`cat config.json|grep database|awk  -F'[:]' '{print $2}'|awk 'NR==1'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
datadir=`find / -name $dataname`
echo $dataname&&echo $dataname>>env.log
echo $datadir&&echo $datadir>>env.log
ls -d -l $datadir&&ls -d -l $datadir>>env.log
echo "####################show redisdir"&&echo "####################show redisdir">>env.log
redisname=$(cat `find / -name redis.conf`|grep dbfilename)
echo $redisname&&echo $redisname>>env.log
redisdir=$(cat `find / -name redis.conf`|grep "\<dir\>"|awk  -F'[ ]' '{print $2}'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g')
ls -d -l $redisdir&&ls -d -l $redisdir>>env.log
echo $redisdir&&echo $redisdir>>env.log
echo "####################show filelist"
find ./ -type f -print0 | xargs -0 ls  -d -l *>>filelist.log
echo "####################show filemd5"
find ./ -type f -print0 | xargs -0 md5sum>>filemd5.log

echo "####################show mysqldatabase"&&echo "####################show mysqldatabase">>env.log
#该片段检测配置是否能连接mysql不收集权限
mysqlip=`cat config.json|grep host|awk  -F'[:]' '{print $2}'|awk 'NR==1'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
mysqlport=`cat config.json|grep port|awk  -F'[:]' '{print $2}'|awk 'NR==2'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
mysqluser=`cat config.json|grep user|awk  -F'[:]' '{print $2}'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
mysqlpasswd=$(cat config.json|grep password|awk  -F'[:]' '{print $2}'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'|tr -s [:space:])
if  [ "$mysqlpasswd" = "" ] ;then
	showdatabases=$(mysql -h$mysqlip -P$mysqlport -u$mysqluser  -e "show databases;")
	echo $showdatabases>>env.log&&echo $showdatabases
else
	showdatabases=$(mysql -h$mysqlip -P$mysqlport -u$mysqluser -p$mysqlpasswd -e "show databases;")
	echo $showdatabases>>env.log&&echo $showdatabases
fi
cat config.json |grep database>>env.log&&cat config.json |grep database

echo "####################show redis">>env.log&&echo "####################show redis">>env.log
#该片段检测配置是否能连接redis不收集权限
redisip=`cat config.json|grep host|awk  -F'[:]' '{print $2}'|awk 'NR==2'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
redisport=`cat config.json|grep port|awk  -F'[:]' '{print $2}'|awk 'NR==3'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
redispasswd=`cat config.json|grep -w "pass"|awk  -F'[:]' '{print $2}'|sed 's/,//g'|sed 's/"//g'|sed 's/^[][ ]*//g'`
showdatabases=$(redis-cli -h $redisip -p $redisport -a $redispasswd keys "GAME*")
echo $showdatabases&&echo $showdatabases >> env.log
mkdir ServerEnvLog >> /dev/null
mv *.log ./ServerEnvLog >> /dev/null
mv *.tar.gz ./ServerEnvLog >> /dev/null
tar czf  console.tar.gz ./logs/console.log 
mv  console.tar.gz ./ServerEnvLog >> /dev/null
tar czf ServerEnvLog.tar.gz ServerEnvLog >> /dev/null
rm -rf ServerEnvLog >> /dev/null
echo "Check Complete!!"

