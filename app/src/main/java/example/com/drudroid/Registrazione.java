package example.com.drudroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import android.app.Activity;

import example.com.drupalun.R;


public class Registrazione extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

    }


    private class RegisterProcess extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();

            //setto l'endpoint del sito di drupal con cui effettuare la registrazione di un nuovo utente
            HttpPost httppost = new HttpPost("http://uniwalltest.altervista.org/portale/?q=app/user/register");


            try {

                //prendo gli elementi necessari per effettuare la connessione

                EditText username = (EditText) findViewById(R.id.editname);
                EditText password = (EditText) findViewById(R.id.editPass);
                EditText email = (EditText) findViewById(R.id.editemail);
                //imposto valore a 1 per inviare un 1 intero che verra compreso nei dati precendenti per creare l'utente
                int valore=1;

                //creo un nuovo oggetto json e carico al suo interno le informazione per effettuare la registrazione

                JSONObject json = new JSONObject();

                //inserico in json tutti i dati necessati
                json.put("name", username.getText().toString().trim());
                json.put("pass", password.getText().toString().trim());
                json.put("mail", email.getText().toString().trim());
                json.put("conf_mail", email.getText().toString().trim());
                json.put("status",valore);



                StringEntity se = new StringEntity(json.toString());
                //imposto e invio i dati al server
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);
                httpclient.execute(httppost);






            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }


        protected void onPostExecute(Integer result) {

            //una volta creato l'utente torna all'acitvity login
            Intent intent = new Intent(Registrazione.this, Login.class);


            startActivity(intent);
        }
    }

    //avvia il processo di registrazione nel momento che si preme il bottone invia
    public void doRegistrazioneButton_click(View view){
        new RegisterProcess().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registrazione, menu);
        return true;
    }


}
