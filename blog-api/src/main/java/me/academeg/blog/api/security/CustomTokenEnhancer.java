package me.academeg.blog.api.security;

import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomTokenEnhancer
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    private AccountService accountService;

    @Autowired
    public CustomTokenEnhancer(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        Account account = accountService.getByEmail(((User) authentication.getPrincipal()).getUsername());
        additionalInfo.put("account_id", account.getId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}