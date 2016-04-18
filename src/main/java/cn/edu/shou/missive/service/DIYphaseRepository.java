package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.DIYphase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hy on 2014/12/19.
 */
@Repository
public interface DIYphaseRepository extends PagingAndSortingRepository<DIYphase,Long> {


    public List<DIYphase> findAll();
    public DIYphase findOne(Long id);


    @Query(value = "select t from DIYphase t where t.user.id=?1")
    public List<DIYphase> findByUserId(Long id);

}
