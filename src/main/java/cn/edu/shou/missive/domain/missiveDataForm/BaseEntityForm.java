package cn.edu.shou.missive.domain.missiveDataForm;

/**
 * Created by sqhe18 on 14-8-17.
 */
public class BaseEntityForm {
    public long id;
    public String createdDate = null;
    public String lastModifiedDate = null;
    public UserFrom createdBy;
    public UserFrom lastModifiedBy;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public UserFrom getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserFrom createdBy) {
        this.createdBy = createdBy;
    }

    public UserFrom getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UserFrom lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
