#!/bin/sh
setenforce 0
echo -e "\033[31m ###################################################################################################### \033[0m"
echo -e "\033[32m ################################### install typesdkserver allinone ################################### \033[0m"
echo -e "\033[32m ################################### website:http://www.typesdk.com ################################### \033[0m"
echo -e "\033[32m ################################### QQ:1771930259                  ################################### \033[0m"
echo -e "\033[31m ###################################################################################################### \033[0m"
myPath="/data/"
if [ ! -d "$myPath" ]; then
	mkdir -p "$myPath"
fi
mkdir  -p /data/typesdk_server
cp -a ../* /data/typesdk_server/
echo -e "\033[31m ###################################################################################################### \033[0m"
echo -e "\033[32m ##################################### install and start mysql  ####################################### \033[0m"
echo -e "\033[31m ###################################################################################################### \033[0m"
cd /data/typesdk_server/Tools
rpm -Uvh mysql57-community-release-el7-9.noarch.rpm
yes|cp mysql-community.repo /etc/yum.repos.d/mysql-community.repo  
yum install mysql-server -y 
mkdir -p /data/typesdk_server/mysql_data
chown -R mysql.mysql /data/typesdk_server/mysql_data
cp /etc/my.cnf /etc/my.cnf.bak
>/etc/my.cnf
cat > /etc/my.cnf <<EOF
[mysqld]
datadir=/data/typesdk_server/mysql_data
socket=/var/lib/mysql/mysql.sock
symbolic-links=0
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid
character-set-server=utf8
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8

EOF
service mysqld start
mysql < /data/typesdk_server/SdkServer_free_git/typesdk_server.sql
mysqladmin -u root password "typesdk.com"
echo -e "\033[31m ###################################################################################################### \033[0m"
echo -e "\033[32m ##################################### install and start nginx  ####################################### \033[0m"
echo -e "\033[31m ###################################################################################################### \033[0m"
cd /data/typesdk_server/Tools
rpm -Uvh nginx-1.8.1-1.el7.ngx.x86_64.rpm
mkdir -p /data/typesdk_server/nginx_html/config
cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.bak
>/etc/nginx/nginx.conf
cat > /etc/nginx/nginx.conf <<EOF
#user  nobody;
worker_processes  auto;
events {
    worker_connections  102400;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    gzip  on;
    server {
        listen       80;
        #server_name  your.domain.name;
        location /config {
            root   /data/typesdk_server/nginx_html;
            index  index.html index.htm;
        }
        location / {
            proxy_pass http://127.0.0.1:40000;
        }    
		location /getIp {
        default_type application/json;
        return 200 '{"ip":"$remote_addr"}';
		}
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}

EOF
mkdir -p /data/typesdk_server/nginx_html/config/
cp -r /data/typesdk_server/Tools/Nginx_SwitchConfig/1001/ /data/typesdk_server/nginx_html/config/
service nginx start
echo -e "\033[31m ###################################################################################################### \033[0m"
echo -e "\033[32m ##################################### install and start redis  ####################################### \033[0m"
echo -e "\033[31m ###################################################################################################### \033[0m"
cd /data/typesdk_server/Tools
rpm -Uvh jemalloc-3.6.0-1.el7.x86_64.rpm 
rpm -Uvh redis-3.2.3-1.el7.x86_64.rpm  
mkdir -p /data/typesdk_server/redis_data
chown -R redis.redis /data/typesdk_server/redis_data
cp /etc/redis.conf /etc/redis.conf.bak
cat > /etc/redis.conf << EOF
bind 0.0.0.0
protected-mode yes
port 6379
tcp-backlog 511
timeout 0
tcp-keepalive 300
daemonize no
supervised no
pidfile /var/run/redis_6379.pid
loglevel notice
logfile /var/log/redis/redis.log
databases 16
save 900 1
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename sdk.rdb
dir /data/typesdk_server/redis_data
slave-serve-stale-data yes
slave-read-only yes
requirepass typesdk.com
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-disable-tcp-nodelay no
slave-priority 100
appendonly no
appendfilename "appendonly.aof"
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
lua-time-limit 5000
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-size -2
list-compress-depth 0
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
hll-sparse-max-bytes 3000
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit slave 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
hz 10
aof-rewrite-incremental-fsync yes

EOF
service redis start
echo -e "\033[31m ###################################################################################################### \033[0m"
echo -e "\033[32m #####################################       install node       ####################################### \033[0m"
echo -e "\033[31m ###################################################################################################### \033[0m"
cd /data/typesdk_server/Tools/
rpm -Uvh nodejs-6.9.5-1nodesource.el7.centos.x86_64.rpm
npm config set registry http://registry.cnpmjs.org
chmod +x /data/typesdk_server/SdkServer_free_git/node_modules/pm2/bin/pm2
ln -s /data/typesdk_server/SdkServer_free_git/node_modules/pm2/bin/pm2 /usr/bin/pm2
echo -e "\033[32m #####################################     staring server  ####################################### \033[0m"
cd /data/typesdk_server/SdkServer_free_git/bin
pm2 start www

