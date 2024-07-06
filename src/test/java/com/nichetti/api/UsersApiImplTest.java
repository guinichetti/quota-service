
package com.nichetti.api;

import com.nichetti.domain.user.service.model.User;
import com.nichetti.spec.api.UserWithQuotaResource;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersApiImplTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        ExtractableResponse<Response> response = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .and()
                .body(Matchers.notNullValue())
                .extract();

        User createdUser = response.body().as(User.class);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getFirstName()).isEqualTo("John");
        assertThat(createdUser.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testGetUserNotFound() {
        String userId = UUID.randomUUID().toString();

        given()
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateUserSuccess() {
        User user = new User();
        user.setFirstName("Steve");
        user.setLastName("Jobs");

        ExtractableResponse<Response> createResponse = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .and()
                .body(Matchers.notNullValue())
                .extract();

        User createdUser = createResponse.body().as(User.class);

        createdUser.setFirstName("Brad");
        createdUser.setLastName("Newman");

        ExtractableResponse<Response> updateResponse = given()
                .contentType("application/json")
                .body(createdUser)
                .when()
                .put("/users/" + createdUser.getId())
                .then()
                .statusCode(200)
                .and()
                .body(Matchers.notNullValue())
                .extract();

        User fetchedUser = updateResponse.body().as(User.class);

        assertThat(fetchedUser).isNotNull();
        assertThat(fetchedUser.getFirstName()).isEqualTo("Brad");
        assertThat(fetchedUser.getLastName()).isEqualTo("Newman");
    }

    @Test
    void testDeleteUserSuccess() {
        String userId = UUID.randomUUID().toString();

        User user = new User();
        user.setId(userId);
        user.setFirstName("Jimmy");
        user.setLastName("Neutron");
        given().contentType("application/json").body(user).post("/users");

        given()
                .when()
                .delete("/users/" + userId)
                .then()
                .statusCode(204);
    }

    @Test
    void testConsumeQuotaSuccess() {
        String userId = UUID.randomUUID().toString();

        User user = new User();
        user.setId(userId);
        user.setFirstName("David");
        user.setLastName("Beckham");
        given().contentType("application/json").body(user).post("/users");

        given()
                .when()
                .post("/users/" + userId + "/consumeQuota")
                .then()
                .statusCode(200);
    }

    @Test
    void testGetUsersQuotaSuccess() {
        String userId = UUID.randomUUID().toString();
        User user = new User();
        user.setId(userId);
        user.setFirstName("Jonny");
        user.setLastName("Bravo");
        given().contentType("application/json").body(user).post("/users");

        ExtractableResponse<Response> response = given()
                .when()
                .get("/users/quota")
                .then()
                .statusCode(200)
                .and()
                .body(Matchers.notNullValue())
                .extract();

        List<UserWithQuotaResource> usersWithQuota = response.body().as(new TypeRef<>() {});

        assertThat(usersWithQuota).isNotNull().isNotEmpty();
    }

    @Test
    void testExceededConsumedQuota() {
        User user = new User();
        user.setFirstName("Joana");
        user.setLastName("Jones");

        ExtractableResponse<Response> createResponse = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .and()
                .body(Matchers.notNullValue())
                .extract();

        User createdUser = createResponse.body().as(User.class);
        assertThat(createdUser).isNotNull();

        String userId = createdUser.getId();

        for (int i = 0; i < 5; i++) {
            given()
                    .when()
                    .post("/users/" + userId + "/consumeQuota")
                    .then()
                    .statusCode(200);
        }

        given()
                .when()
                .post("/users/" + userId + "/consumeQuota")
                .then()
                .statusCode(429);
    }
}
