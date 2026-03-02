package com.learnia.publisher.api.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.learnia.validation.ValidPdfFiles;

import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("v1/receiver")
public class ReceiverController {
    @PutMapping(value = "/{uuidRequest}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> receive(
            @PathVariable(value = "uuidRequest", required = true) UUID uuidRequest,
            @ValidPdfFiles(maxSizeMb = 100) @RequestPart("files") List<FilePart> files) {
        return Mono.just("Received request with UUID: " + uuidRequest + " and files: " + files.size());
    }
}
