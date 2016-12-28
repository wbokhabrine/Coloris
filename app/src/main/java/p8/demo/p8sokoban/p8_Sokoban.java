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
        Log.i("debug","P8soko oncreate");
        userData=new UserData(this);
        userData.readUserData();
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode"); // 0 = new, 1 = continue last game
        userData.setActiveSound(Boolean.parseBoolean(intent.getStringExtra("sound")));
        userData.setGameSaved(true);
        setContentView(R.layout.main);
        mSokobanView = (SokobanView) findViewById(R.id.SokobanView);
        Log.i("debug","intent: "+mode);
        if(Integer.parseInt(mode)==0){
            //on lance une nouvelle partie
            Log.i("debug","new game");
            mSokobanView.setReload(true);
        }else if (Integer.parseInt(mode)==1){
            Log.i("debug","load partie, time: "+userData.getTimer());
            //on charge la partie sauvegardée
            mSokobanView.setReload(false);
            mSokobanView.setCarte(userData.getGameGrid());
            mSokobanView.setTriplet(userData.getTripletTab());
            mSokobanView.setOrientation(userData.getOrientationTab());
            mSokobanView.setScore(userData.getScore());
            mSokobanView.setTime(userData.getTimer());
        }
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
    public void onResume(){
        super.onResume();
        userData.readUserData();
    }

    @Override
    public void onPause(){
        //on écris les données avant de mettre sur pause
        userData.writeUserConfigData();
        if(userData.getGameSaved()){
            Log.i("debug","partie saved, time: "+userData.getTimer());
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