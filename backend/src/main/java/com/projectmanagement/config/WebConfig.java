package com.projectmanagement.config;

import com.projectmanagement.interceptor.ApiLoggingInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Web configuration for interceptors and filters
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final ApiLoggingInterceptor apiLoggingInterceptor;
    
    public WebConfig(ApiLoggingInterceptor apiLoggingInterceptor) {
        this.apiLoggingInterceptor = apiLoggingInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register") // Exclude auth endpoints
                .order(1);
    }
    
    /**
     * Content caching filter for request/response body logging
     */
    @Bean
    public FilterRegistrationBean<Filter> contentCachingFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain filterChain) throws ServletException, IOException {
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
                ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
                
                filterChain.doFilter(wrappedRequest, wrappedResponse);
                wrappedResponse.copyBodyToResponse();
            }
        });
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
} 