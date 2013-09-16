package se.chalmers.dat255.ircsex.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import se.chalmers.dat255.ircsex.R;

/**
 * @author Wilhelm Hedman
 * @date 13-09-13
 */
public class ServerListActivity extends FragmentActivity implements ServerConnectDialogFragment.DialogListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_noservers);
    }

    public void popupServerConnect(View view) {
        DialogFragment fragment = new ServerConnectDialogFragment();
        fragment.show(getSupportFragmentManager(), "serverconnect");
    }

    @Override
    public void onDialogAccept(DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        String server = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_server)).getText().toString();
        String port = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_port)).getText().toString();
        String nickname = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_nickname)).getText().toString();
        // TODO: Do fun stuff with this data
        // TODO: Freeze input and add waitfeedback while opening socket
        // TODO: Validate data and provide feedback
        startActivity(new Intent(this, ChannelActivity.class));
        finish();
    }

    @Override
    public void onDialogCancel(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
