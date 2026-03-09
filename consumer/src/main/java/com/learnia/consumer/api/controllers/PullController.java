package com.learnia.consumer.api.controllers;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("v1/pull")
public class PullController {

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> pull(@RequestPart("file") FilePart file) {
        return convertPdf(file);
    }

    private Mono<String> convertPdf(FilePart file) {
        return DataBufferUtils.join(file.content())
        .map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            return bytes;
        })
        .map(bytes -> {
            try (PDDocument doc = Loader.loadPDF(bytes)) {
                return new PDFTextStripper().getText(doc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping()
    public Mono<String> pull() {
        return Mono.just("aaaa");
    }
}
