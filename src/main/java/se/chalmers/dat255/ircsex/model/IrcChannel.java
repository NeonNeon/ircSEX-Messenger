package se.chalmers.dat255.ircsex.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oed on 3/11/14.
 */
public class IrcChannel {

    private static enum Role {
        OP('@'), HALFOP('%');
        // TODO - add more roles

        private Character flag;
        private static Map<Character, Role> map = new HashMap<Character, Role>();

        static {
            for (Role r : Role.values()) {
                map.put(r.flag, r);
            }
        }

        public static Role valueOf(Character c) {
            return map.get(c);
        }

        private Role(Character flag) {
            this.flag = flag;
        }

        private Character getFlag() {
            return flag;
        }
    }

    private final String name;

    private Map<String, Role> users;
    private List<IrcMessage> messages;

    public IrcChannel(String name) {
        this.name = name;
    }

    public void addUser(String name, Character role) {
        users.put(name, Role.valueOf(role));
    }

    public String getName() {
        return name;
    }

    public Map<String, Role> getUsers() {
        return users;
    }
}
