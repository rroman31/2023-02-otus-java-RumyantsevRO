package ru.otus.processor;

import org.junit.jupiter.api.Test;
import ru.otus.handler.ComplexProcessor;
import ru.otus.model.Message;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TestProcessorExceptionOnEvenSecond {
    @Test
    void oddSecond() {
        var message = new Message.Builder(111L).build();
        var oddClock = Clock.fixed(
                Instant.parse("2023-09-28T12:10:03.000Z"),
                ZoneId.systemDefault()
        );
        var complexProcessorOdd = new ComplexProcessor(List.of(new ProcessorExceptionOnEvenSecond(oddClock)), (ex) -> {
            throw new TestProcessorExceptionOnEvenSecond.ExceptionForTest(ex.getMessage());
        });
        assertThatCode(() -> complexProcessorOdd.handle(message)).doesNotThrowAnyException();
    }

    @Test
    void evenSecond() {
        var message = new Message.Builder(111L).build();
        var evenClock = Clock.fixed(
                Instant.parse("2023-09-28T12:10:04.000Z"),
                ZoneId.systemDefault()
        );
        var complexProcessorEven = new ComplexProcessor(List.of(new ProcessorExceptionOnEvenSecond(evenClock)), (ex) -> {
            throw new TestProcessorExceptionOnEvenSecond.ExceptionForTest(ex.getMessage());
        });

        assertThatThrownBy(() -> complexProcessorEven.handle(message)).hasMessage("Event processing second is: 4");
    }

    private static class ExceptionForTest extends RuntimeException {
        public ExceptionForTest(String message) {
            super(message);
        }
    }
}
