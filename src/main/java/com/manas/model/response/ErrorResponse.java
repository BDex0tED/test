package com.manas.model.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        ErrorDetail error
) {
}
