package com.rsmaxwell.diaries.wordconverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.rsmaxwell.diaries.wordconverter.parser.MyDocument;

public class Converter {

	private File fragmentDir;
	private File fragmentHtml;
	private File fragmentJson;
	private File wordDocument;
	private File documentDir;
	private OutputDocument outputDocument;

	public Converter(String baseDirName, String fragmentDirName) throws IOException, Exception {

		this.fragmentDir = new File(baseDirName, fragmentDirName);

		if (!fragmentDir.exists()) {
			throw new Exception("directory not found: " + fragmentDir.getCanonicalPath());
		}
		fragmentHtml = new File(fragmentDir, "fragment.html");
		if (!fragmentHtml.exists()) {
			throw new Exception("fragmentHtml not found: " + fragmentHtml.getCanonicalPath());
		}
		fragmentJson = new File(fragmentDir, "fragment.json");
		if (!fragmentJson.exists()) {
			throw new Exception("fragmentJson not found: " + fragmentJson.getCanonicalPath());
		}
		wordDocument = new File(fragmentDir, "document.docx");
		if (!wordDocument.exists()) {
			throw new Exception("wordDocument not found: " + wordDocument.getCanonicalPath());
		}

		documentDir = new File(fragmentDir, "document");
	}

	public void clearDocumentDir() throws IOException {

		Path path = documentDir.toPath();

		if (Files.exists(path)) {
			Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}

		if (Files.exists(path)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		Files.createDirectory(path);
	}

	public void unzip() throws IOException {

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(wordDocument))) {

			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {

				if ("word/document.xml".contentEquals(zipEntry.getName())) {

					File file = new File(documentDir, zipEntry.getName());
					File parentFolder = new File(file.getParent());
					parentFolder.mkdirs();

					File newFile = newFile(documentDir, zipEntry);
					FileOutputStream fos = new FileOutputStream(newFile);

					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}

				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
		}
	}

	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	public void parse() throws Exception {

		File documentWordDir = new File(documentDir, "word");
		File wordXmlFile = new File(documentWordDir, "document.xml");

		if (!wordXmlFile.exists()) {
			throw new Exception("wordXmlFile not found: " + wordXmlFile.getCanonicalPath());
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(wordXmlFile);

		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();

		MyDocument document = MyDocument.create(root, 0);
		outputDocument = document.toOutput();
	}

	public void toHtml() throws Exception {

		for (String paragraph : outputDocument.paragraphs) {

			if (paragraph == null) {
				throw new Exception("null html found in fragment: " + paragraph);
			}

			Path htmlPath = new File(fragmentDir, "fragment-generated.html").toPath();
			try (BufferedWriter writer = Files.newBufferedWriter(htmlPath)) {
				writer.write(paragraph);
			}
		}
	}

	public void cleanup() {
		System.out.println("Converter.cleanup");
	}
}