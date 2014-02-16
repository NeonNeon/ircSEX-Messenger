package se.chalmers.dat255.ircsex.model;

/**
 * Created by Oskar on 2013-10-09.
 */
public class SearchlistChannelItem {

    private final String name;
    private final int users;
    private final String topic;

    public SearchlistChannelItem(String name, int users, String topic) {
        this.name = name;
        this.users = users;
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public int getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }
}
