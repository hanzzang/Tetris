package org.sm.game.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JPanel;

public class TetrisGrid extends JPanel {
	private static final long serialVersionUID = -8595014371060301378L;
	private static final int CELL_UNIT = 20;
	private static final Color lineColor = Color.DARK_GRAY;

	public static enum CanMove {
		CANNOT, CAN, UNSETTLED
	}

	private int width;
	private int height;
	private boolean bDual;
	private boolean bPreview;
	private Point[] curShapePos;
	private TetrisBlockShape[] curShapes;
	private TetrisGridCell[][] gridCells;
	private boolean bDelayRepaint;
	private Rectangle rectRepaint;

	public TetrisGrid(int width, int height, boolean bDual, boolean bPreview) {
		setSize(width, height, bDual);

		this.bPreview = bPreview;
		bDelayRepaint = false;

		curShapePos = new Point[2];
		if (bDual) {
			curShapePos[0] = new Point((width / 3) * 2 - 1, 0);
			curShapePos[1] = new Point(width / 3 - 1, 0);
		} else {
			curShapePos[0] = new Point((width - 4) / 2, 0);
			curShapePos[1] = new Point((width - 4) / 2, 0);
		}

		curShapes = new TetrisBlockShape[2];
		for (int i = 0; i < curShapes.length; i++)
			curShapes[i] = null;
	}

	public void setSize(int width, int height, boolean bDual) {
		this.width = width;
		this.height = height;
		this.bDual = bDual;

		setPreferredSize(new Dimension(width * CELL_UNIT, height * CELL_UNIT));

		gridCells = new TetrisGridCell[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++)
				gridCells[x][y] = new TetrisGridCell();
		}

