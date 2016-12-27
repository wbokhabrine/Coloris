package p8.demo.p8sokoban;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

    // tableau modelisant la carte du jeu
    int[][] carte;
    
    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonn�es en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonn�es en X du point d'ancrage de notre carte

    // taille de la carte
    static final int    carteWidth    = 8;
    static final int    carteHeight   = 8;
    static final int    carteTileSize = 35;

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
        	
    	
    }    

    // chargement du niveau a partir du tableau de reference du niveau
    public void loadlevel() {

        carte=ref;

        /*xPlayer = refxPlayer;
        yPlayer = refyPlayer;
        nbDiamond=4;

        for (int i=0; i< nbDiamond; i++) {
            diamants[i][1] = refdiamants[i][1];
            diamants[i][0] = refdiamants[i][0];
        }*/
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
        carte           = new int[carteHeight][carteWidth];
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

    
    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {
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

    public void setUserData(UserData userData){
        this.userData=userData;
    }

    public void setLvl(int id){

    }
}
