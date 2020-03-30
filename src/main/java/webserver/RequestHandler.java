package webserver;

import contoller.Controller;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private DataBase dataBase = new DataBase();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    // TODO 사용자 요청에 대한 처리는 이 곳에 구현한다.
    public void run() {
        log.debug("New Client Connect! Connected IP : {}. Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            Controller controller = RequestMapping.getController(request.getPath());
            if (controller == null) {
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            } else controller.service(request, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) return "/index.html";

        return path;
    }
}
