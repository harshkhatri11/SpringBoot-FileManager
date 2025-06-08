package com.example.SpringBoot_FileManager.controller;

import com.example.SpringBoot_FileManager.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileManagerGUIController {
    private final FileStorageService fileStorageService;

    public FileManagerGUIController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/uploader")
    public String uploader(){
        return "uploader";
    }

    @GetMapping("/list-files")
    public String listFiles(Model model) throws IOException {
        Path directory = fileStorageService.getStorageDirectory();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            List<String> list = new ArrayList<>();
            for (Path path : stream) {
                Path fileName = path.getFileName();
                String string = fileName.toString();
                list.add(string);
            }
            model.addAttribute("files",
                    list);
        }
        return "list_files";
    }
}
