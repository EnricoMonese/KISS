package fr.neamar.kiss.result;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import fr.neamar.kiss.MainActivity;
import fr.neamar.kiss.R;
import fr.neamar.kiss.adapter.RecordAdapter;
import fr.neamar.kiss.pojo.WitPojo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class WitResult extends Result {
    private final WitPojo witPojo;
    private boolean gotApiResult = false;
    public WitResult(WitPojo witPojo) {
        super();
        this.pojo = this.witPojo = witPojo;
    }

    @Override
    public View display(Context context, int position, View v) {
        if (v == null)
            v = inflateFromId(context, R.layout.item_wit);

        TextView appName = (TextView) v.findViewById(R.id.item_wit_text);
        String text = context.getString(R.string.ui_item_wit);
        String ccc = "Thinking...";

        if(gotApiResult == false) {
            new WitApiCall(appName).execute(witPojo.phrase, ccc);
            gotApiResult = true;
        }

        appName.setText(enrichText(String.format(text, "{" + ccc + "}")));

        ((ImageView) v.findViewById(R.id.item_wit_icon)).setColorFilter(getThemeFillColor(context), PorterDuff.Mode.SRC_IN);

        return v;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected PopupMenu buildPopupMenu(Context context, final RecordAdapter parent, View parentView) {
        return inflatePopupMenu(R.menu.menu_item_phone, context, parentView);
    }

    @Override
    protected Boolean popupMenuClickHandler(Context context, RecordAdapter parent, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_phone_createcontact:
                // Create a new contact with this phone number
                Intent createIntent = new Intent(Intent.ACTION_INSERT);
                createIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                createIntent.putExtra(ContactsContract.Intents.Insert.PHONE, witPojo.phrase);
                createIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(createIntent);
                return true;
            case R.id.item_phone_sendmessage:
                String url = "sms:" + witPojo.phrase;
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(messageIntent);
                return true;
        }

        return super.popupMenuClickHandler(context, parent, item);
    }

    @Override
    public void doLaunch(Context context, View v) {
        Log.i("Wot", "wit doLaunch");
    }

    @Override
    public Drawable getDrawable(Context context) {
        //noinspection deprecation: getDrawable(int, Theme) requires SDK 21+
        return context.getResources().getDrawable(android.R.drawable.ic_menu_call);
    }
}
class WitApiCall extends AsyncTask<String, Void, String> {

    final TextView appName;

    WitApiCall(TextView appName) {
        super();
        this.appName = appName;
    }

    @Override
    protected String doInBackground(String... commands) {
        String o = "Error";
        try {
            o = getCommand(commands[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    String getCommand(String command) throws Exception {

        String url = "https://api.wit.ai/message";
        String key = "ISRO2ENIHX6SCFYUHBRGRGEA4M3PFVHF";

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date = df.format(Calendar.getInstance().getTime());

        String param1 = date;
        String param2 = command;
        String charset = "UTF-8";

        String query = String.format("v=%s&q=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));

        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setRequestProperty ("Authorization", "Bearer " + key);
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream inputStream = connection.getInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
        StringBuilder sBuilder = new StringBuilder();

        String line = null;
        while ((line = bReader.readLine()) != null) {
            sBuilder.append(line + "\n");
        }

        inputStream.close();
        return sBuilder.toString();
    }

    protected void onPostExecute(String result) {
        //parse JSON data
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(result);

            if (jObj.getJSONObject("entities").getString("datetime") != null) {
                this.appName.setText("Wit.ai found a date: " + jObj.getJSONObject("entities").getJSONArray("datetime").getJSONObject(0).getString("value"));
            } else {
                this.appName.setText (":/");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //this.appName.setText(enrichText(String.format(String.valueOf(R.string.ui_item_wit), "{" + result + "}")));
    }

    Spanned enrichText(String text) {
        return Html.fromHtml(text.replaceAll("\\{", "<font color=#4caf50>").replaceAll("\\}", "</font>"));
    }

}
