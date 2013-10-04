package se.chalmers.dat255.ircsex.ui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Oskar on 2013-10-04.
 */
public class SearchActivity extends ListActivity {

    private List<String> searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        searchResult = new ArrayList<String>();
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.server_list_item, searchResult);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_channels, menu);

        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Configure the search info and add any event listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("IRCDEBUG", "SUBMIT " + s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("IRCDEBUG", s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
