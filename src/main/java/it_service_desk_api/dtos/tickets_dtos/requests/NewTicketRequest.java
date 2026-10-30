package it_service_desk_api.dtos.tickets_dtos.requests;

import it_service_desk_api.entities.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewTicketRequest (
        @NotBlank (message = "Please enter a title")
        String title,

        @NotBlank (message = "Please enter a description")
        String description,

        @NotNull (message = "Please choose a priority")
        TicketPriority priority
) {
    // The requester, status, and technician are managed by the server, not supplied by the client.
}
