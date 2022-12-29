package api;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;


public class ReqresTest {
    private final String URL = "https://reqres.in";
    @Test
    public void checkAvatarAndIdTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecOK200());
        //Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecError400());

//        List<UserData> users = given()
//                .when()
//                .contentType(ContentType.JSON)
//                .get(URL + "/api/users?page=2")
//                .then().log().all()
//                .extract().body().jsonPath().getList("data", UserData.class);

        List<UserData> users = given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        // users.stream().forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString()))); // first variant
        users.forEach(x-> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString()))); // after optimize to junit

        //Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in"))); // first variant
        Assertions.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in"))); // after optimize to junit

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());

        for(int i=0; i<avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successRegTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());

        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());

    }

    @Test
    public void unSuccessRegTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecError400());
        Register user = new Register("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assertions.assertEquals("Missing password", unSuccessReg.getError());
    }
}
