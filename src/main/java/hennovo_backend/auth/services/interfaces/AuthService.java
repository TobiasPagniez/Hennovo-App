package hennovo_backend.auth.services.interfaces;

import hennovo_backend.auth.dtos.request.ChangePasswordRequest;
import hennovo_backend.auth.dtos.request.LoginRequest;
import hennovo_backend.auth.dtos.response.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(LoginRequest request);

    void changePassword(ChangePasswordRequest request);

}