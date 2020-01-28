package org.sm.game.tetris;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TetrisConfig {
	private static final String PROP_SETTING = "tetris.settings";
	private static final String DEFAULT_SETTING = "smTetris.xml";
	private static final int MAX_RANK = 25;
	private static final String ROOT_NODE = "SMTetrisSettings";
	private static final String TETRIS_MODE_ATTR = "tetris.mode";
	private static final String INIT_LEVEL_ATTR = "init.level";
	private static final String RANK_NODE = "tetris.rank";
	private static final String NAME_ATTR = "name";
	private static final String SCORE_ATTR = "score";
	private static final String LEVEL_ATTR = "level";
	private static final String LINES_ATTR = "lines";
	private static final String DATE_ATTR = "date";

	private static TetrisConfig instance = null;

	private String configPath;
	private TetrisMode tetrisMode;
	private int[] initLevels;
	private XPath xpath;
	private ArrayList<TetrisRank> ranks;
	private boolean bDirty;

	public static TetrisConfig getInstance() throws Exception {
		if (instance == null)
			instance = new TetrisConfig();

		return instance;
	}

	private TetrisConfig() throws Exception {
		File file;

		configPath = System.getProperty(PROP_SETTING);
		if (configPath == null || configPath.length() == 0)
			configPath = DEFAULT_SETTING;

		file = new File(configPath);

		initLevels = new int[2];
		ranks = new ArrayList<TetrisRank>();
		bDirty = false;

		if (file.canRead())
			readConfig(file);
		else {
			tetrisMode = TetrisMode.SINGLE;
			initLevels[0] = 0;
			initLevels[1] = 0;
		}
	}

	public TetrisMode getTetrisMode() {
		return tetrisMode;
	}

	public void setTetrisMode(TetrisMode tetrisMode) {
		if (this.tetrisMode != tetrisMode) {
			this.tetrisMode = tetrisMode;
			bDirty = true;
		}
	}

	public int getInitLevel(int nActive) {
		return initLevels[nActive];
	}

	public void setInitLevel(int nActive, int level) {
		if (initLevels[nActive] != level) {
			initLevels[nActive] = level;
			bDirty = true;
		}
	}

	public int addRank(TetrisRank rank) {
		int i;

		for (i = 0; i < ranks.size(); i++) {
			if ((ranks.get(i).getScore() < rank.getScore())
					|| (ranks.get(i).getScore() == rank.getScore() && ranks.get(i).getLevel() < rank.getLevel())
					|| (ranks.get(i).getScore() == rank.getScore() && ranks.get(i).getLevel() == rank.getLevel()
							&& ranks.get(i).getRows() < rank.getRows()))
				break;
		}

		if (i < MAX_RANK) {
			ranks.add(i, rank);

			while (ranks.size() > MAX_RANK)
				ranks.remove(MAX_RANK);

			bDirty = true;
		} else
			i = -1;

		return i;
	}

	public TetrisRank[] getRanks() {
		return ranks.toArray(new TetrisRank[ranks.size()]);
	}

	public void saveTetrisConfig() {
		try {
			if (bDirty) {
				saveConfig();
				bDirty = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readConfig(File file) throws Exception {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(file);

			xpath = XPathFactory.newInstance().newXPath();
			initConfig(doc);
		} finally {
			xpath = null;
		}
	}

	private void initConfig(Document doc) throws XPathExpressionException {
		Element settings = getElement('/' + ROOT_NODE, doc);

		tetrisMode = valueOf(TetrisMode.class, settings.getAttribute(TETRIS_MODE_ATTR));
		initLevels[0] = Integer.valueOf(settings.getAttribute(INIT_LEVEL_ATTR + '0'));
		initLevels[1] = Integer.valueOf(settings.getAttribute(INIT_LEVEL_ATTR + '1'));

		NodeList ranks = getNodeList(RANK_NODE, settings);
		for (int i = 0; i < ranks.getLength(); i++)
			addRank((Element) ranks.item(i));
	}

	private void addRank(Element rank) {
		addRank(new TetrisRank(rank.getAttribute(NAME_ATTR), Long.valueOf(rank.getAttribute(SCORE_ATTR)),
				Integer.valueOf(rank.getAttribute(LEVEL_ATTR)), Integer.valueOf(rank.getAttribute(LINES_ATTR)),
				new Date(Long.valueOf(rank.getAttribute(DATE_ATTR)))));
	}

	private Element getElement(String expression, Object item) throws XPathExpressionException {
		return (Element) xpath.evaluate(expression, item, XPathConstants.NODE);
	}

	private NodeList getNodeList(String expression, Object item) throws XPathExpressionException {
		return (NodeList) xpath.evaluate(expression, item, XPathConstants.NODESET);
	}

	private void saveConfig() throws Exception {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.newDocument();
		Element rootNode = doc.createElement(ROOT_NODE);

		rootNode.setAttribute(TETRIS_MODE_ATTR, tetrisMode.name());
		rootNode.setAttribute(INIT_LEVEL_ATTR + '0', Integer.toString(initLevels[0]));
		rootNode.setAttribute(INIT_LEVEL_ATTR + '1', Integer.toString(initLevels[1]));
		doc.appendChild(rootNode);

		for (int i = 0; i < ranks.size(); i++) {
			Element rankNode = doc.createElement(RANK_NODE);
			TetrisRank rank = ranks.get(i);

			rankNode.setAttribute(NAME_ATTR, rank.getName());
			rankNode.setAttribute(SCORE_ATTR, Long.toString(rank.getScore()));
			rankNode.setAttribute(LEVEL_ATTR, Integer.toString(rank.getLevel()));
			rankNode.setAttribute(LINES_ATTR, Integer.toString(rank.getRows()));
			rankNode.setAttribute(DATE_ATTR, Long.toString(rank.getDate().getTime()));

			rootNode.appendChild(rankNode);
		}

		saveDocument(doc, configPath);
	}

	private void saveDocument(Document doc, String path) {
		try {
			// xml파일에 저장
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			trans.transform(new DOMSource(doc), new StreamResult(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
		return Enum.valueOf(enumType, name.toUpperCase());
	}
}
