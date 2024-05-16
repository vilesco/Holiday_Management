package ro.axon.dot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ro.axon.dot.security.jwt")
public class JwtProperties {

  private String audience;

  private String issuer;

  private String accessType;

  private String refreshType;

  private Integer accessDuration;

  private Integer refreshDuration;

  private String keyId;
}