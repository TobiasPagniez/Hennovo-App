package hennovo_backend.auth.services.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hennovo_backend.auth.dtos.request.ChangePasswordRequest;
import hennovo_backend.auth.dtos.request.CreateUserRequest;
import hennovo_backend.auth.dtos.request.UpdateUserRequest;
import hennovo_backend.auth.dtos.response.UserResponse;
import hennovo_backend.auth.entitys.NombreRol;
import hennovo_backend.auth.entitys.Rol;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.mapper.UserMapper;
import hennovo_backend.auth.repositorys.RolRepository;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.auth.services.interfaces.UserService;
import hennovo_backend.shared.exception.ConflictException;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final UserMapper userMapper;
        private final PasswordEncoder passwordEncoder;

        @Override
        public UserResponse createUser(CreateUserRequest request) {

                if (usuarioRepository.existsByEmail(request.email())) {
                        throw new ConflictException("El email ya está registrado");
                }

                Rol rolEmpleado = rolRepository
                                .findByNombre(NombreRol.EMPLEADO)
                                .orElseThrow(() -> new NotFoundException("El rol EMPLEADO no existe"));

                Usuario usuario = userMapper.toEntity(
                                request,
                                rolEmpleado);

                usuario.setPassword(
                                passwordEncoder.encode(request.password()));

                usuario = usuarioRepository.save(usuario);

                return userMapper.toResponse(usuario);
        }

        @Override
        public UserResponse getUserById(Long id) {

                Usuario usuario = findUserById(id);

                return userMapper.toResponse(usuario);
        }

        @Override
        public List<UserResponse> getAllUsers() {

                return usuarioRepository.findAll()
                                .stream()
                                .map(userMapper::toResponse)
                                .toList();
        }

        @Override
        public List<UserResponse> getEmpleadosActivos() {

                return usuarioRepository.findAll()
                                .stream()
                                .filter(u -> u.getActivo() && u.getRol().getNombre() == NombreRol.EMPLEADO)
                                .map(userMapper::toResponse)
                                .toList();
        }

        @Override
        public UserResponse updateUser(
                        Long id,
                        UpdateUserRequest request) {

                Usuario usuario = findUserById(id);

                userMapper.updateEntity(usuario, request);

                Usuario usuarioActualizado = usuarioRepository.save(usuario);

                return userMapper.toResponse(usuarioActualizado);
        }

        @Override
        public void deactivateUser(Long id) {

                Usuario usuario = findUserById(id);

                if (!usuario.getActivo()) {
                        throw new ConflictException(
                                        "El usuario ya se encuentra desactivado.");
                }

                usuario.setActivo(false);

                usuarioRepository.save(usuario);
        }

        @Override
        public void changePassword(ChangePasswordRequest request) {

            Usuario usuario = obtenerUsuarioAutenticado();

            if (!passwordEncoder.matches(request.currentPassword(), usuario.getPassword())) {
                throw new UnauthorizedException("La contraseña actual es incorrecta");
            }

            usuario.setPassword(passwordEncoder.encode(request.newPassword()));

            usuarioRepository.save(usuario);
        }

        private Usuario obtenerUsuarioAutenticado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario autenticado no encontrado"));
}

        private Usuario findUserById(Long id) {

                return usuarioRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException(
                                                "Usuario no encontrado."));
        }
}
