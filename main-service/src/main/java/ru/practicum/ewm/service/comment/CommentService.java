package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.model.comment.dto.CommentDto;
import ru.practicum.ewm.model.comment.dto.NewCommentDto;

public interface CommentService {
    CommentDto addCommentToEvent(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto updateCommentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);
}
