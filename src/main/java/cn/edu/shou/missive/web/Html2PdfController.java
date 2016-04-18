package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.MissiveRecSeeCard;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveReceiveRender;
import cn.edu.shou.missive.service.MissiveRecSeeCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by shou on 2015/1/3.
 */
@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class Html2PdfController {

    @Value("${spring.main.receiptid}")
    int receiptid;
    @Autowired
    private MissiveRecSeeCardRepository misssCardR;
    @Autowired
    private MissiveReceiveFunction missF;
    //静态页面
    @RequestMapping(value="/html2pdf/missiveReceive/{instanceId}",method = RequestMethod.GET)
    public String html2Pdf(@PathVariable String instanceId,Model model){

        MissiveRecSeeCard missCard = misssCardR.getMissData(instanceId);
        MissiveReceiveRender missRender = missF.MissiveReceive2MissiveReceiveRender(missCard,"");
        model.addAttribute("missiveReceiveForm",missRender);
        model.addAttribute("hasBgPng",(missCard.getBgPngPath()!=null && !missCard.getBgPngPath().equals("")));
        if (missRender.getLeaderInstruct()!=null)
            model.addAttribute("isNewImgsize",missRender.getLeaderInstruct().id>this.receiptid);
        else
            model.addAttribute("isNewImgsize",true);
        model.addAttribute("instanceid",instanceId);
        return "MissiveReceivePDF";
    }
    @RequestMapping(value="/html2pdf/missiveReceive/pad/{instanceId}",method = RequestMethod.GET)
    public String html2Pdf4pad(@PathVariable String instanceId,Model model){



        MissiveRecSeeCard missCard = misssCardR.getMissData(instanceId);
        MissiveReceiveRender missRender = missF.MissiveReceive2MissiveReceiveRender(missCard,"");
        if (missRender.getLeaderInstruct()!=null)
            model.addAttribute("isNewImgsize",missRender.getLeaderInstruct().id>this.receiptid);
        else
            model.addAttribute("isNewImgsize",true);
        model.addAttribute("missiveReceiveForm",missRender);
        model.addAttribute("hasBgPng",(missCard.getBgPngPath()!=null && !missCard.getBgPngPath().equals("")));
        model.addAttribute("instanceid",instanceId);
        return "MissiveReceivePDF4Pad";
    }
}
