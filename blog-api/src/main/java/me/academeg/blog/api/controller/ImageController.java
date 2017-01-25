package me.academeg.blog.api.controller;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.api.Constants;
import me.academeg.blog.api.common.ApiResult;
import me.academeg.blog.api.exception.EntityNotExistException;
import me.academeg.blog.api.exception.FileFormatException;
import me.academeg.blog.dal.service.ImageService;
import me.academeg.blog.dal.domain.Image;
import me.academeg.blog.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static me.academeg.blog.api.utils.ApiUtils.okResult;
import static me.academeg.blog.api.utils.ApiUtils.singleResult;

/**
 * ImageController Controller
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/images")
@Validated
@Slf4j
public class ImageController {
    private final ImageService imageService;
    private final Class resourceClass;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
        this.resourceClass = Image.class;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ApiResult create(@RequestParam(name = "image") final MultipartFile image) {
        log.info("/CREATE method invoked for {}", resourceClass.getSimpleName());
        if (!image.getContentType().startsWith("image/")) {
            throw new FileFormatException("You can upload only images");
        }

        return singleResult(imageService.create(image));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ApiResult getById(@PathVariable final UUID id) {
        log.info("/GET method invoked for {} id {}", resourceClass.getSimpleName(), id);
        return singleResult(
            Optional
                .ofNullable(imageService.getById(id))
                .<EntityNotExistException>orElseThrow(
                    () -> new EntityNotExistException("Image with id %s not exist", id))
        );
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResult delete(
        @AuthenticationPrincipal final User user,
        @PathVariable final UUID id
    ) {
        log.info("/DELETE method invoked for {} id {}", resourceClass.getSimpleName(), id);
        imageService.delete(id);
        return okResult();
    }

    @RequestMapping(value = "/file/{name}", method = RequestMethod.GET, produces = "image/jpg")
    public byte[] getByName(@PathVariable final String name) {
        log.info("/FILE method invoked for {} name {}", resourceClass.getSimpleName(), name);
        return ImageUtils.toByteArray(new File(Constants.IMAGE_PATH + name));
    }
}