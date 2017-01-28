package com.ivanmagda.mywatchface;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public final class ColourChooserDialog extends DialogFragment {

    public interface Listener {
        void onColourSelected(String colour, String tag);
    }

    private static final String ARG_TITLE = "ARG_TITLE";
    private Listener mColourSelectedListener;

    public static ColourChooserDialog newInstance(String dialogTitle) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, dialogTitle);
        ColourChooserDialog dialog = new ColourChooserDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    public void setColourSelectedListener(Listener colourSelectedListener) {
        this.mColourSelectedListener = colourSelectedListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mColourSelectedListener = (Listener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(ARG_TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] colours = getResources().getStringArray(R.array.colors_array);
                        if (mColourSelectedListener != null) {
                            mColourSelectedListener.onColourSelected(colours[which], getTag());
                        }
                    }
                });
        return builder.create();
    }

}
