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
                .name("????????????")
                .recentlyUsed(LocalDate.of(2021, 3, 3))
                .category(category1)
                .build());

        this.groceryList.add(Grocery.builder()
                        .id(2L)
                        .name("?????????")
                        .recentlyUsed(LocalDate.of(2021, 3, 10))
                        .category(category1)
                .build());

        this.groceryList.add(Grocery.builder()
                        .id(3L)
                        .name("??????")
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

    @DisplayName("1.1 ????????? ?????? ??????")
    @Test
    void ?????????_??????_??????() throws Exception {

        when(groceryService.findAll())
                .thenReturn(groceryList);

        mockMvc.perform(get("/grocery"))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("????????????"))
                .andExpect(jsonPath("$[0].recentlyUsed").exists())
        ;


    }

    @DisplayName("1.2 ????????? ?????? ?????? - ???????????? ??????")
    @Test
    void ?????????????????????__????????????_??????() throws Exception {
        given(groceryService.findByCategoryId(1L))
                .willReturn(groceryList.stream()
                        .filter(item -> item.getCategory().getId() == 1L)
                        .collect(Collectors.toList()));

        // ???????????? ?????????
        this.mockMvc.perform(get("/grocery")
                        .queryParam("category", "1"))
                .andDo(GroceryApiDocs.getGroceries())
                .andExpect(jsonPath("$.length()").value(2))
        ;
    }

    @DisplayName("1.3 ????????? ?????? ?????? - ???????????? ?????? ???????????? ????????? ??????")
    @Test
    void ?????????????????????__????????????_??????_????????????_??????() throws Exception {
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

    @DisplayName("2.1 ????????? ????????? ??????")
    @Test
    void ?????????_?????????_??????() throws Exception {

        var ret = new Grocery();
        ret.setId(1L);
        ret.setName("??????");
        ret.setCategory(new Category());

        // given
        given(groceryService.createGrocery("??????", 1L))
                .willReturn(ret);

        mockMvc
                .perform(
                        post("/grocery")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"??????\", \"categoryId\": 1}")
                )
                .andExpect(status().isCreated())
                .andDo(GroceryApiDocs.postGrocery())
        ;
    }

    @DisplayName("2.2 ????????? ?????? - ????????? ????????? ????????? ?????? ????????? ??????")
    @Test
    void ?????????_??????__?????????_?????????_?????????_??????_?????????_??????() throws Exception {
        this.mockMvc.perform(
                post("/grocery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"categoryId\": 1}")
        )
                .andExpect(status().isBadRequest())
                ;
    }

    @DisplayName("3.1 ????????? ?????? ?????????")
    @Test
    void ?????????_??????_?????????() throws Exception {
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

    @DisplayName("3.2 ????????? ?????? - ???????????? ?????? ????????? ?????? ????????? ??????")
    @Test
    void ?????????_??????__????????????_??????_?????????_??????_?????????_??????() throws Exception {
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