package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wissam on 07/11/16.
 */

public class menu extends Activity {
    private int state;
    private boolean song;
    private Boolean gameAlreadyExist;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);
        // charge le fichier menu.xml comme vue de l'activité
        setContentView(R.layout.menu);
        // l'etat 0 permet de savoir que on est dans le layout menu
        state = 0;

        //Récupération des informations de la base de donnée
        song=true;
        gameAlreadyExist=false;



        // setContentView(R.layout.menu);
        // recuperation de la vue une voie cree � partir de son id
        //mSokobanView.setVisibility(View.VISIBLE);
        // rend visible la vue
    }


    /* lancement du jeu coloris a faire*/
    public void New_game(View view) {
        Intent intent = new Intent(this, p8_Sokoban.class);
        intent.putExtra("lvl", "1");
        startActivity(intent);

    }

    //permet de reprendre une partie sauvegardé
    public void Continue(View view) {
        if (gameAlreadyExist) {
           /* a faire*/ ;
        } else {
            Toast.makeText(this, "Impossible de reprendre une partie, aucune partie n'existe.",
                    Toast.LENGTH_LONG).show();
        }

    }

    // Affiche les 3 meilleurs scores
    public void Best_score(View view) {
        String Pseudo1 = "NONE", bestScore1 = "0.0";
        String Pseudo2 = "NONE", bestScore2 = "0.0";
        String Pseudo3 = "NONE", bestScore3 = "0.0";


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
        song=true;
        Toast.makeText(this, "Son activé",
                Toast.LENGTH_SHORT).show();
    }

    // onclick pour desactive le son
    public void Song_off(View view) {
        song=false;
        Toast.makeText(this, "Son désactivé",
                Toast.LENGTH_SHORT).show();

    }

    // onclick pour quitter le menu
    public void Exit_game(View view) {
        finish();
    }

    // le button back permet de quitter le menu ou revenir en arriere a partir du state 1 ou 2
    @Override
    public void onBackPressed() {
        if (state == 0)
            super.onBackPressed();
        else if (state == 1 || state == 2) {
            setContentView(R.layout.menu);
            state = 0;
        }


    }
}
