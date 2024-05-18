package ru.practicum.ewm.model.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {

    public static final String USER_EMAIL_EMPTY_ERROR_MESSAGE = "User email can't be empty";
    public static final String USER_EMAIL_CORRECTNESS_ERROR_MESSAGE = "Email is incorrect";

    public static final String USER_NAME_EMPTY_ERROR_MESSAGE = "Name can't be empty";
    public static final int USER_NAME_MIN_LENGTH = 2;
    public static final int USER_NAME_MAX_LENGTH = 250;
    public static final String USER_NAME_LENGTH_ERROR_MESSAGE =
            "Name length must be between " + USER_NAME_MIN_LENGTH + " and " + USER_NAME_MAX_LENGTH + " symbols";

    public static final int USER_EMAIL_MIN_LENGTH = 6;
    public static final int USER_EMAIL_MAX_LENGTH = 254;
    public static final String USER_EMAIL_LENGTH_ERROR_MESSAGE = "User e-mail length must be between "
            + USER_EMAIL_MIN_LENGTH + " and " + USER_EMAIL_MAX_LENGTH + ".";

    private Long id;

    @NotBlank(message = USER_EMAIL_EMPTY_ERROR_MESSAGE)
    @Email(message = USER_EMAIL_CORRECTNESS_ERROR_MESSAGE)
    @Size(min = USER_EMAIL_MIN_LENGTH, max = USER_EMAIL_MAX_LENGTH, message = USER_EMAIL_LENGTH_ERROR_MESSAGE)
    private String email;

    @NotBlank(message = USER_NAME_EMPTY_ERROR_MESSAGE)
    @Size(min = USER_NAME_MIN_LENGTH, max = USER_NAME_MAX_LENGTH, message = USER_NAME_LENGTH_ERROR_MESSAGE)
    private String name;
}
