package io.onezero.jangpyunham.web.category;

import io.onezero.jangpyunham.domain.grocery.Category;
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

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    MockMvc mockMvc;

    @MockBean
    private GroceryService groceryService;

    @BeforeEach
    void setUp(WebApplicationContext context,
               RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentation))
                .build();

        var categoryList = new ArrayList<Category>();
        var category1 = new Category();
        category1.setId(1L);
        category1.setName("수산");

        var category2 = new Category();
        category2.setId(2L);
        category2.setName("정육");

        var category3 = new Category();
        category3.setId(3L);
        category3.setName("채소");
        categoryList.add(category1);
        categoryList.add(category2);
        categoryList.add(category3);

        when(groceryService.findAllCategories())
                .thenReturn(categoryList);
    }

    @DisplayName("1. 카테고리 목록 조회")
    @Test
    void 카테고리_목록_조회() throws Exception {

        this.mockMvc.perform(get("/category"))
                .andDo(CategoryApiDocs.getCategories())
//                .andDo(document("Read_Category",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        responseFields(
//                                fieldWithPath("[].id").description("카테고리 ID 필드"),
//                                fieldWithPath("[].name").description("카테고리 이름")
//                        )))
                .andExpect(jsonPath("$[0].name").value("수산"))
                .andExpect(jsonPath("$[1].name").value("정육"))
                .andExpect(jsonPath("$[2].name").value("채소"))
        ;
    }

    @DisplayName("2. 카테고리 추가")
    @Test
    void add_category_and_read() throws Exception {

        var ret = new Category();
        ret.setId(15L);
        ret.setName("주방");

        given(groceryService.createCategory(any()))
                .willReturn(ret);

        this.mockMvc.perform(
                post("/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"주방\"}")
        )
                .andDo(CategoryApiDocs.createCategory())
                .andExpect(status().isCreated())
        ;

    }
}