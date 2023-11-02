package com.finzly.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.core.env.Environment;
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
	private Environment env1;

	@Autowired
	OpenCSVApplication openCSVApplication;

	public boolean isCsvfile(MultipartFile file) {
		String type = "text/csv";
		if (!type.equals(file.getContentType()))
			return false;
		return true;
	}

	private String path;

	public ResponseEntity<String> uploadAndSaveCsv(MultipartFile file) {
		try {
			String script = csvParsing(file.getInputStream());
			path = env1.getProperty("csv.file.path", "Not available");
			return writeScriptToFile(script);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File Not Appended");
	}

	private String csvParsing(InputStream inputStream) {
		try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, "UTF-8")).withSkipLines(0)
				.build()) {

			StringBuilder script = new StringBuilder();

			String[] headerNames = reader.readNext();
			String col1 = headerNames[0];
			String col2 = headerNames[1];

			String env = OpenCSVApplication.env;

			List<String[]> record = reader.readAll();
			for (String[] oneRecord : record) {
				String value1 = oneRecord[0];
				String value2 = oneRecord[1];
				String query = "Insert into " + env + ".properties(" + col1 + "," + col2 + ") values('" + value1 + "','"
						+ value2 + "')";
				script.append(query + "\n");
			}
			return script.toString();

		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ResponseEntity<String> writeScriptToFile(String script) {
		try (FileWriter writer = new FileWriter(path)) {
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
