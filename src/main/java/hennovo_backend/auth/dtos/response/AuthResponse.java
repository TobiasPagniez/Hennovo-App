package hennovo_backend.auth.dtos.response;

public record AuthResponse(

        String token,
        UserResponse usuario

) {
}