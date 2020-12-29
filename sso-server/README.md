## 1 单点登录服务端HTTPS配置
### 1.1 服务端生成证书
```
#使用域名
keytool -genkey -alias sso -keyalg RSA -validity 3650 -keypass changeit -storepass changeit -dname "CN=server.sso.com,OU=hx,O=hx,L=WH,ST=HB,C=CN" –ext -keystore D:/key/sso.keystore

#使用ip
keytool -genkey -alias sso -keyalg RSA -validity 3650 -keypass changeit -storepass changeit -dname "CN=192.168.0.93,OU=hx,O=hx,L=WH,ST=HB,C=CN"  -ext san=ip:192.168.0.93 -keystore /home/key/sso.keystore

#Warning:
#JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore /usr/local/soft/cas/key/138cas.keystore -destkeystore /usr/local/soft/cas/key/138cas.keystore -deststoretype pkcs12" 迁移到行业标准格式 PKCS12。
[root@centos138 key]# 

#说明：指定使用RSA算法，生成别名为sso的证书，口令为changeit，证书的DN为"cn= server.sso.com" 

```

### 1.2 根据keystore生成证书文件
```
keytool -export -alias sso -file /home/key/sso.cer -keystore /home/key/sso.keystore -validity 3650
```

### 1.3 信任授权文件到jdk
```
keytool -import -keystore /opt/jdk1.8/jre/lib/security/cacerts -file /home/key/sso.cer -alias sso -storepass changeit

是否信任此证书? [否]: y
证书已添加到密钥库

```
- 删除cacerts中的证书
```
keytool -delete -alias sso -keystore D:/dev_soft/java/jdk1.8/jre/lib/security/cacerts
```

## cas服务端tomcat添加配置
```
<Connector port="8070" protocol="org.apache.coyote.http11.Http11Protocol"
              maxThreads="150" SSLEnabled="true" scheme="https" secure="true"
              clientAuth="false" sslProtocol="TLS"
              keystoreFile="/home/key/sso.keystore"
              keystorePass="changeit" />

```