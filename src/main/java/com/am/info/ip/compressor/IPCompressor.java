package com.am.info.ip.compressor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

/**
 * @author Abhishek Kumar Mishra
 *
 */
public class IPCompressor {

	static final Logger logger = Logger.getLogger(IPCompressor.class);
	static final String INVALID_IP_REPRESENTER = "-";
	static final char CSV_DELIMITER_CHAR = ',';

	public static void main(String[] args) {
		logger.info("Start Processing.. \n");

		if (args.length > NumberUtils.INTEGER_ZERO) {
			logger.info("Reading Input File: " + args[NumberUtils.INTEGER_ZERO] + "\n");
			generateOutput(args, processInputData(args[NumberUtils.INTEGER_ZERO]));
		} else {
			logger.info("Terminating Program: input data file not provided.");
		}
	}

	public static List<String[]> processInputData(String inputFile) {
		List<String[]> inputIP = new ArrayList<String[]>();
		FileReader fileReader;
		try {
			fileReader = new FileReader(inputFile);
			CSVReader csvReader = new CSVReaderBuilder(fileReader).build();
			inputIP = csvReader.readAll();
		} catch (IOException e) {
			logger.error("Exception occured while reading input csv file. Error: {}", e);
			e.printStackTrace();
		}

		logger.info("CSV Data read successfully from input file \n");
		return compressIP(inputIP);
	}

	private static List<String[]> compressIP(List<String[]> inputIP) {
		List<String[]> compressedIP = new ArrayList<>();

		for (String[] row : inputIP) {
			IPAddress ipAddress = new IPAddressString(row[NumberUtils.INTEGER_ZERO]).getAddress();
			if (ipAddress != null) {
				compressedIP.add(new String[] { row[NumberUtils.INTEGER_ZERO], ipAddress.toCanonicalString() });
			} else {
				compressedIP.add(new String[] { row[NumberUtils.INTEGER_ZERO], INVALID_IP_REPRESENTER });
			}
		}
		logger.info("CSV input data compressed successfully from input file \n");
		return compressedIP;
	}

	public static void generateOutput(String[] runTimeArgu, List<String[]> compressedIPRow) {
		if (runTimeArgu.length < 2) {
			logger.warn(
					"Output file is not provided, Do you want to write output into given input file. Press Y else N");
			Scanner sc = new Scanner(System.in);
			String isAccepted = sc.next();
			sc.close();
			if (!isAccepted.equals("Y")) {
				logger.info("\n****** Showing all records in CLI *********\n");
				for (String[] outputIP : compressedIPRow) {
					String[] outputIPColumn = outputIP;
					logger.info(outputIPColumn[NumberUtils.INTEGER_ZERO] + " -> "
							+ outputIPColumn[NumberUtils.INTEGER_ONE]);
				}
				return;
			}
		}

		String outputFilePath = runTimeArgu[runTimeArgu.length - 1];
		logger.info("Writing data into output File: " + outputFilePath + "\n");

		File file = new File(outputFilePath);
		try (FileWriter outputfile = new FileWriter(file);
				CSVWriter writer = new CSVWriter(outputfile, CSV_DELIMITER_CHAR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
			writer.writeAll(compressedIPRow);
		} catch (IOException e) {
			logger.error("Exception occured while writing output into csv file. Error: {}", e);
		}
	}

}
