package social_media_platform.filter;

import social_media_platform.dto.account.AccountDto;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AdminFilter", urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * Checks if the request is sent from an administrator
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain a filterchain
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

            AccountDto account = (AccountDto) ses.getAttribute("account");

            if (account == null) {
                res.sendRedirect(req.getContextPath() + "/login");
            } else if (account.getId() == 1) {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(req.getContextPath() + "/forbidden");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {}
}
