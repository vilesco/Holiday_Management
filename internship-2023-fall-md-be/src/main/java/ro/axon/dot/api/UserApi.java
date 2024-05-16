package ro.axon.dot.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.axon.dot.model.request.LoginRequest;
import ro.axon.dot.model.request.RefreshTokenRequest;
import ro.axon.dot.model.response.TokenResponse;
import ro.axon.dot.model.response.UserDetailsResponse;
import ro.axon.dot.service.UserService;

import javax.validation.Valid;

import static ro.axon.dot.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_URL)
public class UserApi {

    private final UserService userService;

    @GetMapping(USER)
    public ResponseEntity<UserDetailsResponse> getLoggedUserDetails() {
        return ResponseEntity.ok(userService.getLoggedUserDetails());
    }

    @PostMapping(REFRESH)
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(userService.refreshAccessToken(refreshTokenRequest));
    }

    @PostMapping(LOGOUT)
    public ResponseEntity<Void> logoutUser(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        userService.logoutUser(refreshTokenRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(LOGIN)
    public ResponseEntity<TokenResponse> loginResponse(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(this.userService.login(loginRequest));
    }

}
