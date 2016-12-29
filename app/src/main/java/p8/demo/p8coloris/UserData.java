package p8.demo.p8coloris;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;

/**
 * Created by nico on 25/12/16.
 */


public class UserData {

    private int[] tabHighScore = new int[10]; // tableau des scores (Sauvegarde les 10 meilleurs scores)
    private String[] tabNameHighScore=new String[10]; // tableau des noms pour les meilleurs scores
    private boolean activeSound=true; // boolean pour savoir si le son doit être actif
    private boolean prefCharged=false; // boolean permettant de savoir si les préfèrences ont été chargées
    private boolean gameSaved=false; // boolean pour savoir si une partie a été sauvegardée
    private int timer; // temps de la partie en seconde
    private int score;
    private int[][] gameGrid=new int[8][8]; // grille du jeu (tableau 8x8 de int)
    private int[][] tripletTab=new int [3][3]; // tableau de 3 triplets (1 triplet c'est une pièce de 3 cases)
    private int[] orientationTab = new int[3]; // orientation des 3 tableaux de triplets, 4 etats de 0 à 3 inclu
    private Context context;

    //constructeur initialise les données utilisateurs
    public UserData(Context context) {

        this.context=context;

        //décommenter la ligne ci-dessous pour chercher directement une sauvegarde utilisateur
        //readUserData();

        if(!prefCharged) {
            timer = 0;
            for (int i = 0; i < tabHighScore.length && i <tabNameHighScore.length; i++) {
                tabHighScore[i] = 0;
                tabNameHighScore[i] = new String("NONE");
            }

            for (int i = 0; i < gameGrid.length; i++) {
                for (int y = 0; y < gameGrid[i].length; y++) {
                    gameGrid[i][y] = 0;
                }
            }

            for (int i = 0; i < tripletTab.length; i++) {
                for (int y = 0; y < tripletTab[i].length; y++) {
                    tripletTab[i][y] = 0;
                }
            }

            for(int i=0;i<orientationTab.length;i++){
                orientationTab[i]=0;
            }
        }
    }

    public int getTimer(){
        return timer;
    }

    public void setTimer(int timer){
        this.timer=timer;
    }

    public void setTripletTab(int tripletTab[][]){
        for(int i=0;i<tripletTab.length;i++){
            for(int y=0;y<tripletTab[i].length;y++){
                this.tripletTab[i][y]=tripletTab[i][y];
            }
        }
    }

    public int[][] getTripletTab(){
        return  tripletTab;
    }

    public void setOrientationTab(int orientationTab[]){
        for(int i=0;i<orientationTab.length;i++){
            this.orientationTab[i]=orientationTab[i];
        }
    }

    public int[] getOrientationTab(){
        return  orientationTab;
    }


    public void setGameGrid(int gameGrid[][]){
        for(int i=0;i<gameGrid.length;i++){
            for(int y=0;y<gameGrid[i].length;y++){
                this.gameGrid[i][y]=gameGrid[i][y];
            }
        }
    }



    public int[][] getGameGrid(){
        return  gameGrid;
    }

    public int[] getTabHighScore(){
        return tabHighScore;
    }

    public int getHighScoreAtIndex(int index){
        return tabHighScore[index];
    }

    public void newHighScore(int highScore, String name){
        for(int i=0;i<tabHighScore.length;i++){
            if(highScore>tabHighScore[i]){
                for(int y=tabHighScore.length-1;y>i;y--){
                    tabHighScore[y]=tabHighScore[y-1];
                    tabNameHighScore[y]=tabNameHighScore[y-1];
                }
                tabHighScore[i]=highScore;
                tabNameHighScore[i]=name;
                break;
            }
        }
    }

    public String[] getTabNameHighScore(){
        return tabNameHighScore;
    }

    public String getNameHighScoreAtIndex(int index){
        return tabNameHighScore[index];
    }

    public void setActiveSound(boolean value){
        activeSound=value;
    }

    public boolean getActiveSound(){
        return activeSound;
    }

    public void setPrefCharged(boolean value){
        prefCharged=value;
    }

    public boolean getPrefCharged(){
        return prefCharged;
    }

    public void setGameSaved(boolean value){
        gameSaved=value;
    }

