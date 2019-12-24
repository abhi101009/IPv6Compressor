package com.inn.foresight.ip.compressor;

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
 * @author Abhishek Kumar Mishra Skype: abhishek.innoeye Mob. No. +91 9584842218
 *
 */
public class IPCompressor {

	static final Logger logger = Logger.getLogger(IPCompressor.class);

	public static List<String[]> readAllDataAtOnce(String file) {
		List<String[]> resultIP = new ArrayList<>();
		try {
			FileReader filereader = new FileReader(file);

			CSVReader csvReader = new CSVReaderBuilder(filereader).build();
			List<String[]> allData = csvReader.readAll();

			for (String[] row : allData) {
				IPAddress address = new IPAddressString(row[NumberUtils.INTEGER_ZERO]).getAddress();
				if (address != null) {
					resultIP.add(new String[] { row[NumberUtils.INTEGER_ZERO], address.toCanonicalString() });
				} else {
					resultIP.add(new String[] { row[NumberUtils.INTEGER_ZERO], "-" });
				}
			}
			logger.info("CSV Data read successfully from input file");
		} catch (Exception e) {
			logger.error("Exception occured while reading input csv file. Error: {}", e);
		}
		return resultIP;
	}

	public static void addDataToCSV(String[] runTimeargu, List<String[]> data) {
		String outputFilePath = runTimeargu[NumberUtils.INTEGER_ZERO];
		if (runTimeargu.length < 2) {
			logger.warn("Output file is not provided want to write output into input file. Press Y else N");
			Scanner sc = new Scanner(System.in);
			String isAccepted = sc.next();
			sc.close();
			if (isAccepted != "Y") {
				logger.info("Showing all records in CLI....");
				for (String[] outputIP : data) {
					String[] ipArr = outputIP;
					logger.info(ipArr[NumberUtils.INTEGER_ZERO] + " -> " + ipArr[NumberUtils.INTEGER_ONE]);
				}
				return;
			}
		}

		logger.info("Writing data into output File: " + outputFilePath);

		File file = new File(outputFilePath);
		try (FileWriter outputfile = new FileWriter(file);
				CSVWriter writer = new CSVWriter(outputfile, ',', CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
			writer.writeAll(data);
		} catch (IOException e) {
			logger.error("Exception occured while writing output into csv file. Error: {}", e);
		}
	}

	public static void main(String[] args) {
		logger.info("Start Processing CSV");

		if (args.length > NumberUtils.INTEGER_ZERO) {
			logger.info("Reading Input File: " + args[NumberUtils.INTEGER_ZERO]);
			addDataToCSV(args, readAllDataAtOnce(args[NumberUtils.INTEGER_ZERO]));
		}
	}

}
