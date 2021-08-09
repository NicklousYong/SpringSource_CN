/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

/**
 * Default implementation of the {@link ResponseErrorHandler} interface.
 *
 * <p>This error handler checks for the status code on the {@link ClientHttpResponse}: any
 * code with series {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR} or
 * {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR} is considered to be an
 * error. This behavior can be changed by overriding the {@link #hasError(HttpStatus)}
 * method.
 *
 * <p>
 *  默认实现{@link ResponseErrorHandler}界面
 * 
 * <p>此错误处理程序检查{@link ClientHttpResponse}上的状态代码：系列为{@link orgspringframeworkhttpHttpStatusSeries#CLIENT_ERROR}
 * 或{@link orgspringframeworkhttpHttpStatusSeries#SERVER_ERROR}的任何代码都被视为错误此行为可以通过覆盖{@link #hasError(HttpStatus)}
 * 方法。
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.0
 * @see RestTemplate#setErrorHandler
 */
public class DefaultResponseErrorHandler implements ResponseErrorHandler {

	/**
	 * Delegates to {@link #hasError(HttpStatus)} with the response status code.
	 * <p>
	 *  代表使用响应状态代码{@link #hasError(HttpStatus)}
	 * 
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return hasError(getHttpStatusCode(response));
	}

	private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode;
		try {
			statusCode = response.getStatusCode();
		}
		catch (IllegalArgumentException ex) {
			throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
					response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
		}
		return statusCode;
	}

	/**
	 * Template method called from {@link #hasError(ClientHttpResponse)}.
	 * <p>The default implementation checks if the given status code is
	 * {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR}
	 * or {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR SERVER_ERROR}.
	 * Can be overridden in subclasses.
	 * <p>
	 *  从{@link #hasError(ClientHttpResponse))调用的模板方法} <p>默认实现检查给定的状态代码是否为{@link orgspringframeworkhttpHttpStatusSeries#CLIENT_ERROR CLIENT_ERROR}
	 * 或{@link orgspringframeworkhttpHttpStatusSeries#SERVER_ERROR SERVER_ERROR}可以在子类中覆盖。
	 * 
	 * 
	 * @param statusCode the HTTP status code
	 * @return {@code true} if the response has an error; {@code false} otherwise
	 */
	protected boolean hasError(HttpStatus statusCode) {
		return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
				statusCode.series() == HttpStatus.Series.SERVER_ERROR);
	}

	/**
	 * This default implementation throws a {@link HttpClientErrorException} if the response status code
	 * is {@link org.springframework.http.HttpStatus.Series#CLIENT_ERROR}, a {@link HttpServerErrorException}
	 * if it is {@link org.springframework.http.HttpStatus.Series#SERVER_ERROR},
	 * and a {@link RestClientException} in other cases.
	 * <p>
	 * 如果响应状态代码是{@link orgspringframeworkhttpHttpStatusSeries#CLIENT_ERROR},{@link HttpServerErrorException}
	 * (如果它是{@link orgspringframeworkhttpHttpStatusSeries#SERVER_ERROR})和其他的{@link RestClientException},则此默认
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = getHttpStatusCode(response);
		switch (statusCode.series()) {
			case CLIENT_ERROR:
				throw new HttpClientErrorException(statusCode, response.getStatusText(),
						response.getHeaders(), getResponseBody(response), getCharset(response));
			case SERVER_ERROR:
				throw new HttpServerErrorException(statusCode, response.getStatusText(),
						response.getHeaders(), getResponseBody(response), getCharset(response));
			default:
				throw new RestClientException("Unknown status code [" + statusCode + "]");
		}
	}

	private byte[] getResponseBody(ClientHttpResponse response) {
		try {
			InputStream responseBody = response.getBody();
			if (responseBody != null) {
				return FileCopyUtils.copyToByteArray(responseBody);
			}
		}
		catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}

	private Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return contentType != null ? contentType.getCharset() : null;
	}

}