    public boolean getGameSaved(){
        return gameSaved;
    }


    // readUserData(): initialise les données de la classe depuis la mémoire interne du smartphone
    public void readUserData(){
        if(readConfigFile()){
            prefCharged=true;
        }
        if(gameSaved){
            readGameFile();
        }
    }

    // writeUserData(): écrit les données de classe dans la mémoire interne du smartphone
    public void writeUserData(){
        writeConfigFile();
        if(gameSaved){
            writeGameFile();
        }
    }

    public void writeUserConfigData(){

        writeConfigFile();
    }

    public void writeUserGameData(){

        writeGameFile();
    }

    /*
        debugLog():
        Afficher dans les logI le contenue de la classe userData de façon structuré

    */
    public void debugLog(){
        Log.i("debugUserData", " ");
        String line="";
        line=line.concat("active sound: "+Boolean.toString(activeSound)+"");
        Log.i("debugUserData", line);
        line="";
        line=line.concat("pref charged: "+Boolean.toString(prefCharged)+"");
        Log.i("debugUserData", line);
        line="";
        line=line.concat("game saved: "+Boolean.toString(gameSaved)+"");
        Log.i("debugUserData", line);
        line="";
        line=line.concat("high score: ");
        for(int i=0;i<tabHighScore.length;i++){
            line=line.concat(Integer.toString(tabHighScore[i])+",");
        }
        Log.i("debugUserData", line);
        line="";
        line=line.concat("high score name: ");
        for(int i=0;i<tabNameHighScore.length;i++){
            line=line.concat(tabNameHighScore[i]+",");
        }
        Log.i("debugUserData", line);


        for(int index=0;index<tripletTab.length;index++) {
            line = "";
            line = line.concat("triplet "+index+": ");
            for (int i = 0; i < tripletTab[index].length; i++) {
                line = line.concat(Integer.toString(tripletTab[index][i]) + ",");
            }
            Log.i("debugUserData", line);
        }
        line="";
        line = line.concat("game grid: ");
        Log.i("debugUserData", line);
        for(int index=0;index<gameGrid.length;index++) {
            line = "";
            for (int i = 0; i < gameGrid[index].length; i++) {
                line = line.concat(Integer.toString(gameGrid[index][i]) + ",");
            }
            Log.i("debugUserData", line);
        }

    }


