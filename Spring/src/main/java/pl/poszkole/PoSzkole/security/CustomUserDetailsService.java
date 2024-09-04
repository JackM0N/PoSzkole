package pl.poszkole.PoSzkole.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.model.Role;
import pl.poszkole.PoSzkole.model.Users;
import pl.poszkole.PoSzkole.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles(userObj.getRoles().stream().map(Role::getRoleName).toArray(String[]::new))
                    .build();
        }else {
            throw new UsernameNotFoundException(username);
        }
    }
}

