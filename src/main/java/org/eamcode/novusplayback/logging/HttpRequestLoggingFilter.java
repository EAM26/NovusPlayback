package org.eamcode.novusplayback.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

   private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    private static final Set<String> SAFE_QUERY_ENDPOINTS = Set.of("/watch", "/api/clip.mp4");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startNs = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();

            String query = request.getQueryString();
            boolean logQuery = (query != null) && SAFE_QUERY_ENDPOINTS.contains(uri);

            if (logQuery) {
                log.info("{} {}?{} -> {} ({} ms)", method, uri, query, status, durationMs);
            } else {
                log.info("{} {} -> {} ({} ms)", method, uri, status, durationMs);
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return "/favicon.ico".equals(uri);
    }
}
