package se.chalmers.dat255.ircsex.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.Session;

/**
 * Created by Oskar on 2013-10-07.
 */
public class UserSearchActivity extends SearchActivity {

    private ArrayList<String> result;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = Session.getInstance(this, null);
        session.getActiveServer().getKnownUsers();
    }

    @Override
    public BaseAdapter getAdapter() {
        return new ArrayAdapter<String>(this, R.layout.user_search_list_item, R.id.text1, result);
    }

    @Override
    public void clearAdapter() {
        result = new ArrayList<String>();
        super.clearAdapter();
    }

    @Override
    public void search(String search) {
        content = session.getActiveServer().getKnownUsers();
        search = search.toLowerCase();
        clearAdapter();
        for (String entry : content) {
            if (entry.toLowerCase().contains(search)) {
                result.add(entry);
            }
        }
        update();
    }
}
