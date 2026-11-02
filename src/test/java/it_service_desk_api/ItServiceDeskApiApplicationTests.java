package it_service_desk_api;

import com.jayway.jsonpath.JsonPath;
import it_service_desk_api.entities.Users;
import it_service_desk_api.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"api.security.token.secret=local-test-secret-with-at-least-32-characters",
		"spring.datasource.url=jdbc:h2:mem:it_service_desk_api;MODE=MySQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"api.security.login.max-attempts=2"
})
@AutoConfigureMockMvc
class ItServiceDeskApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtDecoder jwtDecoder;

	@BeforeEach
	void clearUsers() {
		usersRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void registersUserAndAuthenticatesWithBearerToken() throws Exception {
		String password = "securePass123";

		mockMvc.perform(post("/auth/register")
						.with(remoteAddress("192.0.2.10"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name":"Test User","email":"test@example.com","password":"securePass123"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.role").value("USER"))
				.andExpect(jsonPath("$.password").doesNotExist());

		Users user = usersRepository.findByEmail("test@example.com").orElseThrow();
		assertNotEquals(password, user.getPassword());
		assertTrue(passwordEncoder.matches(password, user.getPassword()));

		String response = mockMvc.perform(post("/auth/login")
						.with(remoteAddress("192.0.2.11"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"TEST@example.com","password":"securePass123"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.user.email").value("test@example.com"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		String accessToken = JsonPath.read(response, "$.accessToken");
		assertEquals("test@example.com", jwtDecoder.decode(accessToken).getSubject());

		mockMvc.perform(get("/auth/login").header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isMethodNotAllowed());
		mockMvc.perform(get("/auth/login")).andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsRepeatedLoginAttemptsFromSameAddress() throws Exception {
		String invalidCredentials = """
				{"email":"missing@example.com","password":"wrongPassword"}
				""";

		for (int attempt = 0; attempt < 2; attempt++) {
			mockMvc.perform(post("/auth/login")
							.with(remoteAddress("192.0.2.12"))
							.contentType(MediaType.APPLICATION_JSON)
							.content(invalidCredentials))
					.andExpect(status().isUnauthorized());
		}

		mockMvc.perform(post("/auth/login")
						.with(remoteAddress("192.0.2.12"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(invalidCredentials))
				.andExpect(status().isTooManyRequests());
	}

	@Test
	void rejectsPasswordsThatExceedBcryptByteLimit() throws Exception {
		String password = "á".repeat(37);
		String request = """
				{"name":"Test User","email":"unicode@example.com","password":"%s"}
				""".formatted(password);

		mockMvc.perform(post("/auth/register")
						.with(remoteAddress("192.0.2.13"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isBadRequest());
	}

	private RequestPostProcessor remoteAddress(String address) {
		return request -> {
			request.setRemoteAddr(address);
			return request;
		};
	}
}
