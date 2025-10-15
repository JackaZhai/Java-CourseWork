package com.example.employeesystem.model;

import com.example.employeesystem.annotation.Column;

public class OptionItem {
    @Column("id")
    private String id;

    @Column("name")
    private String name;

    @Column("category")
    private String category;

    @Column("value")
    private String value;

    @Column("remark")
    private String remark;

    public OptionItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
