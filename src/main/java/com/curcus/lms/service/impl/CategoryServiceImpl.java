package com.curcus.lms.service.impl;

import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.model.entity.Category;
import com.curcus.lms.model.mapper.OthersMapper;
import com.curcus.lms.model.request.CategoryRequest;
import com.curcus.lms.model.response.CategoryResponse;
import com.curcus.lms.repository.AdminRepository;
import com.curcus.lms.repository.CategoryRepository;
import com.curcus.lms.service.CategorySevice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategorySevice {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OthersMapper othersMapper;
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Category findById(Long id) {
        try {
            return ((categoryRepository.findById(id)).orElse(null));
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        try {

            if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName()))
                throw new ApplicationException("CATEGORY ALREADY EXISTS");
            Category newCategory = Category.builder()
                    .categoryName(categoryRequest.getCategoryName())
                    .build();

            return othersMapper.toCategoryResponse(categoryRepository.save(newCategory));

        } catch (ApplicationException ex) {
            throw ex;
        }
    }

    @Override
    public List<Category> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }

}
