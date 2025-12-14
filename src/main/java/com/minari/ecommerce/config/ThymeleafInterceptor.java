package com.minari.ecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ThymeleafInterceptor implements HandlerInterceptor {
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && request.getRequestURI().startsWith("/admin/")) {
            String path = request.getRequestURI();
            String currentPage = "dashboard"; // default
            
            if (path.contains("/categories")) {
                currentPage = "categories";
            } else if (path.contains("/products")) {
                currentPage = "products";
            } else if (path.contains("/orders")) {
                currentPage = "orders";
            } else if (path.contains("/customers")) {
                currentPage = "customers";
            } else if (path.contains("/promotions")) {
                currentPage = "promotions";
            } else if (path.contains("/reports")) {
                currentPage = "reports";
            }
            
            modelAndView.addObject("currentPage", currentPage);
        }
        
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
