package com.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService
			) {

		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = authHeader.substring(7);

		try {

			String email = jwtService.extractEmail(token);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				System.out.println("EMAIL: " + userDetails.getUsername());
				System.out.println("AUTHORITIES: " + userDetails.getAuthorities());

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (Exception e) {

		    SecurityContextHolder.clearContext();

		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    response.setContentType("application/json");

		    response.getWriter().write("""
		        {
		            "status": 401,
		            "message": "Invalid or expired token"
		        }
		        """);

		    return;
		}
		System.out.println("BEFORE FILTER CHAIN:");
		System.out.println(
		    SecurityContextHolder.getContext().getAuthentication()
		);

		filterChain.doFilter(request, response);

		System.out.println("AFTER FILTER CHAIN:");
		System.out.println(
		    SecurityContextHolder.getContext().getAuthentication()
		);;
	}
}