# Auth-Service

Microservice in charge of user authentication/authorization and acting as a gateway.

# Execution Commands

TEST spring and maven profile

    mvn spring-boot:run -Dspring-boot.run.profiles=test -P test

POSTGRESQL spring and maven profile

    mvn spring-boot:run -Dspring-boot.run.profiles=postgresql -P postgresql


# Endpoints

## Auth-Service

POST register a new CUSTOMER account

    curl -v -X POST http://localhost:8090/auth/signup \
      -H 'Content-Type: application/json' -H 'Accept: application/json' \
      --data-raw '{
      "userName": "user",
      "password": "user",
      "email": "user@email.com"
      }' | jq

POST start a new session with an existing account

    curl -v -X POST http://localhost:8090/auth/login \
    -H 'Content-Type: application/json' -H 'Accept: application/json' \
    --data-raw '{"userName": "test","password": "test"}' | jq

POST refresh the session and avoid losing the authentication in the app

    curl -v -X POST http://localhost:8090/auth/refresh-token \
    -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer valid-token' \
    --data-raw '{
      "token": "the refresh-token is provided in the login response",
      "userName": "userName"}' | jq


POST logout(this endpoint removes the refresh-token of the current session from the database and the front end should get rid of the user's jwt, a simple way to execute this function)

    curl -v -X POST http://localhost:8090/auth/logout \
    -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer valid-token' \
    --data-raw '{
      "token": "the refresh-token is provided in the login response",
      "userName": "userName"}' | jq

GET obtain the current user, this is necessary since it is the only way that I allow customers to know their id and to be able to edit their accounts, the password or delete their accounts

    curl -v -X GET http://localhost:8090/users -H 'Authorization: Bearer valid-token' -H 'Accept: application/json' | jq

## Account-Service

POST register a new account of any role(admin only)

    curl -v -X POST http://localhost:8090/accounts \
      -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer valid-token' \
      --data-raw '{
      "userName": "new user",
      "password": "new user",
      "email": "new@test.gmail",
      "role": "ADMIN"
      }' | jq

GET all accounts(admin only)

    curl -v -X GET http://localhost:8090/accounts -H 'Authorization: Bearer valid-token' -H 'Accept: application/json' | jq

PUT edit an account(the user only needs to be authenticated, since only administrators have access to any user's id while employees and customers can only know their own)

    curl -v -X PUT http://localhost:8090/accounts \
      -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer valid-token' \
      --data-raw '{
      "userId": 3,
      "userName": "updatetest",
      "email": "updatetest@test.gmail",
      "password": "updatepassword",
      "role": "EMPLOYEE"
      }' | jq

PUT edit the password of the account (like the account edit function, the user must only be authenticated)

    curl -v -X PUT http://localhost:8090/accounts/passwords \
      -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Authorization: Bearer valid-token' \
      --data-raw '{
      "userId": 3,
      "password": "updatepassword"
      }' | jq

DELETE delete the account(user must only be authenticated)

    curl -v -X DELETE http://localhost:8090/accounts/1 -H 'Authorization: Bearer valid-token'
