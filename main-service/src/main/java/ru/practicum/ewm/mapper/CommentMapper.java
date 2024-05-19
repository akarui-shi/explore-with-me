package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.model.comment.Comment;
import ru.practicum.ewm.model.comment.dto.CommentDto;
import ru.practicum.ewm.model.comment.dto.NewCommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto toDto(Comment comment);

    Comment fromDto(NewCommentDto newCommentDto);
}
