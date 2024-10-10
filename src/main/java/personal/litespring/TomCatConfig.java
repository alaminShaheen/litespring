package personal.litespring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import personal.litespring.annotation.*;
import personal.litespring.utils.UrlMatcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TomCatConfig {
    private static final String contextPath = "";
    private static Tomcat tomcat;
    private static Context context;

    public TomCatConfig(int port) {
        initTomcat(port);
    }

    protected static void registerController(Object instance, String controllerUrlMapping) throws Exception {
        Class<?> clazz = instance.getClass();
        tomcat.addServlet(contextPath, clazz.getSimpleName(), new HttpServlet() {
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                try {
                    System.out.println("posting request");
                    handleControllerPostRequest(req, resp, instance, controllerUrlMapping);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                try {
                    System.out.println("getting request");
                    handleControllerGetRequest(req, resp, instance, controllerUrlMapping);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
        });
        String generalizedUrl = controllerUrlMapping;
        if (!generalizedUrl.endsWith("*")) {
            generalizedUrl = Paths.get(generalizedUrl, "*").toString();
        }
        System.out.println("Base path " + generalizedUrl + " registered to " + clazz.getSimpleName());
        context.addServletMappingDecoded(generalizedUrl, clazz.getSimpleName());
    }

    private static void handleControllerPostRequest(HttpServletRequest req, HttpServletResponse resp, Object instance, String controllerUrlMapping) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping methodUrlMapping = method.getAnnotation(PostMapping.class);
                Path properPath = Paths.get(controllerUrlMapping, methodUrlMapping.value());
                boolean urlsMatch = UrlMatcher.matchUrl(properPath.toString(), req.getRequestURI());
                if (urlsMatch) {
                    handleMethodAnnotations(req, resp, instance, method, properPath.toString());
                }
            }
        }
    }

    private static void handleControllerGetRequest(
            HttpServletRequest req, HttpServletResponse resp, Object instance, String controllerUrlMapping) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping methodUrlMapping = method.getAnnotation(GetMapping.class);
                Path properPath = Paths.get(controllerUrlMapping, methodUrlMapping.value());
                boolean urlsMatch = UrlMatcher.matchUrl(properPath.toString(), req.getRequestURI());
                if (urlsMatch) {
                    handleMethodAnnotations(req, resp, instance, method, properPath.toString());
                }
            }
        }
    }

    private static void handleMethodAnnotations(HttpServletRequest req, HttpServletResponse resp, Object classInstance, Method method, String urlMapping) throws Exception {
        Class<?> clazz = classInstance.getClass();
        // Extract path variables and request params
        Object[] args = resolveMethodArguments(req, method, urlMapping);
        // Call the method
        Object returnValue = method.invoke(classInstance, args);

        // Convert the return object to JSON and send the response if annotated with @ResponseBody
        if (method.isAnnotationPresent(ResponseBody.class) || clazz.isAnnotationPresent(RestController.class)) {
            String jsonResponse = new ObjectMapper().writeValueAsString(returnValue);
            resp.setContentType("application/json");
            resp.getWriter().write(jsonResponse);
        }
    }

    private static Object[] resolveMethodArguments(
            HttpServletRequest req, Method method, String urlMapping) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        String path = req.getRequestURI().replace(req.getContextPath(), "");
        String[] urlParts = urlMapping.split("/");
        String[] pathParts = path.split("/");

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (parameter.isAnnotationPresent(PathVariable.class)) {
                // Extract path variable
                PathVariable pathVar = parameter.getAnnotation(PathVariable.class);
                String varName = pathVar.value();
                args[i] = extractPathVariable(varName, urlParts, pathParts);
            }

            if (parameter.isAnnotationPresent(RequestParam.class)) {
                // Extract request param
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramName = requestParam.value();
                args[i] = req.getParameter(paramName) == null ? "" : req.getParameter(paramName);
            }

            if (parameter.isAnnotationPresent(RequestBody.class)) {
                // Extract request body
                Object requestBody = new ObjectMapper().readValue(req.getReader(), parameter.getType());
                args[i] = requestBody;
            }
        }
        return args;
    }

    private static String extractPathVariable(String varName, String[] urlParts, String[] pathParts) {
        // Logic to map the path variable from the URL pattern to the actual pat
        for (int j = 0; j < urlParts.length; j++) {
            if (urlParts[j].equals("{" + varName + "}")) {
                return pathParts[j];
            }
        }
        return null;
    }

    private void initTomcat(int port) {
        try {
            tomcat = new Tomcat();
            tomcat.setPort(port);
            // Ensures that the default HTTP connector (which binds Tomcat to a port and listens for HTTP requests) is created and initialized
            tomcat.getConnector();

            // Create a host
            Host host = tomcat.getHost();
            host.setName("localhost");
            host.setAppBase("webapps");

            tomcat.start();
            // get absolute path of current file
            String docBase = new File(".").getAbsolutePath();

            // contextPath is the URL path where the application is accessed (e.g., "/app")
            // docBase is the directory that contains the application's static files and resources.
            context = tomcat.addContext(contextPath, docBase);
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }
}