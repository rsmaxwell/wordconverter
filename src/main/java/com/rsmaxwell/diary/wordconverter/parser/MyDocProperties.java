package com.rsmaxwell.diary.wordconverter.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class MyDocProperties extends MyElement {

	private String id;
	private String name;
	private String descr;

	public static MyDocProperties create(Element element, int level) throws Exception {

		MyDocProperties properties = new MyDocProperties();

		properties.id = element.getAttribute("id");
		properties.name = element.getAttribute("name");
		properties.descr = element.getAttribute("descr");

		return properties;
	}

	@Override
	public List<String> getPictures() {

		List<String> allPictures = new ArrayList<String>();
		allPictures.add(descr);

		return allPictures;
	}
}
