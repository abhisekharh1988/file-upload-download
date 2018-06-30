package com.granny.fileuploaddownload.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface UploadDownloadService {
    String storeFile(MultipartFile file, String fileName) throws IOException;
    Resource downloadFile(String fileName);
}
