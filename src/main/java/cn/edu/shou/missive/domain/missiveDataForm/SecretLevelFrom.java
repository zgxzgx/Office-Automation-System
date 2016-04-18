package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;


/**
 * Created by sqhe on 14-7-7.
 */
public class SecretLevelFrom extends BaseEntityForm{
    public String secretLevelName;
    public List<MissiveFrom> missives;

    public String getSecretLevelName() {
        return secretLevelName;
    }

    public void setSecretLevelName(String secretLevelName) {
        this.secretLevelName = secretLevelName;
    }

    public List<MissiveFrom> getMissives() {
        return missives;
    }

    public void setMissives(List<MissiveFrom> missives) {
        this.missives = missives;
    }
}
