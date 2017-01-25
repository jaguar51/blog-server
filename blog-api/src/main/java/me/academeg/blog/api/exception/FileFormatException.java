package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * FileFormatException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FileFormatException extends RuntimeException {

    public FileFormatException() {
        this("Wrong file format");
    }

    public FileFormatException(String message) {
        super(message);
    }
}