package personal.litespring;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import java.io.File;

public class TomCatConfig {
    private static final String contextPath = "";
    private static Tomcat tomcat;
    private static Context context;

    public TomCatConfig(int port) {
        initTomcat(port);
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

    protected void registerServlet(Object instance, String className, String urlMapping) {
        tomcat.addServlet(contextPath, className, (HttpServlet) instance);
        context.addServletMappingDecoded(urlMapping, className);
    }
}