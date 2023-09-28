package ru.otus.processor;

import ru.otus.model.Message;

public class ProcessorSwapFields implements Processor {
    @Override
    public Message process(Message message) {
        var currentField11 = message.getField11();
        var currentField12 = message.getField12();

        return message.toBuilder()
                .field11(currentField12)
                .field12(currentField11)
                .build();
    }
}
