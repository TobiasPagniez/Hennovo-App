package hennovo_backend.auth.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import hennovo_backend.auth.dtos.request.ChangePasswordRequest;
import hennovo_backend.auth.dtos.request.LoginRequest;
import hennovo_backend.auth.dtos.response.AuthResponse;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.mapper.UserMapper;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import hennovo_backend.auth.services.interfaces.AuthService;
import hennovo_backend.shared.exception.NotFoundException;
import hennovo_backend.shared.exception.UnauthorizedException;
import hennovo_backend.shared.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse authenticate(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Usuario usuario = usuarioRepository
                .findByEmail(request.email())
                .orElseThrow();

        String token = jwtService.generateToken(userDetails);

        return userMapper.toAuthResponse(usuario, token);
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
}
