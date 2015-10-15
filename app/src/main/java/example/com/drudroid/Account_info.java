package example.com.drudroid;

import android.os.*;
import android.app.Activity;
import android.view.Menu;
import android.widget.*;
import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import example.com.drupalun.R;


public class Account_info extends Activity {
    //rendo reperibili a tutto il codice le seguenti stringhe che conterrano tutte le informazione della sessione
    public String session_id;
    public String session_name;
    public String pass;
    public String mail;
    public String name;
    public String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        Bundle extras = getIntent().getExtras();

//leggo i dati passati dall'acotivity lista
        if (extras != null) {
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
            pass= extras.getString("password");
            mail= extras.getString("mail");
            name= extras.getString("name");
            uid=extras.getString("uid");



        }
        new visualizzaarticolo().execute();


    }


    private class visualizzaarticolo extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();
            //setto l'endpoint di drupal fortnito dal modulo services
            //concateno url del endpoint user piu il numero uid corrispondente all'utente loggato

            String url="http://uniwalltest.altervista.org/portale/?q=app/user/";
            String uri=url+uid;

            HttpGet httpget = new HttpGet(uri);

            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");


            JSONObject json = new JSONObject();

            try {

                //ottengo la risponta dal server e la conservo in json
                HttpResponse response = httpclient.execute(httpget);
                json = new JSONObject(EntityUtils.toString(response.getEntity()));


                return  json;
            }catch (Exception e) {
            }
            return json;
        }



        protected void onPostExecute(final JSONObject result) {

            //carico le informazione dell'untente loggato all'interno delle Textview e le mostra
            try {
                TextView lst = (TextView) findViewById(R.id.View);


                String mResponse = name;

                lst.setText(mResponse);

            }catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TextView lst2 = (TextView) findViewById(R.id.View2);


                String mResponse = mail;

                lst2.setText(mResponse);

            } catch (Exception e) {
                e.printStackTrace();

            }
            try {
                TextView lst3 = (TextView) findViewById(R.id.View3);

                String mResponse = pass;

                lst3.setText(mResponse);

            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_info, menu);
        return true;
    }

}