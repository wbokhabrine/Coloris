package p8.demo.p8coloris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;

import p8.demo.p8coloris.R;

public class ColorisView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private UserData userData;

    private MediaPlayer songPose;
    private MediaPlayer songAlignement;
    private MediaPlayer songFlip;

    // Declaration des images
    private Bitmap 		 block_vide;
    private Bitmap 		 block_bleu;
    private Bitmap 		 block_blanc;
    private Bitmap 		 block_jaune;
    private Bitmap 		 block_rose;
    private Bitmap 		 block_rouge;
    private Bitmap 		 block_vert;
    private Bitmap 		fullGrid,endTime;



	// Declaration des objets Ressources et Context permettant d'acc�der aux ressources de notre application et de les charger
    private Resources 	mRes;
    private Context 	mContext;

    //boolean savoir si on doit creer une partie
    private Boolean reload=true;
    //score et temps
    private int score=0;
    private Integer time; // compte à rebours en secondes
    long t1 = 0;
    long t2 = 0;
    long tDiff = 0;
    Boolean pause=false;
    // tableau modelisant la carte du jeu
    int[][] carte=new int [8][8];

    // 3 tableau généré aléatoirement
    private int [][] tripletTab = new int[3][3];
    private int orientation[]={0, 0, 0}; //orientation des tableaux, 4 etats de 0 à 3 inclu
    // 3 tab 2 coordonnée X Y pour le deplacement d'un tableau avec le touché
    private float [][] ontouchtab = {
            {0,0},
            {0,0},
            {0,0}
    };

    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonn�es en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonn�es en X du point d'ancrage de notre carte

    // taille de la carte
    static final int    carteWidth    = 8;
    static final int    carteHeight   = 8;
    static final int    carteTileSize = 33;

    // constante modelisant les differentes couleur de cases
    static final int    CST_block_vide     = 0;
    static final int    CST_block_blanc     = 1;
    static final int    CST_block_bleu     = 2;
    static final int    CST_block_jaune     = 3;
    static final int    CST_block_rose    = 4;
    static final int    CST_block_rouge     = 5;
    static final int    CST_block_vert     = 6;

    // tableau de reference du terrain
    int [][] ref    = {
        {CST_block_vide, CST_block_vide, CST_block_vide,CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide,CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide,CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide}
    };

        // thread utiliser pour animer les zones de depot des diamants
        public     boolean in      = true;
        private    Thread  cv_thread;

        SurfaceHolder holder;
        
        Paint paint;
        
    /**
     * The constructor called from the main JetBoy activity
     * 
     * @param context 
     * @param attrs 
     */
    public ColorisView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed        
    	holder = getHolder();
        holder.addCallback(this);    
        
        // chargement des images
        mContext	= context;
        mRes 		= mContext.getResources();
        block_vide 		= BitmapFactory.decodeResource(mRes, R.drawable.block_vide);
        block_bleu 		= BitmapFactory.decodeResource(mRes, R.drawable.block_bleu);
        block_blanc 		= BitmapFactory.decodeResource(mRes, R.drawable.block_blanc);
        block_jaune		= BitmapFactory.decodeResource(mRes, R.drawable.block_jaune);
        block_rose 		= BitmapFactory.decodeResource(mRes, R.drawable.block_rose);
        block_rouge 		= BitmapFactory.decodeResource(mRes, R.drawable.block_rouge);
        block_vert 		= BitmapFactory.decodeResource(mRes, R.drawable.block_vert);
        endTime 		= BitmapFactory.decodeResource(mRes, R.drawable.endtime);
        fullGrid 		= BitmapFactory.decodeResource(mRes, R.drawable.fullgrid);

    	// initialisation des parmametres du jeu
    	initparameters();

        // prise de focus pour gestion des touches
        setFocusable(true);
        setOnTouchListener(_otc);


        pause = false;
    }

    // chargement du niveau a partir du tableau de reference du niveau
    public void loadlevel() {
        //on initialise le chrono au temps actuel
        t1=System.currentTimeMillis();
        if(userData != null)
            userData.setGameSaved(true);
        if(reload) {
            score=0;
            time=60;
            carte = ref;

            // init du triplettab
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    tripletTab[i][j] = (int) (Math.random() * (6 - 1 + 1)) + 1;
                    //Log.i("debug","trip: "+ tripletTab[i][j]);
                }
            }
        }

    }



    // initialisation du jeu
    public void initparameters() {
        paint = new Paint();
    	paint.setColor(0xff0000);
    	
    	paint.setDither(true);
    	paint.setColor(0xFFFFFF00);
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeJoin(Paint.Join.ROUND);
    	paint.setStrokeCap(Paint.Cap.ROUND);
    	paint.setStrokeWidth(3);    	
    	paint.setTextAlign(Paint.Align.LEFT);
        carteTopAnchor  = carteTileSize;
        carteLeftAnchor = (getWidth()- carteWidth*carteTileSize)/2;

        songPose=MediaPlayer.create(((Activity) mContext), R.raw.songpose);
        songAlignement=MediaPlayer.create(((Activity) mContext), R.raw.songalignement);
        songFlip=MediaPlayer.create(((Activity) mContext), R.raw.songflip);


        loadlevel();

        // creation du thread
        cv_thread   = new Thread(this);
        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
        	cv_thread.start();
        	Log.e("-FCT-", "cv_thread.start()");
        }
    }

    private void paintEndTime(Canvas canvas) {
        int pixelx=240;
        int pixely=135;
        canvas.drawBitmap(endTime, (getWidth()- pixelx)/2, carteTopAnchor, null);
    }

    private void paintFullGrid(Canvas canvas) {
        int pixelx=240;
        int pixely=135;
        canvas.drawBitmap(fullGrid, (getWidth()- pixelx)/2, carteTopAnchor, null);
    }
    
    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
    	for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                switch (carte[i][j]) {
                    case CST_block_vide:
                    	canvas.drawBitmap(block_vide, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                    	break;
                    case CST_block_blanc:
                        canvas.drawBitmap(block_blanc, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block_bleu:
                        canvas.drawBitmap(block_bleu, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block_jaune:
                        canvas.drawBitmap(block_jaune, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block_rose:
                        canvas.drawBitmap(block_rose, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block_rouge:
                        canvas.drawBitmap(block_rouge, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_block_vert:
                        canvas.drawBitmap(block_vert, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                }
            }
        }
    }


    private void paintTripletTab(Canvas canvas) {
        int halfCarteLeftAnchor = carteLeftAnchor/2;

        //i*halfCarteLeftAnchor  pour decaler les 3 tableau en x
        // j*carteTileSize afficher chaque cube en x
        // i*3*carteTileSize prendre en compte l'affichage de chaque case des tab precedent
        //ontouchtab[i][0] permet le scroll en X

        for (int i=0; i< 3 ; i++) {
            for (int j=0; j< 3; j++) {
                if((orientation[i]&0x01) ==0)
                    switch (tripletTab[i][j]) {
                        case CST_block_blanc:
                            canvas.drawBitmap(block_blanc, i * halfCarteLeftAnchor + j * carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteTileSize + carteWidth * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_bleu:
                            canvas.drawBitmap(block_bleu, i * halfCarteLeftAnchor + j * carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteTileSize + carteWidth * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_jaune:
                            canvas.drawBitmap(block_jaune, i * halfCarteLeftAnchor + j * carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteTileSize + carteWidth * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_rose:
                            canvas.drawBitmap(block_rose, i * halfCarteLeftAnchor + j * carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteTileSize + carteWidth * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_rouge:
                            canvas.drawBitmap(block_rouge, i * halfCarteLeftAnchor + j * carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteTileSize + carteWidth * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_vert:
                            canvas.drawBitmap(block_vert, i * halfCarteLeftAnchor + j * carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteTileSize + carteWidth * carteTileSize + ontouchtab[i][1], null);
                            break;
                    }
                else{
                    switch (tripletTab[i][j]) {
                        case CST_block_blanc:
                            canvas.drawBitmap(block_blanc, i * halfCarteLeftAnchor + carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteWidth * carteTileSize + j * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_bleu:
                            canvas.drawBitmap(block_bleu, i * halfCarteLeftAnchor + carteTileSize + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteWidth * carteTileSize + j * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_jaune:
                            canvas.drawBitmap(block_jaune, i * halfCarteLeftAnchor + carteTileSize  + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteWidth * carteTileSize + j * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_rose:
                            canvas.drawBitmap(block_rose, i * halfCarteLeftAnchor + carteTileSize  + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteWidth * carteTileSize + j * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_rouge:
                            canvas.drawBitmap(block_rouge, i * halfCarteLeftAnchor + carteTileSize  + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteWidth * carteTileSize + j * carteTileSize + ontouchtab[i][1], null);
                            break;
                        case CST_block_vert:
                            canvas.drawBitmap(block_vert, i * halfCarteLeftAnchor + carteTileSize  + i * 3 * carteTileSize + ontouchtab[i][0],
                                    carteTopAnchor*2 + carteWidth * carteTileSize + j * carteTileSize + ontouchtab[i][1], null);
                            break;
                    }
                }
            }
        }
    }

    private void paintInfoBar(Canvas canvas){
        int nextHighScore=0;
        for(int i=userData.getTabHighScore().length-1;i>=0;i--){
            if(score <  userData.getTabHighScore()[i]){
                nextHighScore=userData.getTabHighScore()[i];
                break;
            }
        }
        if(userData.getTabHighScore()[0] <=score){
            nextHighScore=score;
        }
        Rect timeBounds=new Rect();
        Rect scoreBounds=new Rect();
        Rect highScoreBounds=new Rect();
        String strScore=new String("Score: "+Integer.toString(score));
        String strHighScore=new String("Next HighScore: "+Integer.toString(nextHighScore));
        String strTime=new String("Temps: "+Integer.toString(time));
        Paint paint = new Paint();


        paint.setColor(Color.WHITE);
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);

        paint.getTextBounds(strScore,0,strScore.length(),scoreBounds);
        paint.getTextBounds(strHighScore,0,strHighScore.length(),highScoreBounds);
        paint.getTextBounds(strTime,0,strTime.length(),timeBounds);

        int espacement=(canvas.getWidth()- (timeBounds.width()+scoreBounds.width()+highScoreBounds.width()) ) / 2 ;
        int maxHeigh=scoreBounds.height();
        if(highScoreBounds.height()>maxHeigh){
            maxHeigh=highScoreBounds.height();
        }
        if(timeBounds.height()>maxHeigh){
            maxHeigh=timeBounds.height();
        }
        canvas.drawText(strScore,0,canvas.getHeight()-maxHeigh,paint);
        canvas.drawText(strHighScore,scoreBounds.width()+espacement,canvas.getHeight()-maxHeigh,paint);
        canvas.drawText(strTime,scoreBounds.width()+espacement+highScoreBounds.width()+espacement,canvas.getHeight()-maxHeigh,paint);
    }

    
    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
		canvas.drawRGB(10, 10, 10);
        if(time<=0 && !isFullGrid() ){
            paintcarte(canvas);
            paintTripletTab(canvas);
            paintInfoBar(canvas);
            paintEndTime(canvas);
        }else if(isFullGrid()){
            paintcarte(canvas);
            paintTripletTab(canvas);
            paintInfoBar(canvas);
            paintFullGrid(canvas);
        } else {
            paintcarte(canvas);
            paintTripletTab(canvas);
            paintInfoBar(canvas);
        }    	   	
        
    }
    
    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	Log.i("-> FCT <-", "surfaceChanged "+ width +" - "+ height);
    	initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
    	Log.i("-> FCT <-", "surfaceCreated");    	        
    }

    
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("debug","surface destroyed, gameSaved: "+Boolean.toString(userData.getGameSaved()));
        in=false;

        if(userData.getGameSaved()){
            userData.setTimer(time);
            userData.setScore(score);
            userData.setTripletTab(tripletTab);
            userData.setGameGrid(carte);
            userData.setOrientationTab(orientation);
            userData.writeUserData();
            Log.i("debug","userData up to date time: "+userData.getTimer());
        }
        Log.i("-> FCT <-", "surfaceDestroyed");
    }    

    /**
     * run (run du thread cr��)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on lib�re le canvas
     */
    public void run() {
    	Canvas c = null;
        while (in) {
                try {
                    cv_thread.sleep(40);
                    try {
                        if(!pause && !isFullGrid()) {
                            t2 = System.currentTimeMillis();
                            tDiff = t2 - t1;
                            if (tDiff >= 1000) {
                                if (time > 0 && userData.getGameSaved()) {
                                    time -= 1;
                                }
                                t1 = System.currentTimeMillis();
                            }
                        }
                        c = holder.lockCanvas(null);
                        nDraw(c);
                    } finally {
                        if (c != null) {
                            holder.unlockCanvasAndPost(c);
                        }
                    }
                } catch (Exception e) {
                    Log.e("-> RUN <-", "PB DANS RUN");
                }
        }

    }

    public void setOrientation(int[] tabOrientation){
        for(int i=0;i<tabOrientation.length;i++){
            this.orientation[i]=tabOrientation[i];
        }
    }

    public void setCarte(int[][]carte){
        for(int i=0;i<carte.length && i<this.carte.length;i++){
            for(int y=0;y<carte[i].length && y<this.carte[i].length;y++){
                this.carte[i][y]=carte[i][y];
            }
        }
    }

    public void setTriplet(int[][]triplet){
        for(int i=0;i<triplet.length && i<this.tripletTab.length;i++){
            for(int y=0;y<triplet[i].length && y<this.tripletTab[i].length;y++){
                this.tripletTab[i][y]=triplet[i][y];
            }
        }
    }
    

// retourne 0, 1 ou 2 qui correspond à l'indice du tripletTab touché
 int hitTripletTab( MotionEvent event){
     float tx=carteTileSize,ty=carteTileSize;
     float  y=event.getY(),x=event.getX();

     float margex;
     float margey;

     int resx;
     int resy;

     int halfCarteLeftAnchor=carteLeftAnchor/2;


     for(int i=0;i < 3;i++){
         if((orientation[i]&0x01)==1) {
             margex = i * halfCarteLeftAnchor + carteTileSize + i * 3 * carteTileSize;
             margey = carteTopAnchor*2 + carteWidth * carteTileSize ;
         }
         else{
             margex = i * halfCarteLeftAnchor + i * 3 * carteTileSize;
             margey = carteTopAnchor*2 + carteWidth * carteTileSize + carteTileSize;
         }

         resx=(int)((x-margex)/tx);
         resy=(int)((y-margey)/ty);


         if((orientation[i]&0x01)==1) {
             if (resx == 0 && resy == 0 || resx == 0 && resy == 1 || resx == 0 && resy == 2) {
                 return i;
             }

         }
         else{
             if (resx == 0 && resy == 0 || resx == 1 && resy == 0 || resx == 2 && resy == 0) {
                 return i;
             }
         }


     }

    return -1;
 }
    // supprime les blocs de couleurs si il y'a alignement + incrementation du score
    public void colorAlignment(int resx,int resy, boolean orientation){
        int compteurx1,compteurx2;
        int compteury1,compteury2;
        int color;

        boolean [][]flag=new boolean[carteHeight][carteWidth];
        for(int i=0;i< carteHeight;i++)
            for(int j=0;j<carteWidth;j++)
                flag[i][j]=false;

        for(int i=0;i < carteHeight;i++)
            for(int j=0;j<carteWidth;j++){
                color=carte[i][j];
                if(color == CST_block_vide) continue;

                compteurx1=0;
                for(int x=j+1; x < carteWidth; x++){
                    if (carte[i][x] == color)
                        compteurx1 += 1;
                    else break;
                }
                compteurx2=0;
                for(int x=j-1; x >= 0; x--){
                    if (carte[i][x] == color)
                        compteurx2 += 1;
                    else break;
                }
                int totalx=compteurx1+compteurx2+1; // le +1 c'est pour celui qui est en centre (carte[i][j])
               // Log.i("compteur","x="+j+" y="+i+ "compteur x1="+compteurx1+" compteurx2="+compteurx2+" totalx="+totalx);
                if(totalx >= 3){
                    flag[i][j]=true;
                    for(int x=1 ; x < compteurx1;x++)
                        flag[i][j+x]=true;
                    for(int x=1 ; x < compteurx2; x++)
                        flag[i][j-x]=true;
                }



                compteury1=0;
                for(int y=i+1; y < carteHeight; y++){
                    if(carte[y][j] == color)
                        compteury1 += 1;
                    else break;
                }
                compteury2=0;
                for(int y=i-1; y >= 0; y--){
                    if (carte[y][j] == color)
                        compteury2 += 1;
                    else break;
                }
                int totaly=compteury1+compteury2+1; // le +1 c'est pour celui qui est en centre (carte[i][j])
               // Log.i("compteur","x="+j+" y="+i+ "compteur y1="+compteury1+" compteury2="+compteury2+" totaly="+totaly);
                if(totaly >= 3){
                    flag[i][j]=true;
                    for(int y=1 ; y < compteury1; y++)
                        flag[i+y][j]=true;
                    for(int y=1 ; y < compteury2; y++)
                        flag[i-y][j]=true;
                }
            }
        boolean tmp=false;
        for(int i=0; i < carteHeight; i++)
            for(int j=0; j < carteWidth; j++)
                if(flag[i][j] == true ) {
                    tmp=true;
                    carte[i][j] = CST_block_vide;
                    score+=1;
                }
        if(tmp == true)
            playSong(songAlignement);
    }

    // retourne s'il y'a eu un hit sur la carte et fais les actions correspondant au hit//
    boolean hitcarte(MotionEvent event){
        float tx=carteTileSize,ty=carteTileSize;
        float margex=carteLeftAnchor;
        float margey=carteTopAnchor;
        float  y=event.getY(),x=event.getX();

        //Log.i("fct ", "marge="+(x-margex));
        int resx=(int)((x-margex)/tx);
        int resy=(int)((y-margey)/ty);

        if(lastindex==-1)return false;
        if((orientation[lastindex]&0x01) == 1) {
            if(resx<8&& resy<9&&  resy>2 && (x-margex>=0)){
                Log.i("-> FCT <-", "Case carte (" + resx + "," + resy + ") touchée");
                // verifie si il y'a des cases vides + colorie la map si oui et regenere le tab
                if(carte[resy-1][resx] == CST_block_vide && carte[resy-2][resx] == CST_block_vide  && carte[resy-3][resx] ==  CST_block_vide ){
                    carte[resy-1][resx]=tripletTab[lastindex][2];
                    carte[resy-2][resx]=tripletTab[lastindex][1];
                    carte[resy-3][resx]=tripletTab[lastindex][0];

                    colorAlignment(resx, resy, true);

                    for (int j = 0; j < 3; j++)
                        tripletTab[lastindex][j] = (int) (Math.random() * (6 - 1 + 1)) + 1;
                    orientation[lastindex]=0;

                    playSong(songPose);
                }
                return true;
            }
        }
        else
            if(resx<7&& resy<9&&  resx>0 && resy>0) {
               Log.i("-> FCT <-", "Case carte (" + resx + "," + resy + ") touchée");
                if(carte[resy-1][resx-1] == CST_block_vide && carte[resy-1][resx] == CST_block_vide  && carte[resy-1][resx+1] ==  CST_block_vide ){
                    carte[resy-1][resx+1]=tripletTab[lastindex][2];
                    carte[resy-1][resx]=tripletTab[lastindex][1];
                    carte[resy-1][resx-1]=tripletTab[lastindex][0];

                    colorAlignment(resx, resy, true);

                    for (int j = 0; j < 3; j++)
                        tripletTab[lastindex][j] = (int) (Math.random() * (6 - 1 + 1)) + 1;
                    orientation[lastindex]=0;

                    playSong(songPose);
                }

                return true;
            }

        return false;
    }
    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            Log.i("-> FCT <-", "onTouchEvent: "+ event.getX());

    	return super.onTouchEvent(event);
    }

    void reverseTab(int indice){
        int tmp = tripletTab[indice][0];
        tripletTab[indice][0]= tripletTab[indice][2];
        tripletTab[indice][2]=tmp;
    }


    //permet de scroll les tableaux
    static int lastindex=-1;
    static int index=-1;
    private OnTouchListener _otc = new OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("Info","Ontouch");


            if(event.getAction()==MotionEvent.ACTION_DOWN){
                index= hitTripletTab(event);
                lastindex=index;
            }
            if(event.getAction()== MotionEvent.ACTION_UP){



                //La partie est fini on affiche le layout imput score
                if(time<=0 || isFullGrid() ) {
                    in=false;
                    setVisibility(INVISIBLE);
                    ((Activity) mContext).setContentView(R.layout.input_score);
                    TextView text = (TextView)  ((Activity) mContext).findViewById(R.id.textView5);
                    String record;
                    if(score > userData.getTabHighScore()[0]){
                        record="Nouveau Record !";
                    }
                    else{
                        record="Fin de partie !";
                    }
                    String t=record+"\n\nScore: "+score;
                    text.setText(t);
                    return true;
                }


                //remise à jour du scroll
                ontouchtab[0][0] = 0;
                ontouchtab[0][1] = 0;
                ontouchtab[1][0] = 0;
                ontouchtab[1][1] = 0;
                ontouchtab[2][0] = 0;
                ontouchtab[2][1] = 0;

               if( hitcarte(event)==true){

               }
                else {
                   if(lastindex!=-1) {
                       playSong(songFlip);
                       orientation[lastindex] = (orientation[lastindex] + 1) % 4; //permet de boucler de 0 a 3
                       switch (orientation[lastindex]) {
                           case 0:
                               reverseTab(lastindex);
                               break;
                           case 1:
                           /* rien a faire juste a afficher verticalement*/
                               break;
                           case 2:
                               reverseTab(lastindex);
                               break;
                           case 3:
                               break;

                       }
                   }
               }
                index = -1;
            }

            if(time>0 &&!isFullGrid())
            if(index != -1) {
                if((orientation[index]&0x01) == 1) {
                    ontouchtab[index][0] = event.getX() - carteTileSize - carteTileSize / 2 - (index * carteLeftAnchor / 2 + index * 3 * carteTileSize);
                    ontouchtab[index][1] = event.getY() + carteTileSize/2 -carteTileSize * 3 - (carteTopAnchor*2 + carteWidth * carteTileSize+ carteTileSize );
                }
                else {
                    ontouchtab[index][0] = event.getX() - carteTileSize - carteTileSize/2  - (index * carteLeftAnchor/2 + index * 3 * carteTileSize);
                    ontouchtab[index][1] = event.getY() + carteTileSize/2 - carteTileSize * 2 - (carteTopAnchor*2 + carteWidth * carteTileSize + carteTileSize);
                }
            }

                return true;
        }
    };

    public void setUserData(UserData userData){
        this.userData=userData;
    }

    public void setScore(int score){
        this.score=score;
    }

    public void setTime(int time){
        this.time=time;
    }

    public void setReload(Boolean reload){
        this.reload=reload;
    }

    public int getScore(){ return score; }

    public void playSong(MediaPlayer song){
        if(userData.getActiveSound() == true) {
            song.seekTo(0);
            song.start();
        }

    }

    public boolean isFullGrid(){ // vérifie si il reste 3 cellule adjacente disponible
        //on regarde toutes les verticales
        for(int x=0;x<carte.length;x++){
            for(int y=0;y<carte[x].length;y++){
                if(y+2<carte[x].length && carte[x][y] == 0 && carte[x][y+1] == 0 && carte[x][y+2] == 0){
                    return false;
                }
            }
        }

        //on regarde toutes les horizontales
        for(int y=0;y<carte.length;y++){
            for(int x=0;x<carte[y].length;x++){
                if(x+2<carte[y].length && carte[x][y] == 0 && carte[x+1][y] == 0 && carte[x+2][y] == 0){
                    return false;
                }
            }
        }

        return true;
    }
    public void exit(){
        userData.setGameSaved(false);
        ((Activity) mContext).finish();
    }

    public void setPause(Boolean pause){
        this.pause=pause;
        in=!pause;  // met fin au thread



    }
}