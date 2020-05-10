package com.rsmaxwell.diary.wordconverter.parser;

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
	public String getPicture() {
		return descr;
	}
}
