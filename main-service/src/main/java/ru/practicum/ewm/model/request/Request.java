package ru.practicum.ewm.model.request;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "requests")
public class Request {
    public enum State {
        PENDING,
        CONFIRMED,
        REJECTED,
        CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "requester_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User requester;

    @JoinColumn(name = "event_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;
}
