package com.example.employeesystem.service;

import com.example.employeesystem.common.PageResult;
import com.example.employeesystem.dao.OptionItemDao;
import com.example.employeesystem.model.OptionItem;
import com.example.employeesystem.util.IdGenerator;

import java.util.List;
import java.util.Optional;

public class OptionItemService {
    private final OptionItemDao optionItemDao = new OptionItemDao();

    public PageResult<OptionItem> search(String name, String category, int page, int size) {
        return optionItemDao.search(name, category, page, size);
    }

    public Optional<OptionItem> findById(String id) {
        return optionItemDao.findById(id);
    }

    public void create(OptionItem option) {
        option.setId(IdGenerator.uuid());
        optionItemDao.insert(option);
    }

    public void update(OptionItem option) {
        optionItemDao.update(option);
    }

    public void delete(String id) {
        optionItemDao.delete(id);
    }

    public List<OptionItem> findByCategory(String category) {
        return optionItemDao.findByCategory(category);
    }
}
