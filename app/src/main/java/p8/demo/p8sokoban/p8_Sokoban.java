package p8.demo.p8sokoban;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// declaration de notre activity h�rit�e de Activity


public class p8_Sokoban extends Activity {

    private SokobanView mSokobanView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String lvl = intent.getStringExtra("lvl");

        setContentView(R.layout.main);
        mSokobanView = (SokobanView) findViewById(R.id.SokobanView);
        mSokobanView.setLvl(Integer.parseInt(lvl));
        mSokobanView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed(){
       // System.out.println("teeeest");
        mSokobanView.in=false;
        super.onBackPressed();


    }





}