    /*
        readConfigFile():
        lis les configurations de l'utilisateur enregistrées dans la mémoire interne sous un fichier userData.txt
        le fichier à la forme suivante:
        'boolean' -> pour savoir si le son doit etre activé
        'boolean' -> pour savoir si une partie est en cours
        'int,int,...,int' -> tableau de int représentant les highscores
        'string,string,...,string -> tableau de string représentant les noms associés aux highscores
    */
    private boolean readConfigFile(){
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new InputStreamReader(context.openFileInput("userData.txt")));
            String line;
            if( (line = inputReader.readLine()) != null){
                activeSound=Boolean.parseBoolean(line);
            }
            if( (line = inputReader.readLine()) != null){
                gameSaved=Boolean.parseBoolean(line);
            }
            if( (line = inputReader.readLine()) != null){
                int x=0;
                int y=0;
                int i=0;
                y=line.indexOf(',',x);
                while( y != -1 ){
                    tabHighScore[i]=Integer.parseInt(line.substring(x,y));
                    x=y+1;
                    y=line.indexOf(',',x);
                    i++;
                }
            }
            if( (line = inputReader.readLine()) != null){
                int x=0;
                int y=0;
                int i=0;
                y=line.indexOf(',',x);
                while( y != -1 && i <tabHighScore.length){
                    tabNameHighScore[i]=line.substring(x,y);
                    x=y+1;
                    y=line.indexOf(',',x);
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }

    /*
        writeConfigFile():
        écrit les configurations de l'utilisateur actuel dans la mémoire interne sous un fichier userData.txt
        le fichier à la forme suivante:
        'boolean' -> pour savoir si le son doit etre activé
        'boolean' -> pour savoir si une partie est en cours
        'int,int,...,int' -> tableau de int représentant les highscores
        'string,string,...,string -> tableau de string représentant les noms associés aux highscores
    */

    private void writeConfigFile(){
        String endOfLine = System.getProperty("line.separator");
        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("userData.txt",Context.MODE_PRIVATE)));
            outputWriter.write(Boolean.toString(activeSound)+endOfLine);
            outputWriter.write(Boolean.toString(gameSaved)+endOfLine);
            for(int i=0;i<tabHighScore.length;i++){
                outputWriter.write(Integer.toString(tabHighScore[i])+",");
            }
            outputWriter.write(endOfLine);
            for(int i=0;i<tabNameHighScore.length;i++) {
                outputWriter.write(tabNameHighScore[i] + ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
        readGameFile():
        lis l'état de la partie en cours sauvegardée dans le fichier gameData.txt
        le fichier à la forme suivante:
        'int' -> représentant le temps en secondes
        'int,int,int' -> pour le premier triplet
        'int,int,int' -> pour le deuxième triplet
        'int,int,int' -> pour le troisième triplet
        -> tableau de int 8*8 représentant les cellules du jeu
        'int,int,...,int'
        '...,...,...,...'
        'int,int,...,int'
        'string,string,...,string -> tableau de string représentant les noms associés aux highscores
    */
    private boolean readGameFile(){
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new InputStreamReader(context.openFileInput("gameData.txt")));
            String line;
            if( (line = inputReader.readLine()) != null){
                timer=Integer.parseInt(line);
            }
            if( (line = inputReader.readLine()) != null){
                score=Integer.parseInt(line);
            }

            if( (line = inputReader.readLine()) != null) {
                int x = 0;
                int y = 0;
                int i = 0;
                y = line.indexOf(',', x);
                while (y != -1 && i <orientationTab.length) {
                    orientationTab[i]= Integer.parseInt(line.substring(x, y));
                    x = y + 1;
                    y = line.indexOf(',', x);
                    i++;
                }
            }

            for(int index=0;index<tripletTab.length;index++){
                if( (line = inputReader.readLine()) != null) {
                    int x = 0;
                    int y = 0;
                    int i = 0;
                    y = line.indexOf(',', x);
                    while (y != -1 && i <tripletTab[index].length) {
                        tripletTab[index][i]= Integer.parseInt(line.substring(x, y));
                        x = y + 1;
                        y = line.indexOf(',', x);
                        i++;
                    }
                }
            }
            for(int index=0;index<gameGrid.length;index++){
                if( (line = inputReader.readLine()) != null) {
                    int x = 0;
                    int y = 0;
                    int i = 0;
                    y = line.indexOf(',', x);
                    while (y != -1 && i<gameGrid[index].length) {
                        gameGrid[index][i]= Integer.parseInt(line.substring(x, y));
                        x = y + 1;
                        y = line.indexOf(',', x);
                        i++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }

    /*
        writeGameFile():
        écris l'état de la partie en cours sauvegardée dans le fichier gameData.txt
        le fichier à la forme suivante:
        'int' -> représentant le temps en secondes
        'int,int,int' -> pour le premier triplet
        'int,int,int' -> pour le deuxième triplet
        'int,int,int' -> pour le troisième triplet
        -> tableau de int 8*8 représentant les cellules du jeu
        'int,int,...,int'
        '...,...,...,...'
        'int,int,...,int'
        'string,string,...,string -> tableau de string représentant les noms associés aux highscores
    */

    private void writeGameFile(){
        String endOfLine = System.getProperty("line.separator");
        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("gameData.txt",Context.MODE_PRIVATE)));
            outputWriter.write(Integer.toString(timer)+endOfLine);
            outputWriter.write(Integer.toString(score)+endOfLine);
            for (int i = 0; i < orientationTab.length; i++) {
                outputWriter.write(Integer.toString(orientationTab[i]) + ",");
            }
            outputWriter.write(endOfLine);
            for(int index=0;index<tripletTab.length;index++) {
                for (int i = 0; i < tripletTab[index].length; i++) {
                    outputWriter.write(Integer.toString(tripletTab[index][i]) + ",");
                }
                outputWriter.write(endOfLine);
            }
            for(int index=0;index<gameGrid.length;index++) {
                for (int i = 0; i < gameGrid[index].length; i++) {
                    outputWriter.write(Integer.toString(gameGrid[index][i]) + ",");
                }
                outputWriter.write(endOfLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}