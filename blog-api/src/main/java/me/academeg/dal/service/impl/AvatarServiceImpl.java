package me.academeg.dal.service.impl;

import me.academeg.dal.domain.Account;
import me.academeg.dal.domain.Avatar;
import me.academeg.dal.repository.AvatarRepository;
import me.academeg.dal.service.AvatarService;
import me.academeg.dal.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static me.academeg.api.Constants.AVATAR_PATH;

/**
 * AvatarServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Service
public class AvatarServiceImpl implements AvatarService {

    private AvatarRepository avatarRepository;

    @Autowired
    public AvatarServiceImpl(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    @Override
    public Avatar create(MultipartFile file, Account account) {
        Avatar avatar = new Avatar();
        avatar.setOriginalPath(ImageUtils.saveImage(AVATAR_PATH, file));
        avatar.setThumbnailPath(ImageUtils.compressImage(avatar.getOriginalPath(), AVATAR_PATH));
        avatar.setAccount(account);

        if (account.getAvatar() != null) {
            ImageUtils.deleteImages(
                AVATAR_PATH,
                account.getAvatar().getOriginalPath(),
                account.getAvatar().getThumbnailPath()
            );
            avatarRepository.delete(account.getAvatar());
        }

        return avatarRepository.save(avatar);
    }

    @Override
    public Avatar getById(UUID id) {
        return avatarRepository.findOne(id);
    }

    @Override
    public void delete(UUID id) {
        Avatar avatar = avatarRepository.findOne(id);
        ImageUtils.deleteImages(
            AVATAR_PATH,
            avatar.getOriginalPath(),
            avatar.getThumbnailPath()
        );
        avatar.getAccount().setAvatar(null);
        avatar.setAccount(null);
        avatarRepository.delete(id);
    }
}