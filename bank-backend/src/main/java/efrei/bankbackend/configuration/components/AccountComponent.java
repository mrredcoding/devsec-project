package efrei.bankbackend.configuration.components;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("accountComponent")
public class AccountComponent {

    public boolean isOwner(String owner, Authentication authentication) {
        return owner.equals(authentication.getName());
    }
}