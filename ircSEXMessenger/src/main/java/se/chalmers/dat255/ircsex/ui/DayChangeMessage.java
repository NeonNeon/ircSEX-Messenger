package se.chalmers.dat255.ircsex.ui;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-10-01.
 */
public class DayChangeMessage extends InfoMessage {

    public DayChangeMessage(String message) {
        super(message);
    }

    @Override
    public int getLayoutID() {
        return R.layout.day_change_message;
    }
}
