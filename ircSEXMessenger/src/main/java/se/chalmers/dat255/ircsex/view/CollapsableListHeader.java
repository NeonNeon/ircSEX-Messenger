package se.chalmers.dat255.ircsex.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Wilhelm on 2013-09-18.
 */
public class CollapsableListHeader extends TextView {

    public CollapsableListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        setText(getText().toString().toUpperCase());
    }
}
