package org.sm.game.tetris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;

public class TetrisBoard extends JPanel {
	private static final long serialVersionUID = 1414878855461319633L;
	public static final String START_LABEL = "START (F2)";
	public static final String PAUSE_LABEL = "PAUSE (F2)";
	public static final String STOP_LABEL = "STOP (F3)";
	public static final String MAIN_LABEL = "MAIN";

	private TetrisGrid nextGrid;
	private JTextField lblScore;
	private JTextField lblLevel;
	private JTextField lblLines;
	private JButton btnStart;
	private JButton btnEnd;
	private JButton btnMain;

	public TetrisBoard(boolean bMain, Container parent) {
		initContentPane(bMain, parent);
	}

	public void setScore(long score) {
		lblScore.setText(Long.toString(score));
	}

	public long getScore() {
		return Long.parseLong(lblScore.getText());
	}

	public void setLevel(int level) {
		lblLevel.setText(Integer.toString(level));
	}

	public int getLevel() {
		int level;

		try {
			level = Integer.parseInt(lblLevel.getText());
		} catch (NumberFormatException e) {
			level = 0;
		}

		return level;
	}

	public void setRows(int lines) {
		lblLines.setText(Integer.toString(lines));
	}

	public int getRows() {
		return Integer.parseInt(lblLines.getText());
	}

	public void setGameStarted() {
		lblLevel.setEditable(false);
		setLevel(getLevel());
		if (btnStart != null)
			btnStart.setText(PAUSE_LABEL);
	}

	public void setGamePaused() {
		if (btnStart != null)
			btnStart.setText(START_LABEL);
	}

	public void setNextBlockShape(TetrisBlockShape blockShape) {
		nextGrid.setBlockShape(blockShape, 0, true);
	}

	public void reset(int level) {
		nextGrid.reset();
		setScore(0);
		setLevel(level);
		lblLevel.setEditable(true);
		setRows(0);

		if (btnStart != null)
			btnStart.setText(START_LABEL);
	}

	private void initContentPane(boolean bMain, Container parent) {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		setLayout(gbl);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;

		addVerticalStrut(5, gbl, gbc);
		addLabel("NEXT:", gbl, gbc);
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		nextGrid = new TetrisGrid(4, 4, false, true); // next shape
		box.add(nextGrid);
		box.add(Box.createHorizontalGlue());
		addComponent(box, gbl, gbc);

		addVerticalStrut(10, gbl, gbc);
		addLabel("SCORE:", gbl, gbc);
		lblScore = createTextField(false);
		setScore(0);
		addComponent(lblScore, gbl, gbc);

		addLabel("LEVEL:", gbl, gbc);
		lblLevel = createTextField(true);
		if (parent instanceof KeyListener)
			lblLevel.addKeyListener((KeyListener) parent);
		setLevel(0);
		addComponent(lblLevel, gbl, gbc);

		addLabel("LINES:", gbl, gbc);
		lblLines = createTextField(false);
		setRows(0);
		addComponent(lblLines, gbl, gbc);

		if (bMain) {
			addVerticalStrut(20, gbl, gbc);
			btnStart = new JButton(START_LABEL);
			if (parent instanceof ActionListener)
				btnStart.addActionListener((ActionListener) parent);
			addComponent(btnStart, gbl, gbc);

			addVerticalStrut(20, gbl, gbc);
			btnEnd = new JButton(STOP_LABEL);
			if (parent instanceof ActionListener)
				btnEnd.addActionListener((ActionListener) parent);
			addComponent(btnEnd, gbl, gbc);

			addVerticalStrut(20, gbl, gbc);
			btnMain = new JButton(MAIN_LABEL);
			if (parent instanceof ActionListener)
				btnMain.addActionListener((ActionListener) parent);
			addComponent(btnMain, gbl, gbc);

			addVerticalStrut(20, gbl, gbc);
			addLabel("ก็: LEFT", gbl, gbc);
			addLabel("กๆ: RIGHT", gbl, gbc);
			addLabel("ก่: ROTATE", gbl, gbc);
			addLabel("ก้: DOWN", gbl, gbc);
		} else {
			addVerticalStrut(60, gbl, gbc);
			btnStart = null;

			addLabel("A: LEFT", gbl, gbc);
			addLabel("D: RIGHT", gbl, gbc);
			addLabel("W: ROTATE", gbl, gbc);
			addLabel("S: DOWN", gbl, gbc);
		}
	}

	private void addLabel(String text, GridBagLayout gbl, GridBagConstraints gbc) {
		addComponent(new JLabel(text), gbl, gbc);
	}

	private void addVerticalStrut(int height, GridBagLayout gbl, GridBagConstraints gbc) {
		addComponent(Box.createVerticalStrut(height), gbl, gbc);
	}

	private void addComponent(Component component, GridBagLayout gbl, GridBagConstraints gbc) {
		gbl.setConstraints(component, gbc);
		add(component);
	}

	private JTextField createTextField(boolean editable) {
		JTextField tf = new JTextField();

		tf.setBackground(Color.BLACK);
		tf.setForeground(Color.WHITE);
		tf.setHorizontalAlignment(JTextField.RIGHT);
		tf.setEditable(editable);

		return tf;
	}
}
