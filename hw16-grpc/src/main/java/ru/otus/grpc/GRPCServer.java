package ru.otus.grpc;


import io.grpc.ServerBuilder;
import ru.otus.grpc.service.RemoteGeneratorService;

import java.io.IOException;

public class GRPCServer {

    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {

        var generatorService = new RemoteGeneratorService();

        var server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(generatorService).build();
        server.start();
        System.out.println("server waiting for client connections...");
        server.awaitTermination();
    }
}