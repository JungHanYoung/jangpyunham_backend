package io.onezero.jangpyunham.web.grocery;

import io.onezero.jangpyunham.domain.grocery.Category;
import io.onezero.jangpyunham.domain.grocery.Grocery;
import io.onezero.jangpyunham.exception.ResourceNotFoundException;
import io.onezero.jangpyunham.service.GroceryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentRequest;
import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(GroceryController.class)
class GroceryControllerTest {

    MockMvc mockMvc;

    @MockBean
    private GroceryService groceryService;

    private final List<Grocery> groceryList = new ArrayList<>();

    @BeforeEach
    void setUp(WebApplicationContext context,
               RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentation))
                .build();

        var category1 = new Category();
        category1.setId(1L);
        var category2 = new Category();
        category2.setId(2L);

        this.groceryList.add(Grocery.builder()
                .id(1L)
                .name("돼지고기")
                .recentlyUsed(LocalDate.of(2021, 3, 3))
                .category(category1)
                .build());

        this.groceryList.add(Grocery.builder()
                        .id(2L)
                        .name("소고기")
                        .recentlyUsed(LocalDate.of(2021, 3, 10))
                        .category(category1)
                .build());

        this.groceryList.add(Grocery.builder()
                        .id(3L)
                        .name("양파")
                        .recentlyUsed(LocalDate.of(2021, 10, 15))
                        .category(category2)
                .build());

        given(groceryService.findByCategoryId(1L))
                .willReturn(
                        groceryList
                                .stream()
                                .filter(item -> item.getCategory().getId() == 1L)
                                .collect(Collectors.toList()));
    }

    @DisplayName("1.1 식재료 목록 조회")
    @Test
    void 식재료_목록_조회() throws Exception {

        when(groceryService.findAll())
                .thenReturn(groceryList);

        mockMvc.perform(get("/grocery"))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("돼지고기"))
                .andExpect(jsonPath("$[0].recentlyUsed").exists())
        ;


    }

    @DisplayName("1.2 식재료 목록 조회 - 카테고리 검색")
    @Test
    void 식재료목록조회__카테고리_검색() throws Exception {
        given(groceryService.findByCategoryId(1L))
                .willReturn(groceryList.stream()
                        .filter(item -> item.getCategory().getId() == 1L)
                        .collect(Collectors.toList()));

        // 카테고리 필터링
        this.mockMvc.perform(get("/grocery")
                        .queryParam("category", "1"))
                .andDo(GroceryApiDocs.getGroceries())
                .andExpect(jsonPath("$.length()").value(2))
        ;
    }

    @DisplayName("1.3 식재료 목록 조회 - 존재하지 않은 카테고리 검색시 실패")
    @Test
    void 식재료목록조회__존재하지_않은_카테고리_검색() throws Exception {
        given(groceryService.findByCategoryId(20L))
                .willThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(
                get("/grocery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("category", "20")
        )
                .andDo(document("grocery/getAll/not_found_category",
                        getDocumentRequest(),
                        getDocumentResponse()
                        ))
                .andExpect(status().isNotFound())
                ;
    }

    @DisplayName("2.1 식재료 데이터 생성")
    @Test
    void 식재료_데이터_생성() throws Exception {

        var ret = new Grocery();
        ret.setId(1L);
        ret.setName("양파");
        ret.setCategory(new Category());

        // given
        given(groceryService.createGrocery("양파", 1L))
                .willReturn(ret);

        mockMvc
                .perform(
                        post("/grocery")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"양파\", \"categoryId\": 1}")
                )
                .andExpect(status().isCreated())
                .andDo(GroceryApiDocs.postGrocery())
        ;
    }

    @DisplayName("2.2 식재료 생성 - 이름이 공백인 상태로 생성 요청시 실패")
    @Test
    void 식재료_생성__이름이_공백인_상태로_생성_요청시_실패() throws Exception {
        this.mockMvc.perform(
                post("/grocery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"categoryId\": 1}")
        )
                .andExpect(status().isBadRequest())
                ;
    }

    @DisplayName("3.1 식재료 삭제 테스트")
    @Test
    void 식재료_삭제_테스트() throws Exception {
        long id = 1L;

        given(groceryService.delete(id))
                .willReturn(true);

        mockMvc.perform(
                        delete("/grocery/{id}", id)
                )
                .andDo(GroceryApiDocs.deleteGrocery())
                .andExpect(status().is2xxSuccessful())
        ;
    }

    @DisplayName("3.2 식재료 삭제 - 존재하지 않는 식재료 삭제 요청시 실패")
    @Test
    void 식재료_삭제__존재하지_않는_식재료_삭제_요청시_실패() throws Exception {
        long notExistId = 20L;

        given(groceryService.delete(notExistId))
                .willThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(
                delete("/grocery/{id}", notExistId)
        )
                .andDo(document("grocery/delete/not_exist_grocery",
                        getDocumentRequest(),
                        getDocumentResponse()
                        ))
                .andExpect(status().isNotFound())
                ;
    }

}