package org.example.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Set;

/**
 * Generic base response class for REST with builder.
 *
 * @param <T> any type response in {@link #data} field <u>except stream<u/>.
 */
@Builder
@Getter
public class BaseResponse<T> {
    @Nullable
    private T data;
    @Nullable
    private Instant timestamp;
    @Nullable
    private String message;
    @Nullable
    private Set<String> errors;
    @Nullable
    private String errorType;
}
