package roomescape.reservation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.reservation.controller.ReservationController;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReservationController reservationController;

    @Test
    void findAll() {
        RestAssured.given().log().all()
                   .when().get("/reservations")
                   .then().log().all()
                   .statusCode(200)
                   .body("size()", is(0));
        save();
        RestAssured.given().log().all()
                   .when().get("/reservations")
                   .then().log().all()
                   .statusCode(200)
                   .body("size()", is(1));
    }

    @Test
    void save(){
        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given().log().all()
                   .contentType(ContentType.JSON)
                   .body(params)
                   .when().post("/times")
                   .then().log().all()
                   .statusCode(200);

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", "2023-08-05");
        reservation.put("timeId", 1);

        RestAssured.given().log().all()
                   .contentType(ContentType.JSON)
                   .body(reservation)
                   .when().post("/reservations")
                   .then().log().all()
                   .statusCode(200);

        RestAssured.given().log().all()
                   .when().get("/reservations")
                   .then().log().all()
                   .statusCode(200)
                   .body("size()", is(1));
    }

    @Test
    void delete(){
        save();
        RestAssured.given().log().all()
                   .when().delete("/reservations/1")
                   .then().log().all()
                   .statusCode(204);

        RestAssured.given().log().all()
                   .when().delete("/reservations/1")
                   .then().log().all()
                   .statusCode(404);
    }
    @Test
    void db_connection() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void layerArchitecture() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        assertThat(isJdbcTemplateInjected).isFalse();
    }
}