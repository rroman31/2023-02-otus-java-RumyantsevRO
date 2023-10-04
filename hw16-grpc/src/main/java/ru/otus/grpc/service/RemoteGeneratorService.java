package ru.otus.grpc.service;

import io.grpc.stub.StreamObserver;
import ru.otus.grpc.generated.GenerateMessage;
import ru.otus.grpc.generated.GeneratedMessage;
import ru.otus.grpc.generated.GeneratorServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class RemoteGeneratorService extends GeneratorServiceGrpc.GeneratorServiceImplBase {
    @Override
    public void generate(GenerateMessage request, StreamObserver<GeneratedMessage> responseObserver) {
        IntStream.range(request.getFistValue(), request.getLastValue() + 1)
                .forEach(value -> {
                    responseObserver.onNext(GeneratedMessage.newBuilder().setValue(value).build());
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println(e);
                    }
                });
        responseObserver.onCompleted();
    }
}