package konkuk.ptal.domain;

import konkuk.ptal.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long userId;  // userId 필드 추가
    private String email;  // memberId → email
    private String password;
    private List<String> roles;  // Set<String> → List<String> (roles는 실제 String 값으로 변환)

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),  // userId 추가
                user.getEmail(),  // 이메일로 변경
                user.getPasswordHash(),  // 비밀번호는 passwordHash로 변경
                List.of(user.getRole().name())  // Role enum을 String으로 변환하여 리스트에 담기
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;  // username은 email로 변경
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}