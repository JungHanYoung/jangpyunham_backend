package io.onezero.jangpyunham.web.purchase;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.Map;

import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentRequest;
import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentResponse;
import static io.onezero.jangpyunham.web.grocery.GroceryApiDocs.*;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class PurchaseApiDocs {

    public static final String PURCHASE_ID_DESCRIPTION = "구입항목 ID";
    public static final String PURCHASE_COMPLETED_DESCRIPTION = "구입여부(true or false)";
    public static final String PURCHASE_OFFSET_DESCRIPTION = "구입항목 시작지점(기본값: 0)";
    public static final String PURCHASE_LIMIT_DESCRIPTION = "한 페이지에 가져올 구입항목 최대 수(기본값: 10, 최대: 100)";
    public static final String PURCHASE_ORDER_DESCRIPTION = "구입 항목 정렬 순서 오름차순(asc) 또는 내림차순(desc)(기본값 asc)";

    public static RestDocumentationResultHandler getPurchaseList() {
        return document("purchase/getAll",
                getDocumentRequest(),
                getDocumentResponse(),
                requestParameters(
                        parameterWithName("completed").optional().description(PURCHASE_COMPLETED_DESCRIPTION),
                        parameterWithName("order").optional().description(PURCHASE_ORDER_DESCRIPTION),
                        parameterWithName("offset").optional().description(PURCHASE_OFFSET_DESCRIPTION),
                        parameterWithName("limit").optional().description(PURCHASE_LIMIT_DESCRIPTION)
                ),
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description(PURCHASE_ID_DESCRIPTION),
                        fieldWithPath("[].completed").type(JsonFieldType.BOOLEAN).description(PURCHASE_COMPLETED_DESCRIPTION),
                        fieldWithPath("[].grocery.id").type(JsonFieldType.NUMBER).description(GROCERY_ID_DESCRIPTION),
                        fieldWithPath("[].grocery.name").type(JsonFieldType.STRING).description(GROCERY_NAME_DESCRIPTION),
                        fieldWithPath("[].grocery.recentlyUsed").type(JsonFieldType.STRING).description(GROCERY_RECENTLY_USED_DESCRIPTION)
                )
                )
                ;

    }

    public static RestDocumentationResultHandler postPurchase() {
        return document("purchase/create",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("groceryId").type(JsonFieldType.NUMBER).description(GROCERY_ID_DESCRIPTION)
                ),
                responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description(PURCHASE_ID_DESCRIPTION),
                        fieldWithPath("completed").type(JsonFieldType.BOOLEAN).description(PURCHASE_COMPLETED_DESCRIPTION),
                        fieldWithPath("grocery.id").type(JsonFieldType.NUMBER).description(GROCERY_ID_DESCRIPTION),
                        fieldWithPath("grocery.name").type(JsonFieldType.STRING).description(GROCERY_NAME_DESCRIPTION),
                        fieldWithPath("grocery.recentlyUsed").type(JsonFieldType.STRING).description(GROCERY_RECENTLY_USED_DESCRIPTION)
                )
        );
    }

    public static RestDocumentationResultHandler postAllPurchase() {
        return document("purchase/createAll",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("groceryIds").type(JsonFieldType.ARRAY).description("식재료 ID 리스트")
                ),
                httpResponse(
                        Map.of("status", 201)
                )
            );
    }

    public static RestDocumentationResultHandler complete() {
        return document("purchase/complete",
                getDocumentRequest(),
                getDocumentResponse(),
                requestParameters(
                        parameterWithName("id").description(PURCHASE_ID_DESCRIPTION)
                ),
                httpResponse(
                        Map.of("statusCode", 200)
                )
        )
                ;
    }

    public static RestDocumentationResultHandler completeAll() {
        return document("purchase/completeAll",
                getDocumentRequest(),
                getDocumentResponse(),
                requestParameters(
                        parameterWithName("ids").description("완료시킬 구입항목 ID 리스트")
                )
        );
    }

    public static RestDocumentationResultHandler deleteById() {
        return document("purchase/deleteById",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                    parameterWithName("id").description(PURCHASE_ID_DESCRIPTION)
            )
        );
    }

    public static RestDocumentationResultHandler getPurchaseList_uncomplete() {
        return document("purchase/getAll/uncomplete",
            getDocumentRequest(),
            getDocumentResponse()
        );
    }

}
