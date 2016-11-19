package fr.neamar.kiss.loader;

import android.content.Context;

import java.util.ArrayList;

import fr.neamar.kiss.pojo.WitPojo;

public class LoadWitPojos extends LoadPojos<WitPojo> {

    public LoadWitPojos(Context context) {
        super(context, "wit://");
    }

    @Override
    protected ArrayList<WitPojo> doInBackground(Void... params) {
        return null;
    }
}
