package personal.filters;

import personal.litespring.annotation.Autowired;
import personal.litespring.annotation.Component;
import personal.litespring.context.UserContext;
import personal.service.CustomSessionService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthenticationFilter implements Filter {

    @Autowired
    private CustomSessionService customSessionService;

    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Init AuthenticationFilter");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain)
            throws IOException, ServletException {

        System.out.println("Do AuthenticationFilter");

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        customSessionService.getSession(request);

        chain.doFilter(servletRequest, servletResponse);

        UserContext.clear();
    }

    public void destroy() {
        System.out.println("Cleanup AuthenticationFilter");
    }
}