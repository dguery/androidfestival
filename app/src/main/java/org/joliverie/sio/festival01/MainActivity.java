package org.joliverie.sio.festival01;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.joliverie.sio.festival01.metier.Groupe;
import org.joliverie.sio.festival01.metier.Representation;
import org.joliverie.sio.festival01.metier.Lieu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

public class MainActivity extends AppCompatActivity {
    private static final String REPRESENTATIONS_EMPTY  = "<representations><representation><nom>Vide!</nom></representation></representations>";

    // 10.0.2.2 : pour atteindre une 2ressource sur la machine hote de l'AVD
    //private static final String URL_XMLRESSOURCE = "172.15.6.240/FestivalPHP2018_5-master/rest/getAllRepresentations.php";

    private static final String URL_XMLRESSOURCE = "http://192.168.1.16/FestivalPHP2018_5-master/rest/getAllRepresentations.php";
    private ListView listViewRepresentations;
    private ArrayList<Representation> lesRepresentations;
    private ReqHTTPTask reqHTTPTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.listViewRepresentations = (ListView) findViewById(R.id.listViewRepresentation);
        lesRepresentations = new ArrayList<Representation>();
        this.listViewRepresentations.setAdapter(new ArrayAdapter<Representation>(this,
                simple_list_item_1, lesRepresentations));

        if (reqHTTPTask != null) {
            return;
        }

        // instanciation et lancement de la tâche asynchroone de récupération des pays dans la BDD
        reqHTTPTask = new ReqHTTPTask();
        reqHTTPTask.execute((Void) null);

    }
    // Début définition classe ReqHTTPTask
    public class ReqHTTPTask extends AsyncTask<Void, Void, Boolean> {

        // exécution du traitement dans un autre Thread que le thread principal (UI)
        // récupération des données XML et remplissage dans une l'ArrayList lesGroupes
        @Override
        protected Boolean doInBackground(Void... params) {
                // modification de la liste des groupes
                lesRepresentations.clear();
                UtilRepresentationsXML.parseListeRepresentations(URL_XMLRESSOURCE, REPRESENTATIONS_EMPTY, lesRepresentations);
                return true;
        }

        // exécution du traitement dans le thread principal (UI) : il agit sur les contrôles graphiques
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                // affichage du nombre de groupes (50 normalement)
                Toast.makeText(getApplicationContext(),
                        "Nombre de représentations :" + lesRepresentations.size(), Toast.LENGTH_LONG).show();
                // récupération du modèle de données de la ListView
                ArrayAdapter<Representation> modelDeListeRepresentations = (ArrayAdapter<Representation>) listViewRepresentations
                       .getAdapter();
                // envoi du signal que les données (lesRepresentations) ont été modifiées
                modelDeListeRepresentations.notifyDataSetChanged();
                // affichage du message succès
                Toast.makeText(getApplicationContext(), "Succès : " + success,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "erreur de chargement des représentations", Toast.LENGTH_LONG).show();
            }
        }
    } // fin definition classe ReqHTTPTask
}
