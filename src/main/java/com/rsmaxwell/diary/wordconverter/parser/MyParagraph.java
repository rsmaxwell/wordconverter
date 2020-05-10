package com.rsmaxwell.diary.wordconverter.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rsmaxwell.diary.wordconverter.Converter;
import com.rsmaxwell.diary.wordconverter.OutputDocument;
import com.rsmaxwell.diary.wordconverter.relationships.Relationships;

public class MyParagraph extends MyElement {

	private List<MyElement> elements = new ArrayList<MyElement>();

	public static MyParagraph create(Element element, int level) throws Exception {

		MyParagraph paragraph = new MyParagraph();

		NodeList nList = element.getChildNodes();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node child = nList.item(temp);
			int nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				String nodeName = child.getNodeName();
				if ("w:r".equals(nodeName)) {
					paragraph.elements.add(MyRun.create(childElement, level + 1));
				} else if ("w:pPr".equals(nodeName)) {
					// ok
				} else if ("w:proofErr".equals(nodeName)) {
					// ok
				} else if ("w:bookmarkStart".equals(nodeName)) {
					// ok
				} else if ("w:bookmarkEnd".equals(nodeName)) {
					// ok
				} else if ("w:hyperlink".equals(nodeName)) {
					paragraph.elements.add(MyHyperlink.create(childElement, level + 1));
				} else if ("w:del".equals(nodeName)) {
					// ok
				} else {
					throw new Exception("unexpected element: " + nodeName);
				}
			}
		}

		return paragraph;
	}

	public String toHtml(Converter converter) {

		Relationships relationships = converter.getRelationships();

		String picture = getPicture();

		if (picture == null) {
			StringBuilder sb = new StringBuilder();
			for (MyElement element : elements) {
				sb.append(element.toHtml());
			}
			return "<p>" + sb.toString().trim() + "</p>" + LS;
		}

		String id = getHyperlinkId();
		String link = relationships.get(id);

		File file = new File(picture);
		String name = file.getName();
		File parent = file.getParentFile();
		String parentName = parent.getName();
		String image = parentName + "/" + name;

		StringBuilder sb = new StringBuilder();
		sb.append("<figure>" + LS);

		String pad = "";
		if (link != null) {
			sb.append("  <a href=\"" + link + "\" >" + LS);
			pad = "  ";
		}
		sb.append(pad + "  <img src=\"" + image + "\" width=\"600px\" />" + LS);
		sb.append(pad + "  <figcaption>" + toString() + "</figcaption>" + LS);
		if (link != null) {
			sb.append("  </a>" + LS);
		}
		sb.append("</figure>" + LS);

		return sb.toString();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (MyElement element : elements) {
			sb.append(element.toString());
		}

		return sb.toString();
	}

	@Override
	public String getPicture() {

		for (MyElement element : elements) {
			String picture = element.getPicture();
			if (picture != null) {
				return picture;
			}
		}

		return null;
	}

	@Override
	public String getHyperlinkId() {

		for (MyElement element : elements) {
			String hyperlink = element.getHyperlinkId();
			if (hyperlink != null) {
				return hyperlink;
			}
		}

		return null;
	}

	public void toOutput(OutputDocument outputDocument, Converter converter) throws IOException, Exception {
		outputDocument.paragraphs.add(toHtml(converter));
	}
}
