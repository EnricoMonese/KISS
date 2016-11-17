package fr.neamar.kiss.loader;

import android.content.Context;

import java.util.ArrayList;

import fr.neamar.kiss.dataprovider.WitProvider;
import fr.neamar.kiss.pojo.WitPojo;

public class LoadWitPojos extends LoadPojos<WitPojo> {

    public LoadWitPojos(Context context) {
        super(context, WitProvider.PHONE_SCHEME);
    }

    @Override
    protected ArrayList<WitPojo> doInBackground(Void... params) {
        return null;
    }
}
