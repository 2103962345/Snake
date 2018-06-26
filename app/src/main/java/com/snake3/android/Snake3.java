package com.snake3.android;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.SharedPreferences;

public class Snake3 extends Activity {
	//shared preferences
	Timer timer;
	TimerTask task;
	Handler handler;
	Runnable runnable;
	private SharedPreferences gamePrefs;
	public static final String GAME_PREFS = "ArithmeticFile";
	EditText input;
	AlertDialog.Builder nameAlert;
	playField game;
	String name="";
	Button start, hghScr;
	AlertDialog.Builder highScores;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//initiate shared prefs
		gamePrefs = getSharedPreferences(GAME_PREFS, 0);
		//File deletePrefFile = new File("/data/data/com.snake3.android/shared_prefs/ArithmeticFile.xml");
		//deletePrefFile.delete();
		game= (playField)findViewById(R.id.playField1);
    	start = (Button)findViewById(R.id.button1);
    	final Button pause = (Button)findViewById(R.id.button2);
    	final ImageView logo = (ImageView)findViewById(R.id.imageView1);
    	hghScr = (Button)findViewById(R.id.button3);
    	String Scores="\n\n";
		highScores = new AlertDialog.Builder(this);
		highScores.setTitle("High Scores");

		highScores.setPositiveButton("ok", null);
		//get text view
		//get shared prefs
		SharedPreferences scorePrefs = getSharedPreferences(Snake3.GAME_PREFS, 0);
		//get scores
		String[] savedScores = scorePrefs.getString("highScores", "").split("\\|");
		//build string
		StringBuilder scoreBuild = new StringBuilder("");
		for(String score : savedScores){
			scoreBuild.append(score+"\n");
		}
		//display scores
		highScores.setMessage(scoreBuild.toString());

    	pause.setVisibility(View.GONE);
    	pause.setBackgroundResource(R.drawable.button);
    	pause.setTextColor(Color.RED);
    	pause.setTextAppearance(getApplicationContext(), R.style.bold);
    	pause.setText(R.string.pauseButton);

       hghScr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	highScores.show();

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	game.startGame();
            	pause.setVisibility(View.VISIBLE);
            	start.setVisibility(View.GONE);
            	logo.setVisibility(View.GONE);
            	hghScr.setVisibility(View.GONE);
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	game.pause();
            }
        });

		startTimer();
		handler = new Handler();
		 runnable = new Runnable(){
			public void run() {
				if(game!=null&&game.gameOn()&&game.lost()) {
					game.changeLost();
					stopTimer();
					start.setVisibility(View.VISIBLE);
					hghScr.setVisibility(View.VISIBLE);
					start.setText(R.string.restartButton);
					setHighScore();
				}
			}
		};
		startTimer();
    }


	public void stopTimer() {
		if (timer != null) {
			handler.removeCallbacks(runnable);
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void startTimer() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(runnable);
			}
		}, 1000, 500);
	}

	//set high score
	private void setHighScore(){
		final int exScore = game.getScore();
		if(exScore>0){
			//we have a valid score
		final	SharedPreferences.Editor scoreEdit = gamePrefs.edit();

			nameAlert = new AlertDialog.Builder(this);
			nameAlert.setTitle("Enter your name: ");
// Set up the input
			input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			nameAlert.setView(input);
			final String scores = gamePrefs.getString("highScores", "");
// Set up the buttons
			nameAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					name = input.getText().toString();
					//get existing scores

					//check for scores
					if(scores.length()>0) {
						List<Score> scoreStrings = new ArrayList<Score>();
						//split scores
						String[] exScores = scores.split("\\|");
						//add score object for each
						for(String eSc : exScores){
							String[] parts = eSc.split(" : ");
							scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
						}

						Score newScore = new Score(name, exScore);
						//newScore.setScoreName(name);
						scoreStrings.add(newScore);
						//sort
						Collections.sort(scoreStrings);
						//get top ten
						StringBuilder scoreBuild = new StringBuilder("");
						for(int ss=0; ss<scoreStrings.size(); ss++){
							if(ss>=10) break;
							if(ss>0) scoreBuild.append("|");
							scoreBuild.append(scoreStrings.get(ss).getScoreText());
						}
						//write to prefs
						scoreEdit.putString("highScores", scoreBuild.toString());
						scoreEdit.commit();

					}else{
						//no existing scores
						scoreEdit.putString("highScores", ""+name+" : "+exScore);
						scoreEdit.commit();

					}

					SharedPreferences scorePrefs = getSharedPreferences(Snake3.GAME_PREFS, 0);
					//get scores
					String[] savedScores = scorePrefs.getString("highScores", "").split("\\|");
					//build string
					StringBuilder scoreBuild = new StringBuilder("");
					for(String score : savedScores){
						scoreBuild.append(score+"\n");
					}
					highScores.setTitle("High Scores").setMessage(scoreBuild.toString());
					highScores.setPositiveButton("ok", null);
					highScores.show();
				}
			});
			nameAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					startTimer();
				}
			});
			if(scores.length()>0){
				//we have existing scores
				List<Score> scoreStrings = new ArrayList<Score>();
				//split scores
				String[] exScores = scores.split("\\|");
				//add score object for each
				for(String eSc : exScores){
					String[] parts = eSc.split(" : ");
					scoreStrings.add(new Score(parts[0], Integer.parseInt(parts[1])));
				}
				//new score
				Score newScore = new Score(name, exScore);
				for(int s=scoreStrings.size()-1; s>-1; s--){
					if(s==9)if(scoreStrings.get(s).compareTo(newScore)<=0) break;
				//	if(s>-1)
						if((scoreStrings.get(s).compareTo(newScore)>=0)||(s<=9)) {
							nameAlert.show();
						}
							break;
						}
				}


			else nameAlert.show();
		}

	}
	//set high score if activity destroyed
	protected void onDestroy(){
		setHighScore();
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.main, menu);
		return true;
	}
	//save instance state
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//save score
		int exScore = game.getScore();
		savedInstanceState.putInt("score", exScore);
		super.onSaveInstanceState(savedInstanceState);
	}
}