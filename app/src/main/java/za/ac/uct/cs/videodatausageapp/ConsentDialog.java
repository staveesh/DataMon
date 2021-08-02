package za.ac.uct.cs.videodatausageapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConsentDialog extends DialogFragment {

    public static ConsentDialog newInstance() {
        return new ConsentDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage(R.string.consent_text);
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> ((MainActivity) getActivity()).consentProvided());
        alertBuilder.setNegativeButton("No", (dialog, which) -> ((MainActivity) getActivity()).userCancelled());
        return alertBuilder.create();
    }

}
