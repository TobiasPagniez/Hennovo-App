package hennovo_backend.auth.services.interfaces;

import java.util.List;

import hennovo_backend.auth.dtos.request.ChangePasswordRequest;
import hennovo_backend.auth.dtos.request.CreateUserRequest;
import hennovo_backend.auth.dtos.request.UpdateUserRequest;
import hennovo_backend.auth.dtos.response.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    List<UserResponse> getEmpleadosActivos();

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deactivateUser(Long id);

    void changePassword(ChangePasswordRequest request);
}
