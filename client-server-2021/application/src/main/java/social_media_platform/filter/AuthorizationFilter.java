package social_media_platform.filter;

import social_media_platform.utils.SessionUtils;

import javax.annotation.Priority;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/", "/profile", "/verify"})
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * Checks if the request is authorized
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain a filter chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            HttpSession ses = req.getSession();

            if (ses.getAttribute("account") == null) {
                res.sendRedirect(req.getContextPath() + "/login");
            } else {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {}
}
