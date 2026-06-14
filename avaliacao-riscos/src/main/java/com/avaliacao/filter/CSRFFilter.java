package com.avaliacao.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter(urlPatterns = "/protected/*")
public class CSRFFilter implements Filter {

    private static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
    private static final String CSRF_TOKEN_PARAM = "csrfToken";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);

        if (sessionToken == null) {
            sessionToken = gerarToken();
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, sessionToken);
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String requestToken = req.getParameter(CSRF_TOKEN_PARAM);
            if (requestToken == null || !sessionToken.equals(requestToken)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Token CSRF invalido.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String gerarToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void destroy() {
    }
}
