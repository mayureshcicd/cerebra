package com.cerebra.fileprocessor.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.cerebra.fileprocessor.common.EncryptAndDecrypt;
import com.cerebra.fileprocessor.config.ConfigProperties;
import com.cerebra.fileprocessor.logger.helper.RequestHeaderHelper;
import com.cerebra.fileprocessor.logger.helper.formatter.TimestampFormatter;
import com.cerebra.fileprocessor.logger.helper.variable.StringVariable;
import com.cerebra.fileprocessor.logger.model.Log;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogTracer {

	private final ConfigProperties configProperties;
	private ObjectMapper objectMapper;
	private String traceID;
	private String methodName;

	private String uid;
	private List<String> secureAttributes;

	@PostConstruct
	public void init() {
		if (StringUtils.isNotBlank(configProperties.getLogSecureAttribute())) {
			this.secureAttributes = Arrays.asList(configProperties.getLogSecureAttribute().split(StringVariable.COMMA.value()));
		} else {
			this.secureAttributes = new ArrayList<>();
		}
		log.info("Secure Attributes {}", this.secureAttributes);
		this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public void tracingID(String methodName) {
		this.traceID = TimestampFormatter.format(TimestampFormatter.yyyy_MM_dd_HH_mm_ss_SSSSSS,
				TimestampFormatter.currentSQLTimestamp());
		this.methodName = methodName.concat(" operation");
	}

	public void tracingID(HttpServletRequest request) {
		this.methodName = request.getMethod().concat(" operation");
		uid= getAttributeValue(request.getHeader(RequestHeaderHelper.AUTHORIZATION), "email");
		this.traceID = TimestampFormatter.format(TimestampFormatter.yyyy_MM_dd_HH_mm_ss_SSSSSS,
				TimestampFormatter.currentSQLTimestamp());
		this.methodName = methodName.concat(" operation");
	}

	public void tracingID(String methodName, String uid) {
		this.traceID = TimestampFormatter.format(TimestampFormatter.yyyy_MM_dd_HH_mm_ss_SSSSSS,
				TimestampFormatter.currentSQLTimestamp());
		this.uid = uid;
		this.methodName = methodName.concat(" operation");
	}

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || "
			+ " within(@org.springframework.stereotype.Service *) || "
			+ " within(@org.springframework.stereotype.Repository *)")
	private void appPointCut() {
	}

	@Around("appPointCut()")
	public Object applicationLogger(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		try {
			log.info("Request for {} {}", methodName,
					jsonObject(Log.builder().traceID(traceID).requestID(uid)
							.className(proceedingJoinPoint.getTarget().getClass().getSimpleName()
									.concat(StringVariable.FULL_STOP.value())
									.concat(proceedingJoinPoint.getSignature().getName()).concat("()"))
							.request(securedObject(proceedingJoinPoint.getArgs())).build()));
		} catch (Exception e) {
			log.error("Request error for {} {}", methodName,
					jsonObject(Log.builder().traceID(traceID).requestID(uid).error(e.getLocalizedMessage()).build()));
		}

		Object object = proceedingJoinPoint.proceed();
		try {
			log.info("Response for {} {}", methodName,
					jsonObject(Log.builder().traceID(traceID).requestID(uid)
							.className(proceedingJoinPoint.getTarget().getClass().getSimpleName()
									.concat(StringVariable.FULL_STOP.value())
									.concat(proceedingJoinPoint.getSignature().getName()).concat("()"))
							.response(securedObject(object)).build()));

		} catch (Exception e) {
			log.error("Response error for {} {}", methodName,
					jsonObject(Log.builder().traceID(traceID).requestID(uid)
							.className(proceedingJoinPoint.getTarget().getClass().getSimpleName()
									.concat(StringVariable.FULL_STOP.value())
									.concat(proceedingJoinPoint.getSignature().getName()).concat("()"))
							.error(e.getLocalizedMessage()).build()));
		}
		return object;
	}

	@AfterThrowing(pointcut = "within(@org.springframework.web.bind.annotation.RestController *) || "
			+ " within(@org.springframework.stereotype.Service *) || "
			+ " within(@org.springframework.stereotype.Repository *)", throwing = "exception")
	public void logError(JoinPoint joinPoint, Exception exception) throws JsonProcessingException {
		try {
			log.error("Exception for {} {}", methodName, jsonObject(Log.builder().traceID(traceID).requestID(uid)
					.className(joinPoint.getTarget().getClass().getSimpleName().concat(StringVariable.FULL_STOP.value())
							.concat(joinPoint.getSignature().getName()).concat("()"))
					.error(exception.getLocalizedMessage()).build()));
		} catch (Exception e) {
			log.error("Exception error for {} {}", methodName, jsonObject(Log.builder().traceID(traceID).requestID(uid)
					.className(joinPoint.getTarget().getClass().getSimpleName().concat(StringVariable.FULL_STOP.value())
							.concat(joinPoint.getSignature().getName()).concat("()"))
					.error(e.getLocalizedMessage()).build()));
		}
	}

	private Object jsonObject(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			return object;
		}
	}

	private Object securedObject(Object object) {
		try {
			String jsonResponse = objectMapper.writeValueAsString(object);
			JsonNode parentJsonNode = objectMapper.readTree(jsonResponse);
			if (parentJsonNode.getNodeType().toString().equalsIgnoreCase(StringVariable.ARRAY.value())) {
				for (JsonNode childJsonNode : parentJsonNode) {
					Iterator<String> iterator = childJsonNode.fieldNames();
					iterator.forEachRemaining(key -> {
						if (childJsonNode.get(key).getNodeType().toString()
								.equalsIgnoreCase(StringVariable.ARRAY.value())) {
							readKeys(childJsonNode, childJsonNode.get(key));
						} else {
							securedKey(childJsonNode, key);
						}
					});
				}
			} else {
				Iterator<String> iterator = parentJsonNode.fieldNames();
				iterator.forEachRemaining(key -> {
					if (parentJsonNode.get(key).getNodeType().toString()
							.equalsIgnoreCase(StringVariable.ARRAY.value())) {
						readKeys(parentJsonNode, parentJsonNode.get(key));
					} else {
						securedKey(parentJsonNode, key);
					}
				});
			}
			return parentJsonNode;
		} catch (Exception e) {
			return object;
		}
	}

	public void securedKey(JsonNode parentJsonNode, String key) {
		if (secureAttributes.contains(key))
			((ObjectNode) parentJsonNode).put(key, "*********");
	}

	public void readKeys(JsonNode parentJsonNode, JsonNode childJsonNode) {
		for (JsonNode jsonNode : childJsonNode) {
			Iterator<String> iterator = jsonNode.fieldNames();
			iterator.forEachRemaining(key -> {
				if (jsonNode.get(key).getNodeType().toString().equalsIgnoreCase(StringVariable.ARRAY.value())) {
					readKeys(parentJsonNode, jsonNode.get(key));
				} else {
					securedKey(jsonNode, key);
				}
			});
		}
	}

	public String getAttributeValue(String token, String attribute) {
		String jwt = token.substring(7);
		Claims claims = extractAllClaims(jwt);
		return EncryptAndDecrypt.decrypt(claims.get(attribute, String.class));
	}
	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(configProperties.getTokenSecretKey());
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
