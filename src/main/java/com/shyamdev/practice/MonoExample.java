package com.shyamdev.practice;

import reactor.core.publisher.Mono;

public class MonoExample {
    public static void main(String[] args) {
        
        getMono()
            .log()
            .map(String::toUpperCase)
            .subscribe(System.out::println);
    }

    private static Mono<String> getMono() {
        return Mono.just("practice").doOnSubscribe((s) -> System.out.println(s + " subscribed...."));
    }
}