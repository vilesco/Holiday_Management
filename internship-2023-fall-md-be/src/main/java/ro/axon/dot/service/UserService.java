package ro.axon.dot.service;

import static ro.axon.dot.exception.BusinessErrorCode.EMPLOYEE_NOT_FOUND;
import static ro.axon.dot.exception.BusinessErrorCode.INVALID_EMPLOYEE_CREDENTIALS_EXCEPTION;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.axon.dot.config.JwtProperties;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.repositories.EmployeeRepository;
import ro.axon.dot.domain.entities.RefreshTokenEty;
import ro.axon.dot.domain.repositories.RefreshTokenRepository;
import ro.axon.dot.domain.enums.RefreshTokenStatus;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;
import ro.axon.dot.model.request.LoginRequest;
import ro.axon.dot.model.request.RefreshTokenRequest;
import ro.axon.dot.model.response.TokenResponse;
import ro.axon.dot.model.response.UserDetailsResponse;
import ro.axon.dot.model.response.UserDetailsResponse.TeamDetails;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SecurityAccessTokenHandler tokenHandler;
    private final TokenGeneratorHandler tokenGeneratorHandler;
    private final UuidGeneratorHandler uuidGeneratorHandler;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmployeeHandler employeeHandler;
    private final EmployeeRepository employeeRepository;
    private final Clock clock;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsResponse getLoggedUserDetails() {
        EmployeeEty employee = employeeHandler.getEmployee(tokenHandler.getEmployeeIdFromToken());

        UserDetailsResponse userDetails = new UserDetailsResponse();
        userDetails.getRoles().add(employee.getRole());
        userDetails.setUsername(employee.getUsername());
        userDetails.setEmployeeId(employee.getId());

        TeamDetails teamDetails = new TeamDetails();
        teamDetails.setName(employee.getTeam().getName());
        teamDetails.setTeamId(employee.getTeam().getId());

        userDetails.setTeamDetails(teamDetails);

        return userDetails;
    }

    @Transactional
    public TokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            SignedJWT currentRefreshToken = SignedJWT.parse(refreshTokenRequest.getRefreshToken());

            String refreshTokenAudience = currentRefreshToken.getJWTClaimsSet().getSubject();
            String keyId = currentRefreshToken.getHeader().getKeyID();

            RefreshTokenEty refreshTokenEty = refreshTokenRepository.findById(keyId)
                .orElseThrow(() -> BusinessException.builder()
                    .error(BusinessErrorCode.REFRESH_TOKEN_NOT_FOUND)
                    .build());

            EmployeeEty employee = employeeHandler.getEmployee(refreshTokenAudience);

            validateRefreshToken(refreshTokenEty, employee.getId());

            SignedJWT newAccessToken = tokenGeneratorHandler.generateToken(employee,
                jwtProperties.getAccessDuration(), jwtProperties.getAccessType(), jwtProperties.getKeyId());

            SignedJWT newRefreshToken = tokenGeneratorHandler.generateToken(employee,
                jwtProperties.getRefreshDuration(), jwtProperties.getRefreshType(), refreshTokenEty.getId());

            refreshTokenEty.setMdfTms(clock.instant());
            refreshTokenEty.setExpTms(newRefreshToken.getJWTClaimsSet()
                .getExpirationTime().toInstant());

            refreshTokenRepository.save(refreshTokenEty);

            return createTokenResponse(newAccessToken, newRefreshToken);
        } catch (ParseException e) {
            throw BusinessException.builder().error(BusinessErrorCode.TOKEN_PARSE_EXCEPTION).build();
        }
    }

    @Transactional
    public void logoutUser(RefreshTokenRequest refreshTokenRequest) {
        try {
            SignedJWT currentRefreshToken = SignedJWT.parse(refreshTokenRequest.getRefreshToken());

            String refreshTokenAudience = currentRefreshToken.getJWTClaimsSet().getSubject();
            String keyId = currentRefreshToken.getHeader().getKeyID();

            RefreshTokenEty refreshTokenEty = refreshTokenRepository.findById(keyId)
                .orElseThrow(() -> BusinessException.builder()
                    .error(BusinessErrorCode.REFRESH_TOKEN_NOT_FOUND)
                    .build());

            validateRefreshToken(refreshTokenEty, refreshTokenAudience);

            refreshTokenEty.setStatus(RefreshTokenStatus.REVOKED);
            refreshTokenEty.setMdfTms(clock.instant());

            refreshTokenRepository.save(refreshTokenEty);
        } catch (ParseException e) {
            throw BusinessException.builder().error(BusinessErrorCode.TOKEN_PARSE_EXCEPTION).build();
        }
    }

    private void validateRefreshToken(RefreshTokenEty refreshTokenEty, String audienceId) {
        if (!refreshTokenEty.getAudience().getId().equals(audienceId)) {
            throw BusinessException.builder()
                .error(BusinessErrorCode.REFRESH_TOKEN_INVALID_AUDIENCE)
                .build();
        }

        if (refreshTokenEty.getStatus() == RefreshTokenStatus.REVOKED) {
            throw BusinessException.builder()
                .error(BusinessErrorCode.REFRESH_TOKEN_NOT_ACTIVE)
                .build();
        }

        if (refreshTokenEty.getExpTms().isBefore(clock.instant())) {
            throw BusinessException.builder()
                .error(BusinessErrorCode.REFRESH_TOKEN_EXPIRED)
                .build();
        }
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        EmployeeEty foundEmployee = employeeRepository.findByUsername(username).orElseThrow(() -> BusinessException.builder().error(EMPLOYEE_NOT_FOUND).build());
        if (!passwordEncoder.matches(password, foundEmployee.getPassword()))
            throw BusinessException.builder().error(INVALID_EMPLOYEE_CREDENTIALS_EXCEPTION).build();
        try {
            String refreshTokenId = uuidGeneratorHandler.generateUuid();

            SignedJWT newAccessToken = tokenGeneratorHandler.generateToken(foundEmployee,
                    jwtProperties.getAccessDuration(), jwtProperties.getAccessType(), jwtProperties.getKeyId());
            SignedJWT newRefreshToken = tokenGeneratorHandler.generateToken(foundEmployee,
                    jwtProperties.getRefreshDuration(), jwtProperties.getRefreshType(), refreshTokenId);

            RefreshTokenEty refreshTokenEty = new RefreshTokenEty();
            refreshTokenEty.setId(refreshTokenId);
            refreshTokenEty.setStatus(RefreshTokenStatus.ACTIVE);
            refreshTokenEty.setAudience(foundEmployee);
            refreshTokenEty.setMdfTms(clock.instant());
            refreshTokenEty.setExpTms(newRefreshToken.getJWTClaimsSet()
                    .getExpirationTime().toInstant());
            refreshTokenEty.setCrtTms(clock.instant());

            refreshTokenRepository.save(refreshTokenEty);
            return createTokenResponse(newAccessToken, newRefreshToken);
        } catch (ParseException ex) {
            throw BusinessException.builder().error(BusinessErrorCode.TOKEN_PARSE_EXCEPTION).build();
        }
    }

    private TokenResponse createTokenResponse(SignedJWT accessToken, SignedJWT refreshAccessToken) throws ParseException {

        return new TokenResponse(accessToken.serialize(), refreshAccessToken.serialize(), OffsetDateTime.ofInstant(accessToken.getJWTClaimsSet()
                .getExpirationTime().toInstant(), ZoneOffset.UTC), OffsetDateTime.ofInstant(refreshAccessToken.getJWTClaimsSet()
                .getExpirationTime().toInstant(), ZoneOffset.UTC));
    }

}
