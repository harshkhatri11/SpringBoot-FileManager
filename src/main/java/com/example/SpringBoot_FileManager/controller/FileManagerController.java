package com.example.SpringBoot_FileManager.controller;

import com.example.SpringBoot_FileManager.service.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/files")
public class FileManagerController {

    private final FileStorageService fileStorageService;

    public FileManagerController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    private static final Logger log = Logger.getLogger(FileManagerController.class.getName());

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or not provided.");
        }
            try{
                fileStorageService.saveFile(file);
                return ResponseEntity.ok("File uploaded successfully.");
            }catch (IOException e){
                log.log(Level.SEVERE,"Exception during upload",e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
            }

    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String filename){
            try {
                var fileToDownload = fileStorageService.getDownloadFile(filename);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ filename +"\"")
                        .contentLength(fileToDownload.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        // Wraps an InputStream
                        //Not seekable (can’t resume or skip directly unless wrapped manually)
                        //Ideal for Remote or Cloud Storage (e.g., AWS S3) In that case, you'd use an InputStream from S3 SDK or generate a pre-signed URL.
                        .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
            } catch (Exception e) {
              return ResponseEntity.notFound().build();
            }
    }

    @GetMapping("/download-faster")
    public ResponseEntity<Resource> downloadFileFaster(@RequestParam("fileName") String filename){
        try {
            var fileToDownload = fileStorageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ filename +"\"")
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    //Wraps a File directly
                    //Seekable via random access internally
                    //Efficient for local file
                    //Preferred for static files
                    //Automatically sets metadata like length,name,etc...
                    //Resume capability
                    //Chunked download for large files
                    //Not Ideal for Remote or Cloud Storage (e.g., AWS S3)
                    .body(new FileSystemResource(fileToDownload));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
