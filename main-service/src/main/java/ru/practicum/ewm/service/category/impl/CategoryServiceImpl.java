package ru.practicum.ewm.service.category.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundDataException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.category.dto.NewCategoryDto;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.category.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Category> categories = categoryRepository.findAll(pageRequest).getContent();

        log.info("get categories from {}, size {}: {}", from, size, categories);

        return categories.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundDataException("Category with id " + catId + " not found"));

        log.info("category with id {}: {}", catId, category);

        return mapper.toDto(category);
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto category) {
        Category newCategory = categoryRepository.save(mapper.fromDto(category));

        log.info("new category with id {}: {}", newCategory.getId(), newCategory);

        return mapper.toDto(newCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundDataException("Category with id " + catId + " not found"));

        categoryRepository.delete(category);

        log.info("deleted category with id {}: {}", catId, category);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto category) {
        Category categoryToUpdate = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundDataException("Category with id " + catId + " not found"));

        Category newCategory = mapper.fromDto(category);
        newCategory.setId(categoryToUpdate.getId());
        categoryRepository.save(newCategory);

        log.info("updated category with id {}: {}", catId, newCategory);

        return mapper.toDto(newCategory);
    }
}
