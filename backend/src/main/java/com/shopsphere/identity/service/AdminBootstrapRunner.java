package com.shopsphere.identity.service;
import com.shopsphere.identity.domain.Role;
import com.shopsphere.identity.domain.UserAccount;
import com.shopsphere.identity.repository.RoleRepository;
import com.shopsphere.identity.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class AdminBootstrapRunner implements ApplicationRunner {
 private static final Logger log=LoggerFactory.getLogger(AdminBootstrapRunner.class);
 private final UserAccountRepository users; private final RoleRepository roles; private final PasswordEncoder passwords; private final String email; private final String password; private final String displayName;
 public AdminBootstrapRunner(UserAccountRepository users,RoleRepository roles,PasswordEncoder passwords,@Value("${app.bootstrap.admin.email:}") String email,@Value("${app.bootstrap.admin.password:}") String password,@Value("${app.bootstrap.admin.display-name:ShopSphere Administrator}") String displayName){this.users=users;this.roles=roles;this.passwords=passwords;this.email=email;this.password=password;this.displayName=displayName;}
 @Override @Transactional public void run(ApplicationArguments args){if(email.isBlank()||password.isBlank())return;if(password.getBytes(StandardCharsets.UTF_8).length<12||password.getBytes(StandardCharsets.UTF_8).length>72)throw new IllegalStateException("Bootstrap admin password must be between 12 and 72 UTF-8 bytes");Role adminRole=roles.findByCode("ADMIN").orElseThrow(()->new IllegalStateException("ADMIN role is missing"));String normalized=email.trim().toLowerCase(java.util.Locale.ROOT);UserAccount user=users.findByEmailIgnoreCase(normalized).orElseGet(()->users.save(new UserAccount(normalized,passwords.encode(password),displayName,adminRole)));user.assignRole(adminRole);users.save(user);log.info("ShopSphere admin bootstrap account is ready");}
}
