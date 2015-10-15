package example.com.drudroid;

import android.os.*;
import android.app.Activity;
import android.view.Menu;
import android.view.*;
import android.content.*;
import android.widget.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

import example.com.drupalun.R;


public class Lista extends Activity {
    //rendo reperibili a tutto il codice le seguenti stringhe che conterrano tutte le informazione della sessione
    public String session_id;
    public String session_name;
    public String name;
    public String pass;
    public String mail;
    public String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Bundle extras = getIntent().getExtras();

//leggo i dati passati dall'acotivity lista

        if (extras != null) {
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
            pass= extras.getString("password");
            mail= extras.getString("mail");
            name= extras.getString("name");
            uid= extras.getString("uid");



        }


        new Riempilista().execute();
    }

    private class Riempilista extends AsyncTask<String, Void, JSONArray> {


        protected JSONArray doInBackground(String... params) {


            HttpClient httpclient = new DefaultHttpClient();
            //setto l'endpoint di drupal fortnito dal modulo services
            HttpGet httpget = new HttpGet("http://uniwalltest.altervista.org/portale/?q=app/node");

            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");

            JSONArray json = new JSONArray();

            try {
                //leggo la risposta dal server e la conservo in json
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

            //creo un Hashmap che conterra i titoli e l'uri dei nodi ovvero degli articoli
            ArrayList<HashMap<String,String>> lista = new ArrayList<HashMap<String,String>>();




                for(int j=0;j<result.length();j++) {
                    try {

                       //rempio map con le stringhe titolo e uri ottenute da result
                        HashMap<String,String> map = new HashMap<String,String>();
                        map.put("titolo", result.getJSONObject(j).getString("title"));
                        map.put("uri", result.getJSONObject(j).getString("uri"));

                       //rimepio lista degli elementi contenuti da map
                        lista.add(map);





                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



            //creo un array adapter e gli passo l'arraylist per riempire lst ovvero la listview

            ArrayAdapter ad= new ArrayAdapter(Lista.this, android.R.layout.simple_list_item_1,lista);

            lst.setAdapter(ad);


                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                        //estraggo dall'item selezionato dalla listview l'uri del nodo che voglio mostrare in
                        //visualizza articolo e lo passo
                        HashMap<String, Object> obj = (HashMap<String, Object>) lst.getItemAtPosition(position);
                        String name = (String) obj.get("uri");
                        Intent i = new Intent(Lista.this, Visualizzaarticolo.class);
                        i.putExtra("uri", name);
                        i.putExtra("SESSION_ID", session_id);
                        i.putExtra("SESSION_NAME", session_name);
                        startActivity(i);


                    }
                });

        }


    }


    public void addArticoloButton_click(View view){


        Intent intent = new Intent(this, Addarticolo.class);
        intent.putExtra("SESSION_ID", session_id);
        intent.putExtra("SESSION_NAME", session_name);


        startActivity(intent);
    }







    private class logout extends AsyncTask<String, Void, Integer> {

        protected Integer doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            //setto l'endpoint di drupal fortnito dal modulo services per effettuare il logout


            HttpPost httppost = new HttpPost("http://uniwalltest.altervista.org/portale/?q=app/user/logout");
            try {



                //creo un nuovo oggetto json  inserico il sessid


                JSONObject json = new JSONObject();


                json.put("sessid", session_id);



                //attraverso la creazione di un cookie invio il sessid al server per indicare quale sessione chiudere

                StringEntity se = new StringEntity(json.toString());

                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);
                BasicHttpContext mHttpContext = new BasicHttpContext();
                CookieStore mCookieStore      = new BasicCookieStore();


                BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
                cookie.setVersion(0);
                cookie.setDomain(".uniwalltest.altervista.org");
                cookie.setPath("/");
                mCookieStore.addCookie(cookie);
                cookie = new BasicClientCookie("has_js", "1");
                mCookieStore.addCookie(cookie);
                mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                httpclient.execute(httppost,mHttpContext);

                return 0;







            }catch (Exception e) {
                e.printStackTrace();
            }


            return 0;
        }


        protected void onPostExecute(Integer result) {

            //effettuato il logout mi disconnetto e torno all'acitvity login
            Intent intent = new Intent(Lista.this,Login.class);

            startActivity(intent);
            finish();
        }
    }


    public void addaccountinfo_click(View view){
        //passo le variabili globali all'acitvity account info

        Intent i = new Intent(Lista.this, Account_info.class);
        i.putExtra("uid",uid);
        i.putExtra("password", pass);
        i.putExtra("name", name);
        i.putExtra("mail", mail);
        i.putExtra("SESSION_ID", session_id);
        i.putExtra("SESSION_NAME", session_name);
        startActivity(i);

    }


    public void addlogout_click(View view){

        //se clicco sul bottone logout esegue logout
            new logout().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

}