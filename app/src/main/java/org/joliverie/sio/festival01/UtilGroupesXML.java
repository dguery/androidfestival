package org.joliverie.sio.festival01;

import android.util.Xml;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import org.joliverie.sio.festival01.metier.Groupe;
import org.xmlpull.v1.XmlPullParser;


/**
 * Created by jp on 19/02/2018.
 */

public class UtilGroupesXML {
    /**
     * Obtient une liste des groupes à partir d'un flux XML via une ressource web (http)
     * @param urlXmlressource l'url à solliciter
     * @param defaultContent un contenu par défaut si chargement impossible
     * @param dest liste d'instance de groupes à valoriser (par pays, seuls l'id, le nom et le nom du pays sont valorisés)
     */

    /** test du parser avec un fichier xml en dur
     * static public void parseListePays(Context context, String defaultContent, ArrayList<Pays> dest) {
     * XmlPullParser parser = context.getResources().getXml(R.xml.groupes);
     */

    static public void parseListeGroupes(String urlXmlressource, String defaultContent, ArrayList<Groupe> dest) {
        XmlPullParser parser = Xml.newPullParser();


        try {

            // auto-detect the encoding from the stream
            parser.setInput(getInputStream(urlXmlressource, defaultContent), null);

            int eventType = parser.getEventType();
            Groupe currentGroupe = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("groupe")) {
                            currentGroupe = new Groupe();
                        } else if (currentGroupe != null) {
                            if (name.equalsIgnoreCase("id")) {
                                currentGroupe.setId(parser.nextText());
                            } else if (name.equalsIgnoreCase("nom")) {
                                currentGroupe.setNom(parser.nextText());
                            }  else if (name.equalsIgnoreCase("nom-pays")) {
                                currentGroupe.setNomPays(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        // si fin de balise country, on ajoute l'objet dans la liste
                        if (name.equalsIgnoreCase("groupe") && currentGroupe != null) {
                            Log.i("ajout groupe : ", currentGroupe.toString());
                            dest.add(currentGroupe);
                        }
                        // fin des groupes ?
                        else if (name.equalsIgnoreCase("groupes")) {
                            // oui, on signe l'arrêt de la boucle
                            done = true;
                        }
                        break;
                }
                // avance dans le flux
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static private InputStream getInputStream(String strUrl, String defaulContent) {
        InputStream res = null;
        try {
            Log.i("url", strUrl);
            URL url = new URL(strUrl);
            res = url.openConnection().getInputStream();
        } catch (Exception e) {
            Log.d("Erreur de lecture", strUrl + ":"+ e.getMessage());
        }
        if (res == null)
            // retourne quelque chose tout de même...
            res = new ByteArrayInputStream(defaulContent.getBytes());
        return res;
    }
}
