package com.rsmaxwell.diaries.wordconverter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

	private static CommandLine getCommandLine(String[] args) throws ParseException {

		// @formatter:off
		Option version = Option.builder("v")
				            .longOpt("version")
				            .argName("version")
				            .desc("show program version")
				            .build();
		
		Option help = Option.builder("h")
				            .longOpt("help")
				            .argName("help")
				            .desc("show program help")
				            .build();

		Option baseOption = Option.builder("b")
	                        .longOpt("base")
	                        .argName("base directory")
	                        .hasArg()
	                        .required()
	                        .desc("base directory")
	                         .build();
		
		Option fragmentOption = Option.builder("f")
				            .longOpt("fragment")
				            .argName("fragment directory")
				            .hasArg()
				            .required()
				            .desc("fragment directory")
				            .build();
		// @formatter:on

		Options options = new Options();
		options.addOption(version);
		options.addOption(help);
		options.addOption(baseOption);
		options.addOption(fragmentOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine line = parser.parse(options, args);

		if (line.hasOption("version")) {
			System.out.println("version:   " + Version.version());
			System.out.println("buildID:   " + Version.buildID());
			System.out.println("buildDate: " + Version.buildDate());
			System.out.println("gitCommit: " + Version.gitCommit());
			System.out.println("gitBranch: " + Version.gitBranch());
			System.out.println("gitURL:    " + Version.gitURL());

		} else if (line.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("wordConverter <OPTION> ", options);
		}

		return line;
	}

	public static void main(String[] args) throws Exception {

		CommandLine line = getCommandLine(args);

		String baseDirName = line.getOptionValue("b");
		String fragmentDirName = line.getOptionValue("f");

		Converter converter = new Converter(baseDirName, fragmentDirName);
		converter.clearDocumentDir();
		converter.unzip();
		converter.parse();
		converter.toHtml();
		converter.cleanup();
	}
}
