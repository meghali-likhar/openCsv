package com.finzly.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.finzly.OpenCSVApplication;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

@Service
public class OpenCSVService {

	@Autowired
	OpenCSVApplication openCSVApplication;

	public boolean isCsvfile(MultipartFile file) {
		String type = "text/csv";
		if (!type.equals(file.getContentType()))
			return false;
		return true;
	}

	public ResponseEntity<String> uploadAndSaveCsv(MultipartFile file) {
		try {
			String script = csvParsing(file.getInputStream());
			return writeScriptToFile(script);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File Not Appended");
	}

	private String csvParsing(InputStream inputStream) {
		try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, "UTF-8")).withSkipLines(0)
				.build()) {

			StringBuilder insertscript = new StringBuilder();

			String[] headerNames = reader.readNext();
			String col1 = headerNames[0];
			String col2 = headerNames[1];

			String env = OpenCSVApplication.env;
			String appName = OpenCSVApplication.appName;

			List<String[]> record = reader.readAll();
			for (String[] oneRecord : record) {
				String value1 = oneRecord[0];
				String value2 = oneRecord[1];
				String insertquery = "Insert into " + env + ".properties(" + col1 + "," + col2 + ") values('" + appName
						+ "','" + value1 + "','" + value2 + "')";
				insertscript.append(insertquery + "\n");
			}
			return insertscript.toString();

		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ResponseEntity<String> writeScriptToFile(String script) {
		try (FileWriter writer = new FileWriter("src/main/resources/insertData1.sql")) {
			writer.write(script);
			return ResponseEntity.status(HttpStatus.OK).body("Script appended successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Script not appended");
	}

//----------------------------------------------------------------------------------------------------------------------------------

	public ResponseEntity<String> updateCsv(MultipartFile file) {
		try {
			String updatescript = csvParsingUpdate(file.getInputStream());
			return writeUpdateScriptToFile(updatescript);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File Not Appended");
	}

	private String csvParsingUpdate(InputStream inputStream) {
		try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, "UTF-8")).withSkipLines(0)
				.build()) {

			StringBuilder updatescript = new StringBuilder();

			String[] headerNames = reader.readNext();
			String col1 = headerNames[0];
			String col2 = headerNames[1];

			String env = OpenCSVApplication.env;
			// String appName = OpenCSVApplication.appName;

			List<String[]> record = reader.readAll();
			for (String[] oneRecord : record) {
				String value1 = oneRecord[0];
				String value2 = oneRecord[1];
				String updatequery = "UPDATE " + env + ".properties SET " + col2 + " = '" + value2 + "' WHERE " + col1
						+ " = '" + value1 + "'";
				updatescript.append(updatequery + "\n");
			}
			return updatescript.toString();
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
		return "Script not generated";
	}

	private ResponseEntity<String> writeUpdateScriptToFile(String script) {
		try (FileWriter writer = new FileWriter("src/main/resources/updateData.sql")) {
			writer.write(script);
			return ResponseEntity.status(HttpStatus.OK).body("Script appended successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Script not appended");
	}

//	public static String prepare(String[] args) {
//
//		String env = args[0];
//		return env;
//	}

}
