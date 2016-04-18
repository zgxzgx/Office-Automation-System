package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.SecretLevelFrom;
import cn.edu.shou.missive.service.SecretLevelRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/31.
 */
@RestController
@RequestMapping(value = "/api/missive/secretlevel")
public class SecretLevelApiController {
    @Autowired
    private SecretLevelRespository slDAO;
    MissivePublishFunction msf=new MissivePublishFunction();
    @RequestMapping(value="/getsecretlevel", method= RequestMethod.GET)
    public List<SecretLevelFrom> getSecretlevel(){
        List<SecretLevelFrom> slfs=new ArrayList<SecretLevelFrom>();
        List<SecretLevel> sls=slDAO.findAll();
        for(SecretLevel sl:sls){
            SecretLevelFrom slf=msf.secretLevelToSecretLevelFrom(sl);
            slfs.add(slf);
        }
        return slfs;
    }
}
