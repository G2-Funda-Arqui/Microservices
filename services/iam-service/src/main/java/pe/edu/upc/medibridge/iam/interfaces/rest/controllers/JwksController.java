package pe.edu.upc.medibridge.iam.interfaces.rest.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.medibridge.iam.infrastructure.tokens.JwtTokenProvider;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/jwks")
public class JwksController {
    private final JwtTokenProvider jwtTokenProvider;

    public JwksController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return new JWKSet(jwtTokenProvider.getPublicJwk()).toJSONObject();
    }
}
