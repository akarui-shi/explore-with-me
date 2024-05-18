package ru.practicum.ewm.model.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {

    private static final int CATEGORY_NAME_MIN_LENGTH = 1;
    private static final int CATEGORY_NAME_MAX_LENGTH = 50;

    private static final String CATEGORY_NAME_EMPTY_ERROR_MESSAGE = "Category name can't be empty";
    private static final String CATEGORY_NAME_LENGTH_ERROR_MESSAGE =
            "Category name must be between" + CATEGORY_NAME_MIN_LENGTH + " and " + CATEGORY_NAME_MAX_LENGTH + " symbols";

    @NotBlank(message = CATEGORY_NAME_EMPTY_ERROR_MESSAGE)
    @Size(min = CATEGORY_NAME_MIN_LENGTH, max = CATEGORY_NAME_MAX_LENGTH, message = CATEGORY_NAME_EMPTY_ERROR_MESSAGE)
    private String name;

}
