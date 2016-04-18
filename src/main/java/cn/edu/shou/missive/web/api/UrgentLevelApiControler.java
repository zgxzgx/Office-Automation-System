package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.MissivePublishFunction;
import cn.edu.shou.missive.domain.UrgentLevel;
import cn.edu.shou.missive.domain.missiveDataForm.UrgentLevelForm;
import cn.edu.shou.missive.service.UrgentLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sqhe18 on 14-9-5.
 */
@RestController
@RequestMapping(value = "/api/missive/urgentLevel")
public class UrgentLevelApiControler {
    @Autowired
    private UrgentLevelRepository ulr;
    MissivePublishFunction msf=new MissivePublishFunction();
    @RequestMapping(value="/geturgentlevel", method= RequestMethod.GET)
    public List<UrgentLevelForm> getUrgentLevel(){
        List<UrgentLevelForm> urlfs=new ArrayList<UrgentLevelForm>();
        List<UrgentLevel> urls=ulr.findAll();
        for(UrgentLevel ul:urls){
            UrgentLevelForm ulf=msf.urgentLevelToUrgentLevelForm(ul);
            urlfs.add(ulf);
        }
        return urlfs;
    }
}
