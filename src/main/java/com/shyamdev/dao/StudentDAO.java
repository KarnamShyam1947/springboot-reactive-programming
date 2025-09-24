package com.shyamdev.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.shyamdev.dto.StudentDTO;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class StudentDAO {

    private List<StudentDTO> students = Collections.synchronizedList(new ArrayList<>());
    private AtomicLong idGenerator = new AtomicLong(100);

    @PostConstruct
    public void init() {

        students.add(new StudentDTO(1L, "Alice", "Johnson", 20, "alice.johnson@example.com"));
        students.add(new StudentDTO(2L, "Bob", "Smith", 21, "bob.smith@example.com"));
        students.add(new StudentDTO(3L, "Charlie", "Brown", 22, "charlie.brown@example.com"));
        students.add(new StudentDTO(4L, "Diana", "Prince", 23, "diana.prince@example.com"));
        students.add(new StudentDTO(5L, "Ethan", "Hunt", 24, "ethan.hunt@example.com"));
        students.add(new StudentDTO(6L, "Fiona", "Gallagher", 20, "fiona.gallagher@example.com"));
        students.add(new StudentDTO(7L, "George", "Clooney", 25, "george.clooney@example.com"));
        students.add(new StudentDTO(8L, "Hannah", "Montana", 19, "hannah.montana@example.com"));
        students.add(new StudentDTO(9L, "Ian", "Sommerhalder", 22, "ian.sommerhalder@example.com"));
        students.add(new StudentDTO(10L, "Julia", "Roberts", 23, "julia.roberts@example.com"));
        students.add(new StudentDTO(11L, "Kevin", "Hart", 24, "kevin.hart@example.com"));
        students.add(new StudentDTO(12L, "Laura", "Croft", 21, "laura.croft@example.com"));
        students.add(new StudentDTO(13L, "Michael", "Scott", 30, "michael.scott@example.com"));
        students.add(new StudentDTO(14L, "Nancy", "Drew", 19, "nancy.drew@example.com"));
        students.add(new StudentDTO(15L, "Oscar", "Wilde", 25, "oscar.wilde@example.com"));
    }

    public List<StudentDTO> findAll() {
        // return new ArrayList<>(students);
        return students.stream()
                .map(s -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return s;
                })
                .toList();
    }

    public Flux<StudentDTO> findAllReactive() {
        // return Flux.fromIterable(students).delayElements(Duration.ofSeconds(1));
        return Flux.fromIterable(students);
    }
    
    public Mono<StudentDTO> findByIdReactive(Long id) {
        Optional<StudentDTO> opt = students.stream()
            .filter(s -> s.getId().equals(id))
            .findFirst();
        return Mono.justOrEmpty(opt);
    }

    public Mono<StudentDTO> saveReactive(StudentDTO student) {
        if (student.getId() == null) {
            // Create new
            long newId = idGenerator.incrementAndGet();
            student.setId(newId);
            students.add(student);
            return Mono.just(student);
        } else {
            // Update existing (or upsert)
            return findByIdReactive(student.getId())
                .flatMap(existing -> {
                    synchronized (students) {
                        students.remove(existing);
                        students.add(student);
                    }
                    return Mono.just(student);
                })
                .switchIfEmpty(
                    // If no existing, treat as new (or reject)
                    Mono.defer(() -> {
                        // Optionally: reject or treat as insert
                        students.add(student);
                        return Mono.just(student);
                    })
                );
        }
    }

    public Mono<Void> deleteByIdReactive(Long id) {
        return findByIdReactive(id)
            .flatMap(existing -> {
                synchronized (students) {
                    students.remove(existing);
                }
                return Mono.<Void>empty();
            });
    }
}
