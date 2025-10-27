package com.thirdeye3.usermanager.filters;

import com.thirdeye3.usermanager.dtos.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${thirdeye.api.key}")
    private String apiKey;
    
    @Value("${spring.profiles.active}")
    private String profile;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUrl = request.getRequestURL().toString();

        String requestApiKey = request.getHeader("THIRDEYE-API-KEY");
        if ((apiKey != null && apiKey.equals(requestApiKey)) || profile.equalsIgnoreCase("LOCAL")) {
            filterChain.doFilter(request, response);
        } else {
            sendUnauthorizedResponse(response);
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Response<String> res = new Response<>(false, 401, "Invalid Request", null);
        response.getWriter().write(objectMapper.writeValueAsString(res));
    }
}