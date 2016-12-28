package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

// declaration de notre activity h�rit�e de Activity


public class Coloris extends Activity {

    private ColorisView mColorisView;
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
        setContentView(R.layout.main);
        mColorisView = (ColorisView) findViewById(R.id.SokobanView);
        Log.i("debug","intent: "+mode);
        if(Integer.parseInt(mode)==0){
            //on lance une nouvelle partie
            Log.i("debug","new game");
            userData.setGameSaved(false);
            userData.writeUserData();
            mColorisView.setReload(true);
        }else if (Integer.parseInt(mode)==1){
            Log.i("debug","load partie, time: "+userData.getTimer());
            //on charge la partie sauvegardée
            mColorisView.setReload(false);
            mColorisView.setCarte(userData.getGameGrid());
            mColorisView.setTriplet(userData.getTripletTab());
            mColorisView.setOrientation(userData.getOrientationTab());
            mColorisView.setScore(userData.getScore());
            mColorisView.setTime(userData.getTimer());
        }
        mColorisView.setUserData(userData);
        mColorisView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed(){
        // System.out.println("teeeest");
        mColorisView.in=false;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("gameSaved", Boolean.toString(userData.getGameSaved()));
        setResult(1, returnIntent);
        super.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();
        userData.readUserData();
        if(userData.getGameSaved() ){
            mColorisView.setReload(false);
            mColorisView.setCarte(userData.getGameGrid());
            mColorisView.setTriplet(userData.getTripletTab());
            mColorisView.setOrientation(userData.getOrientationTab());
            mColorisView.setScore(userData.getScore());
            mColorisView.setTime(userData.getTimer());
        }
        mColorisView.setPause(false);
    }

    @Override
    public void onPause(){
        //on écris les données avant de mettre sur pause
        userData.writeUserConfigData();
        if(userData.getGameSaved()){
            Log.i("debug","partie saved, time: "+userData.getTimer());
            userData.writeUserGameData();
        }
        mColorisView.setPause(true);
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