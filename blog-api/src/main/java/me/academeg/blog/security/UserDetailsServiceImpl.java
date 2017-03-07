package me.academeg.blog.security;

import lombok.extern.slf4j.Slf4j;
import me.academeg.blog.dal.domain.Account;
import me.academeg.blog.dal.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * UserDetailsServiceImpl Service
 *
 * @author Yuriy A. Samsonov <yuriy.samsonov96@gmail.com>
 * @version 1.0
 */
@Component
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account accountFromDb = accountRepository.getByEmailIgnoreCase(username);
        if (accountFromDb == null) {
            String msg = String.format("User %s was not found", username);
            log.warn(msg);
            throw new UsernameNotFoundException(msg);
        }

        Collection<GrantedAuthority> grantedAuthorities = accountFromDb
            .getRoles()
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return new UserDetailsImpl(
            accountFromDb.getId(),
            accountFromDb.getEmail(),
            accountFromDb.getPassword(),
            grantedAuthorities
        );
    }
}