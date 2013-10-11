package se.chalmers.dat255.ircsex.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.awt.Checkbox;

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
    public static final String EXTRA_USE_SSH = "useSsh";
    public static final String EXTRA_SSH_HOSTNAME = "sshHostname";
    public static final String EXTRA_SSH_USERNAME = "sshUsername";
    public static final String EXTRA_SSH_PASSWORD = "sshPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_noservers);
    }

    public void popupServerConnect(View view) {
        DialogFragment fragment = new ServerConnectDialogFragment();
        fragment.show(getSupportFragmentManager(), "serverconnect");
    }

    public void SshCheckBoxClicked(View view) {
        View layout = ((View)view.getParent()).findViewById(R.id.sshconnect_layout);
        if (((CheckBox)view).isChecked()) {
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDialogAccept(DialogFragment dialogFragment) {
        Dialog dialog = dialogFragment.getDialog();
        String server = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_server)).getText().toString();
        String port = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_port)).getText().toString();
        String nickname = ((TextView)dialog.findViewById(R.id.dialog_serverconnect_nickname)).getText().toString();
        boolean useSsh = ((CheckBox)dialog.findViewById(R.id.dialog_ssh_checkBox)).isChecked();
        String sshHostname = ((TextView)dialog.findViewById(R.id.dialog_sshconnect_hostname)).getText().toString();
        String sshUsername = ((TextView)dialog.findViewById(R.id.dialog_sshconnect_username)).getText().toString();
        String sshPassword = ((TextView)dialog.findViewById(R.id.dialog_sshconnect_password)).getText().toString();
        Intent data = new Intent();
        data.putExtra(EXTRA_SERVER, server);
        data.putExtra(EXTRA_PORT, port);
        data.putExtra(EXTRA_NICKNAME, nickname);
        data.putExtra(EXTRA_USE_SSH, useSsh);
        data.putExtra(EXTRA_SSH_HOSTNAME, sshHostname);
        data.putExtra(EXTRA_SSH_USERNAME, sshUsername);
        data.putExtra(EXTRA_SSH_PASSWORD, sshPassword);
        setResult(RESULT_RETURN_DATA, data);
        finish();
    }

    @Override
    public void onDialogCancel(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }
}
