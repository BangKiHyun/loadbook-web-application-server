package db;

import com.google.common.collect.Maps;
import model.User;

import java.util.Collection;
import java.util.Map;

public class DataBase {
    private static Map<String, User> users = Maps.newHashMap();

    public static void addUser(User user) {
        users.put(user.getId(), user);
    }

    public User findUserById(String id) {
        return users.get(id);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
