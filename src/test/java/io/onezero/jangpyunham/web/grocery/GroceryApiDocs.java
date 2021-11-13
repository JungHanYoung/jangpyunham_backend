package io.onezero.jangpyunham.web.grocery;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.Map;

import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentRequest;
import static io.onezero.jangpyunham.documentation.ApiDocumentUtils.getDocumentResponse;
import static io.onezero.jangpyunham.web.category.CategoryApiDocs.CATEGORY_ID_DESCRIPTION;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class GroceryApiDocs {

    public static final String GROCERY_ID_DESCRIPTION = "식재료 ID";
    public static final String GROCERY_NAME_DESCRIPTION = "식재료 이름";
    public static final String GROCERY_RECENTLY_USED_DESCRIPTION = "식재료 최근 장본 날짜(yyyy-MM-dd)";

    public static RestDocumentationResultHandler getGroceries() {
        return document("grocery/getAll",
                getDocumentRequest(),
                getDocumentResponse(),
                requestParameters(
                        parameterWithName("category").description(CATEGORY_ID_DESCRIPTION).optional()
                ),
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description(GROCERY_ID_DESCRIPTION),
                        fieldWithPath("[].name").type(JsonFieldType.STRING).description(GROCERY_NAME_DESCRIPTION),
                        fieldWithPath("[].recentlyUsed").type(JsonFieldType.STRING).description(GROCERY_RECENTLY_USED_DESCRIPTION).optional()
                )
        );
    }

    public static RestDocumentationResultHandler postGrocery() {
        return document("grocery/create",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description(GROCERY_NAME_DESCRIPTION),
                        fieldWithPath("categoryId").type(JsonFieldType.NUMBER).description(CATEGORY_ID_DESCRIPTION)
                ),
                httpResponse(
                        Map.of("status", 201)
                )
            );
    }

    public static RestDocumentationResultHandler deleteGrocery() {
        return document("grocery/delete",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                        parameterWithName("id").description(GROCERY_ID_DESCRIPTION)
                )
                );
    }
}
