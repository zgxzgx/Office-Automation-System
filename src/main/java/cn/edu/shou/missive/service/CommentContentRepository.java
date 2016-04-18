package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.CommentContent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2014/8/2.
 */
public interface CommentContentRepository extends PagingAndSortingRepository<CommentContent,Long> {
    public List<CommentContent> findAll();
    public CommentContent findById(Long id);
    @Query("select comment from CommentContent comment where comment.id=:id")
    public List<CommentContent>getCommentContentByID(@Param("id")Long ID);//根据ID获取
}
