# DeVitae

This project arose out of a pun: "Developer" + "(curriculum) Vitae".

## What This is For

At time of writing, I still haven't quite worked that out. I have a vague idea of a
place to show off sandboxed code-samples.

At present, this is for learning:

- React.JS Hooks and Contexts
- Authentication using Spring Security + JWT
- Integrating React.JS with a Spring MVC REST API and Spring Security
- Flyway migration management
- Cloud deployment with Docker/Heroku

## Execution

This project can be executed in several ways:

- Local: `gradlew -Dspring.profiles.active=local clean bootRun` -- Uses H2 as a temporary database. Access via `http://localhost:8080/`
- Local ("DEV"): `gradlew clean dockerComposeUp` -- Spins up the project and a MariaDB instance as Docker containers. Access via `http://localhost:8080/`
- Heroku: Deploy as a Heroku app with an attached MariaDB instance. (In mine, I use the free-teir of JawsDB.)

## Notes on Architecture

### Environment Variables

There are some properties you should absolutely set using environment variables (rather than using the built-in default values):
- `spring.datasource.url` -- a URL to your database. Omit the `jdbc:` prefix.
- `PASSWORD_SALT` -- used to configure password-hashing.
- `ADMIN_PASSWORD` -- used to configure the default admin user (`admin`). This is only configured if the admin user is
missing (and, even then, only when database migrations happen). You can safely update the admin password and, so long as
the `admin` user continues to exist, this property will have no effect.

### Migrations

We're using Flyway as our database migration management system.

These migrations principally handle schema creation and changes. That being said, even with the first iteration
there are some migrations that need to be done in Java.

- `org.snowjak.devitae.data.migrations.CheckForDefaultAdminUser`: every time Flyway performs a migration, 
this will check for the existence of the default admin-user (username = 'admin'). If it doesn't exist,
it will (re)create it, with the configured default password (`${ADMIN_PASSWORD}`) and all configured scopes.

### Security

DeVitae uses JWT as its authentication mechanism. This means that, to be considered authenticated, every request must
include an HTTP header of the form: `Authorization: Bearer {token}`.

This `{token}` is the "JWT token" -- a cryptographic encoding of the user's (1) username, (2) "scopes" or
permissions, and (3) other metadata, such as the token's expiration date.

#### Authorization Overview

When DeVitae receives an HTTP request, the configured [JwtAuthenticationProvider](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/server/resource/authentication/JwtAuthenticationProvider.html)
inspects the request for the expected `Authorization` header. If the header is found, the given JWT token
is decoded and translated into an [Authentication](https://docs.spring.io/spring-security/site/apidocs/org/springframework/security/core/Authentication.html),
with all the encoded scopes/permissions. (**Note** that only the scopes/permissions encoded into the JWT token
are included on the Authentication object! Any scopes added *after* the token was issued will be unavailable.)

**Note** too that, because this protocol is **stateless**, the JWT token must be received and decoded with every request
that requires authentication.

### Logging In

To log-in, the user must make a `POST` request to `/login`, with the following JSON payload:

```json
{
  "username": {your username}
  },
  "password": {your password}
}
}
```

If the credentials are valid, the server will respond with a `200` status code and a JSON object of the form:

```json
{
  "authenticated",
  "username": {username},
  "scopes": [{scopes/permissions}]
  "jwt": {JWT token}
}
```

#### Generating the key-file

Executing inside `/src/main/resources/`:

`keytool -genkeypair -alias devitae -keyalg RSA -keysize 2048 -keystore devitae.jks -validity 3650`

#### Configuring the key-file in application.yml

```application.yml
...
devitae:
  auth:
    jwt:
      keystore:
        path: /devitae.jks (i.e., the keystore name)
        password: (your keyfile password))
        alias: devitae
        privateKeyPassphrase: (your private-key's passphrase)
...
```