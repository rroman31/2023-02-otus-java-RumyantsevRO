package ru.otus.grpc;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ru.otus.grpc.generated.GenerateMessage;
import ru.otus.grpc.generated.GeneratedMessage;
import ru.otus.grpc.generated.GeneratorServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        var stub = GeneratorServiceGrpc.newStub(channel);
        var generatedValue = new AtomicInteger(0);

        stub.generate(GenerateMessage.newBuilder().setFistValue(0).setLastValue(30).build(), new StreamObserver<>() {
            @Override
            public void onNext(GeneratedMessage value) {
                System.out.println("new generated value: " + value.getValue());
                generatedValue.getAndSet(value.getValue());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("request completed");
            }
        });

        int currentValue = 0;
        int lastServerValue = Integer.MIN_VALUE;
        for (int i = 0; i < 50; ++i) {
            var serverValue = 0;
            var g = generatedValue.get();
            if (lastServerValue != g) {
                serverValue = g;
                lastServerValue = g;
            }
            currentValue = currentValue + serverValue + 1;
            System.out.println(System.currentTimeMillis()/1000 + " currentValue: " + currentValue);
            TimeUnit.SECONDS.sleep(1);
        }

        channel.shutdown();
    }
}