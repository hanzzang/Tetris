package org.sm.game.tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

public class TetrisRankDlg extends JDialog implements ActionListener, KeyListener {
	private static final long serialVersionUID = -8305720483478419067L;

	private TetrisTable rankTable;
	private JButton okButton;

	private static class TetrisTable extends JTable {
		private static final long serialVersionUID = 2280953393959801619L;

		public TetrisTable(TableModel model) {
			super(model);
		}

		public TableCellRenderer getCellRenderer(int row, int col) {
			TableCellRenderer renderer = super.getCellRenderer(row, col);

			if (renderer instanceof JComponent) {
				Color bg = isCellEditable(row, 0) ? Color.ORANGE : getBackground();
				((JComponent) renderer).setBackground(bg);
			}

			return renderer;
		}
	}

	private static class TetrisRankTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -5154675340112743134L;

		private static final String[] columns = { "Name", "Score", "Level", "Rows", "Date" };
		private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		private TetrisRank[] ranks;
		private ArrayList<Integer> editableRows;

		private TetrisRankTableModel() {
			ranks = null;
			editableRows = new ArrayList<Integer>();
		}

		public String getColumnName(int col) {
			return columns[col];
		}

		public int getColumnCount() {
			return columns.length;
		}

		public Class<?> getColumnClass(int col) {
			Object value = getValueAt(0, col);

			return value == null ? String.class : value.getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return col == 0 && editableRows.contains(row);
		}

		public int getRowCount() {
			return ranks == null ? 0 : ranks.length;
		}

		public Object getValueAt(int row, int col) {
			TetrisRank rank = ranks[row];
			Object value = null;

			if (col == 0)
				value = rank.getName();
			else if (col == 1)
				value = rank.getScore();
			else if (col == 2)
				value = rank.getLevel();
			else if (col == 3)
				value = rank.getRows();
			else if (col == 4)
				value = sdf.format(rank.getDate());

			return value;
		}

		public void setValueAt(Object aValue, int row, int col) {
			if (col == 0 && isCellEditable(row, col)) {
				TetrisRank rank = ranks[row];
				rank.setName(aValue == null ? null : aValue.toString());
			}
		}

		private void setRanks(TetrisRank[] ranks) {
			this.ranks = ranks;

			fireTableDataChanged();
		}

		private void addEditableRow(int row) {
			if (row >= 0 && editableRows.contains(row) == false)
				editableRows.add(row);
		}

		private int getHighestEditableRow() {
			int row = -1;

			for (int er : editableRows) {
				if (row < 0 || row > er)
					row = er;
			}

			return row;
		}
	}

	public TetrisRankDlg(Frame parent, TetrisBoard[] tetrisBoards, TetrisConfig config) {
		super(parent, "Tetris Rank", true);

		initContentPane();
		checkScores(tetrisBoards, config);
		initColumnSizes();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getRootPane().setDefaultButton(okButton);
	}

	private void initContentPane() {
		Container contentPane = getContentPane();
		JPanel mainPanel;

		if (contentPane instanceof JPanel)
			mainPanel = (JPanel) contentPane;
		else {
			mainPanel = new JPanel();
			setContentPane(mainPanel);
		}

		mainPanel.setBorder(new CompoundBorder(mainPanel.getBorder(), new EmptyBorder(10, 10, 10, 10))); // margins
		mainPanel.setLayout(new BorderLayout(6, 6));

		rankTable = new TetrisTable(new TetrisRankTableModel());
		rankTable.setAutoCreateRowSorter(true);
		rankTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane scrollPane = new JScrollPane(rankTable);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		Box boxButtons = new Box(BoxLayout.X_AXIS);
		boxButtons.add(Box.createHorizontalGlue());
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		boxButtons.add(okButton);
		boxButtons.add(Box.createHorizontalGlue());
		mainPanel.add(boxButtons, BorderLayout.SOUTH);

		rankTable.addKeyListener(this);
	}

	private void initColumnSizes() {
		String[] headers = TetrisRankTableModel.columns;
		TableCellRenderer tcr = rankTable.getTableHeader().getDefaultRenderer();
		TableColumnModel tcm = rankTable.getColumnModel();
		TableColumn column = null;
		Component comp;
		ArrayList<Integer> widths = new ArrayList<Integer>();
		int sum = 0;

		for (int col = 0; col < headers.length; col++) {
			column = tcm.getColumn(col);
			comp = tcr.getTableCellRendererComponent(rankTable, headers[col], false, false, 0, 0);
			int width = comp.getPreferredSize().width;

			TableCellRenderer renderer = rankTable.getCellRenderer(0, col);
			for (int row = 0; row < rankTable.getRowCount(); row++) {
				comp = renderer.getTableCellRendererComponent(rankTable, rankTable.getValueAt(row, col), false, false,
						row, col);
				int w = comp.getPreferredSize().width;

				if (w > width)
					width = w;
			}

			column.setPreferredWidth(width);
			widths.add(width);
			sum += width;
		}

		int preferredWidth = rankTable.getParent().getPreferredSize().width;

		for (int col = 0; col < rankTable.getColumnCount(); col++)
			tcm.getColumn(col).setPreferredWidth(preferredWidth * widths.get(col) / sum);
	}

	private void checkScores(TetrisBoard[] tetrisBoards, TetrisConfig config) {
		TetrisRankTableModel tblModel = (TetrisRankTableModel) rankTable.getModel();

		if (tetrisBoards != null) {
			TetrisRank rank = new TetrisRank(null, tetrisBoards[0].getScore(), tetrisBoards[0].getLevel(),
					tetrisBoards[0].getRows(), null);
			tblModel.addEditableRow(config.addRank(rank));

			if (config.getTetrisMode() != TetrisMode.SINGLE) {
				rank = new TetrisRank(null, tetrisBoards[1].getScore(), tetrisBoards[1].getLevel(),
						tetrisBoards[1].getRows(), null);
				tblModel.addEditableRow(config.addRank(rank));
			}
		}

		tblModel.setRanks(config.getRanks());
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			TetrisRankTableModel tblModel = (TetrisRankTableModel) rankTable.getModel();

			int highest = tblModel.getHighestEditableRow();
			if (highest >= 0) {
				rankTable.editCellAt(highest, 0);
				Component c = rankTable.getEditorComponent();
				if (c instanceof JTextComponent)
					((JTextComponent) c).getCaret().setVisible(true);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			if (rankTable.isEditing())
				rankTable.getCellEditor().stopCellEditing();
			dispose();
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getSource() == rankTable) {
			if (rankTable.isEditing() == false)
				dispose();
		}
	}

	public void keyReleased(KeyEvent e) {
	}
}
