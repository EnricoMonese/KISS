package fr.neamar.kiss.dataprovider;

import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;

import java.util.ArrayList;

import fr.neamar.kiss.loader.LoadWitPojos;
import fr.neamar.kiss.pojo.WitPojo;
import fr.neamar.kiss.pojo.Pojo;

public class WitProvider extends Provider<WitPojo> {
    private boolean hasInternet = false;

    @Override
    public void reload() {
        this.initialize(new LoadWitPojos(this));

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        hasInternet = activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public ArrayList<Pojo> getResults(String query) {
        ArrayList<Pojo> pojos = new ArrayList<>();

        // Append an item only if connected to internet
        if (hasInternet) {
            try {
                pojos.add(getResult(query));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pojos;
    }

    private Pojo getResult(String phrase) {
        WitPojo pojo = new WitPojo();
        pojo.phrase = phrase;
        pojo.relevance = 20;
        pojo.name = phrase;
        return pojo;
    }
}
