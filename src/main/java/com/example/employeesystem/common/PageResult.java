package com.example.employeesystem.common;

import java.util.List;

/**
 * 封装分页查询结果，包含数据集合及分页元信息。
 */
public class PageResult<T> {
    private List<T> records;
    private long total;
    private int page;
    private int size;

    public PageResult(List<T> records, long total, int page, int size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<T> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalPages() {
        // 通过向上取整的方式计算总页数
        return (total + size - 1) / size;
    }
}
