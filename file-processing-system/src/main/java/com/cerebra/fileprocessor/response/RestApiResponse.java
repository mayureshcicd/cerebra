package com.cerebra.fileprocessor.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class RestApiResponse<T> {

    private Object message;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty("data")
    private T data;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty("timeStamp")
    private Date timestamp;

    @JsonInclude(Include.NON_EMPTY)
    private Object errors;

}
