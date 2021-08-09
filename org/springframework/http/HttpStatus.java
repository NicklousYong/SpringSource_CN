/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http;

/**
 * Enumeration of HTTP status codes.
 *
 * <p>The HTTP status code series can be retrieved via {@link #series()}.
 *
 * <p>
 *  枚举HTTP状态码
 * 
 *  <p>可以通过{@link #series()}检索HTTP状态代码系列
 * 
 * 
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 * @author Brian Clozel
 * @since 3.0
 * @see HttpStatus.Series
 * @see <a href="http://www.iana.org/assignments/http-status-codes">HTTP Status Code Registry</a>
 * @see <a href="http://en.wikipedia.org/wiki/List_of_HTTP_status_codes">List of HTTP status codes - Wikipedia</a>
 */
public enum HttpStatus {

	// 1xx Informational

	/**
	 * {@code 100 Continue}.
	 * <p>
	 *  {@code 100 Continue}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.2.1">HTTP/1.1: Semantics and Content, section 6.2.1</a>
	 */
	CONTINUE(100, "Continue"),
	/**
	 * {@code 101 Switching Protocols}.
	 * <p>
	 *  {@code 101交换协议}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.2.2">HTTP/1.1: Semantics and Content, section 6.2.2</a>
	 */
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	/**
	 * {@code 102 Processing}.
	 * <p>
	 *  {@code 102处理}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2518#section-10.1">WebDAV</a>
	 */
	PROCESSING(102, "Processing"),
	/**
	 * {@code 103 Checkpoint}.
	 * <p>
	 *  {@code 103 Checkpoint}
	 * 
	 * 
	 * @see <a href="http://code.google.com/p/gears/wiki/ResumableHttpRequestsProposal">A proposal for supporting
	 * resumable POST/PUT HTTP requests in HTTP/1.0</a>
	 */
	CHECKPOINT(103, "Checkpoint"),

	// 2xx Success

	/**
	 * {@code 200 OK}.
	 * <p>
	 * {@code 200 OK}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.3.1">HTTP/1.1: Semantics and Content, section 6.3.1</a>
	 */
	OK(200, "OK"),
	/**
	 * {@code 201 Created}.
	 * <p>
	 *  {@code 201 Created}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.3.2">HTTP/1.1: Semantics and Content, section 6.3.2</a>
	 */
	CREATED(201, "Created"),
	/**
	 * {@code 202 Accepted}.
	 * <p>
	 *  {@code 202 Accepted}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.3.3">HTTP/1.1: Semantics and Content, section 6.3.3</a>
	 */
	ACCEPTED(202, "Accepted"),
	/**
	 * {@code 203 Non-Authoritative Information}.
	 * <p>
	 *  {@code 203非授权信息}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.3.4">HTTP/1.1: Semantics and Content, section 6.3.4</a>
	 */
	NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
	/**
	 * {@code 204 No Content}.
	 * <p>
	 *  {@code 204无内容}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.3.5">HTTP/1.1: Semantics and Content, section 6.3.5</a>
	 */
	NO_CONTENT(204, "No Content"),
	/**
	 * {@code 205 Reset Content}.
	 * <p>
	 *  {@code 205重置内容}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.3.6">HTTP/1.1: Semantics and Content, section 6.3.6</a>
	 */
	RESET_CONTENT(205, "Reset Content"),
	/**
	 * {@code 206 Partial Content}.
	 * <p>
	 *  {@code 206 Partial Content}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7233#section-4.1">HTTP/1.1: Range Requests, section 4.1</a>
	 */
	PARTIAL_CONTENT(206, "Partial Content"),
	/**
	 * {@code 207 Multi-Status}.
	 * <p>
	 *  {@code 207多状态}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc4918#section-13">WebDAV</a>
	 */
	MULTI_STATUS(207, "Multi-Status"),
	/**
	 * {@code 208 Already Reported}.
	 * <p>
	 *  {@code 208已报告}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.1">WebDAV Binding Extensions</a>
	 */
	ALREADY_REPORTED(208, "Already Reported"),
	/**
	 * {@code 226 IM Used}.
	 * <p>
	 *  {@code 226 IM Used}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc3229#section-10.4.1">Delta encoding in HTTP</a>
	 */
	IM_USED(226, "IM Used"),

	// 3xx Redirection

