package com.rational.awesomeproject.service.impl;

import com.rational.awesomeproject.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.TreeMap;

@Slf4j
@Service
public class ObjectFileStorageServiceImpl implements FileStorageService {
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyyMMdd\'T\'HHmmss\'Z\'");
	private static final HttpClient httpClient = HttpClientBuilder.create().build();
	private static final Charset CHARSET_NAME = StandardCharsets.UTF_8;
	private static final String HMAC_ALGORITHM = "HmacSHA256";
	private static final String HASH_ALGORITHM = "SHA-256";
	private static final String AWS_ALGORITHM = "AWS4-HMAC-SHA256";
	private static final String SERVICE_NAME = "s3";
	private static final String REQUEST_TYPE = "aws4_request";
	private static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";
	private static final String REGION_NAME = "kr-standard";

	@Value("${storage.endpoint}")
	private String endpoint;

	@Value("${storage.access-key}")
	private String accessKey;

	@Value("${storage.secret-key}")
	private String secretKey;

	private final WebClient webClient = WebClient.builder().build();

	@Override
	public Flux<DataBuffer> download(String userId, String fileId) {
		URI storageUri = getStorageUri(userId, fileId);

		return webClient.get()
		                .uri(storageUri)
		                .accept(MediaType.APPLICATION_OCTET_STREAM)
		                .headers(httpHeaders -> setHttpHeader(HttpMethod.GET, httpHeaders, storageUri))
		                .retrieve()
		                .bodyToFlux(DataBuffer.class);
	}

	@Override
	public Mono<Boolean> upload(String userId,
	                            String fileId,
	                            String fileName,
	                            long fileSize,
	                            FilePart filePart) {
		URI storageUri = getStorageUri(userId, fileId);

		return webClient.put()
		                .uri(storageUri)
		                .contentType(MediaType.APPLICATION_OCTET_STREAM)
		                .contentLength(fileSize)
		                .headers(httpHeaders -> setHttpHeader(HttpMethod.PUT, httpHeaders, storageUri))
		                .body(BodyInserters.fromDataBuffers(filePart.content()))
		                .retrieve()
		                .bodyToMono(Void.class)
		                .thenReturn(true);
	}

	@Override
	public Mono<Boolean> delete(String userId,
	                            String fileId) {
		URI storageUri = getStorageUri(userId, fileId);

		return webClient.delete()
		                .uri(storageUri)
		                .headers(httpHeaders -> setHttpHeader(HttpMethod.DELETE, httpHeaders, storageUri))
		                .retrieve()
		                .bodyToMono(Void.class)
		                .thenReturn(true);
	}

	private void setHttpHeader(HttpMethod method, HttpHeaders httpHeaders, URI storageUri) {
		String regionName = "kr-standard";
		Date now = new Date();
		DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
		TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
		String datestamp = DATE_FORMATTER.format(now);
		String timestamp = TIME_FORMATTER.format(now);

		httpHeaders.add("Host", storageUri.getHost());
		httpHeaders.add("X-Amz-Date", timestamp);
		httpHeaders.add("X-Amz-Content-Sha256", "UNSIGNED-PAYLOAD");

		String standardizedQueryParameters = "";
		TreeMap<String, String> sortedHeaders = getSortedHeaders(httpHeaders);
		String signedHeaders = getSignedHeaders(sortedHeaders);
		String standardizedHeaders = getStandardizedHeaders(sortedHeaders);
		String canonicalRequest = getCanonicalRequest(method, storageUri, standardizedQueryParameters, standardizedHeaders, signedHeaders);
		String scope = getScope(datestamp, regionName);

		String stringToSign = getStringToSign(timestamp, scope, canonicalRequest);
		String signature = getSignature(secretKey, datestamp, regionName, stringToSign);
		String authorization = getAuthorization(accessKey, scope, signedHeaders, signature);
		httpHeaders.add("Authorization", authorization);
	}

