package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Group;
import cn.edu.shou.missive.domain.ProcessType;
import cn.edu.shou.missive.domain.SecretLevel;
import cn.edu.shou.missive.domain.TaskName;
import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
/**
 * Created by Administrator on 2014/7/31.
 */

public interface SecretLevelRespository extends PagingAndSortingRepository<SecretLevel,Long> {

    //public Group findByGroupName(String group);
    public List<SecretLevel> findAll();
    public SecretLevel findBySecretLevelName(String secretLevelName);

}
