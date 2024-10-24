package personal.litespring;

import com.fasterxml.jackson.databind.ObjectMapper;
import personal.litespring.annotation.Authenticated;
import personal.litespring.annotation.PathVariable;
import personal.litespring.annotation.RequestBody;
import personal.litespring.annotation.RequestParam;
import personal.litespring.context.UserContext;
import personal.litespring.enums.MethodType;
import personal.litespring.models.ControllerMethod;
import personal.models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {
    private final List<ControllerMethod> controllerMethods;
    private final ObjectMapper objectMapper;

    public DispatcherServlet(List<ControllerMethod> controllerMethods) {
        this.controllerMethods = controllerMethods;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp, MethodType.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp, MethodType.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        dispatch(req, resp, MethodType.PUT);
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp, MethodType methodType) {
        try {
            String requestUrl = req.getRequestURI();
            System.out.println("requestUrl = " + requestUrl);


            for (ControllerMethod controllerMethod : controllerMethods) {
                if (controllerMethod.getMethodType() != methodType) continue;
                String mappedUrl = controllerMethod.getUrl();
                if (!PathExtractor.isMatchUrlPattern(mappedUrl, requestUrl)) continue;
                Map<String, String> pathVariable = PathExtractor.pathVariables(mappedUrl, requestUrl);
                String body = readRequestBody(req, methodType);

                Object responseObject = invokeMethod(req, resp, controllerMethod, pathVariable, body);

                resp.setContentType("application/json");
                resp.getWriter().write(objectMapper.writeValueAsString(responseObject));

                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object invokeMethod(
            HttpServletRequest req, HttpServletResponse res, ControllerMethod controllerMethod,
            Map<String, String> pathVariableMap, String body) {
        try {
            Method method = controllerMethod.getMethod();

            if (method.isAnnotationPresent(Authenticated.class)) {
                Authenticated authenticated = method.getAnnotation(Authenticated.class);
                boolean isVerifiedUser = verifyAuthentication(authenticated.roles());
                if (!isVerifiedUser) return null;
            }

            Parameter[] parameters = controllerMethod.getMethod().getParameters();
            Object[] paramObject = new Object[parameters.length];

            for(int i=0; i<parameters.length; i++) {
                if(parameters[i].isAnnotationPresent(PathVariable.class)) {
                    PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
                    paramObject[i] = pathVariableMap.get(pathVariable.value());
                }
                if(parameters[i].isAnnotationPresent(RequestBody.class)) {
                    paramObject[i] = objectMapper.readValue(body, parameters[i].getType());
                }
                if(parameters[i].isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                    paramObject[i] = req.getParameter(requestParam.value());
                }

                if (parameters[i].getType().equals(HttpServletResponse.class)) {
                    paramObject[i] = res;
                }

                if (parameters[i].getType().equals(HttpServletRequest.class)) {
                    paramObject[i] = req;
                }
            }

            return controllerMethod.getMethod().invoke(controllerMethod.getInstance(), paramObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean verifyAuthentication(String[] expectedRoles) {
        User user = UserContext.getUserContext();
        if (user != null) {
            for (String role : expectedRoles) {
                if (!user.getRoles().contains(role)) return false;
            }
        }
        return true;
    }

    private String readRequestBody(HttpServletRequest request, MethodType methodType) {
        if(methodType == MethodType.POST || methodType == MethodType.PUT) {
            try {
                BufferedReader reader = request.getReader();
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line); // Read body content
                }
                return body.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
