package me.academeg.blog.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AccountPermissionException
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccountPermissionException extends AccessDeniedException {

    public AccountPermissionException() {
        this("Access denied. You have no rights.");
    }

    public AccountPermissionException(String message) {
        super(message);
    }

    public AccountPermissionException(String message, Object... args) {
        super(String.format(message, args));
    }
}