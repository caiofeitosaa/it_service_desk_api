package it_service_desk_api.dtos.tickets_dtos.responses;

import it_service_desk_api.entities.Tickets;
import it_service_desk_api.entities.enums.TicketPriority;
import it_service_desk_api.entities.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse (
        Long id,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        Long requesterId,
        Long technicianId,
        LocalDateTime createdDateTime,
        LocalDateTime closedDateTime
) {
    public static TicketResponse response (Tickets ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getRequester().getId(),
                ticket.getTechnician() != null ? ticket.getTechnician().getId() : null,
                ticket.getCreatedDateTime(),
                ticket.getClosedDateTime()
        );
    } // end of response method
} // end of record body
