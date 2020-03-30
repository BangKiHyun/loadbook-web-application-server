package webserver;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private DataBase dataBase = new DataBase();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}. Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현한다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String path = getDefaultPath(request.getPath());

            //post 였을 때 구현 방법
            if ("/user/create".equals(path)) {
                User user = new User(
                        request.getParams("userId"),
                        request.getParams("password"),
                        request.getParams("name"),
                        request.getParams("email")
                );
                dataBase.addUser(user);
                log.debug("User : {} ", user);
                response.sendRedirect("/index.html");
            } else if ("/user/login".equals(path)) {
                User user = dataBase.findUserById(request.getParams("userId"));
                if (user == null) {
                    response.forward("/user/login_failed.html");
                    return;
                }
                log.debug("userID : {}  password : {}",user.getUserId(), user.getPassword());
                log.debug("requestID : {}  requestPassword : {} ", request.getParams("userId"), request.getParams("password"));
                if (user.getUserId().equals(request.getParams("userId")) && user.getPassword().equals(request.getParams("password"))) {
                    response.addHeader("Cookie-Set", "logined=true");
                    response.sendRedirect("/index.html");
                } else {
                    response.sendRedirect("/user/login_failed.html");
                }
            } else if ("/user/list".equals(path)) {
                DataOutputStream dos = new DataOutputStream(out);
                if (!request.isLogin()) {
                    response.sendRedirect("/user/login.html");
                    return;
                }
                Collection<User> users = dataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                response.forwardBody(sb.toString());
            } else {
                response.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
