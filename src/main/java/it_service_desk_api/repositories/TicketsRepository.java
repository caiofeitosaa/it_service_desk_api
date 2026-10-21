package it_service_desk_api.repositories;

import it_service_desk_api.entities.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketsRepository extends JpaRepository <Tickets, Long> {

} // end of interface body