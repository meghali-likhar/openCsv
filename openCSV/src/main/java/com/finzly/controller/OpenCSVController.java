package com.finzly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.finzly.service.OpenCSVService;

@RestController
@RequestMapping("/files")
public class OpenCSVController {
	@Autowired
	OpenCSVService service;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadAndSaveCsv(@RequestParam("file") MultipartFile file) {
		if (service.isCsvfile(file)) {
			return service.uploadAndSaveCsv(file);
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Please Upload the Csv File");
	}
	
	@PutMapping("/update")
	public ResponseEntity<String> updateCsv(@RequestParam("file") MultipartFile file) {
		if (service.isCsvfile(file)) {
			return service.updateCsv(file);
		}
		return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Please Upload the Csv File");
	}

}
