package org.sm.game.tetris;

import java.awt.Color;
import java.awt.Point;

public class TetrisBlockShape {
	private Color color;
	private Point[] points;

	public TetrisBlockShape(byte[] shape, Color color) {
		int index = 0;

		points = new Point[4];

		for (int y = 0; y < 4 && index < 4; y++) {
			int mask = 8;

			for (int x = 0; x < 4 && index < 4; x++) {
				if ((mask & shape[y]) > 0)
					points[index++] = new Point(x, y);
				mask >>= 1;
			}
		}

		this.color = color;
	}

	public Point[] getPoints() {
		return points;
	}

	public Color getColor() {
		return color;
	}
}
