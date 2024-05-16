package ro.axon.dot.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TeamRequest {
    @NotNull
    @NotBlank
    public String name;
}
