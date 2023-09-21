package ru.otus.servlet;

import com.google.gson.Gson;
import ru.otus.converter.ClientConverter;
import ru.otus.data.crm.model.Client;
import ru.otus.data.crm.service.DBServiceClient;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.dto.ClientDto;

import java.io.IOException;
import java.util.stream.Collectors;


public class ClientApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final DBServiceClient dBServiceClient;
    private final Gson gson;

    private final ClientConverter converter = new ClientConverter();

    public ClientApiServlet(DBServiceClient dBServiceClient, Gson gson) {
        this.dBServiceClient = dBServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = dBServiceClient.getClient(extractIdFromRequest(request)).orElse(null);
        ClientDto result = converter.convertToDto(client);
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clientJSON = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        ClientDto clientDto = gson.fromJson(clientJSON, ClientDto.class);
        dBServiceClient.saveClient(converter.convertToModel(clientDto));
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }
}
