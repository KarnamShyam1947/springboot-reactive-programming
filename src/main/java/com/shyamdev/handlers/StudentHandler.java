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

        Mono<StudentDTO> student = studentDAO.findByIdReactive(id);

        return student
                .flatMap(s -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s))
                .switchIfEmpty(ServerResponse
                        .status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of("error", "student not found")));
    }

    public Mono<ServerResponse> createStudent(ServerRequest request) {
        Mono<StudentDTO> studentMono = request.bodyToMono(StudentDTO.class);
        return studentMono
            .flatMap(studentDTO ->
                // ensure ID is null so DAO treats it as new
                {
                    studentDTO.setId(null);
                    return studentDAO.saveReactive(studentDTO);
                }
            )
            .flatMap(saved ->
                ServerResponse
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(saved)
            )
            .onErrorResume(e ->
                ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(Map.of("error", "Could not create student", "details", e.getMessage()))
            );
    }

    public Mono<ServerResponse> updateStudent(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));
        Mono<StudentDTO> studentMono = request.bodyToMono(StudentDTO.class);

        return studentDAO.findByIdReactive(id)
            .flatMap(existing ->
                studentMono.flatMap(updated -> {
                    // Copy over or override fields
                    updated.setId(id);
                    return studentDAO.saveReactive(updated);
                })
            )
            .flatMap(saved ->
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(saved)
            )
            .switchIfEmpty(
                ServerResponse
                    .status(HttpStatus.NOT_FOUND)
                    .bodyValue(Map.of("error", "student not found"))
            )
            .onErrorResume(e ->
                ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(Map.of("error", "Could not update student", "details", e.getMessage()))
            );
    }

    public Mono<ServerResponse> deleteStudent(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));

        return studentDAO.findByIdReactive(id)
            .flatMap(existing ->
                studentDAO.deleteByIdReactive(id)
                    .then(ServerResponse.noContent().build())
            )
            .switchIfEmpty(
                ServerResponse
                    .status(HttpStatus.NOT_FOUND)
                    .bodyValue(Map.of("error", "student not found"))
            )
            .onErrorResume(e ->
                ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .bodyValue(Map.of("error", "Could not delete student", "details", e.getMessage()))
            );
    }
}
