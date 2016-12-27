package p8.demo.p8sokoban;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Timer;

public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /*//Level en cours
    private int IdLevel=1;*/

    private UserData userData;

    // Declaration des images
    private Bitmap 		block_vide;
    private Bitmap 		 block_bleu;
    private Bitmap 		 block_blanc;
    private Bitmap 		 block_jaune;
    private Bitmap 		 block_rose;
    private Bitmap 		 block_rouge;
    private Bitmap 		 block_vert;
   /* private Bitmap 		diamant;
    private Bitmap 		perso;
    private Bitmap 		vide;*/
    /*private Bitmap[] 	zone = new Bitmap[4];
    private Bitmap 		up;
    private Bitmap 		down;
    private Bitmap 		left;
    private Bitmap 		right;*/
    private Bitmap 		win;



	// Declaration des objets Ressources et Context permettant d'acc�der aux ressources de notre application et de les charger
    private Resources 	mRes;
    private Context 	mContext;

    //boolean savoir si on doit creer une partie
    private Boolean reload=true;
    //socre et temps
    private int score=0;
    private Integer time; // compte à rebours en secondes
    long t1 = 0;
    long t2 = 0;
    long tDiff = 0;
    // tableau modelisant la carte du jeu
    int[][] carte=new int [8][8];

    // 3 tableau généré aléatoirement
    private int [][] tripletTab = new int[3][3];
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
        {CST_block_blanc, CST_block_bleu, CST_block_jaune,CST_block_rose, CST_block_rouge, CST_block_vert, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide,CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide,CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide},
        {CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide, CST_block_vide}
    };

   /* //le nombre de diamnts utiliser dans le niveau actuel//
    int nbDiamond=4;

    // position de reference du joueur niveau 1
    int refxPlayer = 4;
    int refyPlayer = 1;

    // position de reference des diamants niveau 1
    int [][] refdiamants   = {
            {2, 3},
            {2, 6},
            {6, 3},
            {6, 6},
    };

    // position courante des diamants
    int [][] diamants   = {
            {2, 3},
            {2, 6},
            {6, 3},
            {6, 6},
            {2, 6},
            {6, 3},
            {6, 6}

        };
*/

   /* // position courante du joueur
        int xPlayer = 4;
        int yPlayer = 1;
        
        // compteur et max pour animer les zones d'arriv�e des diamants
        int currentStepZone = 0;
        int maxStepZone     = 4;
    */

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
    public SokobanView(Context context, AttributeSet attrs) {
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
       /* diamant		= BitmapFactory.decodeResource(mRes, R.drawable.diamant);
    	perso		= BitmapFactory.decodeResource(mRes, R.drawable.perso);
        zone[0] 	= BitmapFactory.decodeResource(mRes, R.drawable.zone_01);
        zone[1] 	= BitmapFactory.decodeResource(mRes, R.drawable.zone_02);
        zone[2] 	= BitmapFactory.decodeResource(mRes, R.drawable.zone_03);
        zone[3] 	= BitmapFactory.decodeResource(mRes, R.drawable.zone_04);
    	vide 		= BitmapFactory.decodeResource(mRes, R.drawable.vide);
    	up 			= BitmapFactory.decodeResource(mRes, R.drawable.up);
    	down 		= BitmapFactory.decodeResource(mRes, R.drawable.down);
    	left 		= BitmapFactory.decodeResource(mRes, R.drawable.left);
    	right 		= BitmapFactory.decodeResource(mRes, R.drawable.right);*/
    	win 		= BitmapFactory.decodeResource(mRes, R.drawable.win);
    	
    	// initialisation des parmametres du jeu
    	initparameters();

    	// creation du thread
        cv_thread   = new Thread(this);

        // prise de focus pour gestion des touches
        setFocusable(true);
        setOnTouchListener(_otc);



    }    

    // chargement du niveau a partir du tableau de reference du niveau
    public void loadlevel() {
        //on initialise le chrono au temps actuel
        t1=System.currentTimeMillis();

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

     /*   xPlayer = refxPlayer;
        yPlayer = refyPlayer;
        nbDiamond=4;*/

        loadlevel();

        if ((cv_thread!=null) && (!cv_thread.isAlive())) {        	
        	cv_thread.start();
        	Log.e("-FCT-", "cv_thread.start()");
        }
    }    

    // dessin des fleches
   /* private void paintarrow(Canvas canvas) {
    	canvas.drawBitmap(up, (getWidth()-up.getWidth())/2, 0, null);
    	canvas.drawBitmap(down, (getWidth()-down.getWidth())/2, getHeight()-down.getHeight(), null);
    	canvas.drawBitmap(left, 0, (getHeight()-up.getHeight())/2, null);
    	canvas.drawBitmap(right, getWidth()-right.getWidth(), (getHeight()-up.getHeight())/2, null);    	
    }*/

    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
    	canvas.drawBitmap(win, carteLeftAnchor+ 3*carteTileSize, carteTopAnchor+ 4*carteTileSize, null);
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
                  /*  case CST_zone:
                    	canvas.drawBitmap(zone[currentStepZone],carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;*/
                  /*  case CST_vide:
                    	canvas.drawBitmap(vide,carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;*/
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


                switch (tripletTab[i][j]) {
                    case CST_block_blanc:
                        canvas.drawBitmap(block_blanc, i*halfCarteLeftAnchor + j*carteTileSize+  i*3*carteTileSize + ontouchtab[i][0],
                                carteTopAnchor+carteTileSize+ carteWidth*carteTileSize+ontouchtab[i][1], null);
                        break;
                    case CST_block_bleu:
                        canvas.drawBitmap(block_bleu, i*halfCarteLeftAnchor + j*carteTileSize+  i*3*carteTileSize + ontouchtab[i][0],
                                carteTopAnchor+carteTileSize+ carteWidth*carteTileSize+ontouchtab[i][1], null);
                        break;
                    case CST_block_jaune:
                        canvas.drawBitmap(block_jaune, i*halfCarteLeftAnchor + j*carteTileSize+  i*3*carteTileSize + ontouchtab[i][0],
                                carteTopAnchor+carteTileSize+ carteWidth*carteTileSize+ontouchtab[i][1], null);
                        break;
                    case CST_block_rose:
                        canvas.drawBitmap(block_rose, i*halfCarteLeftAnchor + j*carteTileSize+  i*3*carteTileSize + ontouchtab[i][0],
                                carteTopAnchor+carteTileSize+ carteWidth*carteTileSize+ontouchtab[i][1], null);
                        break;
                    case CST_block_rouge:
                        canvas.drawBitmap(block_rouge, i*halfCarteLeftAnchor + j*carteTileSize+  i*3*carteTileSize + ontouchtab[i][0],
                                carteTopAnchor+carteTileSize+ carteWidth*carteTileSize+ontouchtab[i][1], null);
                        break;
                    case CST_block_vert:
                        canvas.drawBitmap(block_vert, i*halfCarteLeftAnchor + j*carteTileSize+  i*3*carteTileSize + ontouchtab[i][0],
                                carteTopAnchor+carteTileSize+ carteWidth*carteTileSize+ontouchtab[i][1], null);
                        break;
                  /*  case CST_zone:
                    	canvas.drawBitmap(zone[currentStepZone],carteLeftAnchor+j*carteTileSize+ half, carteTopAnchor+ carteHeight*carteTileSize+3, null);
                        break;*/
                  /*  case CST_vide:
                    	canvas.drawBitmap(vide,carteLeftAnchor+j*carteTileSize+ half, carteTopAnchor+ carteHeight*carteTileSize+3, null);
                        break;*/
                }
            }
        }
    }

    private void paintInfoBar(Canvas canvas){
        int nextHighScore=0;
        Rect timeBounds=new Rect();
        Rect scoreBounds=new Rect();
        Rect highScoreBounds=new Rect();
        String strScore=new String("Score: "+Integer.toString(score));
        String strHighScore=new String("Next HighScore: "+Integer.toString(nextHighScore));
        String strTime=new String("Temps: "+Integer.toString(time));
        Paint paint = new Paint();


        paint.setColor(Color.WHITE);
        paint.setTextSize(14);
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
    
  /*  // dessin du curseur du joueur
    private void paintPlayer(Canvas canvas) {
    	canvas.drawBitmap(perso,carteLeftAnchor+ xPlayer*carteTileSize, carteTopAnchor+ yPlayer*carteTileSize, null);
    }*/

 /*   // dessin des diamants
    private void paintdiamants(Canvas canvas) {
        for (int i=0; i < nbDiamond; i++) {
        	canvas.drawBitmap(diamant,carteLeftAnchor+ diamants[i][1]*carteTileSize, carteTopAnchor+ diamants[i][0]*carteTileSize, null);
        }
    }*/

    // permet d'identifier si la partie est gagnee (tous les diamants � leur place)
    private boolean isWon() {
      /*  for (int i=0; i < nbDiamond; i++) {
            if (!IsCell(diamants[i][1], diamants[i][0], CST_zone)) {
                return false;
            }
        }
         return true;
         */
        return false;

    }
    
    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
		canvas.drawRGB(44,44,44);
    	if (isWon()) {
        	paintcarte(canvas);
        	paintwin(canvas);        	
        } else {
            paintcarte(canvas);
            paintTripletTab(canvas);
            paintInfoBar(canvas);
            /*paintPlayer(canvas);*/
          /*  paintdiamants(canvas);*/
        /*    paintarrow(canvas); */
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
        if(userData.getGameSaved()){
            userData.setTimer(time);
            userData.setScore(score);
            userData.setTripletTab(tripletTab);
            userData.setGameGrid(carte);
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
            if (cv_thread.isAlive()) {
                try {
                    cv_thread.sleep(40);
                    /*currentStepZone = (currentStepZone + 1) % maxStepZone;*/
                    try {
                        t2=System.currentTimeMillis();
                        tDiff=t2-t1;
                        if(tDiff >=1000){
                            if(time>0){
                                time-=1;
                            }
                            t1=System.currentTimeMillis();
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
    
  /*  // verification que nous sommes dans le tableau
    private boolean IsOut(int x, int y) {
        if ((x < 0) || (x > carteWidth- 1)) {
            return true;
        }
        if ((y < 0) || (y > carteHeight- 1)) {
            return true;
        }
        return false;
    }*/

  /*  //controle de la valeur d'une cellule
    private boolean IsCell(int x, int y, int mask) {
        if (carte[y][x] == mask) {
            return true;
        }
        return false;
    }*/

   /* // controle si nous avons un diamant dans la case
    private boolean IsDiamant(int x, int y) {
        for (int i=0; i< nbDiamond; i++) {
            Log.i("POs"," val1: " + diamants[i][1] + " val 2: " + diamants[i][1] );
            if ((diamants[i][1] == x) && (diamants[i][0] == y)) {
                return true;
            }
        }
        return false;
    }*/

   /* // met � jour la position d'un diamant
    private void UpdateDiamant(int x, int y, int new_x, int new_y) {
        for (int i=0; i< nbDiamond; i++) {
            if ((diamants[i][1] == x) && (diamants[i][0] == y)) {
                diamants[i][1] = new_x;
                diamants[i][0] = new_y;
            }
        }
    }    */

    // fonction permettant de recuperer les retours clavier
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

    	Log.i("-> FCT <-", "onKeyUp: "+ keyCode);


    	
       /* int xTmpPlayer	= xPlayer;
        int yTmpPlayer  = yPlayer;
        int xchange 	= 0;
        int ychange 	= 0;



        if (keyCode == KeyEvent.KEYCODE_0) {
        	initparameters();
        }
    	
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
        	ychange = -1;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
        	ychange = 1;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            xchange = -1;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            xchange = 1;        
        }
	        xPlayer = xPlayer+ xchange;
	        yPlayer = yPlayer+ ychange;


        if (IsOut(xPlayer, yPlayer) || IsCell(xPlayer, yPlayer, CST_block_vide)) {
	            xPlayer = xTmpPlayer;
	            yPlayer = yTmpPlayer;
	        } else if (IsDiamant(xPlayer, yPlayer)) {
            int xTmpDiamant = xPlayer;
	            int yTmpDiamant = yPlayer;
	            xTmpDiamant = xTmpDiamant+ xchange;
	            yTmpDiamant = yTmpDiamant+ ychange;
	            if (IsOut(xTmpDiamant, yTmpDiamant) || IsCell(xTmpDiamant, yTmpDiamant, CST_block_vide) || IsDiamant(xTmpDiamant, yTmpDiamant)) {
	                xPlayer = xTmpPlayer;
	                yPlayer = yTmpPlayer;
	            } else {

                    UpdateDiamant(xTmpDiamant- xchange, yTmpDiamant- ychange, xTmpDiamant, yTmpDiamant);
	            }
	        }      */
	    return true;   
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
         margex=i*halfCarteLeftAnchor+ i*3*carteTileSize  ;
         margey=carteTopAnchor+carteWidth*carteTileSize+carteTileSize;

         resx=(int)((x-margex)/tx);
         resy=(int)((y-margey)/ty);

         if(resx==0 && resy==0)
             Log.i("-> FCT <-", "tab Case (0,"+i+") touchée "+y);
         if(resx==1 && resy==0)
             Log.i("-> FCT <-", "tab Case (1,"+i+")  touchée  "+y);
         if(resx==2 && resy==0)
             Log.i("-> FCT <-", "tab Case (2,"+i+") touchée "+y);

         if(resx==0 && resy==0 ||resx==1 && resy==0 || resx==2 && resy==0 ){

             return i;

         }


     }

    return -1;
 }
    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            Log.i("-> FCT <-", "onTouchEvent: "+ event.getX());

       if(isWon()){
          /* if(IdLevel==1){
               IdLevel=2;
               loadlevel2();
           }
           else if(IdLevel==2) {
               IdLevel=3;
               loadlevel3();
           }*/
           userData.setGameSaved(false);
       }

        /* Hit sur la carte */
        float tx=carteTileSize,ty=carteTileSize;
        float margex=carteLeftAnchor;
        float margey=carteTopAnchor;
        float  y=event.getY(),x=event.getX();

        int resx=(int)((x-margex)/tx);
        int resy=(int)((y-margey)/ty);

        if(resx==5 && resy==3)
            Log.i("-> FCT <-", "Case (5,3) touchée");





     /* if (event.getY()<50) {
    		onKeyDown(KeyEvent.KEYCODE_DPAD_UP, null);
    	} else if (event.getY()>getHeight()-50) {
    		if (event.getX()>getWidth()-50) {
        		onKeyDown(KeyEvent.KEYCODE_0, null);
        	} else {
        		onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, null);
        	}
    	} else if (event.getX()<50) {
    		onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
    	} else if (event.getX()>getWidth()-50) {
    		onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
    	}*/

    	return super.onTouchEvent(event);
    }

    static int index=-1;
    //permet de scroll les tableaux
    private OnTouchListener _otc = new OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("Info","Ontouch");
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                index= hitTripletTab(event);
            }
            if(event.getAction()== MotionEvent.ACTION_UP){

                ontouchtab[0][0] = 0;
                ontouchtab[0][1] = 0;
                ontouchtab[1][0] = 0;
                ontouchtab[1][1] = 0;
                ontouchtab[2][0] = 0;
                ontouchtab[2][1] = 0;
                index = -1;
            }

            if(index != -1) {
                ontouchtab[index][0] = event.getX() - carteTileSize - carteTileSize/2  - (index * carteLeftAnchor/2 + index * 3 * carteTileSize);
                ontouchtab[index][1] = event.getY() - carteTileSize * 2 - (carteTopAnchor + carteWidth * carteTileSize + carteTileSize);
            }

                return true;
        }
    };

    public void setUserData(UserData userData){
        this.userData=userData;
    }

    public void setLvl(int id){

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

}