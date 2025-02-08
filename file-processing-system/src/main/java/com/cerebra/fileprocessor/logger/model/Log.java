package com.cerebra.fileprocessor.logger.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Log {

	@JsonProperty("trace_id")
	private String traceID;

	@JsonProperty("request_id")
	private String requestID;

	@JsonProperty("class_name")
	private String className;

	@JsonProperty("request")
	private Object request;

	@JsonProperty("response")
	private Object response;

	@JsonProperty("error")
	private Object error;
}
