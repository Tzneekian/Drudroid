package example.com.drudroid;

import android.os.*;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.*;
import android.content.*;
import android.widget.*;
import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import android.os.Bundle;

import example.com.drupalun.R;


public class Visualizzaarticolo extends Activity {
    //rendo reperibili a tutto il codice le seguenti stringhe che conterrano tutte le informazione della sessione
    public String session_id;
    public String session_name;
    public String uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizzaarticolo);
        Bundle extras = getIntent().getExtras();

//leggo i dati passati dall'acotivity lista

        if (extras != null) {
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
            uri=extras.getString("uri");
        }


        new visualizzaarticolo().execute();
        new Visualizzaarticolo2().execute();
    }



    public void addcommento_click(View view){


        Intent intent = new Intent(Visualizzaarticolo.this, Addcommento.class);
        intent.putExtra("uri", uri);
        intent.putExtra("SESSION_ID", session_id);
        intent.putExtra("SESSION_NAME", session_name);


        startActivity(intent);
    }

    private class visualizzaarticolo extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();
            //setto l'endpoint di drupal fortnito dal modulo services
            //url da vove reperire le info del nodo selezionato e al'inteno di uri


            HttpGet httpget = new HttpGet(uri);

            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");


            JSONObject json = new JSONObject();

            try {

                //leggo la risposta dal server e la conservo in json che sara utilizzato dopo per rimpire il titolo e il body
                HttpResponse response = httpclient.execute(httpget);
                json = new JSONObject(EntityUtils.toString(response.getEntity()));


                return  json;

            }catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }


        protected void onPostExecute(final JSONObject result) {


            //carico le informazione del nodo all'interno delle Textview e le mostra
            try {
                TextView lst = (TextView) findViewById(R.id.View);
                String mResponse1 = result.getString("title");


                lst.setText(mResponse1);

            }catch (Exception e2) {
                Log.v("Error adding article", e2.getMessage());
                                  }


                try {
                    TextView lst2 = (TextView) findViewById(R.id.View2);



                    //estraggo il JSONAarry dall'oggetto body e estraggo il valore value ovvero il body dell'articolo
                    JSONArray cast = result.getJSONObject("body").getJSONArray("und");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject actor = cast.getJSONObject(i);
                        String name = actor.getString("value");
                        lst2.setText(name);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


        }



    }


    private class Visualizzaarticolo2 extends AsyncTask<String, Void, JSONArray> {




        protected JSONArray doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();
            //setto l'endpoint di drupal fortnito dal modulo services

            HttpGet httpget = new HttpGet("http://uniwalltest.altervista.org/portale/?q=app/comment");
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {


                //leggo la risposta dal server e la conservo in json per reperire i commenti dell'articolo
                HttpResponse response = httpclient.execute(httpget);


                json = new JSONArray(EntityUtils.toString(response.getEntity()));

                return json;



            }catch (Exception e) {
                e.printStackTrace();
            }



            return json;
        }



        protected void onPostExecute(final JSONArray result) {


            final ListView lst = (ListView)  findViewById(R.id.listView);


            //creo un arraylist dove carico tutti i commenti dell'articolo

            ArrayList<String> commenti=new ArrayList<String>();

            //estraggo da uri gli utlimi due caratteri ovvero il numero del nodo
            String numeronodo = uri.substring(uri.length() - 2);


            for(int j=0;j<result.length();j++) {
                try {


                    //carico nell'arraylist tutti i commenti ma soltanto quelli con nid uguale a numeronodo
                    if (numeronodo.equals(result.getJSONObject(j).getString("nid"))) {

                       commenti.add(result.getJSONObject(j).getString("subject").toString());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

         //creo un adapter per caricare gli elementi dell'arraylist nella listview
            ArrayAdapter ad= new ArrayAdapter(Visualizzaarticolo.this, android.R.layout.simple_list_item_1,commenti);


            lst.setAdapter(ad);

        }


    }





    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_visualizzaarticolo, menu);
        return true;
    }

}