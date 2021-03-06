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

        String[]    groceryNames    = {"??????", "??????", "????????????", "?????????", "??????"};
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

    @DisplayName("1. ?????? ?????? ??????")
    @Test
    void ??????????????????() throws Exception {

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

    @DisplayName("1.1 ???????????? ?????? ????????? ?????? ??????")
    @Test
    void ????????????_??????_?????????_??????_??????() throws Exception {
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

    @DisplayName("1.2 ???????????? ?????? ????????? ?????? 3??? ?????? ??????")
    @Test
    void ????????????_??????_?????????_??????_3???_??????_??????() throws Exception {
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

    @DisplayName("1.3 ???????????? ?????? ??????????????? 2?????? ????????? 3??? ?????? ??????")
    @Test
    void ??????????????????__2???????????????_3?????????() throws Exception {
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



    @DisplayName("2. ?????? ?????? ??????")
    @Test
    void ????????????_??????() throws Exception {
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
                .andExpect(jsonPath("$.grocery.name").value("????????????"))
                .andExpect(jsonPath("$.grocery.recentlyUsed").exists())
                .andExpect(jsonPath("$.grocery.id").value(1))
                ;
    }

    @DisplayName("2.1 ???????????? ?????? - ???????????? ?????? ???????????? ????????????")
    @Test
    void ????????????_??????__??????X_?????????() throws Exception {
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

    @DisplayName("3. ?????? ?????? ????????? ??????")
    @Test
    void ????????????_???????????????() throws Exception {
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

    @DisplayName("3.1 ???????????? ????????? ?????? - ???????????? ?????? ???????????? ????????? ???????????? ?????????")
    @Test
    void ????????????_?????????_??????__??????????????????_????????????_?????????_????????????_?????????() throws Exception {
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

    @DisplayName("4. ?????? ?????? ??????")
    @Test
    void ????????????_??????() throws Exception {
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

    @DisplayName("5. ????????? ?????? ???????????? ??????")
    @Test
    void ???????????????_????????????_??????() throws Exception {

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

    @DisplayName("6. ?????? ?????? ??????")
    @Test
    void ????????????_??????() throws Exception {
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

    @DisplayName("7. ?????? ?????? ?????? - ?????????????????? ID")
    @Test
    void ????????????_??????__??????????????????_ID() throws Exception {
        given(purchaseService.deleteById(1L))
                .willThrow(ResourceNotFoundException.class);

        this.mockMvc.perform(
                delete("/purchase/{id}", 1L)
        )
                .andExpect(status().isNotFound())
                ;
    }
}