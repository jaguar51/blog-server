package me.academeg.api.controller;

import me.academeg.api.common.ApiResult;
import me.academeg.api.entity.Account;
import me.academeg.api.entity.Avatar;
import me.academeg.api.exception.entity.FileFormatException;
import me.academeg.api.service.AccountService;
import me.academeg.api.service.AvatarService;
import me.academeg.api.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static me.academeg.api.utils.ApiUtils.singleResult;

/**
 * AvatarController Controller
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@Validated
public class AvatarController {

    private final static String AVATAR_PATH = "avatar/";

    private final AvatarService avatarService;
    private final AccountService accountService;

    @Autowired
    public AvatarController(AvatarService avatarService, AccountService accountService) {
        this.avatarService = avatarService;
        this.accountService = accountService;
    }

    @RequestMapping(value = "/account/avatar", method = RequestMethod.POST)
    public ApiResult create(@AuthenticationPrincipal final User user, @RequestParam final MultipartFile image) {
        if (!image.getContentType().startsWith("image/")) {
            throw new FileFormatException("You can create only images");
        }

        File avatarsDir = new File(AVATAR_PATH);
        if (!avatarsDir.exists()) {
            avatarsDir.mkdir();
        }

        Avatar avatar = new Avatar();
        String originalImageName = ImageUtils.saveImage(avatarsDir, image);
        avatar.setOriginalPath(AVATAR_PATH + originalImageName);
        String thumbnailImageName = ImageUtils.compressImage(new File(avatarsDir, originalImageName), avatarsDir);
        avatar.setThumbnailPath(AVATAR_PATH + thumbnailImageName);

        Account account = accountService.getByEmail(user.getUsername());
        deleteAvatarFromStorage(account.getAvatar());
        return singleResult(avatarService.set(avatar, account));
    }

    @RequestMapping(value = "/account/avatar", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal final User user) {
        Account account = accountService.getByEmail(user.getUsername());
        if (account.getAvatar() != null) {
            deleteAvatarFromStorage(account.getAvatar());
            avatarService.delete(account.getAvatar());
        }
    }

    @RequestMapping(value = "/avatar/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        return ImageUtils.toByteArray(new File(AVATAR_PATH + name));
    }

    private void deleteAvatarFromStorage(Avatar avatar) {
        if (avatar == null) {
            return;
        }
        new File(avatar.getOriginalPath()).delete();
        new File(avatar.getThumbnailPath()).delete();
    }
}