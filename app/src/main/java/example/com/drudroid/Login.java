package example.com.drudroid;



import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import example.com.drupalun.R;


public class Login extends Activity {

//rendo reperibili a tutto il codice le seguenti stringhe che conterrano tutte le informazione della sessione
    public String session_name;
    public String session_id;
    public String name;
    public String user;
    public String pass;
    public String pass1;
    public String mail;
    public String uid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }





    private class LoginProcess extends AsyncTask<String, Void, Integer> {

        protected Integer doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
         //imposto result1 a 2 in modo che se la irsposta dal server non restitisce il codice di verifica
           // di avvenuta connessione riapre l'activity login
            Integer result1=2;

          //setto l'endpoint di drupal fortnito dal modulo services
            HttpPost httppost = new HttpPost("http://uniwalltest.altervista.org/portale/?q=app/user/login");

//prendo gli elementi necessari per effettuare la connessione
            EditText username= (EditText) findViewById(R.id.editUsername);
            EditText password= (EditText) findViewById(R.id.editPassword);




            try {





              //passo le stringhe password e username prense dall'ui e le inserico nelle variabili globali pass e user
                pass = password.getText().toString();
                user=username.getText().toString().trim();

                //creo un nuovo oggetto json e carico al suo interno le informazione per effettuare il login
                JSONObject json = new JSONObject();


                json.put("username", user);
                json.put("password", pass);


                StringEntity se = new StringEntity(json.toString());

                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                //invio la i dati con httppost
                httppost.setEntity(se);


                //conservo la risposta dal server in response

                HttpResponse response = httpclient.execute(httppost);

               //dichiaro due variabili che conterrano in modo differente la risposta dal server
                String jsonResponse = EntityUtils.toString(response.getEntity());
                Integer responseStatus = response.getStatusLine().getStatusCode();


                JSONObject  jsonObject = new JSONObject(jsonResponse);

                JSONObject resultJSON = new JSONObject();

                resultJSON.put("responseStatus", responseStatus);
               //verifico se il codice ottenuto con responsestatus è 200(avvenuta connessione..sessione creata)

                if (responseStatus==200) {

                //carrico tutto le informazioni ottenute da jsonResponse e le carico nelle variabili globali per conservarle
                    session_name = jsonObject.getString("session_name");
                    session_id = jsonObject.getString("sessid");
                    JSONObject j_user = jsonObject.getJSONObject("user");
                    name = j_user.getString("name");
                    mail = j_user.getString("mail");
                    uid = j_user.getString("uid");
                    pass1= password.getText().toString();

                    result1=1;


                 }



            }catch (Exception e) {
                e.printStackTrace();

            }

            return  result1 ;


        }


        protected void onPostExecute(Integer result1) {
         //se il doInbackground restituisce come valore di ritorno 2 la connessione non è stata stabilita
         // e riapre l'activity login con un messaggio di errore
            if(result1==2){
                Toast.makeText(getApplicationContext(),
                        "Errore:Username o password errati", Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(Login.this, Login.class);
                startActivity(intent1);



            }
            //se il doInbackground restituisce come valore di ritorno 1 la connessione  è stata stabilita
            // e apre l'activity Listactovity con un messaggio di login effettuato

         else if(result1==1) {
                Toast.makeText(getApplicationContext(),
                        "Login effettuato", Toast.LENGTH_LONG).show();
             Intent intent = new Intent(Login.this, Lista.class);

             intent.putExtra("uid", uid);
             intent.putExtra("name", name);
             intent.putExtra("mail", mail);
             intent.putExtra("password", pass1);
             intent.putExtra("SESSION_ID", session_id);
             intent.putExtra("SESSION_NAME", session_name);


             startActivity(intent);
         }


        }

    }


//effettua il processo di login prima descritto quando si preme sul bottone di login
    public void doLoginButton_click(View view){
        new LoginProcess().execute();
    }

//Apre l'acitvity Resgistrazione se si preme il bottone registrazione

    public void doRegistrazioneButton_click(View view){


        Intent intent = new Intent(this ,Registrazione.class);

        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

}