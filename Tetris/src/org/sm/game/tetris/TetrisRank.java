package org.sm.game.tetris;

import java.util.Date;

public class TetrisRank {
	private String name;
	private long score;
	private int level;
	private int rows;
	private Date date;

	public TetrisRank(String name, long score, int level, int rows, Date date) {
		this.name = name;
		this.score = score;
		this.level = level;
		this.rows = rows;
		this.date = date == null ? new Date() : date;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public long getScore() {
		return score;
	}

	public int getLevel() {
		return level;
	}

	public int getRows() {
		return rows;
	}

	public Date getDate() {
		return date;
	}
}
