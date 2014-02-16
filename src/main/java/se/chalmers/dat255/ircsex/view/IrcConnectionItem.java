package se.chalmers.dat255.ircsex.view;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Wilhelm on 2013-09-23.
 */
public interface IrcConnectionItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
    public String getText();
}
