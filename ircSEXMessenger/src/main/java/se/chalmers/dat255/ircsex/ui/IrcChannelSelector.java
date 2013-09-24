package se.chalmers.dat255.ircsex.ui;

/**
 * Created by Wilhelm on 2013-09-24.
 */

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class IrcChannelSelector {
    private Set<Integer> headingIndices;

    public IrcChannelSelector() {
        headingIndices = new HashSet<Integer>();
    }

    public void setHeader(int index) {
        headingIndices.add(index);
    }

    public void removeHeader(int index) {
        headingIndices.remove(index);
    }

    public boolean isIndexHeading(int position) {
        for (Integer i : headingIndices) {
            if (position == i) return true;
        }
        return false;
    }
}
