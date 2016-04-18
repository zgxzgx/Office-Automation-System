package cn.edu.shou.missive.domain;

import org.activiti.engine.task.Task;

import java.util.List;

/**
 * Created by sqhe on 14/11/27.
 */
public class PageableTaskList {
    private int taskTotal;
    private List<Task> tasklist;
    private int currentPageNum;
    private int pageSize;
    private int pageTotal;

    public PageableTaskList(int taskTotal, List<Task> tasklist, int currentPageNum, int pageSize, int pageTotal) {
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

    public List<Task> getTasklist() {
        return tasklist;
    }

    public void setTasklist(List<Task> tasklist) {
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
