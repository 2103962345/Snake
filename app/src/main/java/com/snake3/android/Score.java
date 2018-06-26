package com.snake3.android;

/**
 * Created by Alexandra on 11.02.2017.
 */
public class Score implements Comparable<Score> {

    //score date and number
    private String scoreName;
    public int scoreNum;

    public Score(String name, int num){
        scoreName=name;
        scoreNum=num;
    }

    //check this score against another
    public int compareTo(Score sc){
        //return 0 if equal
        //1 if passed greater than this
        //-1 if this greater than passed
        return sc.scoreNum>scoreNum? 1 : sc.scoreNum<scoreNum? -1 : 0;
    }

    //return score display text
    public String getScoreText(){
        return scoreName+" : "+scoreNum;
    }
    public void setScoreName(String name){
        scoreName=name;
    }

}
