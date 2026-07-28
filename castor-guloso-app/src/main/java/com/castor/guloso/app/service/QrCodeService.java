package com.castor.guloso.app.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Gera QR Codes em PNG para o cardápio digital.
 */
@Service
public class QrCodeService {

    private static final int DEFAULT_SIZE = 1024;
    private static final String FORMAT = "png";

    /**
     * Gera QR Code PNG como array de bytes.
     *
     * @param content URL ou texto para codificar
     * @param size    tamanho em pixels (largura = altura)
     */
    public byte[] generate(String content, int size) throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, FORMAT, out);
        return out.toByteArray();
    }

    /**
     * Gera QR Code PNG com logo centralizada.
     *
     * @param content  URL ou texto para codificar
     * @param size     tamanho em pixels
     * @param logo     imagem do logo (pode ser null)
     */
    public byte[] generateWithLogo(String content, int size, BufferedImage logo)
            throws WriterException, IOException {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);
        if (logo == null) {
            return generate(content, size);
        }

        // Sobrepoe o logo no centro
        Graphics2D g = qrImage.createGraphics();
        int logoSize = size / 5;
        int x = (size - logoSize) / 2;
        int y = (size - logoSize) / 2;

        g.setComposite(AlphaComposite.SrcOver);
        g.drawImage(logo, x, y, logoSize, logoSize, null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(qrImage, FORMAT, out);
        return out.toByteArray();
    }

    /**
     * Gera QR Code para o cardápio, opcionalmente para uma mesa específica.
     */
    public byte[] generateMenuQr(String baseUrl, Integer tableNumber)
            throws WriterException, IOException {

        String url = baseUrl + "/cardapio";
        if (tableNumber != null) {
            url += "?mesa=" + tableNumber;
        }
        return generate(url, DEFAULT_SIZE);
    }
}
