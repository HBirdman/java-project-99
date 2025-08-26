package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    public boolean isOwner(long userId) {
        String ownersEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String requestedEmail = userRepository.findById(userId).get().getEmail();
        return requestedEmail.equals(ownersEmail);
    }
}
