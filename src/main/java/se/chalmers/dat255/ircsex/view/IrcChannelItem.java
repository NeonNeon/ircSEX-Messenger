package se.chalmers.dat255.ircsex.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Wilhelm on 2013-09-23.
 */
public class IrcChannelItem implements IrcConnectionItem {
    private String string;

    public IrcChannelItem(String string) {
        this.string = string;
    }

    @Override
    public int getViewType() {
        return IrcConnectionItemAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.server_list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(string);

        return view;
    }

    @Override
    public String getText() {
        return string;
    }
}
