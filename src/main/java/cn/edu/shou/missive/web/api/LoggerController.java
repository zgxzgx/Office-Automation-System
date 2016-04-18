package cn.edu.shou.missive.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shou on 2015/1/9.
 */
@RestController
//供移动端日志接口
public class LoggerController {
       private final static Logger logger = LoggerFactory.getLogger(LoggerController.class);
       @RequestMapping(value="/api/pad/log")
       public void padLog(String logMessage,String logIndex){
          if(logIndex.equals("error")){
              logger.error(logMessage);
          }
          else if(logIndex.equals("info")){
              logger.info(logMessage);
          }
       }
}
