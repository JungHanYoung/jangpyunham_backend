package io.onezero.jangpyunham.web.category;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class CategoryApiDocs {

    public static final String CATEGORY_ID_DESCRIPTION = "카테고리 ID";
    public static final String CATEGORY_NAME_DESCRIPTION = "카테고리 이름";

    public static RestDocumentationResultHandler getCategories() {
        return document("category/getAll",
                responseFields(
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description(CATEGORY_ID_DESCRIPTION),
                        fieldWithPath("[].name").type(JsonFieldType.STRING).description(CATEGORY_NAME_DESCRIPTION)
                )
                );
    }

    public static RestDocumentationResultHandler createCategory() {
        return document("category/create",
                requestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description(CATEGORY_NAME_DESCRIPTION)
                )
        );
    }
}
