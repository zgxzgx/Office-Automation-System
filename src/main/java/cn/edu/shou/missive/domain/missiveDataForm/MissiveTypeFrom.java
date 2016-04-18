package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;

/**
 * Created by sqhe on 14-7-18.
 */
public class MissiveTypeFrom extends BaseEntityForm {
    public String typeName;
    public List<MissiveFrom> missives;
    public boolean isDeleted;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<MissiveFrom> getMissives() {
        return missives;
    }

    public void setMissives(List<MissiveFrom> missives) {
        this.missives = missives;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
