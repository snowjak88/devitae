# DeVitae

This project arose out of a pun, "Developer" + "(curriculum) Vitae".

At time of writing, I still haven't worked out what this is for. I have a vague idea of a
place to show off sandboxed code-samples. At present, this is for learning how to integrate
Spring Security with React.JS, as well as newer React features like Contexts and Hooks.

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

To log-in, the user must make a `POST` request to `/login`, with the following form-data:

- `username`
- `password`

If the credentials are valid, the server will respond with a `200` status code and a JSON object of the form:

```json
{
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