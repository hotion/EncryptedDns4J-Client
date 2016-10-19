# EncryptedDns4J-Client
 DNS防污染客户端，配合服务端 [EncryptedDns4J-Server](https://github.com/snail007/EncryptedDns4J-Server) 实现DNS的无污染解析。  
  
# Requirement
Linux  
JDK1.8+  
Maven 3.3+  
unzip  

# Usage:
In your local PC or local server ：  

git clone https://github.com/snail007/EncryptedDns4J-Client.git  

cd EncryptedDns4J-Client  

mvn install  

cp target/EncryptedDns4J-Client-1.0-SNAPSHOT-package.zip /root/  

cd /root/  

unzip EncryptedDns4J-Client-1.0-SNAPSHOT-package.zip  

cd EncryptedDns4J-Client-1.0-SNAPSHOT  

java -jar EncryptedDns4J-Client-1.0-SNAPSHOT-jar-with-dependencies.jar development  

# Notice

"development" is config subfolder's name,local it in config/development  

you can use "production" or "testing" or "development" for different environment.  

#Configuration

;DNS服务监听IP,所有IP使用：0.0.0.0  
listen_ip=0.0.0.0  
;DNS服务监听端口  
listen_port=53  
;与服务器通讯加密数据的加密key，需要和服务端一致  
encrypt_key=xxxxxxxx  
;后端查询使用的真实dns ip地址  
dns_ip=8.8.8.8  
;后端查询使用的真实dns端口  
dns_port=53  
;dns查询超时时间,单位毫秒  
timeout=10000  

;后端server地址  
server_ip=127.0.0.1  
;后端server监听端口  
server_port=10888  

#End
启动之后，让其他设备dns地址指向运行客户端的机器ip即可。  
为了提高性能，可以安装dnsmasq进行dns缓存，修改listen_port为其它端口比如：10053，然后  
在dnsmasq里面指定server=ip:10053即可。  

