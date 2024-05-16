package ro.axon.dot.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;

@Service
public class SecurityAccessTokenHandler {

  private JwtAuthenticationToken getJwtAuthenticationToken() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof JwtAuthenticationToken) {
      return (JwtAuthenticationToken) authentication;
    }

    throw new IllegalStateException("No JWT Auth found in Security Context!");
  }

  public String getEmployeeIdFromToken() {
    var authentication = getJwtAuthenticationToken();
    if (authentication.getToken().getSubject() == null) {
      throw BusinessException.builder()
        .error(BusinessErrorCode.TOKEN_DETAILS_EMPLOYEE_ID_MISSING)
        .build();
    }
    return authentication.getToken().getSubject();
  }

}
