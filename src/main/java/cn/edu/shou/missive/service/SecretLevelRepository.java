package cn.edu.shou.missive.service;


import cn.edu.shou.missive.domain.SecretLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by XQQ on 2014/9/8.
 */
public interface SecretLevelRepository extends PagingAndSortingRepository<SecretLevel, Long> {
    public List<SecretLevel> findAll();
    @Query("select secretLv.id,secretLv.secretLevelName from SecretLevel secretLv")
    public List<SecretLevel>getAllSecretLv();

    @Query("select secretLv from SecretLevel secretLv where secretLv.id=:id")
    public List<SecretLevel> getSecretLvByID(@Param("id")Long ID);
}