	private byte[] sign(String stringData, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] data = stringData.getBytes(CHARSET_NAME);
		Mac e = Mac.getInstance(HMAC_ALGORITHM);
		e.init(new SecretKeySpec(key, HMAC_ALGORITHM));
		return e.doFinal(data);
	}

	private String hash(String text) {
		try {
			MessageDigest e = MessageDigest.getInstance(HASH_ALGORITHM);
			e.update(text.getBytes(CHARSET_NAME));
			return Hex.encodeHexString(e.digest());
		} catch (Exception var3) {
			//
		}
		return "";
	}

	private TreeMap<String, String> getSortedHeaders(HttpHeaders httpHeaders) {
		TreeMap<String, String> sortedHeaders = new TreeMap<>();
		for (String key : httpHeaders.keySet()) {
			sortedHeaders.put(key, httpHeaders.getFirst(key));
		}

		return sortedHeaders;
	}

	private String getSignedHeaders(TreeMap<String, String> sortedHeaders) {
		StringBuilder signedHeadersBuilder = new StringBuilder();
		for (String headerName : sortedHeaders.keySet()) {
			if (signedHeadersBuilder.length() > 0)
				signedHeadersBuilder.append(';');
			signedHeadersBuilder.append(headerName.toLowerCase());
		}
		return signedHeadersBuilder.toString();
	}

	private String getStandardizedHeaders(TreeMap<String, String> sortedHeaders) {
		StringBuilder standardizedHeadersBuilder = new StringBuilder();
		for (String headerName : sortedHeaders.keySet()) {
			standardizedHeadersBuilder.append(headerName.toLowerCase()).append(":").append(sortedHeaders.get(headerName)).append("\n");
		}

		return standardizedHeadersBuilder.toString();
	}

	private String getCanonicalRequest(HttpMethod method, URI requestUri, String standardizedQueryParameters, String standardizedHeaders, String signedHeaders) {
		return method + "\n" +
				requestUri.getPath() + "\n" +
				standardizedQueryParameters + "\n" +
				standardizedHeaders + "\n" +
				signedHeaders + "\n" +
				UNSIGNED_PAYLOAD;
	}

	private String getScope(String datestamp, String regionName) {
		return datestamp + "/" +
				regionName + "/" +
				SERVICE_NAME + "/" +
				REQUEST_TYPE;
	}

	private String getStringToSign(String timestamp, String scope, String canonicalRequest) {
		return AWS_ALGORITHM +
				"\n" +
				timestamp + "\n" +
				scope + "\n" +
				hash(canonicalRequest);
	}

	private String getSignature(String secretKey, String datestamp, String regionName, String stringToSign) {
		try {
			byte[] kSecret = ("AWS4" + secretKey).getBytes(CHARSET_NAME);
			byte[] kDate = sign(datestamp, kSecret);
			byte[] kRegion = sign(regionName, kDate);
			byte[] kService = sign(SERVICE_NAME, kRegion);
			byte[] signingKey = sign(REQUEST_TYPE, kService);

			return Hex.encodeHexString(sign(stringToSign, signingKey));
		} catch (Exception e) {
			//
		}
		return "";
	}

	private String getAuthorization(String accessKey, String scope, String signedHeaders, String signature) {
		String signingCredentials = accessKey + "/" + scope;
		String credential = "Credential=" + signingCredentials;
		String signerHeaders = "SignedHeaders=" + signedHeaders;
		String signatureHeader = "Signature=" + signature;

		return AWS_ALGORITHM + " " +
				credential + ", " +
				signerHeaders + ", " +
				signatureHeader;
	}

	private URI getStorageUri(String userId, String storageId) {
		return UriComponentsBuilder.fromHttpUrl(endpoint)
		                           .path("/{bucketName}")
		                           .path("/{userId}")
		                           .path("/{storageId}")
		                           .build("rational331", userId, storageId);
	}
}
