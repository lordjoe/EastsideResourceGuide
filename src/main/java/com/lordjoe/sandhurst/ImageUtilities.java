package com.lordjoe.sandhurst;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.lordjoe.resource_guide.util.DatabaseConnection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.UUID;

public class ImageUtilities {


    public static ImageAsset uploadImage(byte[] fileBytes, int sourceId, ImageAssetType sourceType) throws Exception {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(fileBytes));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizeImage(original), "jpg", os);
        byte[] jpegData = os.toByteArray();

        String fileName = UUID.randomUUID() + ".jpg";
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create("images/" + fileName, jpegData, "image/jpeg");

        String imageUrl = String.format("https://storage.googleapis.com/%s/%s", bucket.getName(), blob.getName());

        // Save metadata in your database
        ImageAsset asset = new ImageAsset( sourceId, sourceType, imageUrl);
        try(Connection conn = DatabaseConnection.getConnection()) {
            new ImageAssetDAO(conn).insert(asset);
        }

        return asset;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage) {
        int maxWidth = 1024;
        int width = Math.min(originalImage.getWidth(), maxWidth);
        int height = (originalImage.getHeight() * width) / originalImage.getWidth();
        Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        resized.getGraphics().drawImage(tmp, 0, 0, null);
        return resized;
    }
}
