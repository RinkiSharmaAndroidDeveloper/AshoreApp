package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by Rinki on 1/19/2017.
 */
public class CategoryMapper implements IModelMapper<Category, CategoryModel> {
    @Override
    public CategoryModel Map(Category category) {
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId(category.getId());
        categoryModel.setServerId(category.getServerId());
        categoryModel.setProfileId(category.getProfileId());
        categoryModel.setName(category.getName());
        categoryModel.setSelected(true);
        return categoryModel;
    }
}
