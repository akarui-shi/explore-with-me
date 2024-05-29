package ru.practicum.ewm.model.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {

    public static final String COMMENT_TEXT_EMPTY_ERROR_MESSAGE = "Comment text cannot be empty";
    public static final int COMMENT_TEXT_MIN_LENGTH = 1;
    public static final int COMMENT_TEXT_MAX_LENGTH = 2000;
    public static final String COMMENT_TEXT_LENGTH_ERROR_MESSAGE =
            "Comment text must be between" + COMMENT_TEXT_MIN_LENGTH + " and " + COMMENT_TEXT_MAX_LENGTH + " characters long.";

    @NotBlank(message = COMMENT_TEXT_EMPTY_ERROR_MESSAGE)
    @Size(min = COMMENT_TEXT_MIN_LENGTH, max = COMMENT_TEXT_MAX_LENGTH, message = COMMENT_TEXT_LENGTH_ERROR_MESSAGE)
    private String text;
}
