package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.*;
import cn.edu.shou.missive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by sqhe on 14-7-19.
 */
@RestController
@RequestMapping(value = "/api/missive/missivepublish")
public class MissivePublishApiController {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository ur;
    @Autowired
    private GroupRepository gr;
    @Autowired
    private MissiveRepository mr;
    @Autowired
    private CommentContentRepository ccr;
    @Autowired
    private MissiveVersionRepository mvr;
    @Autowired
    private AttachmentRepository attachr;
    @Autowired
    private MissiveTypeRepository mtr;
    @Autowired
    private SecretLevelRespository slr;
    @Autowired
    private MissivePublishRepository mpDAO;
    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    public MissivePublishApiController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping(value="/getCurrentEditableFields/{taskid}/{processType}", method= RequestMethod.GET)
    public List<String> getCurrentEditableField(@PathVariable int taskid, @PathVariable int processType){
        List<String> result =  taskDAO.getCurrentEditableFieldsByTaskId(taskid,processType);
        return result;

    }

    @RequestMapping(value="", method= RequestMethod.GET)
    public List<Map<String,Object>> getMissiveFileds(){
        return this.jdbcTemplate.queryForList("select * from users");
    }

    @RequestMapping(value="/users", method= RequestMethod.GET)
    public Iterable<User> getUsers(){
        return ur.findAll();
    }

    @RequestMapping(value="/getMissivePublishForm", method= RequestMethod.GET)
    public MissivePublishForm getMissive(Long id){
        MissivePublishForm missivePublishForm=new MissivePublishForm();
        MissivePublish missivePublish=mpDAO.findByProcessID(id);
        MissivePublishFunction mfFunction=new MissivePublishFunction();
        missivePublishForm=mfFunction.MissivePublishToMissivePublishForm(missivePublish);
        return missivePublishForm;
    }

    @RequestMapping(value="/test")
    @ResponseBody
    public String test()
    {
        Missive m = new Missive();
        m.setName("456");
        return this.mr.save(m).toString();
    }
}
