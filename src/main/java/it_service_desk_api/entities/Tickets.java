package it_service_desk_api.entities;

import it_service_desk_api.entities.enums.TicketPriority;
import it_service_desk_api.entities.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "tickets")
public class Tickets {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String title;

    @Column (nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private Users requester;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Users technician;

    @Column (nullable = false)
    private LocalDateTime createdDateTime;

    @Column
    private LocalDateTime closedDateTime;

    public Tickets(
            String title,
            String description,
            TicketPriority priority,
            TicketStatus status,
            Users requester,
            Users technician,
            LocalDateTime createdDateTime,
            LocalDateTime closedDateTime
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.requester = requester;
        this.technician = technician;
        this.createdDateTime = createdDateTime;
        this.closedDateTime = closedDateTime;
    } // end of constructor
} // end of class body
