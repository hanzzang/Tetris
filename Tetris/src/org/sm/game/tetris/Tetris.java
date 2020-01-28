package org.sm.game.tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Tetris implements ActionListener {
	JFrame mainFrame;
	JPanel mainPanel;
	JButton btnGame;
	JButton btnExit;
	JButton btnSingle;
	JButton btnCoop;
	JButton btnCompete;
	JFrame modeFrame;
	JPanel modePanel;
	BufferedImage img = null;

	public Tetris() {
		mainFrame = new JFrame("SM - HN & MY Tetris");
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setSize(500, 700);

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(2,1));
		mainPanel.setBackground(new Color(0,73,140));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(0,73,140));

		ImageIcon b1 = new ImageIcon("IMG/st4.png");
		ImageIcon b2 = new ImageIcon("IMG/exit.png");
		ImageIcon b3 = new ImageIcon("IMG/tetrisheart3.png");

		Image bi1 = b1.getImage();
		Image bi2 = b2.getImage();
		Image bi3 = b3.getImage();

		Image bn1 = bi1.getScaledInstance(200, 80, Image.SCALE_SMOOTH);
		Image bn2 = bi2.getScaledInstance(310, 190, Image.SCALE_SMOOTH);
		Image bn3 = bi3.getScaledInstance(400, 250, Image.SCALE_SMOOTH);

		JLabel imageLabel1 = new JLabel(new ImageIcon(bn3));

		btnGame = new JButton(new ImageIcon(bn1));
		btnGame.setBorderPainted(false);
		btnGame.setContentAreaFilled(false);
		btnGame.setFocusPainted(false);
		btnGame.addActionListener(this);

		btnExit = new JButton(new ImageIcon(bn2));
		btnExit.setBorderPainted(false);
		btnExit.setContentAreaFilled(false);
		btnExit.setFocusPainted(false);
		btnExit.addActionListener(this);

		mainPanel.add("Center", imageLabel1);
		buttonPanel.add("Center", btnGame);
		buttonPanel.add("Center", btnExit);
		
		mainPanel.add("Center", buttonPanel);

		mainFrame.getContentPane().add(mainPanel, "Center");

		mainFrame.setDefaultCloseOperation(TetrisFrame.EXIT_ON_CLOSE);
		moveToCenter(mainFrame);
		mainFrame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnGame) {
			mainFrame.dispose();
			try {
				OpenTetrisMode();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btnExit) {
			System.exit(0);
		} else if (e.getSource() == btnSingle) {
			modeFrame.dispose();
			TetrisMode newMode = Enum.valueOf(TetrisMode.class, "SINGLE");
			try {
				OpenTetrisFrame(newMode);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btnCoop) {
			modeFrame.dispose();
			TetrisMode newMode = Enum.valueOf(TetrisMode.class, "COOP");
			try {
				OpenTetrisFrame(newMode);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btnCompete) {
			modeFrame.dispose();
			TetrisMode newMode = Enum.valueOf(TetrisMode.class, "COMPETE");
			try {
				OpenTetrisFrame(newMode);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public void OpenTetrisMode() throws Exception {
		modeFrame = new JFrame("SM - HN & MY Tetris");
		modeFrame.setLayout(new BorderLayout());
		modeFrame.setSize(500, 700);

		modePanel = new JPanel();
		modePanel.setLayout(new GridLayout(5,1));
		modePanel.setBackground(new Color(0,73,140));

		ImageIcon b1 = new ImageIcon("IMG/single.png");
		ImageIcon b2 = new ImageIcon("IMG/coop.png");
		ImageIcon b3 = new ImageIcon("IMG/compete.png");
		ImageIcon b4 = new ImageIcon("IMG/heart1.png");

		Image bi1 = b1.getImage();
		Image bi2 = b2.getImage();
		Image bi3 = b3.getImage();
		Image bi4 = b4.getImage();

		Image bn1 = bi1.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
		Image bn2 = bi2.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
		Image bn3 = bi3.getScaledInstance(150, 50, Image.SCALE_SMOOTH);
		Image bn4 = bi4.getScaledInstance(50, 50, Image.SCALE_SMOOTH);


		JLabel imageLabel1 = new JLabel(new ImageIcon(bn4));
		JLabel imageLabel2 = new JLabel(new ImageIcon(bn4));
		
		btnSingle = new JButton(new ImageIcon(bn1));
		btnSingle.setBorderPainted(false);
		btnSingle.setContentAreaFilled(false);
		btnSingle.setFocusPainted(false);
		btnSingle.addActionListener(this);

		btnCoop = new JButton(new ImageIcon(bn2));
		btnCoop.setBorderPainted(false);
		btnCoop.setContentAreaFilled(false);
		btnCoop.setFocusPainted(false);
		btnCoop.addActionListener(this);

		btnCompete = new JButton(new ImageIcon(bn3));
		btnCompete.setBorderPainted(false);
		btnCompete.setContentAreaFilled(false);
		btnCompete.setFocusPainted(false);
		btnCompete.addActionListener(this);


		modePanel.add("Center", imageLabel1);
		modePanel.add("Center", btnSingle);
		modePanel.add("Center", btnCoop);
		modePanel.add("Center", btnCompete);
		modePanel.add("Center", imageLabel2);

		modeFrame.add(modePanel);

		modeFrame.setDefaultCloseOperation(TetrisFrame.EXIT_ON_CLOSE);
		Tetris.moveToCenter(modeFrame);
		modeFrame.setVisible(true);
	}

	public void OpenTetrisFrame(TetrisMode newMode) throws Exception {
		TetrisFrame tetrisFrame = new TetrisFrame(newMode);

		tetrisFrame.setDefaultCloseOperation(TetrisFrame.EXIT_ON_CLOSE);
		tetrisFrame.pack();
		Tetris.moveToCenter(tetrisFrame);
		tetrisFrame.setVisible(true);
	}

	public static void moveToCenter(Component component) {
		Dimension dParent;
		Dimension dComponent = component.getSize();
		Component parent = component.getParent();
		Point offset;

		if (parent == null) {
			dParent = Toolkit.getDefaultToolkit().getScreenSize();
			offset = new Point(0, 0);
		} else {
			dParent = parent.getSize();
			offset = parent.getLocation();
		}

		component.setLocation(offset.x + (int) (dParent.getWidth() - dComponent.getWidth()) / 2,
				offset.y + (int) (dParent.getHeight() - dComponent.getHeight()) / 2);
	}

	public static void main(String[] args) {
		new Tetris();
	}
}
