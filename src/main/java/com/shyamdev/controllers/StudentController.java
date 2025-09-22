package com.shyamdev.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyamdev.dao.StudentDAO;
import com.shyamdev.dto.StudentDTO;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentDAO studentDAO;

    @GetMapping
    public List<StudentDTO> getAll() {
        return studentDAO.findAll();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentDTO> getAllReactive() {
        return studentDAO.findAllReactive();
    }
    
}
