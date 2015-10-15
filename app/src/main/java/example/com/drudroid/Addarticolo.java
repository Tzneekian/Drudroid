package example.com.drudroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;

import example.com.drupalun.R;


public class Addarticolo extends Activity {
    //rendo reperibili a tutto il codice le seguenti stringhe che conterrano tutte le informazione della sessione


    public String session_id;
    public String session_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addarticolo);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //leggo i dati passati dall'acotivity lista

            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");

        }
    }




    public void addArticleButton_click(View view){

        new addArticleTask().execute(session_name, session_id);
    }


    private class addArticleTask extends AsyncTask<String, Void, Integer> {

        protected Integer doInBackground(String... params) {

            //leggo i parametri session_name e sessid passati dalla lista activity

            String session_name=params[0];
            String session_id=params[1];


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://uniwalltest.altervista.org/portale/?q=app/node");



            try {

                //ottengo il titolo e il corpo dell'articolo dalla ui
                TextView txtTitle = (TextView) findViewById(R.id.editTitle);
                TextView txtBody = (TextView) findViewById(R.id.editBody);

                //estrae e inserisce i dati nelle stringhe create
                String title=txtTitle.getText().toString().trim();
                String body=txtBody.getText().toString().trim();


                //qui definisco il corpo del json da invia all'endpoint node, contiene le strighe ES:"+title+"csono
                // le informazioni ottenute dall ui e inserite nelle stringhe
                StringEntity se = new StringEntity( " { \"title\":\""+title+"\",\"type\":\"article\",\"body\":{\"und\":[{ \"value\":\""+body+"\"}]}}");
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);


                BasicHttpContext mHttpContext = new BasicHttpContext();
                CookieStore mCookieStore      = new BasicCookieStore();

                //creo un cookie per effettua la sessione di invio al server rest di drupal
                BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
                cookie.setVersion(0);
                cookie.setDomain(".uniwalltest.altervista.org");
                cookie.setPath("/");
                mCookieStore.addCookie(cookie);
                cookie = new BasicClientCookie("has_js", "1");
                mCookieStore.addCookie(cookie);
                mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
                //invio al server il contenuto fi mHttpContext
               httpclient.execute(httppost, mHttpContext);


                return 0;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }


        protected void onPostExecute(Integer result) {

            //riapre l'activity lista
            Intent intent = new Intent(Addarticolo.this, Lista.class);
            intent.putExtra("SESSION_ID", session_id);
            intent.putExtra("SESSION_NAME", session_name);
            startActivity(intent);
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

}