package cn.edu.shou.missive.service;
import cn.edu.shou.missive.domain.UploadFlag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
/*import org.springframework.data.rest.core.annotation.RepositoryRestResource;*/
import java.util.List;

/**
 * Created by TISSOT on 2014/7/13.
 */
public interface UploadFlagRepository extends CrudRepository <UploadFlag,Long> {
    @Query("select a from UploadFlag a where a.user = :userName and  a.isDel=:isDelor")
    public List<UploadFlag> findByUser(@Param("userName") String userName, @Param("isDelor") String isDelor);

    public UploadFlag findByContent(String content);

    @Query("select a from UploadFlag a where  a.content=:content and a.user = :userName  ")
    public UploadFlag findByContentandUser(@Param("content") String content, @Param("userName") String userName);


    @Query("select a from UploadFlag a where  a.instanceId=:instanceId")
    public List<UploadFlag> findListByPlace(@Param("instanceId") String instanceId);

}
