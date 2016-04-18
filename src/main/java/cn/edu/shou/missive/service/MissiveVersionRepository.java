package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveVersion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2014/8/2.
 */
public interface MissiveVersionRepository extends PagingAndSortingRepository<MissiveVersion, Long> {
    public List<MissiveVersion> findAll();
    public MissiveVersion findById(Long id);
    @Query("select missVer from MissiveVersion missVer where missVer.id=:id")
    public List<MissiveVersion>getMissiveVersionByID(@Param("id")Long ID);
    //根据公文ID以及最新版本号，获取最新版本
    @Query("select missVer from MissiveVersion missVer where missVer.versionNumber=:num and missVer.missive.id=:misID")
    public MissiveVersion getVersionByLastVersionAndMissiveID(@Param("num")Long num,@Param("misID")Long misID);
    @Query("select missVer from MissiveVersion missVer where missVer.missive.id=:misID")
    public MissiveVersion getVersionByMissiveID(@Param("misID")Long misID);


}
