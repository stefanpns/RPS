package com.stefan.springjwt.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.stefan.springjwt.payload.response.MessageResponse;
import com.stefan.springjwt.models.FileInfo;
import com.stefan.springjwt.uploadservice.FilesStorageService;

public class FilesController {

  @Autowired
  FilesStorageService storageService;

  public ResponseEntity<MessageResponse> uploadFile(MultipartFile file) {
    String message = "";
    try {
      storageService.save(file, "");
      message = "Uploaded the file successfully: " + file.getOriginalFilename();
      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
    } catch (Exception e) {
      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
    }
  }

  public ResponseEntity<Resource> getFile(String filename) {
    Resource file = storageService.load(filename);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }
}
