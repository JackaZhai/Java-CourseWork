package com.example.employeesystem.common;

import java.util.List;

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
        return (total + size - 1) / size;
    }
}
