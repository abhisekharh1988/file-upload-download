package com.granny.fileuploaddownload.service;

import com.granny.fileuploaddownload.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UploadDownloadServiceImpl implements UploadDownloadService {
    @Value("${upload.folder}")
    private String UPLOAD_FOLDER_PATH;
    @Value("${supported.image.extensions}")
    private String SUPPORTED_IMAGE_EXTENSIONS;

    @Override
    public String storeFile(MultipartFile file, String fileName) throws IOException {
        String fileDownloadUri = null;
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if(fileName.contains("..")){
            throw new FileStorageException("Sorry, file name contains invalid path sequence " + fileName);
        }
        String[] arr = originalFileName.split("\\.");
        List<String> supportedExtList = Arrays.asList(SUPPORTED_IMAGE_EXTENSIONS.split(","));

        if(!supportedExtList.contains(arr[1].toUpperCase())){
            throw new FileStorageException("Sorry, file extension is invalid. " + fileName);
        }
        try {
            byte[] bytes = file.getBytes();
            fileName = fileName + "." +arr[1];
            Path path = Paths.get(UPLOAD_FOLDER_PATH + fileName);
            Files.write(path, bytes);

            fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("download/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException e) {
            log.error("IO Exception has occurred while storing the image. File name:  " + fileName, e);
            throw e;
        }
        return fileDownloadUri;
    }

    @Override
    public Resource downloadFile(String fileName){
        Path fileStoragePath = Paths.get(UPLOAD_FOLDER_PATH).toAbsolutePath().resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(fileStoragePath.toUri());
            if(resource.exists())
                return resource;
            else {
                log.error("File is not found. File Name : " +fileName);
                throw new com.granny.fileuploaddownload.exception.FileNotFoundException("Image file is not found in storage");

            }
        } catch (MalformedURLException e) {
            log.error("MalformedURLException has occurred for download. File Name: " + fileName + " Cause: " +e.getCause());
            throw new com.granny.fileuploaddownload.exception.FileNotFoundException("Image file is not found in storage");
        }
    }
}
