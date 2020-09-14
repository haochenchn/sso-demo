//package com.aaron.cas.controller;
//
//import org.apache.http.HttpStatus;
//import org.apereo.cas.services.RegexRegisteredService;
//import org.apereo.cas.services.RegisteredService;
//import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
//import org.apereo.cas.services.ServicesManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.net.URL;
//
///**
// * @author Aaron
// * @description 动态注册service
// * @date 2020/9/14
// */
//@RestController
//public class CasServicesController {
//    private static final Logger LOGGER = LoggerFactory.getLogger(CasServicesController.class);
//
//    @Autowired
//    @Qualifier("servicesManager")
//    private ServicesManager servicesManager;
//
//    /*
//    APP1-1000.json
//    {
//      "@class" : "org.apereo.cas.services.RegexRegisteredService",
//      "serviceId" : "^(https|imaps|http)://app1.cas.com.*",
//      "name" : "测试客户端app1",
//      "id" : 1000,
//      "description" : "这是app1的客户端",
//      "evaluationOrder" : 10,
//      "theme" : "app1",
//      "attributeReleasePolicy" : {
//        "@class" : "org.apereo.cas.services.ReturnAllAttributeReleasePolicy"
//      }
//    }*/
//    /**
//     * 注册service
//     * @param indexUrl client主页url
//     * @param name client名
//     * @param id 顺序
//     * @return
//     */
//    @RequestMapping(value = "/addClient",method = RequestMethod.GET)
//    public Object addClient(String indexUrl, String name, long id) {
//        ReturnMessage returnMessage = new ReturnMessage();
//        try {
//            RegisteredService oldService = servicesManager.findServiceBy(id);
//            if(oldService != null) {
//                // id已存在
//            }
//            RegexRegisteredService service = new RegexRegisteredService();
//            ReturnAllAttributeReleasePolicy re = new ReturnAllAttributeReleasePolicy();
//            String serviceId = indexUrl + ".*";
//            service.setServiceId(serviceId);
//            service.setName(name);
//            service.setId(id);
//            service.setAttributeReleasePolicy(re);
//            //这个是为了单点登出而作用的
//            service.setLogoutUrl(new URL(indexUrl));
//            servicesManager.save(service);
//            //执行load让他生效
//            servicesManager.load();
//            returnMessage.setCode(HttpStatus.SC_OK);
//            returnMessage.setMessage("添加成功");
//            LOGGER.info("注册了服务[{}]: {}", name, indexUrl);
//            return returnMessage;
//        } catch (Exception e) {
//            LOGGER.error("注册服务异常",e);
//            returnMessage.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
//            returnMessage.setMessage("添加失败");
//            return returnMessage;
//        }
//    }
//
//    /**
//     * 删除service
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/deleteClient",method = RequestMethod.GET)
//    public Object deleteClient(long id) {
//        ReturnMessage returnMessage = new ReturnMessage();
//        try {
//            RegisteredService service = servicesManager.findServiceBy(id);
//            // 这里可能会报审计错误，直接进行捕获即可，不影响删除逻辑
//            servicesManager.delete(service);
//            //执行load生效
//            servicesManager.load();
//
//            returnMessage.setCode(200);
//            returnMessage.setMessage("删除成功");
//            LOGGER.info("删除了服务[{}]: {}", service.getName(), service.getServiceId());
//            return returnMessage;
//        } catch (Exception e) {
//            LOGGER.error("删除service异常",e);
//            returnMessage.setCode(500);
//            returnMessage.setMessage("删除失败");
//            return returnMessage;
//        }
//    }
//
//    public class ReturnMessage{
//
//        private Integer code;
//
//        private String message;
//
//        public Integer getCode() {
//            return code;
//        }
//
//        public void setCode(Integer code) {
//            this.code = code;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//    }
//}
