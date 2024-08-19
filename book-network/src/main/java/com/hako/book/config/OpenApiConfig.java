package com.hako.book.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
@OpenAPIDefinition(
  info = @Info(
    contact = @Contact(
      name = "Hako",
      email = "",
      url = "https://github.com/hakandincturk"
    ),
    title = "Book API",
    version = "1.0",
    description = "Documentation for Book API",
    license = @License(
      name = "Apache 2.0",
      url = "http://www.apache.org/licenses/LICENSE-2.0.html"
    )
  ),
  servers = {
    @Server(
      description = "Local",
      url = "http://127.0.0.1:8080/api/v1"
    ),
    @Server(
      description = "Production",
      url = "{production-url}"
    )
  },
  security = {
    @SecurityRequirement(
      name = "bearerAuth"
    )
  }
)
@SecurityScheme(
  name = "bearerAuth",
  description = "JWT authentication",
  scheme = "bearer",
  type = SecuritySchemeType.HTTP,
  bearerFormat = "JWT",
  in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
  
}
