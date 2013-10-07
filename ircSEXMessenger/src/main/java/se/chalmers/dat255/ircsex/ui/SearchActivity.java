package se.chalmers.dat255.ircsex.ui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.Session;

/**
 * Created by Oskar on 2013-10-04.
 */
public class SearchActivity extends ListActivity {
    public static final int CHANNEL_FLAG = 0;
    public static final int USER_FLAG = 1;
    public static final int RESULT_RETURN_CHANNEL = 30;
    public static final String EXTRA_CHANNEL = "channelName";
    public static final String REQUEST_CODE = "requestCode";

    private SimpleAdapter adapter;
    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";
    private static final String TEXT3 = "text3";

    private Session session;

    private Map<String, String> channels;
    private Set<String> content;
    ArrayList<HashMap<String,String>> result;
    private boolean channelSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        session = Session.getInstance(this, null);
        channelSearch = getIntent().getExtras().getInt(REQUEST_CODE) == CHANNEL_FLAG;
        if (channelSearch) {
            session.getActiveServer().listChannels();
            channels = new HashMap<String, String>();
        }
        content = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        clearAdapter();
    }

    private void clearAdapter() {
        result = new ArrayList<HashMap<String,String>>();
        adapter = new SimpleAdapter(
                this,
                result,
                R.layout.channel_search_list_item,
                new String[]{TEXT1, TEXT2, TEXT3},
                new int[]{R.id.text1, R.id.text2, R.id.text3});
        setListAdapter(adapter);
    }

    private void search(String search) {
        if (content.isEmpty()) {
            if (channelSearch) {
                channels = session.getActiveServer().getChannels();
                content = channels.keySet();
            } else {
                content = session.getActiveServer().getKnownUsers();
            }
        }
        search = search.toLowerCase();
        clearAdapter();
        for (String entry : content) {
            if (channelSearch && (entry.toLowerCase().contains(search) // TODO: Improve efficiency
                    || channels.get(entry).toLowerCase().contains(search))) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(TEXT1, entry.substring(0, entry.lastIndexOf(" ")));
                item.put(TEXT2, entry.substring(entry.lastIndexOf(" ")+1) + " users");
                item.put(TEXT3, channels.get(entry));
                result.add(item);
            } else if (entry.toLowerCase().contains(search)) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(TEXT1, entry);
                item.put(TEXT2, "");
                item.put(TEXT3, "");
                result.add(item);
            }
        }
        adapter.notifyDataSetChanged();
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
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                SearchActivity.this.finish();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String text = ((TextView) ((LinearLayout) v).getChildAt(0)).getText().toString();
        if (getIntent().getExtras().getInt("requestCode") == CHANNEL_FLAG) {
            text = text.substring(0, text.indexOf(" "));
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_CHANNEL, text);
        setResult(RESULT_RETURN_CHANNEL, data);
        finish();
    }
}
