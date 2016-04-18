package cn.edu.shou.missive.domain;

import javax.persistence.Entity;
import java.util.Date;
import java.util.List;

/**
 * Created by sqhe on 14-8-7.
 */

public class SignFrom {

    private Long id;
    private Group   primaryHandlGroup;
    private User    createUser;
    private Date    createDate;
    private UrgentLevel urgentLevel;
    private List<CommentContent> groupCommentList;
    private List<CommentContent> officeCommentList;
    private List<CommentContent> leaderCommentList;
    private List<CommentContent> signGroupCommentList;
    private Missive missive;


}
