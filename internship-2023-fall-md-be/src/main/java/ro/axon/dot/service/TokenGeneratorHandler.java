package ro.axon.dot.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.axon.dot.config.JwtKeyProperties;
import ro.axon.dot.config.JwtProperties;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;

@Component
@RequiredArgsConstructor
public class TokenGeneratorHandler {

  private final JwtProperties jwtProperties;

  private final JwtKeyProperties jwtKeyProperties;

  private final Clock clock;

  private RSASSASigner signer;

  @PostConstruct
  public void setup() {
    signer = new RSASSASigner((jwtKeyProperties.getPrivateKeyLocation()));
  }


  public SignedJWT generateToken(EmployeeEty employeeEty, int duration, String type, String keyId) {
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
      .subject(employeeEty.getId())
      .issuer(jwtProperties.getIssuer())
      .audience(jwtProperties.getAudience())
      .claim("username", employeeEty.getUsername())
      .claim("email", employeeEty.getEmail())
      .claim("typ", type)
      .claim("roles", Collections.singletonList(employeeEty.getRole()))
      .notBeforeTime(new Date(clock.millis()))
      .expirationTime(Date.from(clock.instant().plusSeconds(duration)))
      .build();

    // keyID from application.yml for accessToken, for refreshToken generate using UuidGeneratorHandler.generateUuid();
    var signedJwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build(), claimsSet);

    try {
      signedJwt.sign(signer);
      return signedJwt;
    } catch (JOSEException e) {
       throw BusinessException.builder().error(BusinessErrorCode.TOKEN_GENERATION_EXCEPTION).build();
    }
  }
}
