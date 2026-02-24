package efrei.bankbackend.configuration.components;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("accountComponent")
public class AccountComponent {

    public boolean isOwner(String owner) {
        Authentication authentication = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());

        return owner.equals(authentication.getName());
    }
}