package com.aaron.cas;

import net.unicon.cas.client.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@PropertySource(value={"classpath:config/path.properties"},ignoreResourceNotFound=true,encoding="utf-8")
//@ImportResource("classpath:spring/*.xml")
//@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@EnableCasClient//开启cas
public class SsoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SsoClientApplication.class, args);
	}
	
}
