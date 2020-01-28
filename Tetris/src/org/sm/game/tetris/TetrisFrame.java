package org.sm.game.tetris;

import java.awt.*;
import java.awt.event.*;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class TetrisFrame extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = -7507386662957748527L;

	// 레벨에 따른 속도 ( 레벨1 : 1 초에 1칸, 레벨2 : 0.7초에 한칸 ... )
	private static final long[] tetrisSpeeds = { 1000, 700, 490, 343, 240, 168, 117, 82, 60, 50 };
	// 점수를 동시 삭제행이 많을수록 점수가 기하급수적으로 늘어나도록 조정
	// 점수 규칙 ( 행삭제갯수가 0 이면 1, 1이면 10, ... )
	private static final int[] scoreRule = { 10, 25, 60, 150, 400 };
	// 레벨업이 몇 삭제행 단위로 동작하는지 (5이상으로 설정) => 테스트할때 빨리하려고 5로 조정해놨어요
	private static final int levelupRows = 5;

	private static enum GameStatus {
		STOPPED, STARTED, PAUSED
	};

	private static final String MENU_STOP_GAME = "STOP (F3)";
	private static final String MENU_SHOW_RANK = "Show Rank";

	TetrisConfig gameConfig;
	private JPanel[] outerBoxes;
	private TetrisGrid[] gameGrids;
	private TetrisBoard[] tetrisBoards;
	private TetrisBlock[] tetrisBlocks;
	private TetrisMode gameMode;
	private TetrisTimer[] timers;
	private TetrisTimerTask[] timerTasks;
	private int[] levelRows;
	private GameStatus gameStatus;

	public TetrisFrame(TetrisMode newMode) throws Exception {
		super("SM - HN & MY Tetris");

		gameConfig = TetrisConfig.getInstance();

		outerBoxes = new JPanel[2];
		tetrisBoards = new TetrisBoard[2];
		tetrisBlocks = new TetrisBlock[2];
		gameGrids = new TetrisGrid[2];
		timers = new TetrisTimer[2];
		timerTasks = new TetrisTimerTask[2];
		levelRows = new int[2];
		gameStatus = GameStatus.STOPPED;

		initContentPane(newMode);
		initEventListeners();
		setResizable(false);
	}

	public void setScore(int nActive, long score) {
		tetrisBoards[nActive].setScore(score);
	}

	public void setLevel(int nActive, int level) {
		tetrisBoards[nActive].setLevel(level);
	}

	public void setLines(int nActive, int lines) {
		tetrisBoards[nActive].setRows(lines);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton && (TetrisBoard.START_LABEL.equals(((JButton) e.getSource()).getText())
				|| TetrisBoard.PAUSE_LABEL.equals(((JButton) e.getSource()).getText()))) {
			controlGame();
		} else if (e.getSource() instanceof JButton
				&& (TetrisBoard.STOP_LABEL.equals(((JButton) e.getSource()).getText()))) {
			stopGame(false);
		} else if (e.getSource() instanceof JButton
				&& (TetrisBoard.MAIN_LABEL.equals(((JButton) e.getSource()).getText()))) {
			stopGame(false);
			dispose();
			new Tetris();
		} else if (e.getSource() instanceof JMenuItem) {
			String actionCmd = ((JMenuItem) e.getSource()).getActionCommand();

			if (MENU_STOP_GAME.equals(actionCmd))
				stopGame(false);
			else if (MENU_SHOW_RANK.equals(actionCmd))
				showRank(false);
			else if (TetrisBoard.START_LABEL.equals(actionCmd) || TetrisBoard.PAUSE_LABEL.equals(actionCmd))
				controlGame();
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F2)
			controlGame();
		else if (e.getKeyCode() == KeyEvent.VK_F3 && gameStatus != GameStatus.STOPPED)
			stopGame(false);
		else if (gameStatus == GameStatus.STARTED) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT)
				moveShape(-1, 0, 0);
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				moveShape(1, 0, 0);
			else if (e.getKeyCode() == KeyEvent.VK_UP)
				rotateShape(0);
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				moveShape(0, -1, 0);
			else if (gameMode == TetrisMode.SINGLE) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					moveShape(0, -1, 0);
			} else if (e.getKeyCode() == KeyEvent.VK_A)
				moveShape(-1, 0, 1);
			else if (e.getKeyCode() == KeyEvent.VK_D)
				moveShape(1, 0, 1);
			else if (e.getKeyCode() == KeyEvent.VK_W)
				rotateShape(1);
			else if (e.getKeyCode() == KeyEvent.VK_S)
				moveShape(0, -1, 1);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_OPENED)
			requestFocusInWindow();
	}

	private void initContentPane(TetrisMode newMode) {
		JPanel mainPanel;
		Container contentPane = getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		if (contentPane instanceof JPanel)
			mainPanel = (JPanel) contentPane;
		else {
			mainPanel = new JPanel();
			setContentPane(mainPanel);
		}
		ImageIcon b1 = new ImageIcon("IMG/sm.png");
		Image bi1 = b1.getImage();
		Image bn1 = bi1.getScaledInstance(110, 140, Image.SCALE_SMOOTH);
		JLabel imageLabel1 = new JLabel(new ImageIcon(bn1));

		mainPanel.setBorder(new CompoundBorder(mainPanel.getBorder(), new EmptyBorder(10, 10, 10, 10))); // margins
		mainPanel.setBackground(new Color(0, 73, 140));
		mainPanel.setLayout(gbl);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.gridheight = 1;

		outerBoxes[0] = new JPanel();
		outerBoxes[0].setBackground(Color.WHITE);
		addComponent(outerBoxes[0], mainPanel, gbl, gbc, false);
		addComponent(Box.createGlue(), mainPanel, gbl, gbc, false);

		gbc.gridheight = 2;
		outerBoxes[1] = new JPanel();
		outerBoxes[1].setBackground(Color.WHITE);
		addComponent(outerBoxes[1], mainPanel, gbl, gbc, false);

		JPanel outerBox = new JPanel();
		gameGrids[0] = new TetrisGrid(22, 37, false, false);
		outerBox.add(gameGrids[0]);
		outerBox.setBackground(Color.WHITE);
		addComponent(outerBox, mainPanel, gbl, gbc, true);

		timers[0] = new TetrisTimer("TetrisTimer0");
		timerTasks[0] = new TetrisTimerTask(this, 0);
		levelRows[0] = 0;

		gbc.gridheight = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		tetrisBlocks[0] = new TetrisBlock();
		tetrisBoards[0] = new TetrisBoard(true, this);
		tetrisBoards[0].setLevel(gameConfig.getInitLevel(0));
		tetrisBoards[0].setBackground(Color.white);
		outerBox = new JPanel();
		outerBox.add(tetrisBoards[0]);
		outerBox.setBackground(Color.white);
		addComponent(outerBox, mainPanel, gbl, gbc, true);
		addComponent(imageLabel1, mainPanel, gbl, gbc, false);
		addComponent(Box.createGlue(), mainPanel, gbl, gbc, false);

		setGameMode(null, newMode);
	}

	private TetrisMode setGameMode(TetrisMode prevMode, TetrisMode newMode) {
		if ((prevMode == null || prevMode == TetrisMode.SINGLE) && newMode != TetrisMode.SINGLE) {
			tetrisBlocks[1] = new TetrisBlock();
			tetrisBoards[1] = new TetrisBoard(false, this);
			tetrisBoards[1].setLevel(gameConfig.getInitLevel(1));
			tetrisBoards[1].setBackground(Color.white);
			outerBoxes[0].add(tetrisBoards[1]);
			outerBoxes[0].setVisible(true);
			timers[1] = new TetrisTimer("TetrisTimer1");
			timerTasks[1] = new TetrisTimerTask(this, 1);
			levelRows[1] = 0;
		} else if (newMode == TetrisMode.SINGLE) {
			if (prevMode != null)
				outerBoxes[0].remove(tetrisBoards[1]);
			outerBoxes[0].setVisible(false);
			tetrisBlocks[1] = null;
			tetrisBoards[1] = null;
			timerTasks[1] = null;
			timers[1] = null;
		}

		if (newMode == TetrisMode.COMPETE) {
			gameGrids[1] = new TetrisGrid(22, 37, false, false);
			outerBoxes[1].add(gameGrids[1]);
			outerBoxes[1].setBackground(Color.white);
			outerBoxes[1].setVisible(true);
		} else {
			if (prevMode == TetrisMode.COMPETE)
				outerBoxes[1].remove(gameGrids[1]);
			gameGrids[1] = null;
			outerBoxes[1].setVisible(false);
		}

		if (newMode == TetrisMode.COOP)
			gameGrids[0].setSize(28, 37, true);
		else if (prevMode == TetrisMode.COOP)
			gameGrids[0].setSize(22, 37, false);

		gameMode = newMode;
		gameConfig.setTetrisMode(newMode);
		return newMode;
	}

	private void initEventListeners() {
		addKeyListener(this);

		MouseAdapter ma = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showPopupMenu(e);
			}
		};
		addMouseListener(ma);
	}

	private void controlGame() {
		requestFocusInWindow();

		int boardCount = gameMode == TetrisMode.SINGLE ? 1 : 2;

		if (gameStatus == GameStatus.STOPPED) // Start
		{
			for (int i = 0; i < boardCount; i++) {
				tetrisBoards[i].setGameStarted();
				int level = tetrisBoards[i].getLevel();
				if (level >= tetrisSpeeds.length) {
					level = tetrisSpeeds.length - 1;
					tetrisBoards[i].setLevel(level);
				}

				if (level != gameConfig.getInitLevel(i))
					gameConfig.setInitLevel(i, level);

				newShape(i);
			}
			gameStatus = GameStatus.STARTED;
		} else if (gameStatus == GameStatus.STARTED) // Pause
		{
			for (int i = 0; i < boardCount; i++) {
				tetrisBoards[i].setGamePaused();
				timers[i].cancel();
			}
			gameStatus = GameStatus.PAUSED;
		} else if (gameStatus == GameStatus.PAUSED) // Continue
		{
			for (int i = 0; i < boardCount; i++) {
				tetrisBoards[i].setGameStarted();
				setTimeout(i);
			}
			gameStatus = GameStatus.STARTED;
		}

		pack();
	}

	private void stopGame(boolean rank) {
		gameStatus = GameStatus.STOPPED;
		timers[0].cancel();
		if (timers[1] != null)
			timers[1].cancel();

		if (rank) {
			showRank(rank);
		}

		gameGrids[0].reset();
		if (gameMode == TetrisMode.COMPETE)
			gameGrids[1].reset();
		tetrisBoards[0].reset(gameConfig.getInitLevel(0));
		levelRows[0] = 0;
		if (gameMode != TetrisMode.SINGLE) {
			tetrisBoards[1].reset(gameConfig.getInitLevel(1));
			levelRows[1] = 0;
		}

		gameConfig.saveTetrisConfig();
	}

	private void showRank(boolean bShowCurrent) {
		TetrisBoard[] tbs = bShowCurrent ? tetrisBoards : null;
		TetrisRankDlg dlgRank = new TetrisRankDlg(this, tbs, gameConfig);

		dlgRank.pack();
		Tetris.moveToCenter(dlgRank);
		dlgRank.setVisible(true);
	}

	private void setTimeout(int nActive) {
		int level = tetrisBoards[nActive].getLevel();

		timers[nActive].schedule(timerTasks[nActive], tetrisSpeeds[level]);
	}

	private void downShape(int nActive) {
		TetrisGrid gameGrid = getGameGrid(nActive);
		TetrisGrid.CanMove canMove;
		boolean bStopGame = false;

		synchronized (this) {
			canMove = gameGrid.canMoveShape(0, 1, nActive);

			if (canMove == TetrisGrid.CanMove.CANNOT)
				bStopGame = onShapeDropped(nActive);
			else {
				if (canMove == TetrisGrid.CanMove.CAN)
					gameGrid.moveShape(0, 1, nActive);
				setTimeout(nActive); // dual
			}
		}

		if (bStopGame)
			stopGame(true);
	}

	private void moveShape(int x, int y, int nActive) {
		TetrisGrid gameGrid = getGameGrid(nActive);

		if (y < 0) {
			boolean bStopGame = false;

			timers[nActive].cancel();
			y = 1;
			synchronized (this) {
				TetrisGrid.CanMove canMove;
				while ((canMove = gameGrid.canMoveShape(x, y, nActive)) == TetrisGrid.CanMove.CAN)
					y++;

				gameGrid.moveShape(0, y - 1, nActive);
				if (canMove == TetrisGrid.CanMove.CANNOT)
					bStopGame = onShapeDropped(nActive);
				else
					setTimeout(nActive);
			}

			if (bStopGame)
				stopGame(true);
		} else {
			synchronized (this) {
				if (gameGrid.canMoveShape(x, 0, nActive) == TetrisGrid.CanMove.CAN)
					gameGrid.moveShape(x, 0, nActive);
			}
		}
	}

	private void rotateShape(int nActive) {
		TetrisGrid gameGrid = getGameGrid(nActive);
		TetrisBlockShape rotatedShape = tetrisBlocks[nActive].getRotatedShape();

		synchronized (this) {
			if (gameGrid.canMoveShape(0, 0, rotatedShape.getPoints(), nActive) == TetrisGrid.CanMove.CAN) {
				tetrisBlocks[nActive].rotateShape();
				gameGrid.setBlockShape(rotatedShape, nActive, false);
			}
		}
	}

	private boolean onShapeDropped(int nActive) {
		TetrisGrid gameGrid = getGameGrid(nActive);
		int deletedRows = gameGrid.deleteFilledRows(nActive);
		int level = tetrisBoards[nActive].getLevel();
		long score = scoreRule[deletedRows] * (level + 1);

		if (deletedRows > 0) {
			tetrisBoards[nActive].setRows(tetrisBoards[nActive].getRows() + deletedRows);

			if ((level + 1) < tetrisSpeeds.length
					&& tetrisBoards[nActive].getRows() >= levelRows[nActive] + levelupRows) {
				levelRows[nActive] = tetrisBoards[nActive].getRows();
				tetrisBoards[nActive].setLevel(level + 1);
			}

			if (gameGrid.isBottomEmpty(nActive))
				score += 10000;
		}

		if (gameMode == TetrisMode.COOP)
			score *= 2;

		tetrisBoards[nActive].setScore(tetrisBoards[nActive].getScore() + score);

		return newShape(nActive);
	}

	private TetrisGrid getGameGrid(int nActive) {
		return gameMode == TetrisMode.COMPETE ? gameGrids[nActive] : gameGrids[0];
	}

	private boolean newShape(int nActive) {
		boolean bStopGame = false;
		tetrisBlocks[nActive].newBlock();
		tetrisBoards[nActive].setNextBlockShape(tetrisBlocks[nActive].getBlockShape(true));

		if (getGameGrid(nActive).setBlockShape(tetrisBlocks[nActive].getBlockShape(false), nActive, true))
			setTimeout(nActive);
		else
			bStopGame = true;

		return bStopGame;
	}

	private void addComponent(Component component, Container boxOuter, GridBagLayout gbl, GridBagConstraints gbc,
			boolean bInset) {
		gbc.insets = bInset ? new Insets(2, 2, 2, 2) : new Insets(0, 0, 0, 0);
		gbl.setConstraints(component, gbc);
		boxOuter.add(component);
	}

	private void showPopupMenu(MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();

		String startPause = gameStatus == GameStatus.STARTED ? TetrisBoard.PAUSE_LABEL : TetrisBoard.START_LABEL;
		JMenuItem mi = new JMenuItem(startPause);
		mi.setActionCommand(startPause);
		mi.addActionListener(this);
		popup.add(mi);

		if (gameStatus != GameStatus.STOPPED) {
			mi = new JMenuItem(MENU_STOP_GAME);
			mi.setActionCommand(MENU_STOP_GAME);
			mi.addActionListener(this);
			popup.add(mi);
		} else {
			popup.addSeparator();

			mi = new JMenuItem(MENU_SHOW_RANK);
			mi.setActionCommand(MENU_SHOW_RANK);
			mi.addActionListener(this);
			popup.add(mi);
		}

		popup.show(this, e.getX(), e.getY());
	}

	private static class TetrisTimerTask extends TimerTask {
		private TetrisFrame testFrame;
		private int nActive;

		private TetrisTimerTask(TetrisFrame testFrame, int nActive) {
			this.testFrame = testFrame;
			this.nActive = nActive;
		}

		public void run() {
			testFrame.downShape(nActive);
		}
	}

}
