package chatapp.models;

import java.util.List;
import java.util.Map;

public class User {
    String name;
    String user_name;
    String email;
    Map<User, List<Message>> chat_list;
}
