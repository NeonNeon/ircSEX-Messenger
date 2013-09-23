package se.chalmers.dat255.ircsex.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Wilhelm on 2013-09-18.
 */
public class CollapsableListHeader extends TextView {
    private String header;

    public CollapsableListHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        header = getText().toString().toUpperCase();
        setText(header);
    }

    public void setDisabled() {
        Spannable wordToSpan = new SpannableString(header + " (DISCONNECTED)");
        wordToSpan.setSpan(new ForegroundColorSpan(0xFFFF4444), header.length(), wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(wordToSpan);
    }

    public void setEnabled() {
        setText(header);
    }
}
