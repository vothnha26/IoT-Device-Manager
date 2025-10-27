package com.iot.management.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class WebContextConfig {

    @Autowired
    private ServletContext servletContext;

    @ModelAttribute
    public void addWebContextAttributes(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpSession session) {
        request.setAttribute("request", request);
        request.setAttribute("response", response);
        request.setAttribute("session", session);
        request.setAttribute("servletContext", servletContext);
    }
}