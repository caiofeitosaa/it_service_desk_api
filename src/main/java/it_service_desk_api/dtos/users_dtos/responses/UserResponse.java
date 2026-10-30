package it_service_desk_api.dtos.users_dtos.responses;

import it_service_desk_api.entities.Users;
import it_service_desk_api.entities.enums.Roles;

public record UserResponse (Long id, String name, String email, Roles role) {

    public static UserResponse response (Users user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    } // end of response method

} // end of record body
