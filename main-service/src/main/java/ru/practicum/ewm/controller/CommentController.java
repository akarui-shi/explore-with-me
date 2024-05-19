package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.dto.CommentDto;
import ru.practicum.ewm.model.comment.dto.NewCommentDto;
import ru.practicum.ewm.service.comment.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addCommentToEvent(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId,
                                        @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("POST /events/{}/comment with body {}", eventId, newCommentDto);
        return commentService.addCommentToEvent(userId, eventId, newCommentDto);
    }

    @PatchMapping("users/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId,
                                    @RequestBody @Valid NewCommentDto updateCommentDto) {
        log.info("PATCH users/{}/events/{}/comments/{} with body {}",
                userId, eventId, commentId, updateCommentDto);
        return commentService.updateComment(userId, eventId, commentId, updateCommentDto);
    }

    @DeleteMapping("users/{userId}/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.info("DELETE users/{}/events/{}/comments/{}", userId, eventId, commentId);
        commentService.deleteComment(userId, eventId, commentId);
    }
}
