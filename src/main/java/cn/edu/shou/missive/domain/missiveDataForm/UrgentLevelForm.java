package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;

/**
 * Created by sqhe18 on 14-9-4.
 */
public class UrgentLevelForm extends BaseEntityForm {
    public String value;
    public List<MissiveFrom> missives;
    public boolean isDel;
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isDel() {
        return isDel;
    }

    public void setDel(boolean isDel) {
        this.isDel = isDel;
    }
}
