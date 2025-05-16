package roomescape.presentation.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.AuthService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.LoginRequest;
import roomescape.presentation.dto.response.MemberResponse;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        String accessToken = authService.login(loginRequest);
        String cookie = createCookieFromToken(accessToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .build();
    }

    private String createCookieFromToken(String accessToken) {
        return ResponseCookie.from("token", accessToken)
                .path("/")
                .httpOnly(true)
                .maxAge(60)
                .build()
                .toString();
    }

    @GetMapping("/login/check")
    public ResponseEntity<MemberResponse> checkLogin(@AuthenticationPrincipal LoginMember loginMember) {
        MemberResponse memberResponse = MemberResponse.from(loginMember);
        return ResponseEntity.ok().body(memberResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        String emptyCookie = createEmptyCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, emptyCookie)
                .build();
    }

    private String createEmptyCookie() {
        return ResponseCookie.from("token", null)
                .path("/")
                .maxAge(0)
                .build()
                .toString();
    }
}
