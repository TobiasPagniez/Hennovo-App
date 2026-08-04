package hennovo_backend.auth.services.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hennovo_backend.auth.dtos.request.CreateUserRequest;
import hennovo_backend.auth.dtos.request.UpdateUserRequest;
import hennovo_backend.auth.dtos.response.UserResponse;
import hennovo_backend.auth.entitys.Rol;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.mapper.UserMapper;
import hennovo_backend.auth.repositorys.RolRepository;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.auth.services.interfaces.UserService;
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
            throw new RuntimeException("El email ya se encuentra registrado.");
        }

        Rol rol = rolRepository.findByNombre(request.rol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado."));

        Usuario usuario = userMapper.toEntity(request, rol);

        usuario.setPassword(passwordEncoder.encode(request.password()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return userMapper.toResponse(usuarioGuardado);
    }
    
    private Usuario findUserById(Long id) {

    return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
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
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        Usuario usuario = findUserById(id);

        userMapper.updateEntity(usuario, request);

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return userMapper.toResponse(usuarioActualizado);
    }

    @Override
    public void deactivateUser(Long id) {

        Usuario usuario = findUserById(id);
        
        if (!usuario.getActivo()) {
            throw new RuntimeException("El usuario ya se encuentra desactivado.");
        }        

        usuario.setActivo(false);

        usuarioRepository.save(usuario);
    }

}