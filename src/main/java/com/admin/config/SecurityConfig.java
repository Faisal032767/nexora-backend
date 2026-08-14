package com.admin.config;

import com.admin.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("https://nexora-auth.netlify.app/"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		configuration.setAllowedHeaders(List.of("*"));

		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))

				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //
						// Public
						.requestMatchers("/auth/login", "/auth/register").permitAll()

						// Logged-in user's own profile
						.requestMatchers(HttpMethod.GET, "/users/me").authenticated()
						.requestMatchers(HttpMethod.PUT, "/users/me").authenticated()

						// Admin user management
						.requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

						// Everything else requires authentication
						.anyRequest().authenticated())

				.exceptionHandling(ex -> ex

						// 401 - Not authenticated
						.authenticationEntryPoint((request, response, authException) -> {

							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.setContentType("application/json");

							response.getWriter().write("""
									{
									    "status": 401,
									    "message": "Authentication required"
									}
									""");
						})

						// 403 - Authenticated but insufficient permission
						.accessDeniedHandler((request, response, accessDeniedException) -> {

							response.setStatus(HttpServletResponse.SC_FORBIDDEN);
							response.setContentType("application/json");

							response.getWriter().write("""
									{
									    "status": 403,
									    "message": "Access denied"
									}
									""");
						}))

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}
}