package me.academeg.blog.dal.service;

import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.domain.Avatar;
import me.academeg.blog.dal.repository.AvatarRepository;
import me.academeg.blog.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Service
@Transactional
public class AvatarService {

    private final AvatarRepository avatarRepository;

    @Value("${me.academeg.blog.avatars.path:avatar/}")
    private String avatarPath;

    @Autowired
    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public Avatar create(MultipartFile file, Account account) {
        Avatar avatar = new Avatar();
        avatar.setOriginalPath(ImageUtils.saveImage(avatarPath, file));
        avatar.setThumbnailPath(ImageUtils.compressImage(avatar.getOriginalPath(), avatarPath));
        Avatar oldAvatar = account.getAvatar();
        if (oldAvatar != null) {
            ImageUtils.deleteImages(
                avatarPath,
                oldAvatar.getOriginalPath(),
                oldAvatar.getThumbnailPath()
            );
            account.setAvatar(null);
            avatarRepository.delete(oldAvatar);
        }
        avatar.setAccount(new Account(account.getId()));
        return avatarRepository.saveAndFlush(avatar);
    }

    @Transactional(readOnly = true)
    public Avatar getById(UUID id) {
        return avatarRepository.findOne(id);
    }

    public void delete(UUID id) {
        Avatar avatar = getById(id);
        ImageUtils.deleteImages(
            avatarPath,
            avatar.getOriginalPath(),
            avatar.getThumbnailPath()
        );
        avatar.setAccount(null);
        avatarRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public byte[] getFile(String name) {
        return ImageUtils.toByteArray(new File(avatarPath + name));
    }
}
