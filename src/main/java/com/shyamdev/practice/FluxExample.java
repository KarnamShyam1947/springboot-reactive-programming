package com.shyamdev.practice;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import reactor.core.publisher.Flux;

public class FluxExample {
    
    public static void main(String[] args) throws InterruptedException {
        int NUM_OF_ELEMENTS = 10;

        CountDownLatch countDownLatch = new CountDownLatch(NUM_OF_ELEMENTS);
        
        getFlexStream(NUM_OF_ELEMENTS)
            .log()
            .doOnNext((s) -> countDownLatch.countDown())
            .map(s -> s.toUpperCase())
            .filter(s -> s.contains("1"))
            .subscribe(System.out::println);

        countDownLatch.await();
    }

    private static Flux<String> getFlexStream(int count) {
        return Flux.range(1, count+1)
                .map(i -> "example"+i)
                .delayElements(Duration.ofSeconds(1));
        
    }

}
