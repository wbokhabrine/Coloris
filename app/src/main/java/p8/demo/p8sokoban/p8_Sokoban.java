package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

// declaration de notre activity h�rit�e de Activity


public class p8_Sokoban extends Activity {

    private SokobanView mSokobanView;
    private UserData userData;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);

        userData=new UserData(this);
        Intent intent = getIntent();
       // String lvl = intent.getStringExtra("lvl");
        userData.setActiveSound(Boolean.parseBoolean(intent.getStringExtra("sound")));
        userData.setGameSaved(true);
        setContentView(R.layout.main);
        mSokobanView = (SokobanView) findViewById(R.id.SokobanView);
       // mSokobanView.setLvl(Integer.parseInt(lvl));
        mSokobanView.setUserData(userData);
        mSokobanView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed(){
        // System.out.println("teeeest");
        mSokobanView.in=false;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("gameSaved", Boolean.toString(userData.getGameSaved()));
        setResult(1, returnIntent);
        super.onBackPressed();


    }

    @Override
    public void onPause(){
        //on écris les données avant de mettre sur pause
        userData.writeUserConfigData();
        if(userData.getGameSaved()){
            userData.writeUserGameData();
        }
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

}