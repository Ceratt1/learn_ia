package com.learnia.publisher.api.controllers;

import java.util.UUID;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.learnia.publisher.api.dtos.request.ReceiverRequest;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("v1/receiver")
public class ReceiverController {

    @PutMapping(value = "/{uuidRequest}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> receive(
            @PathVariable(value = "uuidRequest", required = true) UUID uuidRequest,
            @Valid @RequestPart("request") ReceiverRequest request,
            @RequestPart("file") FilePart file) {
        validatePdf(file);
        return validateFileSize(file)
                .thenReturn("Received request with uuid: " + uuidRequest + " and description: " + request.description());
    }

    private void validatePdf(FilePart file) {
        String filename = file.filename();
        String contentType = file.headers().getFirst("Content-Type");
        boolean isPdfName = StringUtils.hasText(filename) && filename.toLowerCase().endsWith(".pdf");
        boolean isPdfContentType = MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);

        if (!isPdfName || !isPdfContentType) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only .pdf files are accepted");
        }
    }

    private Mono<Void> validateFileSize(FilePart file) {
        return file.content()
                .map(buffer -> {
                    int size = buffer.readableByteCount();
                    DataBufferUtils.release(buffer);
                    return (long) size;
                })
                .reduce(0L, Long::sum)
                .flatMap(totalBytes -> {
                    if (totalBytes > ReceiverRequest.MAX_PDF_SIZE_BYTES) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "PDF must have at most 100 MB"));
                    }
                    return Mono.empty();
                });
    }
}
