package ru.otus.processor;

import ru.otus.exception.ExceptionOnEvenSecond;
import ru.otus.model.Message;

import java.time.Clock;
import java.time.LocalTime;

public class ProcessorExceptionOnEvenSecond implements Processor {

    private final Clock clock;

    public ProcessorExceptionOnEvenSecond(Clock clock) {
        this.clock = clock;
    }

    public ProcessorExceptionOnEvenSecond() {
        this(Clock.systemDefaultZone());
    }

    @Override
    public Message process(Message message) {
        int currentSecond = LocalTime.now(clock).getSecond();
        if (currentSecond % 2 == 0) {
            throw new ExceptionOnEvenSecond(currentSecond);
        }
        return message.toBuilder().build();
    }
}
