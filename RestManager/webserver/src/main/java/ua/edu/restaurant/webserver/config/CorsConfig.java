package ua.edu.restaurant.webserver.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public OncePerRequestFilter methodOverrideFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                String override = request.getHeader("X-HTTP-Method-Override");
                if ("POST".equalsIgnoreCase(request.getMethod()) && override != null) {
                    final String method = override.toUpperCase();
                    HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                        @Override
                        public String getMethod() { return method; }
                    };
                    filterChain.doFilter(wrapper, response);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }
}