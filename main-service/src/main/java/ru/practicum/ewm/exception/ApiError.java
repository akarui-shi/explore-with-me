package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ApiError {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String errors;

    private String message;

    private String reason;

    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime timestamp;

}
