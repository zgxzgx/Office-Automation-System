package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiliwei on 2014/8/24.
 */
@Controller
@RequestMapping(value="/api")
@SessionAttributes(value = {"userbase64","user"})
public class FullSearchController {


    @Autowired
    ActivitiService act;

    @Autowired
    private MissiveRecSeeCardRepository mrscr;
    @Autowired
    UserRepository usrR;

    @Autowired
    private MissiveVersionRepository mvService;

    private final static Logger logger = LoggerFactory.getLogger(FullSearchController.class);

    RestTemplate template = new RestTemplate();

    @RequestMapping(value = "/fullSearch/search",method = RequestMethod.POST ,produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getSearchResult(@RequestParam("searchValue") String searchValue,@RequestParam("from") Long from,@RequestParam("size") Long size,@AuthenticationPrincipal User currentUser){

        final HttpHeaders headers = new HttpHeaders();

        Map<String,Object> request = new HashMap<String, Object>();
        Map<String,Object> requestInner = new HashMap<String, Object>();
        Map<String,Object> requestInner1 = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterAss = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterMain = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterCopy = new HashMap<String, Object>();
        Map<String,Object> requestInnerQuery = new HashMap<String, Object>();
        Map<String,Object> requestInnerQTerm = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterOr = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterOrTermAss = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterOrTermMain = new HashMap<String, Object>();
        Map<String,Object> requestInnerFilterOrTermCopy = new HashMap<String, Object>();
        Map<String,Object> requestInnerQTermMuti = new HashMap<String, Object>();

        int userID=Integer.parseInt(Long.toString(currentUser.id));//获取到登录者的ID
        int userGroup=Integer.parseInt(Long.toString(currentUser.group.id));//获取到登录者的组
        int[] searchUser={userID};//查询者
        int[] searchUserGroup={userGroup};//查询者组

        requestInnerQTermMuti.put("query",searchValue);
        requestInnerQTermMuti.put("fields",ImmutableList.of("missiveNum", "missiveTitle","missiveContent","author","missiveType"));
        requestInnerQuery.put("multi_match",requestInnerQTermMuti);
        requestInner.put("query",requestInnerQuery);

        //流程经手过以及主送和抄送的用户
        requestInnerFilterOrTermAss.put("assigns",searchUser);//流程经手过的人员
        requestInnerFilterOrTermMain.put("mainSend",searchUserGroup);//主送人员
        requestInnerFilterOrTermCopy.put("copySend",searchUserGroup);//抄送人员

        requestInnerFilterAss.put("term", requestInnerFilterOrTermAss);
        requestInnerFilterMain.put("term", requestInnerFilterOrTermMain);
        requestInnerFilterCopy.put("term",requestInnerFilterOrTermCopy);
        requestInnerFilterOr.put("or", Arrays.asList(requestInnerFilterAss,requestInnerFilterMain,requestInnerFilterCopy));
        requestInner.put("filter",requestInnerFilterOr);


        request.put("query", ImmutableMap.of("filtered", requestInner));


        requestInner1.put("missiveTitle",ImmutableMap.of());
        requestInner1.put("author",ImmutableMap.of());
        requestInner1.put("assigns",ImmutableMap.of());
        requestInner1.put("time",ImmutableMap.of());
        requestInner1.put("missiveNum",ImmutableMap.of());
        requestInner1.put("missiveType",ImmutableMap.of());
        requestInner1.put("missiveContent",ImmutableMap.of());
        requestInner1.put("mainSend",ImmutableMap.of());
        requestInner1.put("copySend",ImmutableMap.of());

        request.put("highlight",ImmutableMap.of("fields",requestInner1));

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        String jsonString = "";
        try {
            objectMapper.writeValue(writer,request);
             jsonString = writer.toString();
        } catch (IOException e) {
            logger.error("getSearchResult exception:",e);


            e.printStackTrace();
        }

        String requestUrl="http://localhost:9200/search/process/_search?from="+from+"&size="+size;

        ResponseEntity<String> temp = template.postForEntity(requestUrl,request,String.class);

        String restCall = temp.getBody();

        //HttpStatus status=temp.getStatusCode();

        return restCall;
    }




    @RequestMapping(value = "/fullSearch/setContent/{taskType}/{taskID}/{processID}",method = RequestMethod.GET)
    @ResponseBody
    public String setSearchContent(@PathVariable("taskType")String taskType,@PathVariable("taskID")String taskID,@PathVariable("processID")String processID,@AuthenticationPrincipal User currentUser){

        //测试程序
        //完成每一步的时候执行插入全文检索
        boolean isFishedProcess=act.isProcessFinished(processID);
        isFishedProcess=true;
        MissiveRecSeeCard mrsc = mrscr.getMissData(String.valueOf(processID));
        Missive missive = mrsc.getMissiveInfo();
        MissiveVersion missiveVersion=mvService.getVersionByMissiveID(missive.getId());



//        //ID为公文记录对应的编号
//        String userName=currentUser.getUsername();//获取到当前用户名
//        int[] assignsUsers={};
//        //将公文插入到全文检索数据库中
//        if (!taskType.equals("")){
//            assignsUsers=getActivitiMissive(processID);
//            searchR.setSearchContent(taskType,processID,taskID,assignsUsers);
//        }

        return "true";
    }
    //activiti查询的公文
    private int[] getActivitiMissive(String processID){
        int[] assigns={};//获取所有参与流程用户
        String userID="";
        List<String> assignStr= act.getProcessUsersByProcessID(processID);//根据用户，获取到用所参与的所有任务
        if (assignStr.size()>0) {
            assigns = new int[assignStr.size()];
            for (int i = 0; i < assignStr.size(); ++i) {
                userID = usrR.getUserIDByUserName(assignStr.get(i));//获取用户ID
                assigns[i] = Integer.parseInt(userID);
            }
        }
        return assigns;
    }

}
