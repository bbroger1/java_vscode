package com.avaliacao.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSRFFilter implements Filter {

    private static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
    private static final String CSRF_TOKEN_PARAM = "csrfToken";
    private static final Logger LOG = Logger.getLogger(CSRFFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("CSRFFilter inicializado");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        String sessionToken = (session != null) ? (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR) : null;

        if (sessionToken == null && session != null) {
            sessionToken = gerarToken();
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, sessionToken);
            LOG.fine("Novo token CSRF gerado para sessão: " + session.getId());
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String requestToken = req.getParameter(CSRF_TOKEN_PARAM);
            
            // JSF prefixa com formId:csrfToken, procurar em todos os parâmetros
            if (requestToken == null) {
                Enumeration<String> paramNames = req.getParameterNames();
                while (paramNames != null && paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (paramName.endsWith(":csrfToken")) {
                        requestToken = req.getParameter(paramName);
                        LOG.fine("Token CSRF encontrado no parâmetro: " + paramName);
                        break;
                    }
                }
            }
            
            if (requestToken == null) {
                LOG.warning("Token CSRF não encontrado na requisição POST para: " + req.getRequestURI());
                logAllParameters(req);
            }
            
            if (requestToken == null || (sessionToken != null && !sessionToken.equals(requestToken))) {
                LOG.warning("Token CSRF inválido. Session: " + sessionToken + ", Request: " + requestToken);
                res.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Token CSRF invalido.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void logAllParameters(HttpServletRequest req) {
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = req.getParameter(paramName);
            LOG.fine("  Param: " + paramName + " = " + paramValue);
        }
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
