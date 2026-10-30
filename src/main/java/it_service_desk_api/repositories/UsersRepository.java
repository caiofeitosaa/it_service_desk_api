package it_service_desk_api.repositories;

import it_service_desk_api.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository <Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

} // end of interface body