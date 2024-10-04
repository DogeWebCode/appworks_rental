package tw.school.rental_backend.middleware;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Log4j2
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {

        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            attributes.put("SPRING_SECURITY_CONTEXT", auth);
            log.info("WebSocket handshake authenticated successfully for user: {}", auth.getName());
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {
        if (exception == null) {
            log.info("WebSocket handshake completed successfully: {}", request.getURI());
        } else {
            log.error("WebSocket handshake failed: {}", request.getURI(), exception);
        }
    }

    // 從 ServerHttpRequest 中解析 Token
    private String resolveToken(ServerHttpRequest request) {
        List<String> authValues = request.getHeaders().get("Authorization");
        if (authValues != null && !authValues.isEmpty()) {
            String bearerToken = authValues.get(0);
            if (bearerToken.startsWith("Bearer ")) {
                log.debug("JWT token found in Authorization header");
                return bearerToken.substring(7);
            }
        }
        // 如果從 Header 中未獲取到，嘗試從 URL 參數中獲取
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        String tokenFromParams = params.getFirst("token");
        if (tokenFromParams != null) {
            log.debug("JWT token found in URL parameters");
        } else {
            log.warn("No JWT token found in request");
        }
        return tokenFromParams;
    }
}