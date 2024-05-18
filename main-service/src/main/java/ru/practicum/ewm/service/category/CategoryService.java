package ru.practicum.ewm.service.category;

import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long catId);

    CategoryDto createCategory(NewCategoryDto category);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto category);
}
