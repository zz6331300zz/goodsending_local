docker run -d \
--name mysql-goodsending \
-e MYSQL_ROOT_PASSWORD="rootpassword" \
-e MYSQL_USER="goods" \
-e MYSQL_PASSWORD="goodsspassword" \
-e MYSQL_DATABASE="goodsending" \
-e TZ="Asia/Seoul"
-p 3306:3306 \
mysql:latest