package com.rsmaxwell.diary.wordconverter.parser;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MyTableRow {

	private ArrayList<MyTableColumn> columns = new ArrayList<MyTableColumn>();

	public static MyTableRow create(Element element, int level) throws Exception {

		MyTableRow tableRow = new MyTableRow();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:tc".contentEquals(nodeName)) {
					tableRow.columns.add(MyTableColumn.create(childElement, level + 1));
				} else if ("w:tblPrEx".contentEquals(nodeName)) {
					// ok
				} else if ("w:trPr".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return tableRow;
	}

	@Override
	public String toString() {
		return "";
	}
}
