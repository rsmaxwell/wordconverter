package com.rsmaxwell.diary.wordconverter.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsmaxwell.diary.wordconverter.Converter;
import com.rsmaxwell.diary.wordconverter.OutputDocument;

public class MyBody {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyBody create(Element element, int level) throws Exception {

		MyBody body = new MyBody();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:p".contentEquals(nodeName)) {
					body.elements.add(MyParagraph.create(childElement, level + 1));
				} else if ("w:tbl".contentEquals(nodeName)) {
					body.elements.add(MyTable.create(childElement, level + 1));
				} else if ("w:sectPr".contentEquals(nodeName)) {
					// ok
				} else if ("w:bookmarkEnd".contentEquals(nodeName)) {
					// ok
				} else if ("w:bookmarkStart".contentEquals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return body;
	}

	public void toOutput(OutputDocument outputDocument, Converter converter) throws Exception {

		for (MyElement element : elements) {

			if (element instanceof MyParagraph) {
				MyParagraph myParagraph = (MyParagraph) element;
				myParagraph.toOutput(outputDocument, converter);

			} else {
				throw new Exception("Unexpected element type: " + element.getClass().getSimpleName());
			}
		}
	}
}
