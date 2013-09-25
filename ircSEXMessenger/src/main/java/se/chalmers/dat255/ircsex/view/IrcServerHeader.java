package se.chalmers.dat255.ircsex.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Wilhelm on 2013-09-23.
 */
public class IrcServerHeader implements IrcConnectionItem {
    private final String name;
    private TextView connection;
    private CollapsibleListHeader listHeader;

    public IrcServerHeader(String name) {
        this.name = name;
    }

    public void expand() {
        connection.setVisibility(connection.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void disconnect() {
        listHeader.setDisconnected();
    }

    public void reconnect() {
        listHeader.setConnected();
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

        connection = (TextView) view.findViewById(R.id.server_header_connection);
        listHeader = (CollapsibleListHeader) view.findViewById(R.id.server_name);
        listHeader.setServerName(name);
        return view;
    }

    @Override
    public String getText() {
        return name;
    }
}
