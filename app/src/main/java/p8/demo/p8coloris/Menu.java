package p8.demo.p8coloris;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wissam on 07/11/16.
 */

public class Menu extends Activity {
    private int state;
    private UserData userData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);
        // charge le fichier Menu.xml comme vue de l'activité
        setContentView(R.layout.menu);

        // l'etat 0 permet de savoir que on est dans le layout Menu
        state = 0;

        //Récupération des informations de la base de donnée
        userData=new UserData(this);
        userData.readUserData(); // on lis les données en interne
        if(!userData.getPrefCharged()){ // on initialise par défaut si aucune préf n'a pu etre chargée
            userData.setActiveSound(true);
            userData.setGameSaved(false);
        }

        //pour debuger, affiche les variables son activé et partie sauveargée
        Log.i("debug","son: "+Boolean.toString(userData.getActiveSound()));
        Log.i("debug","game exist: "+Boolean.toString(userData.getGameSaved()));

    }


    /* lancement du jeu coloris a faire*/
    public void New_game(View view) {
        Intent intent = new Intent(this, Coloris.class);
        intent.putExtra("mode", "0");
        intent.putExtra("sound",Boolean.toString(userData.getActiveSound()));
        startActivityForResult(intent,0); // on attend en résultat l'état du jeu (en cours ou terminé)
    }

    //permet de reprendre une partie sauvegardé
    public void Continue(View view) {
        if (userData.getGameSaved()) {
            Intent intent = new Intent(this, Coloris.class);
            intent.putExtra("mode", "1");
            intent.putExtra("sound",Boolean.toString(userData.getActiveSound()));
            startActivityForResult(intent,0);
        } else {
            Toast.makeText(this, "Impossible de reprendre une partie, aucune partie n'existe.",
                    Toast.LENGTH_LONG).show();
        }

    }

    // Affiche les 3 meilleurs scores
    public void Best_score(View view) {

        String Pseudo1 = userData.getNameHighScoreAtIndex(0), bestScore1 = Integer.toString(userData.getHighScoreAtIndex(0));
        String Pseudo2 = userData.getNameHighScoreAtIndex(1), bestScore2 = Integer.toString(userData.getHighScoreAtIndex(1));
        String Pseudo3 = userData.getNameHighScoreAtIndex(2), bestScore3 = Integer.toString(userData.getHighScoreAtIndex(2));


        if(Pseudo1.equals(""))Pseudo1="None";
        if(Pseudo2.equals(""))Pseudo2="None";
        if(Pseudo3.equals(""))Pseudo3="None";

        setContentView(R.layout.best_score);
        // l'etat 1 permet de savoir que on est dans le layout best_score
        state = 1;

        TextView text1 = (TextView) findViewById(R.id.textView);
        TextView text2 = (TextView) findViewById(R.id.textView2);
        TextView text3 = (TextView) findViewById(R.id.textView3);

        text1.setText(Pseudo1 + ":" + "\n" + bestScore1);
        text2.setText(Pseudo2 + ":" + "\n" + bestScore2);
        text3.setText(Pseudo3 + ":" + "\n" + bestScore3);
    }




    public void System_settings(View view) {
        setContentView(R.layout.system_settings);
        // l'etat 2 permet de savoir que on est dans le layout system
        state = 2;

    }

    // onclick pour active le son
    public void Song_on(View view) {
        userData.setActiveSound(true);
        Toast.makeText(this, "Son activé",
                Toast.LENGTH_SHORT).show();
    }

    // onclick pour desactive le son
    public void Song_off(View view) {
        userData.setActiveSound(false);
        Toast.makeText(this, "Son désactivé",
                Toast.LENGTH_SHORT).show();

    }

    // onclick pour quitter le Menu
    public void Exit_game(View view) {
        finish();
    }

    // le button back permet de quitter le Menu ou revenir en arriere a partir du state 1 ou 2
    @Override
    public void onBackPressed() {
        if (state == 0)
            super.onBackPressed();
        else if (state == 1 || state == 2) {
            setContentView(R.layout.menu);
            state = 0;
        }


    }

    @Override
    public void onResume(){
        super.onResume();
        userData.readUserData();
    }

    @Override
    public void onPause(){
        //on écris les données avant de mettre sur pause
        userData.writeUserConfigData();
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    //onActivityResult, on récupere l'état du jeu (en cours ou terminé)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode==0 && resultCode==1 ) {
            String intentReturn = data.getStringExtra("gameSaved");
            userData.setGameSaved(Boolean.parseBoolean(intentReturn));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}