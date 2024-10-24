package personal.controller;


import personal.litespring.annotation.*;
import personal.litespring.context.UserContext;
import personal.models.SessionData;
import personal.models.User;
import personal.models.dto.DeleteResponse;
import personal.models.dto.LoginResponse;
import personal.models.dto.RegisterResponse;
import personal.service.AuthService;
import personal.service.CustomSessionService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Component
@RestController
@RequestMapping(basePath = "/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private CustomSessionService customSessionService;

    @GetMapping(basePath = "/register")
    @ResponseBody
//    public RegisterResponse register(@RequestBody RegisterRequest request) {
    public RegisterResponse register(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        User user = User.builder()
                .name(username)
                .username(username.toLowerCase())
                .password(password)
                .roles(List.of("ADMIN"))
                .build();
        boolean isSuccess = authService.register(user);
        if (isSuccess) {
            return RegisterResponse.builder()
                    .user(user)
                    .build();
        }
        return null;
    }

    @GetMapping(basePath = "/login")
    @ResponseBody
    public LoginResponse login(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, HttpServletResponse servletResponse) {
        User user = authService.signIn(username, password);
        if (user != null) {
            String sessionId = UUID.randomUUID().toString();
            SessionData sessionData = customSessionService.createSession(sessionId, username);
            return LoginResponse.builder().sessionId(sessionData.getId()).build();
        }
        return null;
    }

    @Authenticated(roles = {"SUPER_ADMIN"})
    @GetMapping(basePath = "/self")
    public User user() {
        if (UserContext.getUserContext() != null) {
            return authService.getUser(UserContext.getUserContext().getUsername());
        }
        return null;
    }

    @Authenticated(roles = {"SUPER_ADMIN"})
    @GetMapping(basePath = "/self/delete")
    public DeleteResponse selfDelete() {
        if (UserContext.getUserContext() != null) {
            boolean success = authService.deleteUser(UserContext.getUserContext().getUsername());
            return DeleteResponse.builder().success(success).build();
        }
        return DeleteResponse.builder().success(false).build();
    }
}
