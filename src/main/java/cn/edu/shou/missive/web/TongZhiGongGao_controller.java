package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by TISSOT on 2014/6/25.
 */
@RestController
@RequestMapping(value="/db")
@SessionAttributes(value = {"userbase64","user_id","user"})
public class TongZhiGongGao_controller {

    @Autowired
    private ScheduleRepository sr;

    @Autowired
    private NotificationRepository nfr;

    @Autowired
    private Notification_UserRepository nfuDAO;


    @Autowired UserDAO userDAO;

    @Autowired GroupRepository groupDAO;

    private final static Logger logger = LoggerFactory.getLogger(TongZhiGongGao_controller.class);


    //新建公告
   @RequestMapping(value="/notification_new")
    public void newNotification(@RequestParam("user[]")ArrayList<Long> user,@RequestParam("userGroup[]") ArrayList<Long> userGroup,String title,String contentHtml,String content,String time,@AuthenticationPrincipal User currentUser)
   {

       List<User> users=new ArrayList<User>(); //存放收件人
       List<User> groups=new ArrayList<User>(); //存放收件组

       List<Long>allJuniorGroup=new ArrayList<Long>();
       if(user.get(0)==0&&userGroup.get(0)!=0){   //收件人为空，收件组不为空

           for(int i=0;i<userGroup.size();i++){

               Group leafGroup=this.groupDAO.findOne(userGroup.get(i));
               List<Group>leafList=leafGroup.getGroupList();

               if(leafList.size()>0){

                   for(Group leafNode:leafList){

                       allJuniorGroup.add(leafNode.getId());

                   }

               }
               else {

                   allJuniorGroup.add(userGroup.get(i));

               }

           }

           groups=this.userDAO.findUserListByGroupList(allJuniorGroup);

           users.addAll(groups);

       }else if(userGroup.get(0)==0&&user.get(0)!=0){ //收件组为空，收件人不为空


           users=this.userDAO.findUserListByIdList(user);

       }
       else if(userGroup.get(0)!=0&&user.get(0)!=0){ //收件人和收件组都不为空

           for(int i=0;i<userGroup.size();i++){

               Group leafGroup=this.groupDAO.findOne(userGroup.get(i));
               List<Group>leafList=leafGroup.getGroupList();

               if(leafList.size()>0){

                   for(Group leafNode:leafList){

                       allJuniorGroup.add(leafNode.getId());

                   }

               }
               else {

                   allJuniorGroup.add(userGroup.get(i));

               }

           }

           users=this.userDAO.findUserListByIdList(user);
           groups=this.userDAO.findUserListByGroupList(allJuniorGroup);

           List<User> middle = new ArrayList<User>(groups);
           middle.removeAll(users);
           users.addAll(middle);

       }


       Notification nf=new Notification();

       nf.setWriter(currentUser.getName());
       nf.setTitle(title);
       nf.setContentHtml(contentHtml);
       nf.setContent(content);
       nf.setIsDel("0");
       nf.setTime(time);
       nf.setCreateTime(new Date());
       nfr.save(nf);

       for(int i=0;i<users.size();i++){

           Notification_User nfu=new Notification_User();

           nfu.setUser(users.get(i));
           nfu.setNotification(nf);
           nfu.setStatus(false);
           nfuDAO.save(nfu);
       }

   }

   //更新为已读
    @RequestMapping(value = "/notification_update" )
    public void setRead(@RequestParam("id") Long id){

        Notification_User nfu=nfuDAO.findOne(id);
        nfu.setStatus(true);

        nfuDAO.save(nfu);
    }

    //获取未读公告条数
    @RequestMapping(value = "/notification_unread")
    public Long getUnReadNum(@AuthenticationPrincipal User user){

        Long unReadNum=nfuDAO.getUnreadCount(user);

        return unReadNum;
    }

    //获取已发送的公告
    @RequestMapping(value = "/notification_send")
    public Page<Notification> getSend(@AuthenticationPrincipal User user,@RequestParam int page, @RequestParam int size){

        String writer=user.getName();
        Pageable pageable = new PageRequest(
                page,size,new Sort(Sort.Direction.DESC,"id")
        );
        Page<Notification>searchResult=this.nfr.findSendResult(writer,pageable);

        return searchResult;
    }

