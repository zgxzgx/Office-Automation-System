package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.Attachment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/8/2.
 */
public interface AttachmentRepository extends PagingAndSortingRepository<Attachment,Long>{
    public List<Attachment> findAll();
    public Attachment findByAttachmentTittle(String attachmentTittle);
    @Query("select attach from Attachment attach where attach.id=:id")
    public List<Attachment>getAttachmentByID(@Param("id")Long ID);//根据ID查找
    @Query("select attach.attachmentTittle from Attachment attach where attach.missiveVersion.id=:id")
    public ArrayList<String>getAttachmentByVersionId(@Param("id")Long ID);
}
