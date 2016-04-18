package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.DIYphase;
import cn.edu.shou.missive.domain.User;
import cn.edu.shou.missive.service.DIYphaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2014/12/19.
 */
@RestController
@SessionAttributes(value = {"userbase64","user_id","user"})
@RequestMapping(value="/diyphase")
public class DIYphaseController {
    @Autowired
    DIYphaseRepository DR;


    //read获取所有用户短语
    @RequestMapping(value = "/phasegrid")
    public List<DIYphase> getPhaseGrid(@AuthenticationPrincipal User currentUser){
        List<DIYphase> dlist=DR.findByUserId(currentUser.getId());
        if(dlist.isEmpty()){
            DIYphase uu=new DIYphase();
            uu.setPhaseContent("同意");
            uu.setUser(currentUser);
            DR.save(uu);
            DIYphase uu1=new DIYphase();
            uu1.setPhaseContent("已阅");
            uu1.setUser(currentUser);
            DR.save(uu1);
            DIYphase uu2=new DIYphase();
            uu2.setPhaseContent("请领导签发");
            uu2.setUser(currentUser);
            DR.save(uu2);
        }

        List<DIYphase> dlist2= new ArrayList<DIYphase>();
        dlist2=DR.findByUserId(currentUser.getId());
        return dlist2;
    }
    //通过用户id获取该用户下所有自定义短语内容
    @RequestMapping(value="/getPhase/{userid}")
    public List<String> getPhaseByUserId(@PathVariable Long userid){
        List<String> phases = new ArrayList<String>();
        List<DIYphase> ldy = DR.findByUserId(userid);
        if(ldy!=null&&ldy.size()!=0) {
            for (DIYphase dp : ldy) {
                phases.add(dp.getPhaseContent());
            }
        }
        return  phases;
    }

    //创建用户词条
    @RequestMapping(value = "/createPhase")
    public List<DIYphase> createPhase(@RequestParam String phaseContent,@AuthenticationPrincipal User currentUser){
        DIYphase uu=new DIYphase();
        uu.setPhaseContent(phaseContent);
        uu.setUser(currentUser);
        DR.save(uu);
        List<DIYphase> list=new ArrayList<DIYphase>();
        list.add(uu);
        return list;
    }

    //更新用户词条
    @RequestMapping(value = "updatePhaseList")
    public List<DIYphase> updatePhaseList(@RequestParam long id,@RequestParam String phaseContent){
        DIYphase uu=DR.findOne(id);
        uu.setPhaseContent(phaseContent);
        DIYphase updatePhaseList=DR.save(uu);
        List<DIYphase> PhaseList=new ArrayList<DIYphase>();
        PhaseList.add(updatePhaseList);
        return PhaseList;
    }

    //删除用户词条
    @RequestMapping(value = "deletePhaseList")
    public List<DIYphase> deleteUrgentLevel(@RequestParam long id){
        DIYphase uu=DR.findOne(id);
        DR.delete(uu);
        List<DIYphase> PhaseList=new ArrayList<DIYphase>();
        PhaseList.add(uu);
        return PhaseList;

    }


}
