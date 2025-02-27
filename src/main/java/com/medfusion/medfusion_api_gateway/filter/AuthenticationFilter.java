package com.medfusion.medfusion_api_gateway.filter;

import com.medfusion.medfusion_api_gateway.util.JWTUtil;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private RestTemplate template;
    @Autowired
    private JWTUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Put configuration properties here
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(routeValidator.isSecured.test(exchange.getRequest())) {
                // Header contains token or not
                if(exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    String token = exchange.getRequest().getHeaders().get("Authorization").get(0);
                    // Validate token
                    if(token != null && token.startsWith("Bearer ")) {
                        // Put validation logic here
                        token = token.substring(7);
                        try {
//                            template.getForEntity("http://AUTH-SERVER/validate?token" + token, String.class);
                            jwtUtil.validateToken(token);
                        } catch (Exception e) {
                            throw new RuntimeException("Invalid token");
                        }
                    } else {
                        System.out.println("Invalid access ....!");
                        throw new RuntimeException("Invalid token");
                    }
                } else {
                    throw new RuntimeException("Authorization header is missing");
                }
            }
            // Put filter logic here
            return chain.filter(exchange);
        };
    }
}
