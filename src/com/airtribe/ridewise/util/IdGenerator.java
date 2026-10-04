package com.airtribe.ridewise.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private final AtomicInteger counter = new AtomicInteger(0);

    public int nextId() {
        return counter.incrementAndGet();
    }
}
