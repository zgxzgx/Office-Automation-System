package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Tester;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sqhe on 14-8-10.
 */
@Repository
public interface TesterRepository extends PagingAndSortingRepository<Tester, Long> {
}