    //查询公告
    @RequestMapping(value = "/notification_search")
    public Page<Notification_User> getSearch(@AuthenticationPrincipal User user,@RequestParam("searchValue")String searchValue,@RequestParam int page, @RequestParam int size){

        Pageable pageable = new PageRequest(
                page,size,new Sort(Sort.Direction.DESC,"id")
        );
        Page<Notification_User>searchResult=this.nfuDAO.findSearchResult(user,searchValue,pageable);

        return searchResult;
    }


    //删除公文
    @RequestMapping(value = "/notification_delete",method = RequestMethod.POST)
    public void delSelected(@RequestParam("multiID") String multiID){

        String IDList[] = multiID.split(",");

        List<Long> delNotIds = new ArrayList<Long>();
        for(int i=0;i<IDList.length;i++){

            Long id=Long.parseLong(IDList[i]);
            delNotIds.add(id);

        }

        nfuDAO.delMultiData(delNotIds);

    }

   //获取分页信息
    @RequestMapping(value = "/notification_get1",method = RequestMethod.POST)
    public Page<Notification_User> getNotice(@RequestParam int page, @RequestParam int size,@AuthenticationPrincipal User user){

        Pageable pageable = new PageRequest(
                page,size,new Sort(Sort.Direction.DESC,"id")
        );

        Page<Notification_User> pageResult=nfuDAO.findByUser(user,pageable);
        for(Notification_User uu:pageResult)
        {
            uu.getNotification();
            uu.getUser();
            uu.getUser().getGroup();
        }

        return pageResult;
    }

    //获取详细信息
    @RequestMapping(value = "/notification_getDetail",method = RequestMethod.POST)
    public Notification getNoticeDetail(@RequestParam("selectID") Long selectID){

        //Notification noticeDetail=nfr.findOne(selectID);

        Notification_User nfu=nfuDAO.findOne(selectID);

        return nfu.getNotification();
    }
    //新建日程
    @RequestMapping(value="/richeng_new")
    public void richeng_new(String noTitle,String noStart,String noEnd,String noCon,String noPeo){
        Schedule sd=new Schedule();
        sd.setTitle(noTitle);
        sd.setStartTime(noStart);
        sd.setEndTime(noEnd);
        sd.setContent(noCon);
        sd.setUser(noPeo);
        sd.setIsDel("0");
        sd.setCreateTime(new Date());
        sr.save(sd);

    }
    //获取日程
    @RequestMapping(value="/richeng_get")
    public void getRiCheng(String user,HttpServletResponse response){
        List<Schedule> ls= sr.findByUser(user,"0");
        String res="";
        if(ls!=null&&ls.size()!=0) {
            for (Schedule sd : ls) {
                String title = sd.getTitle();
                Long id = sd.getId();
                String start = "new Date(\"" + sd.getStartTime().trim() + "\")";
                String end = "new Date(\"" + sd.getEndTime().trim() + "\")";
                String content = sd.getContent();
                res += "{id:" + id + ",start:" + start + ",end:" + end + ",description:'" + content + "',title:'" + title + "'},";
            }
            res = "[" + res.substring(0, res.length() - 1) + "]";
        }
        else{
            res="[]";
        }
        response.setCharacterEncoding("UTF-8");
        try{response.getWriter().write(res);}
        catch(Exception e){
            logger.error("getRiCheng exception:",e);

            e.printStackTrace();
        }
    }
    @RequestMapping(value="/richeng_del")
    public void delRicheng(Long id){
        Schedule sd=sr.findOne(id);
        sd.setIsDel("1");//字段isDel为1时表明该条记录已被删除
        sr.save(sd);
    }
    @RequestMapping(value="/richeng_edit")
    public void editRicheng(String noTitle,String noStart,String noEnd,String noCon,Long id,String user){
        Schedule sd =new Schedule();
        sd.setId(id);
        sd.setTitle(noTitle);
        sd.setUser(user);
        sd.setIsDel("0");
        sd.setStartTime(noStart);
        sd.setEndTime(noEnd);
        sd.setContent(noCon);
        sr.save(sd);
    }
    @RequestMapping(value="/richeng_move")
    public void moveRicheng(String start,String end, Long id){
        Schedule sd =sr.findOne(id);
        sd.setStartTime(start);
        sd.setEndTime(end);
        sr.save(sd);
    }


    @RequestMapping(value="/getNotiCount/{userId}")
    public Long getUserCount(Long userId,@AuthenticationPrincipal User user){
        Long unReadCount= this.nfuDAO.getUnReadCount(user.getId());
        return unReadCount;
    }


}
