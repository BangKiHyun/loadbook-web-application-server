package webserver;

import contoller.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListUserController());
        /*controllers.put("/index.html", new DefaultController());
        controllers.put("/", new DefaultController());*/
    }

    public static Controller getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
