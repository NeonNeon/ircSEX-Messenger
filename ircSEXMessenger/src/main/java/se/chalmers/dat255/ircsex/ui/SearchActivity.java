package se.chalmers.dat255.ircsex.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

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

        searchResult = new ArrayList<String>();
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.server_list_item, searchResult);
        setListAdapter(adapter);
    }


}
