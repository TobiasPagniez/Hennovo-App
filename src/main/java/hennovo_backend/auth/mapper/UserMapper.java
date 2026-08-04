package hennovo_backend.auth.mapper;

import org.springframework.stereotype.Component;

import hennovo_backend.auth.dtos.request.CreateUserRequest;
import hennovo_backend.auth.dtos.request.UpdateUserRequest;
import hennovo_backend.auth.dtos.response.AuthResponse;
import hennovo_backend.auth.dtos.response.UserResponse;
import hennovo_backend.auth.entitys.Rol;
import hennovo_backend.auth.entitys.Usuario;

@Component
public class UserMapper {

    public Usuario toEntity(CreateUserRequest request, Rol rol) {

        Usuario usuario = new Usuario();

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());
        usuario.setRol(rol);

        return usuario;
    }

    public UserResponse toResponse(Usuario usuario) {

        return new UserResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.getActivo()
        );
    }

    public AuthResponse toAuthResponse(
            Usuario usuario,
            String token) {

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol().getNombre()
        );
    }

    public void updateEntity(
            Usuario usuario,
            UpdateUserRequest request) {

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
    }
}