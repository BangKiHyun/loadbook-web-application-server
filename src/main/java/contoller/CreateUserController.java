package contoller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParams("userId"),
                request.getParams("password"),
                request.getParams("name"),
                request.getParams("email")
        );
        log.debug("User : {} ", user);
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }
}
