package com.granny.fileuploaddownload.controller;

import com.granny.fileuploaddownload.service.UploadDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Controller
@Slf4j
public class UploadDownloadController {

    @Autowired
    private UploadDownloadService uploadDownloadService;

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("filename") String fileName,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadImage";
        }
        try {
            String imageUrl = uploadDownloadService.storeFile(file, fileName);
            log.info("File Upload is successful. File name: " +fileName);
            redirectAttributes.addFlashAttribute("message", "File is uploaded successfully. Uri : " +imageUrl);
        } catch (IOException e) {
            log.error("IO exception has occurred while storing the file.", e);
            redirectAttributes.addFlashAttribute("message",
                    "File upload failed'" + fileName + "'");
        }

        return "redirect:/uploadImage";
    }

    @GetMapping("/uploadImage")
    public String upload() {
        return "upload";
    }

    @GetMapping("/downloadImage")
    public String download() {
        return "download";
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request){
            Resource resource = uploadDownloadService.downloadFile(fileName);
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.error("Could not determine file type.");

            }

            // Fallback to the default content type if type could not be determined
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            log.info("File Download is succesful. File name : " +fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFileFromWebPage(@RequestParam("filename") String fileName, HttpServletRequest request){
        Resource resource = uploadDownloadService.downloadFile(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.error("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }
}
