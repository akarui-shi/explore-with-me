package ru.practicum.ewm.model.compilation.dto;

import lombok.Data;
import javax.validation.constraints.Size;
import java.util.List;

import static ru.practicum.ewm.model.compilation.dto.NewCompilationDto.*;

@Data
public class UpdateCompilationDto {
    private List<Long> events;

    private Boolean pinned;

    @Size(min = COMPLICATION_TITLE_MIN_LENGTH, max = COMPLICATION_TITLE_MAX_LENGTH, message = COMPLICATION_TITLE_LENGTH_ERROR_MESSAGE)
    private String title;
}
