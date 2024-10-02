package com.sumerge.careertrack.user_management_svc.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sumerge.careertrack.user_management_svc.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    //TODO: try to make the userDetailsService Final and remove the @Autowired
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
     @NonNull HttpServletResponse response,
     @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                final String authorizationHeader = request.getHeader("Authorization");
                final String jwt;
                final String userEmail;
                if (request.getRequestURI().equals("/auth/login")||authorizationHeader == null ||  ( !authorizationHeader.startsWith("Bearer "))) {
                    filterChain.doFilter(request, response);
                    
                    return;
                }
                jwt = authorizationHeader.substring(7);
                userEmail = jwtService.extractUserEmail(jwt);
                if(userEmail!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    try{
                        if(jwtService.isTokenValid(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                    catch(Exception e){
                        SecurityContextHolder.clearContext();
                    }
                }
                filterChain.doFilter(request, response);

            }
        }
