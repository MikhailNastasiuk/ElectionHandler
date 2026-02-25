package pl.miken.electionhandler.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.miken.electionhandler.dto.request.UserRequest;
import pl.miken.electionhandler.dto.response.UserResponse;
import pl.miken.electionhandler.facade.UserFacade;

import java.util.List;

@Validated
@RestController
@Tag(name = "Users")
@RequestMapping(value = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @GetMapping("/all-users")
    @Operation(summary = "Return list of all active users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userFacade.getAllActiveUsers());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user data")
    public ResponseEntity<UserResponse> getUserData(@NotNull @PathVariable Long userId) {
        return ResponseEntity.ok(userFacade.getUserData(userId));
    }

    @PostMapping("/add")
    @Operation(summary = "Add new user")
    public ResponseEntity<UserResponse> addUser(
            @Valid
            @NotNull
            @RequestBody
            UserRequest userRequest
    ) {
        return ResponseEntity.ok(userFacade.createUser(userRequest));
    }

    @DeleteMapping("/deactivate/{userId}")
    @Operation(summary = "Deactivate user")
    public void deactivateUser(@NotNull @PathVariable Long userId) {
        userFacade.deactivateUser(userId);
    }

    @PatchMapping("/activate/{userId}")
    @Operation(summary = "Activate user")
    public void activate(@NotNull @PathVariable Long userId) {
        userFacade.activateUser(userId);
    }
}
