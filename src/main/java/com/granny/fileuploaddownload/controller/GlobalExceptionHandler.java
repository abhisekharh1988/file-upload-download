package com.granny.fileuploaddownload.controller;

import com.granny.fileuploaddownload.exception.FileNotFoundException;
import com.granny.fileuploaddownload.exception.FileStorageException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MultipartException.class, FileStorageException.class})
    public String handleError1(Exception e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", e.getMessage());
        return "redirect:/uploadImage";

    }

    @ExceptionHandler({FileNotFoundException.class})
    public String handleError2(Exception e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("message", e.getMessage());
        return "redirect:/downloadImage";

    }
}
