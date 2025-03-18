package com.example.anbdapi

import com.example.anbdapi.domain.user.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
    @Autowired val userRepository: UserRepository
) {

    // 실제 소셜 로그인 후 토큰을 넣으세요.
    private val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQxNTg4NTQ2LCJleHAiOjE3NDE1OTIxNDYsImp0aSI6IjhiZTQwNmIxLTcwMTctNDI2OS1iYTc4LTBhNTQwNTU1Y2YzOCJ9.MVgUILBTHhnd8SV8zVBLhc9feci1D2iJMvUaL1ngvGc"

    @Test
    @Order(1)
    @DisplayName("GET /v1/users/me - 현재 사용자 정보 조회")
    fun getUserInfoTest() {
        mockMvc.get("/v1/users/me") {
            header("Authorization", "Bearer $ACCESS_TOKEN")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.body.email") { value("tnwoql327@gmail.com") }
            }
            .andDo { print() }
    }

    @Test
    @Order(2)
    @DisplayName("PUT /v1/users/profiles - 프로필 업데이트 테스트")
    fun updateProfileTest() {
        val requestBody = mapOf(
            "gender" to "MALE",
            "birthDate" to "2000-01-01",
            "nickname" to "TestNickname",
            "profileImage" to "http://test.image/url",
            "shareCategory" to "FOOD"
        )
        val json = objectMapper.writeValueAsString(requestBody)

        // 1. 프로필 업데이트
        mockMvc.put("/v1/users/profiles") {
            header("Authorization", "Bearer $ACCESS_TOKEN")
            contentType = MediaType.APPLICATION_JSON
            content = json
        }
            .andExpect {
                status { isOk() }
            }
            .andDo { print() }

        // 2. 프로필 업데이트 이후 사용자 정보를 조회하여 입력한 데이터와 비교
        mockMvc.get("/v1/users/me") {
            header("Authorization", "Bearer $ACCESS_TOKEN")
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.body.gender") { value("MALE") }
                jsonPath("$.body.birthDate") { value("2000-01-01") }
                jsonPath("$.body.nickname") { value("TestNickname") }
                jsonPath("$.body.profileImage") { value("http://test.image/url") }
                jsonPath("$.body.shareCategory") { value("FOOD") }

            }
            .andDo { print() }
    }

    @Test
    @Order(3)
    @DisplayName("POST /v1/users/logout - 로그아웃 테스트")
    fun logoutTest() {
        mockMvc.post("/v1/users/logout") {
            header("Authorization", "Bearer $ACCESS_TOKEN")
        }
            .andExpect {
                status { isOk() }
            }
            .andDo { print() }
        val user = userRepository.findByEmail("tnwoql327@gmail.com")
        Assertions.assertNotNull(user, "로그아웃 이후에도 사용자는 존재해야 합니다.")
        Assertions.assertNull(user?.refreshToken, "사용자는 존재하나 리프레시토큰은 null이어야 합니다.")
    }

    @Test
    @Order(4)
    @DisplayName("PATCH /v1/users/withdraw - 회원 탈퇴 테스트")
    fun withdrawUserTest() {
        // 1. 회원 탈퇴 요청
        mockMvc.patch("/v1/users/withdraw") {
            header("Authorization", "Bearer $ACCESS_TOKEN")
        }
            .andExpect {
                status { isOk() }
            }
            .andDo { print() }

        // 2. 탈퇴한 후 다시 사용자 정보 요청 (User Not Found 기대)
        mockMvc.get("/v1/users/me") {
            header("Authorization", "Bearer $ACCESS_TOKEN")
        }
            .andExpect {
                status { isNotFound() }
            }
            .andDo { print() }
    }
}