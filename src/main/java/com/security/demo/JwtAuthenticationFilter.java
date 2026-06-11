package com.security.demo;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sun.net.httpserver.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        

        String token = null;
        String username = null;
        
        String path = request.getRequestURI();
        
        if (path.contains("/users/register") ||
        	    path.contains("/users/login"))
        {
        		filterChain.doFilter(request, response);
        		return;
        }
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) 
        {
        		System.out.println(authHeader);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }
        System.out.println(authHeader);
        // 1. Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
            System.out.println(token);
            username = jwtService.extractUsername(token);
        }
        
        System.out.println("AUTH SET = " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("Username: "+ username);
        // 2. Validate token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println(userDetails);
            
            boolean valid = jwtService.validateToken(token, userDetails);

            System.out.println("Token Valid = " + valid);

            if (jwtService.validateToken(token, userDetails)) 
            {
            		System.out.println("Inside validation block");
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                System.out.println("Auth: "+authToken);

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("AUTH AFTER SET = "
                        + SecurityContextHolder.getContext().getAuthentication());
            }
        }
        System.out.println(
        	    "FINAL AUTH = " +
        	    SecurityContextHolder.getContext().getAuthentication()
        	);
        filterChain.doFilter(request, response);
    }
}