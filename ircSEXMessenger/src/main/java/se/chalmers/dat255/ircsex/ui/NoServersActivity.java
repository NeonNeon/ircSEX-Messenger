package se.chalmers.dat255.ircsex.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.ui.dialog.ServerConnectDialogFragment;

/**
 * @author Wilhelm Hedman
 * @date 13-09-13
 */
public class NoServersActivity extends FragmentActivity implements ServerConnectDialogFragment.DialogListener {
    public static final int REQUEST_SERVER = 10;
    public static final int RESULT_RETURN_DATA = 20;
    public static final String EXTRA_SERVER = "server";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_NICKNAME = "nickname";
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";

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
        String username = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_username)).getText().toString();
        String password = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_password)).getText().toString();
        Intent data = new Intent();
        data.putExtra(EXTRA_SERVER, server);
        data.putExtra(EXTRA_PORT, port);
        data.putExtra(EXTRA_NICKNAME, nickname);
        data.putExtra(EXTRA_USERNAME, username);
        data.putExtra(EXTRA_PASSWORD, password);
        setResult(RESULT_RETURN_DATA, data);
        finish();
    }

    @Override
    public void onDialogCancel(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
