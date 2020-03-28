package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            log.debug("request line :  {}", line);
            if (line == null) {
                return;
            }
            String[] tokens = line.split(" ");
            int contentLength = 0;
            boolean logined = false;

            while (!"".equals(line)) {
                line = br.readLine();
                log.debug("header : {}", line);
                if (line.contains("Cookie")) {
                    logined = isLogin(line);
                }

                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }
            }

            String url = tokens[1];

            //post 였을 때 구현 방법
            if ("/user/create".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                dataBase.addUser(user);
                log.debug("User : {} ", user);
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html");
            } else if ("/user/login".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                User user = dataBase.findUserById(params.get("userId"));

                DataOutputStream dos = new DataOutputStream(out);
                if (user == null) {
                    responseResource(dos, "/user/login_failed.html");
                    return;
                }

                if (user.getUserId().equals(params.get("userId")) && user.getPassword().equals(params.get("password"))) {
                    response302LoginSuccessHeader(dos);
                } else {
                    responseResource(dos, "/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {
                DataOutputStream dos = new DataOutputStream(out);
                if (!logined) {
                    responseResource(dos, "/user/login.html");
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
                byte[] body = sb.toString().getBytes();
                response200Header(dos, body.length);
                responseBody(dos, body);
            }

            //form 태크 method : get 였을 때 구현 방법
            /*if (url.startsWith("/user/create")) {
                int idx = url.indexOf("?");
                String queryString = url.substring(idx + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("User : {} ", user);
            }*/

            else {
                DataOutputStream dos = new DataOutputStream(out);
                responseResource(dos, url);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isLogin(String line) {
        String[] headerToken = line.split(":");
        Map<String, String> cookie = HttpRequestUtils.parseCookies(headerToken[1].trim());
        String value = cookie.get("logined");

        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());//trim() 메서드는 양 옆의 공백을 지워준다
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(DataOutputStream dos, String url) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