		invalidate();
	}

	public boolean setBlockShape(TetrisBlockShape blockShape, int nActive, boolean bNewShape) {
		boolean valid;
		setDelayRepaint(true);
		resetShapeArea(nActive);

		if (bNewShape) {
			if (bDual) {
				curShapePos[nActive].x = (width / 3) * (2 - nActive) - 2;
				curShapePos[nActive].y = 0;
			} else {
				curShapePos[nActive].x = width / 2 - 2;
				curShapePos[nActive].y = 0;
			}

			if (bPreview == false)
				curShapePos[nActive].x++;
		}

		curShapes[nActive] = blockShape;
		valid = updateShapeArea(nActive);
		setDelayRepaint(false);

		return valid;
	}

	public CanMove canMoveShape(int x, int y, int nActive) {
		return canMoveShape(x, y, curShapes[nActive].getPoints(), nActive);
	}

	public CanMove canMoveShape(int x, int y, Point[] shapePoints, int nActive) {
		CanMove canMove = CanMove.CAN;

		for (int i = 0; i < shapePoints.length && canMove != CanMove.CANNOT; i++) {
			int dx = curShapePos[nActive].x + x + shapePoints[i].x;
			int dy = curShapePos[nActive].y + y + shapePoints[i].y;

			if (dx >= 0 && dx < width && dy >= 0 && dy < height) {
				if (gridCells[dx][dy].isEmpty(nActive) == false)
					canMove = gridCells[dx][dy].getActive() < 0 ? CanMove.CANNOT : CanMove.UNSETTLED;
			} else
				canMove = CanMove.CANNOT;
		}

		return canMove;
	}

	public void moveShape(int x, int y, int nActive) {
		setDelayRepaint(true);
		resetShapeArea(nActive);

		curShapePos[nActive].x += x;
		curShapePos[nActive].y += y;

		for (Point point : curShapes[nActive].getPoints()) {
			int dx = curShapePos[nActive].x + point.x;
			int dy = curShapePos[nActive].y + point.y;

			gridCells[dx][dy].setProperties(curShapes[nActive].getColor(), nActive);
			repaint(dx * CELL_UNIT, dy * CELL_UNIT, CELL_UNIT, CELL_UNIT);
		}

		setDelayRepaint(false);
	}

	public int deleteFilledRows(int nActive) {
		int deletedRows = 0;
		int minY = height - 1;
		int maxY = 0;

		setDelayRepaint(true);

		for (Point point : curShapes[nActive].getPoints()) {
			int dx = curShapePos[nActive].x + point.x;
			int dy = curShapePos[nActive].y + point.y;

			gridCells[dx][dy].resetActive();
			repaint(dx * CELL_UNIT, dy * CELL_UNIT, CELL_UNIT, CELL_UNIT);

			if (maxY < point.y)
				maxY = point.y;
			if (minY > point.y)
				minY = point.y;
		}

		int minDirtyY = curShapePos[nActive].y + minY;
		maxY += curShapePos[nActive].y;
		for (int y = minDirtyY; y <= maxY; y++) {
			boolean empty = false;
			for (int x = 0; x < width && empty == false; x++)
				empty = gridCells[x][y].isEmpty(nActive);

			if (empty == false) {
				int dirtyY = deleteRow(y, nActive);
				if (dirtyY < minDirtyY)
					minDirtyY = dirtyY;
				deletedRows++;
			}
		}

		if (deletedRows > 0) {
			repaint(0, minDirtyY * CELL_UNIT, width * CELL_UNIT, (maxY - minDirtyY + 1) * CELL_UNIT);
			if (bDual)
				curShapePos[(nActive + 1) % 2].y += deletedRows;
		}

		curShapes[nActive] = null;
		setDelayRepaint(false);

		return deletedRows;
	}

	public boolean isBottomEmpty(int nActive) {
		boolean empty = false;

		if (curShapes[nActive] == null) {
			int y = height - 1;
			empty = true;
			for (int x = 0; x < width && empty; x++)
				empty = gridCells[x][y].isEmpty(nActive);
		}

		return empty;
	}

	public void reset() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++)
				gridCells[x][y].reset();
		}

		for (int i = 0; i < curShapes.length; i++)
			curShapes[i] = null;

		repaint();
	}

	public void repaint(int x, int y, int width, int height) {
		if (bDelayRepaint)
			rectRepaint.add(new Rectangle(x, y, width, height));
		else
			super.repaint(x, y, width, height);
	}

	protected void paintComponent(Graphics g) {
		Color oldColor = g.getColor();
		Shape clip = g.getClip();
		int xpos = 0;
		int ypos;

		for (int x = 0; x < width; x++) {
			ypos = 0;
			for (int y = 0; y < height; y++) {
				if (clip.intersects(xpos, ypos, CELL_UNIT, CELL_UNIT))
					paintCell(g, xpos, ypos, gridCells[x][y]);
				ypos += CELL_UNIT;
			}
			xpos += CELL_UNIT;
		}

		g.setColor(oldColor);
	}

	private int deleteRow(int y, int nActive) {
		int minY = y;

		for (int x = 0; x < width; x++) {
			for (int dy = y; dy > 0; dy--) {
				gridCells[x][dy].copy(gridCells[x][dy - 1]);
				if (minY > dy && gridCells[x][dy].isEmpty(nActive) == false)
					minY = dy;
			}

			if (gridCells[x][0].isEmpty(-1) == false) {
				gridCells[x][0].reset();
				minY = 0;
			}
		}

		return minY;
	}

	private void setDelayRepaint(boolean bDelayRepaint) {
		this.bDelayRepaint = bDelayRepaint;

		if (bDelayRepaint)
			rectRepaint = new Rectangle();
		else {
			super.repaint(rectRepaint);
			rectRepaint = null;
		}
	}

	private void paintCell(Graphics g, int xpos, int ypos, TetrisGridCell cell) {
		g.setColor(cell.getColor());
		g.fillRect(xpos, ypos, CELL_UNIT, CELL_UNIT);

		if (cell.getActive() < 0) {
			g.setColor(lineColor);
			g.drawLine(xpos, ypos + CELL_UNIT - 1, xpos + CELL_UNIT - 1, ypos + CELL_UNIT - 1);
			g.drawLine(xpos + CELL_UNIT - 1, ypos, xpos + CELL_UNIT - 1, ypos + CELL_UNIT - 1);
		}
	}

	private void resetShapeArea(int nActive) {
		if (curShapes[nActive] != null) {
			for (Point pt : curShapes[nActive].getPoints()) {
				if (pt.x >= 0 && pt.y >= 0) {
					int x = curShapePos[nActive].x + pt.x;
					int y = curShapePos[nActive].y + pt.y;

					gridCells[x][y].reset();
					repaint(x * CELL_UNIT, y * CELL_UNIT, CELL_UNIT, CELL_UNIT);
				}
			}
		}
	}

	private boolean updateShapeArea(int nActive) {
		boolean valid = true;

		if (curShapes[nActive] != null) {
			for (Point point : curShapes[nActive].getPoints()) {
				int x = curShapePos[nActive].x + point.x;
				int y = curShapePos[nActive].y + point.y;

				valid = valid && gridCells[x][y].isEmpty(nActive);
				gridCells[x][y].setProperties(curShapes[nActive].getColor(), nActive);
				repaint(x * CELL_UNIT, y * CELL_UNIT, CELL_UNIT, CELL_UNIT);
			}
		}

		return valid;
	}
}
