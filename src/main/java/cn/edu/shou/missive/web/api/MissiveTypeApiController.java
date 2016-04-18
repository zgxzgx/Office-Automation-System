package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MissiveTypeFrom;
import cn.edu.shou.missive.service.MissiveRepository;
import cn.edu.shou.missive.service.MissiveTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/31.
 */
@RestController
@RequestMapping(value = "/api/missive/missivetype")
public class MissiveTypeApiController {
    MissivePublishFunction mpf=new MissivePublishFunction();
    @Autowired
    private MissiveTypeRepository mtDAO;

    @Autowired
    private MissiveRepository msDao;
    @RequestMapping(value="/getmissivetype", method= RequestMethod.GET)
    public List<MissiveTypeFrom> getMissiveType(){
        List<MissiveTypeFrom> mtfs=new ArrayList<MissiveTypeFrom>();
        List<MissiveType> mts=mtDAO.findAll();
        for(MissiveType mt:mts){
            MissiveTypeFrom mtf=mpf.missiveTypeToMissiveTypeFrom(mt);
            mtfs.add(mtf);
        }
        return mtfs;
    }

    @RequestMapping(value="/getmissivetypebytypename/{type}", method= RequestMethod.GET)
    public MissiveType getMissiveTypeby(@PathVariable String type){
        return mtDAO.findByTypeName(type);
    }

    @RequestMapping(value="/getmissive", method= RequestMethod.GET)
    public List<Missive> getMissiveTy(){
        return msDao.findAll();
    }


}
