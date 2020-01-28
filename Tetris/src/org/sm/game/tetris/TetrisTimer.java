package org.sm.game.tetris;

import java.util.TimerTask;

public class TetrisTimer extends Thread {
	private boolean started;
	private long delay;
	private TimerTask task;
	private boolean canceled;
	private boolean stopMe;

	public TetrisTimer(String name) {
		super(name);
		started = false;
		delay = 0;
		canceled = false;
		stopMe = false;
	}

	public void run() {
		boolean scheduled = false;

		do {
			synchronized (this) {
				try {
					scheduled = delay != 0;
					wait(delay);
				} catch (InterruptedException e) {
				}
			}

			if (canceled)
				canceled = false;
			else if (scheduled) {
				delay = 0;
				task.run();
			}
		} while (stopMe == false);
	}

	public synchronized void schedule(TimerTask task, long delay) {
		this.task = task;
		this.delay = delay;

		if (started == false) {
			started = true;
			start();
		} else
			notify();
	}

	public synchronized void cancel() {
		canceled = true;
		delay = 0;
		notify();
	}

	public synchronized void terminate() {
		stopMe = true;
		notify();
	}
}
