package com.castor.guloso.app.rest;

import com.castor.guloso.app.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    /**
     * Gera QR Code PNG para o cardápio.
     */
    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQr(
            @RequestParam(defaultValue = "http://localhost:7080") String baseUrl,
            @RequestParam(required = false) Integer mesa) {

        try {
            byte[] image = qrCodeService.generateMenuQr(baseUrl, mesa);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(image);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
