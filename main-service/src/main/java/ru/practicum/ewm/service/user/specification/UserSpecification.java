package ru.practicum.ewm.service.user.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.model.user.User;

import java.util.List;

@UtilityClass
public class UserSpecification {

    public static Specification<User> idIn(List<Long> ids) {
        if (ids == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("id")).value(ids);
    }
}
