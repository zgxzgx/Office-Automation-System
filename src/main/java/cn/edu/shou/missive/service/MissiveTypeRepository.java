package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveType;
import cn.edu.shou.missive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by sqhe on 14-7-20.
 */

public interface MissiveTypeRepository extends PagingAndSortingRepository<MissiveType, Long> {
    public List<MissiveType> findAll();
    public MissiveType findByTypeName(String typeName);
    @Query("select missive from MissiveType missive where  missive.id=:id ")
    List<MissiveType>getMissiveTypeByID(@Param("id")Long ID);
}

