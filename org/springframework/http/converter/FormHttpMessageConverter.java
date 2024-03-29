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

package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeUtility;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link HttpMessageConverter} to read and write 'normal' HTML
 * forms and also to write (but not read) multipart data (e.g. file uploads).
 *
 * <p>In other words, this converter can read and write the
 * {@code "application/x-www-form-urlencoded"} media type as
 * {@link MultiValueMap MultiValueMap&lt;String, String&gt;} and it can also
 * write (but not read) the {@code "multipart/form-data"} media type as
 * {@link MultiValueMap MultiValueMap&lt;String, Object&gt;}.
 *
 * <p>When writing multipart data, this converter uses other
 * {@link HttpMessageConverter HttpMessageConverters} to write the respective
 * MIME parts. By default, basic converters are registered (for {@code Strings}
 * and {@code Resources}). These can be overridden through the
 * {@link #setPartConverters partConverters} property.
 *
 * <p>For example, the following snippet shows how to submit an HTML form:
 * <pre class="code">
 * RestTemplate template = new RestTemplate();  // FormHttpMessageConverter is configured by default
 * MultiValueMap&lt;String, String&gt; form = new LinkedMultiValueMap&lt;String, String&gt;();
 * form.add("field 1", "value 1");
 * form.add("field 2", "value 2");
 * form.add("field 2", "value 3");
 * template.postForLocation("http://example.com/myForm", form);
 * </pre>
 *
 * <p>The following snippet shows how to do a file upload:
 * <pre class="code">
 * MultiValueMap&lt;String, Object&gt; parts = new LinkedMultiValueMap&lt;String, Object&gt;();
 * parts.add("field 1", "value 1");
 * parts.add("file", new ClassPathResource("myFile.jpg"));
 * template.postForLocation("http://example.com/myFileUpload", parts);
 * </pre>
 *
 * <p>Some methods in this class were inspired by
 * {@code org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity}.
 *
 * <p>
 *  实现{@link HttpMessageConverter}来读取和写入"正常"HTML表单以及写(但不读)多部分数据(例如文件上传)
 * 
 * 换句话说,该转换器可以将{@code"application / x-www-form-urlencoded"}媒体类型读取并写入{@link MultiValueMap MultiValueMap&lt; String,String&gt;}
 * ,并且它也可以写(但是不读取){@code"multipart / form-data"}媒体类型为{@link MultiValueMap MultiValueMap&lt; String,Object&gt;}
 * 。
 * 
 *  <p>在编写多部分数据时,此转换器使用其他{@link HttpMessageConverter HttpMessageConverters}来编写各自的MIME部分默认情况下,注册基本转换器(对于{@code Strings}
 * 和{@code Resources})可以通过{@link #setPartConverters partConverters}属性。
 * 
 *  <p>例如,以下代码段显示了如何提交HTML表单：
 * <pre class="code">
 * RestTemplate template = new RestTemplate(); // FormHttpMessageConverter默认配置MultiValueMap&lt; String,S
 * tring&gt; form = new LinkedMultiValueMap&lt; String,String&gt;(); formadd("field 1","value 1"); forma
 * dd("field 2","value 2"); formadd("field 2","value 3"); templatepostForLocation("http：// examplecom / 
 * myForm",form);。
 * </pre>
 * 
 *  <p>以下代码段显示了如何进行文件上传：
 * <pre class="code">
 *  MultiValueMap&lt; String,Object&gt; parts = new LinkedMultiValueMap&lt; String,Object&gt;(); partsad
 * d("field 1","value 1"); partsadd("file",新的ClassPathResource("myFilejpg")); templatepostForLocation("h
 * ttp：// examplecom / myFileUpload",parts);。
 * </pre>
 * 
 *  <p>此类中的某些方法受{@code orgapachecommonshttpclientmethodsmultipartMultipartRequestEntity}的启发
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.0
 * @see MultiValueMap
 */
public class FormHttpMessageConverter implements HttpMessageConverter<MultiValueMap<String, ?>> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


	private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();

	private List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();

	private Charset charset = DEFAULT_CHARSET;

	private Charset multipartCharset;


	public FormHttpMessageConverter() {
		this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);

		this.partConverters.add(new ByteArrayHttpMessageConverter());
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		this.partConverters.add(stringHttpMessageConverter);
		this.partConverters.add(new ResourceHttpMessageConverter());

		applyDefaultCharset();
	}


	/**
	 * Set the list of {@link MediaType} objects supported by this converter.
	 * <p>
	 * 设置此转换器支持的{@link MediaType}对象列表
	 * 
	 */
	public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
		this.supportedMediaTypes = supportedMediaTypes;
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Collections.unmodifiableList(this.supportedMediaTypes);
	}

	/**
	 * Set the message body converters to use. These converters are used to
	 * convert objects to MIME parts.
	 * <p>
	 *  设置消息体转换器使用这些转换器用于将对象转换为MIME部分
	 * 
	 */
	public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
		Assert.notEmpty(partConverters, "'partConverters' must not be empty");
		this.partConverters = partConverters;
	}

	/**
	 * Add a message body converter. Such a converter is used to convert objects
	 * to MIME parts.
	 * <p>
	 *  添加消息体转换器这样的转换器用于将对象转换为MIME部分
	 * 
	 */
	public void addPartConverter(HttpMessageConverter<?> partConverter) {
		Assert.notNull(partConverter, "'partConverter' must not be null");
		this.partConverters.add(partConverter);
	}

	/**
	 * Set the default character set to use for reading and writing form data when
	 * the request or response Content-Type header does not explicitly specify it.
	 * <p>By default this is set to "UTF-8". As of 4.3, it will also be used as
	 * the default charset for the conversion of text bodies in a multipart request.
	 * In contrast to this, {@link #setMultipartCharset} only affects the encoding of
	 * <i>file names</i> in a multipart request according to the encoded-word syntax.
	 * <p>
	 *  当请求或响应Content-Type头没有明确指定它时,设置用于读写表单数据的默认字符集<p>默认情况下,这被设置为"UTF-8"。
	 * 从43开始,它也将被使用作为多部分请求中文本体转换的默认字符集与此相反,{@link #setMultipartCharset}仅影响根据编码字语法的多部分请求中<i>文件名</i>的编码。
	 * 
	 */
	public void setCharset(Charset charset) {
		if (charset != this.charset) {
			this.charset = (charset != null ? charset : DEFAULT_CHARSET);
			applyDefaultCharset();
		}
	}

	/**
	 * Apply the configured charset as a default to registered part converters.
	 * <p>
	 *  将配置的字符集作为默认值应用于注册的零件转换器
	 * 
	 */
	private void applyDefaultCharset() {
		for (HttpMessageConverter<?> candidate : this.partConverters) {
			if (candidate instanceof AbstractHttpMessageConverter) {
				AbstractHttpMessageConverter<?> converter = (AbstractHttpMessageConverter<?>) candidate;
				// Only override default charset if the converter operates with a charset to begin with...
				if (converter.getDefaultCharset() != null) {
					converter.setDefaultCharset(this.charset);
				}
			}
		}
	}

	/**
	 * Set the character set to use when writing multipart data to encode file
	 * names. Encoding is based on the encoded-word syntax defined in RFC 2047
	 * and relies on {@code MimeUtility} from "javax.mail".
	 * <p>If not set file names will be encoded as US-ASCII.
	 * <p>
	 * 将编写多部分数据时使用的字符集设置为编码文件名称编码基于RFC 2047中定义的编码字语法,并依赖于"javaxmail"中的{@code MimeUtility} <p>如果未设置文件名将被编码如US
	 * -ASCII。
	 * 
	 * 
	 * @param multipartCharset the charset to use
	 * @since 4.1.1
	 * @see <a href="http://en.wikipedia.org/wiki/MIME#Encoded-Word">Encoded-Word</a>
	 */
	public void setMultipartCharset(Charset multipartCharset) {
		this.multipartCharset = multipartCharset;
	}


	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (!MultiValueMap.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			// We can't read multipart....
			if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) && supportedMediaType.includes(mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		if (!MultiValueMap.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null || MediaType.ALL.equals(mediaType)) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.isCompatibleWith(mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MultiValueMap<String, String> read(Class<? extends MultiValueMap<String, ?>> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

		MediaType contentType = inputMessage.getHeaders().getContentType();
		Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : this.charset);
		String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

		String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx == -1) {
				result.add(URLDecoder.decode(pair, charset.name()), null);
			}
			else {
				String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
				String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
				result.add(name, value);
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(MultiValueMap<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		if (!isMultipart(map, contentType)) {
			writeForm((MultiValueMap<String, String>) map, contentType, outputMessage);
		}
		else {
			writeMultipart((MultiValueMap<String, Object>) map, outputMessage);
		}
	}


	private boolean isMultipart(MultiValueMap<String, ?> map, MediaType contentType) {
		if (contentType != null) {
			return MediaType.MULTIPART_FORM_DATA.includes(contentType);
		}
		for (String name : map.keySet()) {
			for (Object value : map.get(name)) {
				if (value != null && !(value instanceof String)) {
					return true;
				}
			}
		}
		return false;
	}

	private void writeForm(MultiValueMap<String, String> form, MediaType contentType,
			HttpOutputMessage outputMessage) throws IOException {

		Charset charset;
		if (contentType != null) {
			outputMessage.getHeaders().setContentType(contentType);
			charset = (contentType.getCharset() != null ? contentType.getCharset() : this.charset);
		}
		else {
			outputMessage.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			charset = this.charset;
		}
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
			String name = nameIterator.next();
			for (Iterator<String> valueIterator = form.get(name).iterator(); valueIterator.hasNext();) {
				String value = valueIterator.next();
				builder.append(URLEncoder.encode(name, charset.name()));
				if (value != null) {
					builder.append('=');
					builder.append(URLEncoder.encode(value, charset.name()));
					if (valueIterator.hasNext()) {
						builder.append('&');
					}
				}
			}
			if (nameIterator.hasNext()) {
				builder.append('&');
			}
		}
		final byte[] bytes = builder.toString().getBytes(charset.name());
		outputMessage.getHeaders().setContentLength(bytes.length);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
				@Override
				public void writeTo(OutputStream outputStream) throws IOException {
					StreamUtils.copy(bytes, outputStream);
				}
			});
		}
		else {
			StreamUtils.copy(bytes, outputMessage.getBody());
		}
	}

	private void writeMultipart(final MultiValueMap<String, Object> parts, HttpOutputMessage outputMessage) throws IOException {
		final byte[] boundary = generateMultipartBoundary();
		Map<String, String> parameters = Collections.singletonMap("boundary", new String(boundary, "US-ASCII"));

		MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
		HttpHeaders headers = outputMessage.getHeaders();
		headers.setContentType(contentType);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() {
				@Override
				public void writeTo(OutputStream outputStream) throws IOException {
					writeParts(outputStream, parts, boundary);
					writeEnd(outputStream, boundary);
				}
			});
		}
		else {
			writeParts(outputMessage.getBody(), parts, boundary);
			writeEnd(outputMessage.getBody(), boundary);
		}
	}

	private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
		for (Map.Entry<String, List<Object>> entry : parts.entrySet()) {
			String name = entry.getKey();
			for (Object part : entry.getValue()) {
				if (part != null) {
					writeBoundary(os, boundary);
					writePart(name, getHttpEntity(part), os);
					writeNewLine(os);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
		Object partBody = partEntity.getBody();
		Class<?> partType = partBody.getClass();
		HttpHeaders partHeaders = partEntity.getHeaders();
		MediaType partContentType = partHeaders.getContentType();
		for (HttpMessageConverter<?> messageConverter : this.partConverters) {
			if (messageConverter.canWrite(partType, partContentType)) {
				HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os);
				multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
				if (!partHeaders.isEmpty()) {
					multipartMessage.getHeaders().putAll(partHeaders);
				}
				((HttpMessageConverter<Object>) messageConverter).write(partBody, partContentType, multipartMessage);
				return;
			}
		}
		throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter " +
				"found for request type [" + partType.getName() + "]");
	}


	/**
	 * Generate a multipart boundary.
	 * <p>This implementation delegates to
	 * {@link MimeTypeUtils#generateMultipartBoundary()}.
	 * <p>
	 *  生成多部分边界<p>此实现委托给{@link MimeTypeUtils#generateMultipartBoundary()}
	 * 
	 */
	protected byte[] generateMultipartBoundary() {
		return MimeTypeUtils.generateMultipartBoundary();
	}

	/**
	 * Return an {@link HttpEntity} for the given part Object.
	 * <p>
	 *  返回给定零件对象的{@link HttpEntity}
	 * 
	 * 
	 * @param part the part to return an {@link HttpEntity} for
	 * @return the part Object itself it is an {@link HttpEntity},
	 * or a newly built {@link HttpEntity} wrapper for that part
	 */
	protected HttpEntity<?> getHttpEntity(Object part) {
		if (part instanceof HttpEntity) {
			return (HttpEntity<?>) part;
		}
		else {
			return new HttpEntity<Object>(part);
		}
	}

	/**
	 * Return the filename of the given multipart part. This value will be used for the
	 * {@code Content-Disposition} header.
	 * <p>The default implementation returns {@link Resource#getFilename()} if the part is a
	 * {@code Resource}, and {@code null} in other cases. Can be overridden in subclasses.
	 * <p>
	 *  返回给定multipart部分的文件名该值将用于{@code Content-Disposition}标头<p>如果部分是{@code资源},则默认实现返回{@link Resource#getFilename()}
	 * )在其他情况下,{@code null}可以在子类中被覆盖。
	 * 
	 * 
	 * @param part the part to determine the file name for
	 * @return the filename, or {@code null} if not known
	 */
	protected String getFilename(Object part) {
		if (part instanceof Resource) {
			Resource resource = (Resource) part;
			String filename = resource.getFilename();
			if (this.multipartCharset != null) {
				filename = MimeDelegate.encode(filename, this.multipartCharset.name());
			}
			return filename;
		}
		else {
			return null;
		}
	}


	private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
		os.write('-');
		os.write('-');
		os.write(boundary);
		writeNewLine(os);
	}

	private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
		os.write('-');
		os.write('-');
		os.write(boundary);
		os.write('-');
		os.write('-');
		writeNewLine(os);
	}

	private static void writeNewLine(OutputStream os) throws IOException {
		os.write('\r');
		os.write('\n');
	}


	/**
	 * Implementation of {@link org.springframework.http.HttpOutputMessage} used
	 * to write a MIME multipart.
	 * <p>
	 * 用于编写MIME多部分的{@link orgspringframeworkhttpHttpOutputMessage}的实现
	 * 
	 */
	private static class MultipartHttpOutputMessage implements HttpOutputMessage {

		private final OutputStream outputStream;

		private final HttpHeaders headers = new HttpHeaders();

		private boolean headersWritten = false;

		public MultipartHttpOutputMessage(OutputStream outputStream) {
			this.outputStream = outputStream;
		}

		@Override
		public HttpHeaders getHeaders() {
			return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
		}

		@Override
		public OutputStream getBody() throws IOException {
			writeHeaders();
			return this.outputStream;
		}

		private void writeHeaders() throws IOException {
			if (!this.headersWritten) {
				for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
					byte[] headerName = getAsciiBytes(entry.getKey());
					for (String headerValueString : entry.getValue()) {
						byte[] headerValue = getAsciiBytes(headerValueString);
						this.outputStream.write(headerName);
						this.outputStream.write(':');
						this.outputStream.write(' ');
						this.outputStream.write(headerValue);
						writeNewLine(this.outputStream);
					}
				}
				writeNewLine(this.outputStream);
				this.headersWritten = true;
			}
		}

		private byte[] getAsciiBytes(String name) {
			try {
				return name.getBytes("US-ASCII");
			}
			catch (UnsupportedEncodingException ex) {
				// Should not happen - US-ASCII is always supported.
				throw new IllegalStateException(ex);
			}
		}
	}


	/**
	 * Inner class to avoid a hard dependency on the JavaMail API.
	 * <p>
	 *  内部类避免了对JavaMail API的严重依赖
	 */
	private static class MimeDelegate {

		public static String encode(String value, String charset) {
			try {
				return MimeUtility.encodeText(value, charset, null);
			}
			catch (UnsupportedEncodingException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

}
