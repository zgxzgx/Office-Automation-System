package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.SignatureEvent;

import cn.edu.shou.missive.service.TrimAlpha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sqhe on 15/2/3.
 */
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class SignatureController {

    @Autowired
    private SimpMessagingTemplate template;
    @MessageMapping("broadcastingEvent/{id}")
    //@SendTo("/app/broadcast")
    public void broadcastingSignatureEvent(SignatureEvent event,@DestinationVariable String id) throws Exception {
        // event.setMessage("ok");
        if (event.getEventType().equals("save"))
        {
            TrimAlpha ta = new TrimAlpha(event.getImageBase64());
            ta.trim();
            event.setImageBase64(ta.getBase64());
        }
        template.convertAndSend("/app/broadcast/"+id,event);
        //return event;
    }

    @RequestMapping(value = "/handSignaturePadOpen/{socketID}/{width}/{height}",method= RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public String syncPadFalse(@PathVariable String socketID,@PathVariable String width,@PathVariable String height){
//        return "<div class='alert alert-info bigger-110'>1.确认您使用的是Chrome浏览器<br/>2.确认您已安装好手写板插件</div><h1>手写功能暂不能使用<h1>";
        return "signatureNotFound";
    }
}
