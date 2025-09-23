package com.shyamdev.routes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.shyamdev.handlers.StudentHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class StudentRoutes {

    private final StudentHandler studentHandler;

    @Bean
    public RouterFunction<ServerResponse> getRoutes() {
        return RouterFunctions
                .route()
                .GET("/api/v1/route/student", studentHandler::getStudents)
                .GET("/api/v1/route/student/{id}", studentHandler::getStudent)
                .build();
    }
    
}
