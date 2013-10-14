package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageView;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.NetworkStateHandler;

/**
 * Created by Oskar on 2013-10-03.
 */
public class NoInternetActivity extends Activity implements NetworkStateHandler.ConnectionListener {

    private int image = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkStateHandler.addListener(this);
        setContentView(R.layout.activity_no_internet_connection);
        setImage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkStateHandler.removeListener(this);
    }

    private void setImage() {
        int rand;
        do {
            rand = (int) (Math.random() * 5);
        } while (rand == image);
        image = rand;
        int imageResource = getResources().getIdentifier("@drawable/nointernet" + rand, null, getPackageName());
        ImageView imageView = (ImageView) findViewById(R.id.noInternetImage);
        Drawable res = getResources().getDrawable(imageResource);
        imageView.setImageDrawable(res);
    }

    public void retry(View view) {
        NetworkStateHandler.notify(this);
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onOnline() {
        finish();
    }

    @Override
    public void onOffline() {
        setImage();
    }
}
