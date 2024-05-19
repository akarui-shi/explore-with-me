package ru.practicum.ewm.service.comment.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotAuthorizedException;
import ru.practicum.ewm.exception.NotFoundDataException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.dto.CommentDto;
import ru.practicum.ewm.model.comment.dto.NewCommentDto;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.comment.CommentService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addCommentToEvent(Long userId, Long eventId, NewCommentDto newCommentDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id " + eventId + " not found"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));

        Comment comment = commentMapper.fromDto(newCommentDto);
        comment.setEvent(event);
        comment.setAuthor(user);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved: {}", savedComment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto updateCommentDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id " + eventId + " not found"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundDataException("Comment with id " + commentId + " not found"));

        if (comment.getAuthor().getId() != userId) {
            throw new NotAuthorizedException("User with id '" + userId + "' is not author of comment with id '" +
                    comment.getId() + "'.");
        }

        comment.setText(updateCommentDto.getText());
        comment.setEvent(event);
        comment.setAuthor(user);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved: {}", savedComment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundDataException("Event with id " + eventId + " not found"));
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundDataException("User with id " + userId + " not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundDataException("Comment with id " + commentId + " not found"));

        if (comment.getAuthor().getId() != userId) {
            throw new NotAuthorizedException("User with id '" + userId + "' is not author of comment with id '" +
                    comment.getId() + "'.");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted: {}", comment);
    }
}
