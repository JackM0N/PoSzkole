package pl.poszkole.PoSzkole.security;

import lombok.Getter;

@Getter
public record AuthenticationResponse(String token) {
}
