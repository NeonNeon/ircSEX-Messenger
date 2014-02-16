package se.chalmers.dat255.ircsex.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Wilhelm on 2013-09-23.
 */
public class IrcConnectionItemAdapter extends ArrayAdapter<IrcConnectionItem> {
    private LayoutInflater inflater;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public IrcConnectionItemAdapter(Context context, List<IrcConnectionItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(inflater, convertView);
    }
}
