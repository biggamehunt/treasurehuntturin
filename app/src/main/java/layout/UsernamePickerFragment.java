package layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.andrea22.gamehunt.TeamManagment;

public class UsernamePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText username = new EditText(getActivity());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Insert username:")
                .setView(username)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                ((TeamManagment) getActivity()).doPositiveClick(username.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                ((TeamManagment) getActivity()).doNegativeClick();
                            }
                        }).create();
    }
}