package qa.tradevalidation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.containsStringIgnoringCase;

import java.io.File;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ValidateTrade {

	private static RequestSpecification requestSpec;
	private static ResponseSpecification responseSpec;
	
	@BeforeClass(alwaysRun=true)
	public static void createRequestAndResponseSpecification() {

		requestSpec = new RequestSpecBuilder().
				setBaseUri("http://localhost:12345").
				addHeader("Content-Type","application/json").
				build();
		responseSpec = new ResponseSpecBuilder().
				expectStatusCode(200).
				expectContentType(ContentType.JSON).
				build();
	}

	@Test(groups = {"regression", "common"})
	public void test_Validate_ValueDate_CantBe_Before_Than_Trade_Date() {
		File file = new File("src/test/resources/valueDtCantBeBeforeThanTradeDt.json");
		
		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("cannot be null and it has to be after trade date"));
	}
	
	@Test(groups = {"regression", "common"})
	public void test_Validate_ValueDate_CantBe_Holiday() {
		
		File file = new File("src/test/resources/valueDtCantBeHoliday.json");
		
		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("cannot fall on Saturday/Sunday"));
	}
	
	@Test(groups = {"regression", "common"})
	public void test_Validate_Not_Suuported_CounterParties() {
		
		File file = new File("src/test/resources/counterPartyNotSupported.json");
		
		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("is not supported. Supported counterparties: [[PLUTO2, PLUTO1]]"));
	}
	
	@Test(groups = {"regression", "common"})
	public void test_Validate_Invalid_Currencies() {
		
		File file = new File("src/test/resources/invalidCurrency.json");
		
		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("Invalid currency pair"));
	}
	
	@Test(groups = {"regression", "optionType"})
	public void test_Validate_Invalid_Option_Type() {
		File file = new File("src/test/resources/invalidOptionStyle.json");

		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("Valid option styles are: [AMERICAN, EUROPEAN]"));
	}
	
	@Test(groups = {"regression", "optionType"})
	public void test_Validate_Expiry_Date_For_Option_Style() {
		File file = new File("src/test/resources/validateExpiryDateOptionStyle.json");

		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("has to be before delivery date"));
	}
	
	@Test(groups = {"regression", "optionType"})
	public void test_Validate_Premium_Date_For_Option_Style() {
		File file = new File("src/test/resources/validatePremiumDateOptionStyle.json");

		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("ERROR")).
			body("messages[0]", containsStringIgnoringCase("has to be before delivery date"));
	}
	
	@Test(groups = {"regression", "common"})
	public void test_Validate_Trade() {
		File file = new File("src/test/resources/validateTradeHappyPath.json");

		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validate").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", equalTo("SUCCESS"));
	}
	
	@Test(groups = {"regression", "common"})
	public void test_Validate_BatchTrade() {
		File file = new File("src/test/resources/validateBatchTradeHappyPath.json");

		given().
			log().all().
			spec(requestSpec).
			body(file).
		when().
			post("/validateBatch").
		then().
			log().all().
			spec(responseSpec).
		and().
			assertThat().
			body("status", hasItems("SUCCESS","SUCCESS","SUCCESS"));
	}
}