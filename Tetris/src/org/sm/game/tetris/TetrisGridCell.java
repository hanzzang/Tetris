package org.sm.game.tetris;

import java.awt.Color;

public class TetrisGridCell {
	private static final Color BLANK_COLOR = Color.BLACK;

	private Color color;
	private int nActive;

	public TetrisGridCell() {
		reset();
	}

	public TetrisGridCell(Color color, int nActive) {
		setProperties(color, nActive);
	}

	public void setProperties(Color color, int nActive) {
		this.color = color;
		this.nActive = nActive;
	}

	public void reset() {
		setProperties(BLANK_COLOR, -1);
	}

	public Color getColor() {
		return color;
	}

	public int getActive() {
		return nActive;
	}

	public void resetActive() {
		nActive = -1;
	}

	public boolean isEmpty(int nActive) {
		return color == BLANK_COLOR || this.nActive == nActive;
	}

	public void copy(TetrisGridCell from) {
		color = from.color;
		nActive = from.nActive;
	}
}