	/**
	 * {@code 300 Multiple Choices}.
	 * <p>
	 *  {@code 300多项选择}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.1">HTTP/1.1: Semantics and Content, section 6.4.1</a>
	 */
	MULTIPLE_CHOICES(300, "Multiple Choices"),
	/**
	 * {@code 301 Moved Permanently}.
	 * <p>
	 *  {@code 301永久移动}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.2">HTTP/1.1: Semantics and Content, section 6.4.2</a>
	 */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	/**
	 * {@code 302 Found}.
	 * <p>
	 *  {@code 302 Found}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.3">HTTP/1.1: Semantics and Content, section 6.4.3</a>
	 */
	FOUND(302, "Found"),
	/**
	 * {@code 302 Moved Temporarily}.
	 * <p>
	 *  {@code 302暂时移动}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc1945#section-9.3">HTTP/1.0, section 9.3</a>
	 * @deprecated In favor of {@link #FOUND} which will be returned from {@code HttpStatus.valueOf(302)}
	 */
	@Deprecated
	MOVED_TEMPORARILY(302, "Moved Temporarily"),
	/**
	 * {@code 303 See Other}.
	 * <p>
	 *  {@code 303查看其他}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.4">HTTP/1.1: Semantics and Content, section 6.4.4</a>
	 */
	SEE_OTHER(303, "See Other"),
	/**
	 * {@code 304 Not Modified}.
	 * <p>
	 *  {@code 304未修改}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-4.1">HTTP/1.1: Conditional Requests, section 4.1</a>
	 */
	NOT_MODIFIED(304, "Not Modified"),
	/**
	 * {@code 305 Use Proxy}.
	 * <p>
	 *  {@code 305使用代理}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.5">HTTP/1.1: Semantics and Content, section 6.4.5</a>
	 * @deprecated due to security concerns regarding in-band configuration of a proxy
	 */
	@Deprecated
	USE_PROXY(305, "Use Proxy"),
	/**
	 * {@code 307 Temporary Redirect}.
	 * <p>
	 *  {@code 307临时重定向}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.4.7">HTTP/1.1: Semantics and Content, section 6.4.7</a>
	 */
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	/**
	 * {@code 308 Permanent Redirect}.
	 * <p>
	 *  {@code 308永久重定向}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7238">RFC 7238</a>
	 */
	PERMANENT_REDIRECT(308, "Permanent Redirect"),

	// --- 4xx Client Error ---

