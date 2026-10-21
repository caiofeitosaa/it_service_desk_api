package it_service_desk_api.repositories;

import it_service_desk_api.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository <Users, Long> {

} // end of interface body