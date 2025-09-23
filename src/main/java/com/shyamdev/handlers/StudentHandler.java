package com.shyamdev.handlers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.shyamdev.dao.StudentDAO;
import com.shyamdev.dto.StudentDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StudentHandler {

    private final StudentDAO studentDAO;

    public Mono<ServerResponse> getStudents(ServerRequest request) {
        Flux<StudentDTO> students = studentDAO.findAllReactive();
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(students, StudentDTO.class);
    }

    public Mono<ServerResponse> getStudent(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));

        Mono<StudentDTO> student = studentDAO.findAllReactive()
                .filter(s -> s.getId() == id)
                .singleOrEmpty(); 

        return student
                .flatMap(s -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s))
                .switchIfEmpty(ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of("error", "student not found")));
    }

}
