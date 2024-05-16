package ro.axon.dot.model.response;

import java.time.OffsetDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    @NotEmpty
    private String accessToken;
    @NotEmpty
    private String refreshToken;
    @NotNull
    private OffsetDateTime accessTokenExpirationTime;
    @NotNull
    private OffsetDateTime refreshTokenExpirationTime;
}
