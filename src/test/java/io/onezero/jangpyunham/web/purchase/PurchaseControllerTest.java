package io.onezero.jangpyunham.web.purchase;

import io.onezero.jangpyunham.domain.grocery.Category;
import io.onezero.jangpyunham.domain.grocery.Grocery;
import io.onezero.jangpyunham.domain.purchase.Purchase;
import io.onezero.jangpyunham.exception.ResourceNotFoundException;
import io.onezero.jangpyunham.service.PurchaseService;
import io.onezero.jangpyunham.web.purchase.dto.CreateAllPurchaseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentRequest;
import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {

    MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    List<Purchase> list = new ArrayList<>();

    @BeforeEach
    void setUp(WebApplicationContext context,
               RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentation))
                .build();

        String[]    groceryNames    = {"양파", "대파", "돼지고기", "물티슈", "사과"};
        int[]       categoryIds     = {1,1,2,3,4};
        boolean[]   completedList   = {false, true, false, true, false};

        for(int i = 0; i < 5; i++) {
            list.add(Purchase.builder()
                    .id((long) (i+1))
                    .completed(completedList[i])
                    .grocery(Grocery.builder()
                            .id(1L)
                            .name(groceryNames[i])
                            .recentlyUsed(LocalDate.of(2021, 6, 15))
                            .category(Category.builder()
                                    .id((long) categoryIds[i])
                                    .build())
                            .build()
                    )
                    .build());
        }
    }

    @DisplayName("1. 장볼 목록 조회")
    @Test
    void 장볼목록조회() throws Exception {

        Page page;

        when(purchaseService.findAll(0, 10))
                .thenReturn(new PageImpl<>(list));

        this.mockMvc.perform(
                get("/purchase")
        )
                .andDo(PurchaseApiDocs.getPurchaseList())
                .andExpect(jsonPath("$.length()").value(5))
                ;

        //

    }

    @DisplayName("1.1 완료되지 않은 장보기 목록 조회")
    @Test
    void 완료되지_않은_장보기_목록_조회() throws Exception {
        given(purchaseService.findAll(false, 0, 10))
                .willReturn(list
                        .stream()
                        .filter(item -> !item.isCompleted())
                        .collect(Collectors.toList())
                );

        this.mockMvc.perform(
                        get("/purchase")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("completed", "false")
                )
                .andDo(PurchaseApiDocs.getPurchaseList_uncomplete())
                .andExpect(jsonPath("$.length()").value(3))
        ;
    }

    @DisplayName("1.2 완료되지 않은 장보기 목록 3개 정보 요청")
    @Test
    void 완료되지_않은_장보기_목록_3개_정보_요청() throws Exception {
        given(purchaseService.findAll(false, 0, 3))
                .willReturn(list.stream()
                        .limit(3)
                        .collect(Collectors.toList())
                );

        this.mockMvc.perform(
                get("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("completed", "false")
                        .queryParam("limit", "3")
        )
                .andDo(document("purchase/getAll/uncomplete_and_limit",
                        getDocumentRequest(),
                        getDocumentResponse()
                ))
                .andExpect(jsonPath("$.length()").value(3))
                ;
    }

    @DisplayName("1.3 완료되지 않은 장보기목록 2번째 페이지 3개 정보 요청")
    @Test
    void 구입항목조회__2번째페이지_3개요청() throws Exception {
        given(purchaseService.findAll(false, 3, 3))
                .willReturn(list.stream()
                        .skip(3)
                        .limit(3)
                        .collect(Collectors.toList())
                );

        this.mockMvc.perform(
                get("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("completed", "false")
                        .queryParam("offset", "3")
                        .queryParam("limit", "3")
        )
                .andDo(document("purchase/getAll/uncomplete_and_nextpage_and_limit",
                        getDocumentRequest(),
                        getDocumentResponse()
                        ))
                ;
    }



    @DisplayName("2. 구입 항목 생성")
    @Test
    void 구입항목_생성() throws Exception {
        given(purchaseService.create(2L))
                .willReturn(list.get(2));

        this.mockMvc.perform(
                post("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groceryId\": 2}")
        )
                .andDo(PurchaseApiDocs.postPurchase())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.grocery.name").value("돼지고기"))
                .andExpect(jsonPath("$.grocery.recentlyUsed").exists())
                .andExpect(jsonPath("$.grocery.id").value(1))
                ;
    }

    @DisplayName("2.1 구입항목 생성 - 존재하지 않는 식재료로 항목생성")
    @Test
    void 구입항목_생성__존재X_식재료() throws Exception {
        given(purchaseService.create(50L))
                .willThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(
                post("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groceryId\": 50}")
        )
                .andExpect(status().isNotFound())
                ;
    }

    @DisplayName("3. 구입 항목 여러개 생성")
    @Test
    void 구입항목_여러개생성() throws Exception {
        given(purchaseService.createAll(any()))
                .willReturn(list.stream().limit(3).collect(Collectors.toList()));

        this.mockMvc.perform(
                post("/purchase/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groceryIds\": [2,3,4]}")
        )
                .andDo(PurchaseApiDocs.postAllPurchase())
                .andExpect(status().isCreated())
                ;
    }

    @DisplayName("3.1 구입항목 여러개 생성 - 존재하지 않는 식재료가 포함된 리스트로 생성시")
    @Test
    void 구입항목_여러개_생성__존재하지않는_식재료가_포함된_리스트로_생성시() throws Exception {
        given(purchaseService.createAll(List.of(30L, 40L)))
                .willThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(
                post("/purchase/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groceryIds\": [30, 40]}")
        )
                .andExpect(status().isNotFound())
                ;
    }

    @DisplayName("4. 완료 여부 변경")
    @Test
    void 완료여부_변경() throws Exception {
        given(purchaseService.complete(1L))
                .willReturn(1L);

        this.mockMvc.perform(
                patch("/purchase/complete")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("id", "1")
        )
                .andDo(PurchaseApiDocs.complete())
                .andExpect(status().isOk())
                ;

        verify(purchaseService).complete(1L);
    }

    @DisplayName("5. 여러개 항목 완료여부 변경")
    @Test
    void 여러개항목_완료여부_변경() throws Exception {

        given(purchaseService.completeAll(List.of(1L, 2L)))
                .willReturn(List.of(1L, 2L));

        this.mockMvc.perform(
                patch("/purchase/complete/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("ids", "1,2")
        )
                .andDo(PurchaseApiDocs.completeAll())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                ;

        verify(purchaseService).completeAll(List.of(1L, 2L));
    }

    @DisplayName("6. 구입 항목 삭제")
    @Test
    void 구입항목_삭제() throws Exception {
        given(purchaseService.deleteById(1L))
                .willReturn(1L);

        this.mockMvc.perform(
                delete("/purchase/{id}", 1L)
        )
                .andDo(PurchaseApiDocs.deleteById())
                .andExpect(status().isOk())
                ;

        verify(purchaseService).deleteById(1L);
    }

    @DisplayName("7. 구입 항목 삭제 - 존재하지않는 ID")
    @Test
    void 구입항목_삭제__존재하지않는_ID() throws Exception {
        given(purchaseService.deleteById(1L))
                .willThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(
                delete("/purchase/{id}", 1L)
        )
                .andExpect(status().isNotFound())
                ;
    }
}