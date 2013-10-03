package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;

/**
 * Created by Oskar on 2013-10-03.
 */
public class NoInternetActivity extends Activity implements NetworkStateHandler.ConnectionListener {

    private boolean online;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        online = false;
        NetworkStateHandler.addListener(this);
        setContentView(R.layout.activity_no_internet_connection);
        setImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImage();
    }

    private void setImage() {
        int rand = (int) (Math.random() * 4);
        int imageResource = getResources().getIdentifier("@drawable/nointernet" + rand, null, getPackageName());
        ImageView imageView = (ImageView) findViewById(R.id.noInternetImage);
        Drawable res = getResources().getDrawable(imageResource);
        imageView.setImageDrawable(res);
    }

    @Override
    public void onBackPressed() {
        if (online) {
            super.onBackPressed();
        }
    }

    @Override
    public void onOnline() {
        online = true;
        onBackPressed();
    }

    @Override
    public void onOffline() {}
}
