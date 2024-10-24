package personal.filters;


import personal.litespring.annotation.Component;

import javax.servlet.*;
import java.io.IOException;

@Component
public class LoggingFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init LoggingFilter");
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Inside LoggingFilter");
        System.out.println("Request received from: " + request.getRemoteAddr());

        chain.doFilter(request, response);  // Continue to next filter or servlet

        System.out.println("Response sent.");
    }
}