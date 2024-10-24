package personal.service;

import personal.litespring.annotation.Autowired;
import personal.litespring.annotation.Component;
import personal.litespring.context.UserContext;
import personal.models.SessionData;
import personal.models.User;
import personal.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomSessionService {
    private Map<String, SessionData> sessionMap = new HashMap<>();
    private static final String SESSION_ID_NAME = "CUSTOM_SESSION";

    @Autowired
    private UserRepository userRepository;

    public SessionData createSession(String sessionId, String username) {
        if (sessionMap.containsKey(sessionId)) return null;

        SessionData sessionData = SessionData.builder().id(sessionId).username(username).createdTime(System.currentTimeMillis()).build();
        sessionMap.put(sessionId, sessionData);

        return sessionData;
    }

    public SessionData getSession(HttpServletRequest request) {
        String token = request.getParameter("token");
        if (sessionMap.containsKey(token)) {
            SessionData sessionData = sessionMap.get(token);
            User user = userRepository.getUser(sessionData.getUsername());
            UserContext.setUserContext(user);
            return sessionData;
        }
        return null;
    }

    public boolean removeSession(String sessionId) {
        return sessionMap.remove(sessionId) != null;
    }
}
