package se.chalmers.dat255.ircsex.view;

import android.view.LayoutInflater;
import android.view.View;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Wilhelm on 2013-09-23.
 */
public class IrcServerHeader implements IrcConnectionItem {
    private final String name;

    public IrcServerHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return IrcConnectionItemAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.server_list_header, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        CollapsibleListHeader text = (CollapsibleListHeader) view.findViewById(R.id.server_name);
        text.setServerName(name);
        return view;
    }

    @Override
    public String getText() {
        return name;
    }
}