package me.academeg.blog.dal.utils;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.Constants;
import me.academeg.blog.api.exception.BlogClientException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * ImageUtils
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Slf4j
public final class ImageUtils {

    public static String saveImage(final String path, final MultipartFile file) {
        createFileStorage(path);
        String fileName = UUID.randomUUID().toString();
        log.info("Save image in path '{}' with name '{}'", path, fileName);
        try (
            InputStream inputStream = file.getInputStream();
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path + fileName))
        ) {
            byte[] cache = new byte[16384];
            int count;
            while ((count = inputStream.read(cache)) > 0) {
                outputStream.write(cache, 0, count);
            }
        } catch (IOException e) {
            log.error("Error during save image in path '{}'", path);
            throw new BlogClientException(e);
        }
        return fileName;
    }

    public static String compressImage(final String originalImageFileName, final String dir) {
        log.info("Compress original image '{}' in path '{}'", originalImageFileName, dir);
        String fileName = UUID.randomUUID().toString();
        try {
            BufferedImage originalImage = ImageIO.read(new File(dir + originalImageFileName));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            if (originalImage.getWidth() < Constants.MAX_THUMBNAIL_SIZE
                && originalImage.getHeight() < Constants.MAX_THUMBNAIL_SIZE) {
                return originalImageFileName;
            }

            BufferedImage resizeImagePng = resizeImage(originalImage, type);
            ImageIO.write(resizeImagePng, "png", new File(dir, fileName));
        } catch (IOException e) {
            log.error("Error during compress image '{}' in path '{}'", originalImageFileName, dir);
            throw new BlogClientException(e);
        }
        return fileName;
    }

    public static byte[] toByteArray(final File file) {
        log.info("Read image to byte array from file '{}'", file.getAbsolutePath());
        try (
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        ) {
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            log.error("Cannot read file {}", file.getAbsolutePath());
            throw new BlogClientException(e);
        }
    }

    private static BufferedImage resizeImage(final BufferedImage image, final int type) {
        final float maxSize = Math.max(image.getWidth(), image.getHeight());
        final int scale = (int) Math.ceil(maxSize / Constants.MAX_THUMBNAIL_SIZE);
        final int width = image.getWidth() / scale;
        final int height = image.getHeight() / scale;

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    public static void deleteImages(final String path, final String... fileNames) {
        log.info("Delete files on path '{}'", path);
        Arrays
            .stream(fileNames)
            .map(f -> Optional.ofNullable(path).orElse("") + f)
            .map(File::new)
            .forEach(File::delete);
    }

    private static void createFileStorage(final String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean res = file.mkdirs();
            if (res) {
                log.info("Dirs '{}' was successful created", path);
            }
        }
    }
}
