package dev.lqwd.cloudfilestorage.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;


public abstract class BaseController {

    protected <T> ResponseEntity<T> buildOkResponse(T body) {
        return ResponseEntity
                .ok()
                .body(body);
    }

    protected <T> ResponseEntity<T> buildCreatedResponse(T body, String uri) {
        return ResponseEntity
                .created(URI.create(uri))
                .body(body);
    }

    protected ResponseEntity<Void> buildNoContentResponse() {
        return ResponseEntity
                .noContent()
                .build();
    }

    protected <T> ResponseEntity<T> buildDownloadResponse(T body, String fileName){
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(body);
    }

}
