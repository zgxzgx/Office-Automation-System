package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Attachment;
import cn.edu.shou.missive.domain.Authorities;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by sqhe on 14-8-13.
 */
public interface AuthoritiesRepository extends PagingAndSortingRepository<Authorities,Long> {
    @Query(value = "select t from Authorities t where t.user.id in ?1")
    public List<Authorities> getAuthoritiesListByuserID(Long userId);
}
