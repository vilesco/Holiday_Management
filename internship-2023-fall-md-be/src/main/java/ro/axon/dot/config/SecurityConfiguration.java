package ro.axon.dot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public JwtKeyProperties jwtKeyProperties() {
        return new JwtKeyProperties();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var roles = jwt.getClaim("roles");
            return roles == null ? null : new HashSet<>(mapRolesToGrantedAuthorities((Collection<String>) roles));
        });
        return jwtConverter;
    }

    private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
        return roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(
                        auth -> auth
                                .antMatchers(HttpMethod.POST, "/api/v1/login", "/api/v1/refresh", "/api/v1/logout")
                                .permitAll()
                                .antMatchers(HttpMethod.GET, "/api/v1/requests").hasAnyRole("HR", "TEAM_LEAD")
                                .antMatchers(HttpMethod.PATCH, "/api/v1/employees/{employeeId}/requests/{requestId}")
                                .hasAnyRole("HR", "TEAM_LEAD")
                                .antMatchers(HttpMethod.PATCH, "/api/v1/employees/{employeeId}",
                                        "/api/v1/employees/{employeeId}/inactivate").hasRole("HR")
                                .antMatchers(HttpMethod.POST, "/api/v1/employees", "/api/v1/teams").hasRole("HR")
                                .antMatchers(HttpMethod.PUT, "/api/v1/employees/days-off").hasRole("HR")
                                .anyRequest().authenticated()
                )
                .cors()
                .and()
                .csrf()
                .disable()
                .httpBasic().disable()
                .oauth2ResourceServer(
                        oauth -> oauth.jwt(
                                token -> token.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                );
    }

}