package contoller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(request.getParams("userId"));
        log.debug("User : {}", user);
        if (isLogin(user, request)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("/index.html");
            return;
        }

        response.sendRedirect("/user/login_failed.html");
    }

    private boolean isLogin(User user, HttpRequest request) {
        return user != null &&
                user.getUserId().equals(request.getParams("userId")) &&
                user.getPassword().equals(request.getParams("password"));
    }
}