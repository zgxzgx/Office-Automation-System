package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Feedback;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Dezzi on 2014/9/8.
 */
@Repository
public interface FeedbackRepository extends PagingAndSortingRepository<Feedback, Long> {


    public List<Feedback> findAll();


}
