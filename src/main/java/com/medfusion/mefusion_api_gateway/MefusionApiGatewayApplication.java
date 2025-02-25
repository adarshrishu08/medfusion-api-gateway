package com.medfusion.mefusion_api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
public class MefusionApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MefusionApiGatewayApplication.class, args);
	}


	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("user-service", r -> r.path("/users/**")
						.uri("lb://USER-SERVICE"))
				.route("auth-service", r -> r.path("/auth/**")
						.uri("lb://AUTH-SERVICE"))
				.build();
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
				.cors(Customizer.withDefaults())
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/auth/**").permitAll()
						.anyExchange().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
		return http.build();
	}

}
