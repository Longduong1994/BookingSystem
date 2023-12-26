package booking.security.user_principle;

import booking.entity.User;
import booking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            UserPrincipal userPrincipal = UserPrincipal.builder()
                    .user(user.get())
                    .authorities(user.get().getRoles().stream().map(item -> new SimpleGrantedAuthority(item.getRoleName())).collect(Collectors.toSet()))
                    .build();
            return userPrincipal;
        } else {
            throw new UsernameNotFoundException(username + " not found");
        }
    }
}
