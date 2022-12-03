//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Servlet Filter implementation class LoginFilter
// */
//@WebFilter(filterName = "DashboardFilter", urlPatterns = "/_dashboard/*")
//public class DashboardFilter implements Filter {
//    private final ArrayList<String> allowedURIs = new ArrayList<>();
//
//    /**
//     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//     */
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        System.out.println("DashboardFilter: " + httpRequest.getRequestURI());
//
//        // Check if this URL is allowed to access without logging in
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//            // Keep default action: pass along the filter chain
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Redirect to login page if the "user" attribute doesn't exist in session
//        if (httpRequest.getSession().getAttribute("employee") == null) {
//            httpResponse.sendRedirect("/cs122b-fall22-team-46/dashboard-login.html");
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//    private boolean isUrlAllowedWithoutLogin(String requestURI) {
//        /*
//         Setup your own rules here to allow accessing some resources without logging in
//         Always allow your own login related requests(html, js, servlet, etc..)
//         You might also want to allow some CSS files, etc..
//         */
//        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
//    }
//
//    public void init(FilterConfig fConfig) {
//
//    }
//
//    public void destroy() {
//        // ignored.
//    }
//
//}