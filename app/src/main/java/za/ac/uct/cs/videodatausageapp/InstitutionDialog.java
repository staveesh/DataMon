package za.ac.uct.cs.videodatausageapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class InstitutionDialog extends DialogFragment {

    public static InstitutionDialog newInstance() {
        return new InstitutionDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle(R.string.uni_selection_text);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item);
        adapter.add("CPUT");
        adapter.add("UCT");
        adapter.add("UWC");
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) getActivity()).userCancelled();
            }
        });
        alertBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = adapter.getItem(which);
                ((MainActivity) getActivity()).institutionSelected(name);
            }
        });

        return alertBuilder.create();
    }
}

