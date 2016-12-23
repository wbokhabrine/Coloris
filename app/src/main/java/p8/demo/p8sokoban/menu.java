package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by wissam on 07/11/16.
 */

public class menu extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);
        // charge le fichier menu.xml comme vue de l'activit�
        setContentView(R.layout.menu);


        // setContentView(R.layout.menu);
        // recuperation de la vue une voie cree � partir de son id
        //mSokobanView.setVisibility(View.VISIBLE);
        // rend visible la vue
    }

    public void LoadLevel1(View view) {
        Intent intent= new Intent(this,p8_Sokoban.class);
        intent.putExtra("lvl", "1");
        startActivity(intent);

    }

   public  void LoadLevel2(View view) {
        Intent intent= new Intent(this,p8_Sokoban.class);
        intent.putExtra("lvl", "2");
        startActivity(intent);

    }

    public void LoadLevel3(View view) {
        Intent intent= new Intent(this,p8_Sokoban.class);
        intent.putExtra("lvl", "3");
        startActivity(intent);

    }


}
