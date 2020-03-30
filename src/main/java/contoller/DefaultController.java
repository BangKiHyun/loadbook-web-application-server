package contoller;

import http.HttpRequest;
import http.HttpResponse;

public class DefaultController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.forward("/index.html");
    }
}
