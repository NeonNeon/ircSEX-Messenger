package se.chalmers.dat255.ircsex.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.TextView;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-09-17.
 */
public class JoinChannelDialogFragment extends DialogFragment {
    private DialogListener dialogListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_join_channel, null))
                .setTitle("Join channel")
                .setPositiveButton(R.string.hint_join, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onDialogAccept(JoinChannelDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.hint_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onDialogCancel(JoinChannelDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public void onDialogAccept(DialogFragment dialog){
        String channelName = ((TextView) dialog.getDialog().findViewById(R.id.dialog_join_channel_channel_name)).getText().toString();
        //TODO: Send to Rascal
    }

    public interface DialogListener {
        public void onDialogAccept(DialogFragment dialog);
        public void onDialogCancel(DialogFragment dialog);
    }
}