	/**
	 * {@code 400 Bad Request}.
	 * <p>
	 *  {@code 400 Bad Request}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.1">HTTP/1.1: Semantics and Content, section 6.5.1</a>
	 */
	BAD_REQUEST(400, "Bad Request"),
	/**
	 * {@code 401 Unauthorized}.
	 * <p>
	 *  {@code 401未经授权}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7235#section-3.1">HTTP/1.1: Authentication, section 3.1</a>
	 */
	UNAUTHORIZED(401, "Unauthorized"),
	/**
	 * {@code 402 Payment Required}.
	 * <p>
	 *  {@code 402付款必需}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.2">HTTP/1.1: Semantics and Content, section 6.5.2</a>
	 */
	PAYMENT_REQUIRED(402, "Payment Required"),
	/**
	 * {@code 403 Forbidden}.
	 * <p>
	 *  {@code 403 Forbidden}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.3">HTTP/1.1: Semantics and Content, section 6.5.3</a>
	 */
	FORBIDDEN(403, "Forbidden"),
	/**
	 * {@code 404 Not Found}.
	 * <p>
	 *  {@code 404 Not Found}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.4">HTTP/1.1: Semantics and Content, section 6.5.4</a>
	 */
	NOT_FOUND(404, "Not Found"),
	/**
	 * {@code 405 Method Not Allowed}.
	 * <p>
	 *  {@code 405方法不允许}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.5">HTTP/1.1: Semantics and Content, section 6.5.5</a>
	 */
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	/**
	 * {@code 406 Not Acceptable}.
	 * <p>
	 *  {@code 406不可接受}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.6">HTTP/1.1: Semantics and Content, section 6.5.6</a>
	 */
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	/**
	 * {@code 407 Proxy Authentication Required}.
	 * <p>
	 *  {@code 407 Proxy Authentication Required}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7235#section-3.2">HTTP/1.1: Authentication, section 3.2</a>
	 */
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
	/**
	 * {@code 408 Request Timeout}.
	 * <p>
	 *  {@code 408 Request Timeout}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.7">HTTP/1.1: Semantics and Content, section 6.5.7</a>
	 */
	REQUEST_TIMEOUT(408, "Request Timeout"),
	/**
	 * {@code 409 Conflict}.
	 * <p>
	 * {@code 409 Conflict}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.8">HTTP/1.1: Semantics and Content, section 6.5.8</a>
	 */
	CONFLICT(409, "Conflict"),
	/**
	 * {@code 410 Gone}.
	 * <p>
	 *  {@code 410 Gone}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.9">HTTP/1.1: Semantics and Content, section 6.5.9</a>
	 */
	GONE(410, "Gone"),
	/**
	 * {@code 411 Length Required}.
	 * <p>
	 *  {@code 411需要长度}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.10">HTTP/1.1: Semantics and Content, section 6.5.10</a>
	 */
	LENGTH_REQUIRED(411, "Length Required"),
	/**
	 * {@code 412 Precondition failed}.
	 * <p>
	 *  {@code 412前提条件失败}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7232#section-4.2">HTTP/1.1: Conditional Requests, section 4.2</a>
	 */
	PRECONDITION_FAILED(412, "Precondition Failed"),
	/**
	 * {@code 413 Payload Too Large}.
	 * <p>
	 *  {@code 413有效负载太大}
	 * 
	 * 
	 * @since 4.1
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.11">HTTP/1.1: Semantics and Content, section 6.5.11</a>
	 */
	PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
	/**
	 * {@code 413 Request Entity Too Large}.
	 * <p>
	 *  {@code 413请求实体太大}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.14">HTTP/1.1, section 10.4.14</a>
	 * @deprecated In favor of {@link #PAYLOAD_TOO_LARGE} which will be returned from {@code HttpStatus.valueOf(413)}
	 */
	@Deprecated
	REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
	/**
	 * {@code 414 URI Too Long}.
	 * <p>
	 *  {@code 414 URI太长}
	 * 
	 * 
	 * @since 4.1
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.12">HTTP/1.1: Semantics and Content, section 6.5.12</a>
	 */
	URI_TOO_LONG(414, "URI Too Long"),
	/**
	 * {@code 414 Request-URI Too Long}.
	 * <p>
	 *  {@code 414 Request-URI太长}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.15">HTTP/1.1, section 10.4.15</a>
	 * @deprecated In favor of {@link #URI_TOO_LONG} which will be returned from {@code HttpStatus.valueOf(414)}
	 */
	@Deprecated
	REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
	/**
	 * {@code 415 Unsupported Media Type}.
	 * <p>
	 *  {@code 415不支持的媒体类型}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.13">HTTP/1.1: Semantics and Content, section 6.5.13</a>
	 */
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	/**
	 * {@code 416 Requested Range Not Satisfiable}.
	 * <p>
	 *  {@code 416请求的范围不满意}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7233#section-4.4">HTTP/1.1: Range Requests, section 4.4</a>
	 */
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"),
	/**
	 * {@code 417 Expectation Failed}.
	 * <p>
	 *  {@code 417期望失败}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.5.14">HTTP/1.1: Semantics and Content, section 6.5.14</a>
	 */
	EXPECTATION_FAILED(417, "Expectation Failed"),
	/**
	 * {@code 418 I'm a teapot}.
	 * <p>
	 *  {@code 418我是茶壶}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2324#section-2.3.2">HTCPCP/1.0</a>
	 */
	I_AM_A_TEAPOT(418, "I'm a teapot"),
	/**
	/* <p>
	/* 
	 * @deprecated See <a href="http://tools.ietf.org/rfcdiff?difftype=--hwdiff&url2=draft-ietf-webdav-protocol-06.txt">WebDAV Draft Changes</a>
	 */
	@Deprecated
	INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource"),
	/**
	/* <p>
	/* 
	 * @deprecated See <a href="http://tools.ietf.org/rfcdiff?difftype=--hwdiff&url2=draft-ietf-webdav-protocol-06.txt">WebDAV Draft Changes</a>
	 */
	@Deprecated
	METHOD_FAILURE(420, "Method Failure"),
	/**
	/* <p>
	/* 
	 * @deprecated See <a href="http://tools.ietf.org/rfcdiff?difftype=--hwdiff&url2=draft-ietf-webdav-protocol-06.txt">WebDAV Draft Changes</a>
	 */
	@Deprecated
	DESTINATION_LOCKED(421, "Destination Locked"),
	/**
	 * {@code 422 Unprocessable Entity}.
	 * <p>
	 *  {@code 422 Unprocessible Entity}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.2">WebDAV</a>
	 */
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
	/**
	 * {@code 423 Locked}.
	 * <p>
	 *  {@code 423 Locked}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.3">WebDAV</a>
	 */
	LOCKED(423, "Locked"),
	/**
	 * {@code 424 Failed Dependency}.
	 * <p>
	 *  {@code 424失败的依赖关系}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.4">WebDAV</a>
	 */
	FAILED_DEPENDENCY(424, "Failed Dependency"),
	/**
	 * {@code 426 Upgrade Required}.
	 * <p>
	 *  {@code 426需要升级}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2817#section-6">Upgrading to TLS Within HTTP/1.1</a>
	 */
	UPGRADE_REQUIRED(426, "Upgrade Required"),
	/**
	 * {@code 428 Precondition Required}.
	 * <p>
	 *  {@code 428必需前提条件}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-3">Additional HTTP Status Codes</a>
	 */
	PRECONDITION_REQUIRED(428, "Precondition Required"),
	/**
	 * {@code 429 Too Many Requests}.
	 * <p>
	 *  {@code 429太多请求}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-4">Additional HTTP Status Codes</a>
	 */
	TOO_MANY_REQUESTS(429, "Too Many Requests"),
	/**
	 * {@code 431 Request Header Fields Too Large}.
	 * <p>
	 *  {@code 431请求标题字段太大}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-5">Additional HTTP Status Codes</a>
	 */
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
	/**
	 * {@code 451 Unavailable For Legal Reasons}.
	 * <p>
	 *  {@code 451不适用于法定理由}
	 * 
	 * 
	 * @see <a href="https://tools.ietf.org/html/draft-ietf-httpbis-legally-restricted-status-04">
	 * An HTTP Status Code to Report Legal Obstacles</a>
	 * @since 4.3
	 */
	UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

