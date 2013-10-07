package se.chalmers.dat255.ircsex.ui.search;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Oskar on 2013-10-04.
 */
public abstract class SearchActivity extends ListActivity {

    public static final int RESULT_RETURN_CHANNEL = 30;
    public static final String EXTRA_CHANNEL = "channelName";

    private BaseAdapter adapter;
    public static final String TEXT1 = "text1";
    public static final String TEXT2 = "text2";
    public static final String TEXT3 = "text3";

    public Set<String> content;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        content = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        clearAdapter();
    }

    public abstract BaseAdapter getAdapter();

    public abstract void search(String search);

    public void clearAdapter() {
        adapter = getAdapter();
        setListAdapter(adapter);
    }

    public void update() {
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

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Configure the search info and add any event listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
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

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.expandActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String text = ((TextView) v.findViewById(android.R.id.text1)).getText().toString();
        Intent data = new Intent();
        data.putExtra(EXTRA_CHANNEL, text);
        setResult(RESULT_RETURN_CHANNEL, data);
        finish();
    }
}
