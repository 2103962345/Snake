package com.snake3.android;

import com.snake3.android.playField;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class gameThread extends Thread {
	private SurfaceHolder _surfaceHolder;
	private playField _playField;
	private boolean _run = false;
	public gameThread(SurfaceHolder surfaceHolder, playField playField){
		_surfaceHolder = surfaceHolder;
		_playField = playField;
	}
	public void setRunning(boolean run) {
		_run = run;
	}
	@Override
	public void run() 
	{
		Canvas c;
		while(_run)
		{
			c = null;
			try 
			{
				c = _surfaceHolder.lockCanvas(null);
				synchronized (_surfaceHolder)
				{
					_playField.onDraw(c);
				}
			}
			finally
			{
				if(c != null)
				{
					_surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
