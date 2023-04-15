package com.demo.pet.model.helper;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Logger requestLogger = LoggerFactory.getLogger(getClass());
	protected Logger responseLogger = LoggerFactory.getLogger(getClass());

	private volatile boolean loggedMissingBuffering;

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		Instant startTime = Instant.now();
		String traceId = null;
		try {
			traceId = (String) RequestContextHolder.currentRequestAttributes().getAttribute("traceId",
					RequestAttributes.SCOPE_REQUEST);
		} catch (IllegalStateException e) {
			// this case mostly for queue consumer
			// since it is triggered not by API call, there is no requestcontext or
			// httprequest created before
			// then method RequestContextHolder.currentRequestAttributes will throw
			// IllegalStateException
			traceId = generateTokenUUID();
		}
		logRequest(request, body, traceId);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(request, response, Duration.between(startTime, Instant.now()).toMillis(), traceId);
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body, String traceId) {
//		if (requestLogger.isDebugEnabled()) {
			String uri = request.getURI().toString();

			StringBuilder builder = new StringBuilder("Sending ").append(request.getMethod()).append(" request to ")
					.append(uri).append(" traceId=").append(traceId);

			if (body.length > 0 && hasTextBody(request.getHeaders())) {
				String bodyText = new String(body, determineCharset(request.getHeaders()));
				builder.append(": [").append(bodyText).append("]");
			}
			requestLogger.debug(builder.toString());
//		}
	}

	private void logResponse(HttpRequest request, ClientHttpResponse response, long duration, String traceId) {
//		if (responseLogger.isDebugEnabled()) {
			try {
				String uri = request.getURI().toString();

				StringBuilder builder = new StringBuilder("Received \"").append(response.getRawStatusCode()).append(" ")
						.append(response.getStatusText()).append("\" response for ").append(request.getMethod())
						.append(" request to ").append(uri);
				HttpHeaders responseHeaders = response.getHeaders();
//                http://localhost:9200/reflex/handler?adn=3636&msisdn=6281213649999&msg=fsdMkiovidssone+6281348512029+703+123456&callback_url=httpget:http://localhost:9100/package-activation/result?trx_id_digipos=DGP265
				long contentLength = responseHeaders.getContentLength();
				if (contentLength != 0) {
					String bodyText = org.springframework.util.StreamUtils.copyToString(response.getBody(),
							determineCharset(responseHeaders));
					builder.append(": [").append(bodyText).append("]");

					builder.append(" with content of length ").append(contentLength);
					MediaType contentType = responseHeaders.getContentType();
					if (contentType != null) {
						builder.append(" and content type ").append(contentType);
					} else {
						builder.append(" and unknown content type");
					}
				}
				builder.append(",").append("timeTaken=").append(duration).append(",").append("traceId=")
						.append(traceId);

				responseLogger.debug(builder.toString());
			} catch (IOException e) {
				responseLogger.warn("Failed to log response for {} request to {}", request.getMethod(),
						request.getURI(), e);
			}
//		}
	}

	private String getMaskedUri(URI uri) {
		String url = uri.toString();
		if (url != null) {
			UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUri(uri);
			Map<String, String> query = getQueryMap(uri.getQuery());
			String msg = query.get("msg");

			if (msg != null) {
				String[] arr = msg.split("\\+|%2B");
				if (arr.length >= 3) {
					arr[3] = "***";
					msg = String.join("+", arr);
					url = urlBuilder.replaceQueryParam("msg", msg).build().toUriString();
				}
			}
		}

		return url;
	}

	private static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<>();

		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}

	private boolean hasTextBody(HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		if (contentType != null) {
			String subtype = contentType.getSubtype();
			return "text".equals(contentType.getType()) || "xml".equals(subtype) || "json".equals(subtype);
		}
		return false;
	}

	private Charset determineCharset(HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		if (contentType != null) {
			try {
				Charset charSet = contentType.getCharset();
				if (charSet != null) {
					return charSet;
				}
			} catch (UnsupportedCharsetException e) {
				logger.warn("cannot determine response charset");
			}
		}
		return StandardCharsets.UTF_8;
	}

	protected boolean isBuffered(ClientHttpResponse response) {
		// class is non-public, so we check by name
		boolean buffered = response instanceof BufferingClientHttpRequestFactory;
		if (!buffered && !loggedMissingBuffering) {
			logger.warn(
					"Can't log HTTP response bodies, as you haven't configured the RestTemplate with a BufferingClientHttpRequestFactory");
			loggedMissingBuffering = true;
		}
		return buffered;
	}

	public static String generateTokenUUID() {
		try {
			MessageDigest salt = MessageDigest.getInstance("SHA-256");
			salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
			String token = bytesToHex(salt.digest());
			return token;
		} catch (NoSuchAlgorithmException var3) {
			log.error("[GenerateUUID]", var3);
			return "";
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		byte[] var2 = bytes;
		int var3 = bytes.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			byte b = var2[var4];
			builder.append(String.format("%02x", b));
		}

		return builder.toString();
	}

}