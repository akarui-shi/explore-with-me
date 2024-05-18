package ru.practicum.ewm.model.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.ewm.model.user.dto.UserDto.*;

@Data
public class NewUserDto {

    @NotBlank(message = USER_EMAIL_EMPTY_ERROR_MESSAGE)
    @Email(message = USER_EMAIL_CORRECTNESS_ERROR_MESSAGE)
    @Size(min = USER_EMAIL_MIN_LENGTH, max = USER_EMAIL_MAX_LENGTH, message = USER_EMAIL_LENGTH_ERROR_MESSAGE)
    private String email;

    @NotBlank(message = USER_NAME_EMPTY_ERROR_MESSAGE)
    @Size(min = USER_NAME_MIN_LENGTH, max = USER_NAME_MAX_LENGTH, message = USER_NAME_LENGTH_ERROR_MESSAGE)
    private String name;
}
