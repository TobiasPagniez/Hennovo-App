package hennovo_backend.shared.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hennovo_backend.auth.entitys.NombreRol;
import hennovo_backend.auth.entitys.Rol;
import hennovo_backend.auth.entitys.Usuario;
import hennovo_backend.auth.repositorys.RolRepository;
import hennovo_backend.auth.repositorys.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Crear roles si todavía no existen
        for (NombreRol nombreRol : NombreRol.values()) {

            if (!rolRepository.existsByNombre(nombreRol)) {

                Rol rol = new Rol();
                rol.setNombre(nombreRol);

                rolRepository.save(rol);
            }
        }

        // Crear administrador inicial
        if (!usuarioRepository.existsByEmail("admin@hennovo.com")) {

            Rol rolAdmin = rolRepository
                    .findByNombre(NombreRol.ADMIN)
                    .orElseThrow(() ->
                            new RuntimeException("No existe el rol ADMIN"));

            Usuario admin = new Usuario();

            admin.setNombre("Administrador");
            admin.setApellido("Hennovo");
            admin.setEmail("admin@hennovo.com");

            admin.setPassword(
                    passwordEncoder.encode("Admin1234")
            );

            admin.setActivo(true);
            admin.setRol(rolAdmin);

            usuarioRepository.save(admin);

            System.out.println("=================================");
            System.out.println("ADMIN INICIAL CREADO");
            System.out.println("Email: admin@hennovo.com");
            System.out.println("Password: Admin1234");
            System.out.println("=================================");
        }
    }
}
