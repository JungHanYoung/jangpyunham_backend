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
        category1.setName("??????");

        var category2 = new Category();
        category2.setId(2L);
        category2.setName("??????");

        var category3 = new Category();
        category3.setId(3L);
        category3.setName("??????");
        categoryList.add(category1);
        categoryList.add(category2);
        categoryList.add(category3);

        when(groceryService.findAllCategories())
                .thenReturn(categoryList);
    }

    @DisplayName("1. ???????????? ?????? ??????")
    @Test
    void ????????????_??????_??????() throws Exception {

        this.mockMvc.perform(get("/category"))
                .andDo(CategoryApiDocs.getCategories())
//                .andDo(document("Read_Category",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        responseFields(
//                                fieldWithPath("[].id").description("???????????? ID ??????"),
//                                fieldWithPath("[].name").description("???????????? ??????")
//                        )))
                .andExpect(jsonPath("$[0].name").value("??????"))
                .andExpect(jsonPath("$[1].name").value("??????"))
                .andExpect(jsonPath("$[2].name").value("??????"))
        ;
    }

    @DisplayName("2. ???????????? ??????")
    @Test
    void add_category_and_read() throws Exception {

        var ret = new Category();
        ret.setId(15L);
        ret.setName("??????");

        given(groceryService.createCategory(any()))
                .willReturn(ret);

        this.mockMvc.perform(
                post("/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"??????\"}")
        )
                .andDo(CategoryApiDocs.createCategory())
                .andExpect(status().isCreated())
        ;

    }
}