	// --- 5xx Server Error ---

	/**
	 * {@code 500 Internal Server Error}.
	 * <p>
	 *  {@code 500 Internal Server Error}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.6.1">HTTP/1.1: Semantics and Content, section 6.6.1</a>
	 */
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	/**
	 * {@code 501 Not Implemented}.
	 * <p>
	 *  {@code 501未实施}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.6.2">HTTP/1.1: Semantics and Content, section 6.6.2</a>
	 */
	NOT_IMPLEMENTED(501, "Not Implemented"),
	/**
	 * {@code 502 Bad Gateway}.
	 * <p>
	 *  {@code 502 Bad Gateway}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.6.3">HTTP/1.1: Semantics and Content, section 6.6.3</a>
	 */
	BAD_GATEWAY(502, "Bad Gateway"),
	/**
	 * {@code 503 Service Unavailable}.
	 * <p>
	 *  {@code 503服务不可用}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.6.4">HTTP/1.1: Semantics and Content, section 6.6.4</a>
	 */
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	/**
	 * {@code 504 Gateway Timeout}.
	 * <p>
	 * {@code 504网关超时}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.6.5">HTTP/1.1: Semantics and Content, section 6.6.5</a>
	 */
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	/**
	 * {@code 505 HTTP Version Not Supported}.
	 * <p>
	 *  {@code 505 HTTP版本不支持}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-6.6.6">HTTP/1.1: Semantics and Content, section 6.6.6</a>
	 */
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported"),
	/**
	 * {@code 506 Variant Also Negotiates}
	 * <p>
	 *  {@code 506 Variant also Negotiates}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2295#section-8.1">Transparent Content Negotiation</a>
	 */
	VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
	/**
	 * {@code 507 Insufficient Storage}
	 * <p>
	 *  {@code 507存储不足}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.5">WebDAV</a>
	 */
	INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
	/**
	 * {@code 508 Loop Detected}
	 * <p>
	 *  {@code 508 Loop Detected}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc5842#section-7.2">WebDAV Binding Extensions</a>
 	 */
	LOOP_DETECTED(508, "Loop Detected"),
	/**
	 * {@code 509 Bandwidth Limit Exceeded}
	 * <p>
	 *  {@code 509超出带宽限制}
	 * 
 	 */
	BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),
	/**
	 * {@code 510 Not Extended}
	 * <p>
	 *  {@code 510 Not Extended}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc2774#section-7">HTTP Extension Framework</a>
	 */
	NOT_EXTENDED(510, "Not Extended"),
	/**
	 * {@code 511 Network Authentication Required}.
	 * <p>
	 *  {@code 511需要网络验证}
	 * 
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc6585#section-6">Additional HTTP Status Codes</a>
	 */
	NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");


	private final int value;

	private final String reasonPhrase;


	HttpStatus(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}


	/**
	 * Return the integer value of this status code.
	 * <p>
	 *  返回此状态码的整数值
	 * 
	 */
	public int value() {
		return this.value;
	}

	/**
	 * Return the reason phrase of this status code.
	 * <p>
	 *  返回此状态代码的原因短语
	 * 
	 */
	public String getReasonPhrase() {
		return this.reasonPhrase;
	}

	/**
	 * Whether this status code is in the HTTP series
	 * {@link org.springframework.http.HttpStatus.Series#INFORMATIONAL}.
	 * This is a shortcut for checking the value of {@link #series()}.
	 * <p>
	 *  这个状态代码是否在HTTP系列中{@link orgspringframeworkhttpHttpStatusSeries#INFORMATIONAL}这是检查{@link #series()}的值的
	 * 快捷方式。
	 * 
	 */
	public boolean is1xxInformational() {
		return Series.INFORMATIONAL.equals(series());
	}

	/**
	 * Whether this status code is in the HTTP series
	 * {@link org.springframework.http.HttpStatus.Series#SUCCESSFUL}.
	 * This is a shortcut for checking the value of {@link #series()}.
	 * <p>
	 *  这个状态代码是否在HTTP系列中{@link orgspringframeworkhttpHttpStatusSeries#SUCCESSFUL}这是检查{@link #series()}的值的快捷方
	 * 式。
	 * 
	 */
	public boolean is2xxSuccessful() {
		return Series.SUCCESSFUL.equals(series());
	}

	/**
	 * Whether this status code is in the HTTP series
	 * {@link org.springframework.http.HttpStatus.Series#REDIRECTION}.
	 * This is a shortcut for checking the value of {@link #series()}.
	 * <p>
	 * 这个状态代码是否在HTTP系列中{@link orgspringframeworkhttpHttpStatusSeries#REDIRECTION}这是检查{@link #series()}的值的快捷方
	 * 式。
	 * 
	 */
	public boolean is3xxRedirection() {
		return Series.REDIRECTION.equals(series());
	}


	/**
	 * Whether this status code is in the HTTP series
	 * {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR}.
	 * This is a shortcut for checking the value of {@link #series()}.
	 * <p>
	 *  这个状态代码是否在HTTP系列中{@link orgspringframeworkhttpHttpStatusSeries#CLIENT_ERROR}这是检查{@link #series()}的值的快
	 * 捷方式。
	 * 
	 */
	public boolean is4xxClientError() {
		return Series.CLIENT_ERROR.equals(series());
	}

	/**
	 * Whether this status code is in the HTTP series
	 * {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR}.
	 * This is a shortcut for checking the value of {@link #series()}.
	 * <p>
	 *  这个状态代码是否在HTTP系列中{@link orgspringframeworkhttpHttpStatusSeries#SERVER_ERROR}这是检查{@link #series()}的值的快
	 * 捷方式。
	 * 
	 */
	public boolean is5xxServerError() {
		return Series.SERVER_ERROR.equals(series());
	}

	/**
	 * Returns the HTTP status series of this status code.
	 * <p>
	 *  返回此状态代码的HTTP状态系列
	 * 
	 * 
	 * @see HttpStatus.Series
	 */
	public Series series() {
		return Series.valueOf(this);
	}

	/**
	 * Return a string representation of this status code.
	 * <p>
	 *  返回此状态代码的字符串表示形式
	 * 
	 */
	@Override
	public String toString() {
		return Integer.toString(this.value);
	}


	/**
	 * Return the enum constant of this type with the specified numeric value.
	 * <p>
	 *  使用指定的数值返回此类型的枚举常数
	 * 
	 * 
	 * @param statusCode the numeric value of the enum to be returned
	 * @return the enum constant with the specified numeric value
	 * @throws IllegalArgumentException if this enum has no constant for the specified numeric value
	 */
	public static HttpStatus valueOf(int statusCode) {
		for (HttpStatus status : values()) {
			if (status.value == statusCode) {
				return status;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
	}


	/**
	 * Enumeration of HTTP status series.
	 * <p>Retrievable via {@link HttpStatus#series()}.
	 * <p>
	 *  HTTP状态系列的枚举<p>可通过{@link HttpStatus#series()}检索
	 * 
	 */
	public enum Series {

		INFORMATIONAL(1),
		SUCCESSFUL(2),
		REDIRECTION(3),
		CLIENT_ERROR(4),
		SERVER_ERROR(5);

		private final int value;

		Series(int value) {
			this.value = value;
		}

		/**
		 * Return the integer value of this status series. Ranges from 1 to 5.
		 * <p>
		 * 将此状态系列范围的整数值从1返回到5
		 */
		public int value() {
			return this.value;
		}

		public static Series valueOf(int status) {
			int seriesCode = status / 100;
			for (Series series : values()) {
				if (series.value == seriesCode) {
					return series;
				}
			}
			throw new IllegalArgumentException("No matching constant for [" + status + "]");
		}

		public static Series valueOf(HttpStatus status) {
			return valueOf(status.value);
		}
	}

}
