package se.chalmers.dat255.ircsex.ui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.Session;

/**
 * Created by Oskar on 2013-10-04.
 */
public class SearchActivity extends ListActivity {

    private ArrayAdapter<String> adapter;
    private Session session;

    private Map<String, String> channels;
    private Set<String> content;
    private List<String> searchResult;

    public static final int CHANNEL_FLAG = 0;
    public static final int USER_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        session = Session.getInstance(this, null);
        if (getIntent().getExtras().getInt("requestCode") == CHANNEL_FLAG) {
            session.getActiveServer().listChannels();
            channels = new HashMap<String, String>();
        }
        content = new LinkedHashSet<String>();

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchResult = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.server_list_item, android.R.id.text1, searchResult);
        setListAdapter(adapter);
    }

    private void search(String search) {
        if (content.isEmpty()) {
            if (getIntent().getExtras().getInt("requestCode") == CHANNEL_FLAG) {
                channels = session.getActiveServer().getChannels();
                content = channels.keySet();
            } else {
                content = session.getActiveServer().getKnownUsers();
            }
        }

        adapter.clear();
        for (String entry : content) {
            if (getIntent().getExtras().getInt("requestCode") == CHANNEL_FLAG
                && (entry.contains(search) || channels.get(entry).contains(search))) {
                adapter.add(entry + " - " + channels.get(entry));
            } else if (entry.contains(search)) {
                adapter.add(entry);
            }
        }
        getListView().invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_channels, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Configure the search info and add any event listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                SearchActivity.this.search(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
