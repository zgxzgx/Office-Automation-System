package cn.edu.shou.missive.domain;


import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import org.activiti.engine.history.HistoricTaskInstance;

import java.util.List;

/**
 * Created by hy on 2014/11/28.
 */
public class PageableHistoryTaskFilterList {
    private int taskTotal;
    private List<TaskForm> tasklist;
    private int currentPageNum;
    private int pageSize;
    private int pageTotal;

    public PageableHistoryTaskFilterList(int taskTotal, List<TaskForm> tasklist, int currentPageNum, int pageSize, int pageTotal) {
        this.taskTotal = taskTotal;
        this.tasklist = tasklist;
        this.currentPageNum = currentPageNum;
        this.pageSize = pageSize;
        this.pageTotal = pageTotal;
    }

    public int getTaskTotal() {
        return taskTotal;
    }

    public void setTaskTotal(int taskTotal) {
        this.taskTotal = taskTotal;
    }

    public List<TaskForm> getTasklist() {
        return tasklist;
    }

    public void setTasklist(List<TaskForm> tasklist) {
        this.tasklist = tasklist;
    }

    public int getCurrentPageNum() {
        return currentPageNum;
    }

    public void setCurrentPageNum(int currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }
}
