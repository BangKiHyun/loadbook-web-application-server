package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {
    //한번 생성한 후 재사용할 수 있어야 하기 때문에 static 으로 구현
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String id) {
        HttpSession session = sessions.get(id);

        if (session == null) {
            session = new HttpSession(id);
            sessions.put(id, session);
            return session;
        }

        return session;
    }

    static void remove(String id) {
        sessions.remove(id);
    }
}
