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

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.comparator.CompoundComparator;

/**
 * A sub-class of {@link MimeType} that adds support for quality parameters as defined
 * in the HTTP specification.
 *
 * <p>
 *  {@link MimeType}的子类,它添加了对HTTP规范中定义的质量参数的支持
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @since 3.0
 * @see <a href="http://tools.ietf.org/html/rfc7231#section-3.1.1.1">HTTP 1.1: Semantics and Content, section 3.1.1.1</a>
 */
public class MediaType extends MimeType implements Serializable {

	private static final long serialVersionUID = 2069937152339670231L;

	/**
	 * Public constant media type that includes all media ranges (i.e. "&#42;/&#42;").
	 * <p>
	 *  包含所有媒体范围的公共媒体类型(即"* / *")
	 * 
	 */
	public static final MediaType ALL;

	/**
	 * A String equivalent of {@link MediaType#ALL}.
	 * <p>
	 * 相当于{@link MediaType#ALL}的String
	 * 
	 */
	public static final String ALL_VALUE = "*/*";

	/**
	 *  Public constant media type for {@code application/atom+xml}.
	 * <p>
	 *  / ** {@code application / atom + xml}的公共常量媒体类型
	 * 
	 */
	public final static MediaType APPLICATION_ATOM_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_ATOM_XML}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_ATOM_XML}的String
	 * 
	 */
	public final static String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

	/**
	 * Public constant media type for {@code application/x-www-form-urlencoded}.
	 * <p>
	 *  公共常规媒体类型为{@code application / x-www-form-urlencoded}
	 * 
	 */
	public final static MediaType APPLICATION_FORM_URLENCODED;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_FORM_URLENCODED}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_FORM_URLENCODED}的String
	 * 
	 */
	public final static String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

	/**
	 * Public constant media type for {@code application/json}.
	 * <p>
	 *  {@code application / json}的公共媒体类型
	 * 
	 * 
	 * @see #APPLICATION_JSON_UTF8
	 */
	public final static MediaType APPLICATION_JSON;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_JSON}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_JSON}的String
	 * 
	 * 
	 * @see #APPLICATION_JSON_UTF8_VALUE
	 */
	public final static String APPLICATION_JSON_VALUE = "application/json";

	/**
	 * Public constant media type for {@code application/json;charset=UTF-8}.
	 * <p>
	 *  {@code application / json; charset = UTF-8}的公共常规媒体类型
	 * 
	 */
	public final static MediaType APPLICATION_JSON_UTF8;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_JSON_UTF8}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_JSON_UTF8}的String
	 * 
	 */
	public final static String APPLICATION_JSON_UTF8_VALUE = APPLICATION_JSON_VALUE + ";charset=UTF-8";

	/**
	 * Public constant media type for {@code application/octet-stream}.
	 * <p>
	 *  {@code application / octet-stream}的公共常规媒体类型
	 * 
	 */
	public final static MediaType APPLICATION_OCTET_STREAM;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_OCTET_STREAM}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_OCTET_STREAM}的String
	 * 
	 */
	public final static String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

	/**
	 * Public constant media type for {@code application/pdf}.
	 * <p>
	 *  公共常规媒体类型为{@code application / pdf}
	 * 
	 * 
	 * @since 4.3
	 */
	public final static MediaType APPLICATION_PDF;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_PDF}.
	 * <p>
	 * 相当于{@link MediaType#APPLICATION_PDF}的String
	 * 
	 * 
	 * @since 4.3
	 */
	public final static String APPLICATION_PDF_VALUE = "application/pdf";

	/**
	 * Public constant media type for {@code application/xhtml+xml}.
	 * <p>
	 *  {@code application / xhtml + xml}的公共媒体类型
	 * 
	 */
	public final static MediaType APPLICATION_XHTML_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_XHTML_XML}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_XHTML_XML}的String
	 * 
	 */
	public final static String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

	/**
	 * Public constant media type for {@code application/xml}.
	 * <p>
	 *  {@code application / xml}的公共媒体类型
	 * 
	 */
	public final static MediaType APPLICATION_XML;

	/**
	 * A String equivalent of {@link MediaType#APPLICATION_XML}.
	 * <p>
	 *  相当于{@link MediaType#APPLICATION_XML}的String
	 * 
	 */
	public final static String APPLICATION_XML_VALUE = "application/xml";

	/**
	 * Public constant media type for {@code image/gif}.
	 * <p>
	 *  {@code image / gif}的公共媒体类型
	 * 
	 */
	public final static MediaType IMAGE_GIF;

	/**
	 * A String equivalent of {@link MediaType#IMAGE_GIF}.
	 * <p>
	 *  相当于{@link MediaType#IMAGE_GIF}的String
	 * 
	 */
	public final static String IMAGE_GIF_VALUE = "image/gif";

	/**
	 * Public constant media type for {@code image/jpeg}.
	 * <p>
	 *  {@code image / jpeg}的公共媒体类型
	 * 
	 */
	public final static MediaType IMAGE_JPEG;

	/**
	 * A String equivalent of {@link MediaType#IMAGE_JPEG}.
	 * <p>
	 *  相当于{@link MediaType#IMAGE_JPEG}的String
	 * 
	 */
	public final static String IMAGE_JPEG_VALUE = "image/jpeg";

	/**
	 * Public constant media type for {@code image/png}.
	 * <p>
	 *  公共常规媒体类型为{@code image / png}
	 * 
	 */
	public final static MediaType IMAGE_PNG;

	/**
	 * A String equivalent of {@link MediaType#IMAGE_PNG}.
	 * <p>
	 *  相当于{@link MediaType#IMAGE_PNG}的String
	 * 
	 */
	public final static String IMAGE_PNG_VALUE = "image/png";

	/**
	 * Public constant media type for {@code multipart/form-data}.
	 * <p>
	 *  {@code multipart / form-data}的公共媒体类型
	 * 
	 */
	public final static MediaType MULTIPART_FORM_DATA;

	/**
	 * A String equivalent of {@link MediaType#MULTIPART_FORM_DATA}.
	 * <p>
	 *  相当于{@link MediaType#MULTIPART_FORM_DATA}的String
	 * 
	 */
	public final static String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

	/**
	 * Public constant media type for {@code text/html}.
	 * <p>
	 *  {@code text / html}的公共媒体类型
	 * 
	 */
	public final static MediaType TEXT_HTML;

	/**
	 * A String equivalent of {@link MediaType#TEXT_HTML}.
	 * <p>
	 * 相当于{@link MediaType#TEXT_HTML}的String
	 * 
	 */
	public final static String TEXT_HTML_VALUE = "text/html";

	/**
	 * Public constant media type for {@code text/markdown}.
	 * <p>
	 *  {@code text / markdown}的公共媒体类型
	 * 
	 * 
	 * @since 4.3
	 */
	public final static MediaType TEXT_MARKDOWN;

	/**
	 * A String equivalent of {@link MediaType#TEXT_MARKDOWN}.
	 * <p>
	 *  相当于{@link MediaType#TEXT_MARKDOWN}的String
	 * 
	 * 
	 * @since 4.3
	 */
	public final static String TEXT_MARKDOWN_VALUE = "text/markdown";

	/**
	 * Public constant media type for {@code text/plain}.
	 * <p>
	 *  {@code text / plain}的公共媒体类型
	 * 
	 */
	public final static MediaType TEXT_PLAIN;

	/**
	 * A String equivalent of {@link MediaType#TEXT_PLAIN}.
	 * <p>
	 *  相当于{@link MediaType#TEXT_PLAIN}的String
	 * 
	 */
	public final static String TEXT_PLAIN_VALUE = "text/plain";

	/**
	 * Public constant media type for {@code text/xml}.
	 * <p>
	 *  {@code text / xml}的公共媒体类型
	 * 
	 */
	public final static MediaType TEXT_XML;

	/**
	 * A String equivalent of {@link MediaType#TEXT_XML}.
	 * <p>
	 *  相当于{@link MediaType#TEXT_XML}的String
	 * 
	 */
	public final static String TEXT_XML_VALUE = "text/xml";


	private static final String PARAM_QUALITY_FACTOR = "q";


	static {
		ALL = valueOf(ALL_VALUE);
		APPLICATION_ATOM_XML = valueOf(APPLICATION_ATOM_XML_VALUE);
		APPLICATION_FORM_URLENCODED = valueOf(APPLICATION_FORM_URLENCODED_VALUE);
		APPLICATION_JSON = valueOf(APPLICATION_JSON_VALUE);
		APPLICATION_JSON_UTF8 = valueOf(APPLICATION_JSON_UTF8_VALUE);
		APPLICATION_OCTET_STREAM = valueOf(APPLICATION_OCTET_STREAM_VALUE);
		APPLICATION_PDF = valueOf(APPLICATION_PDF_VALUE);
		APPLICATION_XHTML_XML = valueOf(APPLICATION_XHTML_XML_VALUE);
		APPLICATION_XML = valueOf(APPLICATION_XML_VALUE);
		IMAGE_GIF = valueOf(IMAGE_GIF_VALUE);
		IMAGE_JPEG = valueOf(IMAGE_JPEG_VALUE);
		IMAGE_PNG = valueOf(IMAGE_PNG_VALUE);
		MULTIPART_FORM_DATA = valueOf(MULTIPART_FORM_DATA_VALUE);
		TEXT_HTML = valueOf(TEXT_HTML_VALUE);
		TEXT_MARKDOWN = valueOf(TEXT_MARKDOWN_VALUE);
		TEXT_PLAIN = valueOf(TEXT_PLAIN_VALUE);
		TEXT_XML = valueOf(TEXT_XML_VALUE);
	}


	/**
	 * Create a new {@code MediaType} for the given primary type.
	 * <p>The {@linkplain #getSubtype() subtype} is set to "&#42;", parameters empty.
	 * <p>
	 *  为给定的主要类型创建新的{@code MediaType} <p> {@linkplain #getSubtype()子类型}设置为"*",参数为空
	 * 
	 * 
	 * @param type the primary type
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 */
	public MediaType(String type) {
		super(type);
	}

	/**
	 * Create a new {@code MediaType} for the given primary type and subtype.
	 * <p>The parameters are empty.
	 * <p>
	 *  为给定的主要类型和子类型创建新的{@code MediaType} <p>参数为空
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 */
	public MediaType(String type, String subtype) {
		super(type, subtype, Collections.<String, String>emptyMap());
	}

	/**
	 * Create a new {@code MediaType} for the given type, subtype, and character set.
	 * <p>
	 *  为给定的类型,子类型和字符集创建一个新的{@code MediaType}
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @param charset the character set
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 */
	public MediaType(String type, String subtype, Charset charset) {
		super(type, subtype, charset);
	}

	/**
	 * Create a new {@code MediaType} for the given type, subtype, and quality value.
	 * <p>
	 *  为给定的类型,子类型和质量值创建一个新的{@code MediaType}
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @param qualityValue the quality value
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 */
	public MediaType(String type, String subtype, double qualityValue) {
		this(type, subtype, Collections.singletonMap(PARAM_QUALITY_FACTOR, Double.toString(qualityValue)));
	}

	/**
	 * Copy-constructor that copies the type, subtype and parameters of the given
	 * {@code MediaType}, and allows to set the specified character set.
	 * <p>
	 * 复制构造函数,用于复制给定{@code MediaType}的类型,子类型和参数,并允许设置指定的字符集
	 * 
	 * 
	 * @param other the other media type
	 * @param charset the character set
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 * @since 4.3
	 */
	public MediaType(MediaType other, Charset charset) {
		super(other, charset);
	}

	/**
	 * Copy-constructor that copies the type and subtype of the given {@code MediaType},
	 * and allows for different parameter.
	 * <p>
	 *  复制构造函数,复制给定的{@code MediaType}的类型和子类型,并允许不同的参数
	 * 
	 * 
	 * @param other the other media type
	 * @param parameters the parameters, may be {@code null}
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 */
	public MediaType(MediaType other, Map<String, String> parameters) {
		super(other.getType(), other.getSubtype(), parameters);
	}

	/**
	 * Create a new {@code MediaType} for the given type, subtype, and parameters.
	 * <p>
	 *  为给定的类型,子类型和参数创建一个新的{@code MediaType}
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @param parameters the parameters, may be {@code null}
	 * @throws IllegalArgumentException if any of the parameters contain illegal characters
	 */
	public MediaType(String type, String subtype, Map<String, String> parameters) {
		super(type, subtype, parameters);
	}


	@Override
	protected void checkParameters(String attribute, String value) {
		super.checkParameters(attribute, value);
		if (PARAM_QUALITY_FACTOR.equals(attribute)) {
			value = unquote(value);
			double d = Double.parseDouble(value);
			Assert.isTrue(d >= 0D && d <= 1D,
					"Invalid quality value \"" + value + "\": should be between 0.0 and 1.0");
		}
	}

	/**
	 * Return the quality value, as indicated by a {@code q} parameter, if any.
	 * Defaults to {@code 1.0}.
	 * <p>
	 *  返回质量值,如{@code q}参数所示,如果有任何默认值为{@code 10}
	 * 
	 * 
	 * @return the quality factory
	 */
	public double getQualityValue() {
		String qualityFactory = getParameter(PARAM_QUALITY_FACTOR);
		return (qualityFactory != null ? Double.parseDouble(unquote(qualityFactory)) : 1D);
	}

	/**
	 * Indicate whether this {@code MediaType} includes the given media type.
	 * <p>For instance, {@code text/*} includes {@code text/plain} and {@code text/html}, and {@code application/*+xml}
	 * includes {@code application/soap+xml}, etc. This method is <b>not</b> symmetric.
	 * <p>
	 *  指示此{@code MediaType}是否包含给定的媒体类型<p>例如,{@code text / *}包括{@code text / plain}和{@code text / html}和{@code application / * + xml}
	 * 包括{@code application / soap + xml}等。
	 * 这个方法是<b>不</b>对称。
	 * 
	 * 
	 * @param other the reference media type with which to compare
	 * @return {@code true} if this media type includes the given media type; {@code false} otherwise
	 */
	public boolean includes(MediaType other) {
		return super.includes(other);
	}

	/**
	 * Indicate whether this {@code MediaType} is compatible with the given media type.
	 * <p>For instance, {@code text/*} is compatible with {@code text/plain}, {@code text/html}, and vice versa.
	 * In effect, this method is similar to {@link #includes(MediaType)}, except that it <b>is</b> symmetric.
	 * <p>
	 * 指示此{@code MediaType}是否与给定的媒体类型<p>兼容。
	 * 例如,{@code text / *}与{@code text / plain},{@code text / html}兼容,反之亦然实际上,这种方法类似于{@link #includes(MediaType)}
	 * ,除了它<b>是</b>对称。
	 * 指示此{@code MediaType}是否与给定的媒体类型<p>兼容。
	 * 
	 * 
	 * @param other the reference media type with which to compare
	 * @return {@code true} if this media type is compatible with the given media type; {@code false} otherwise
	 */
	public boolean isCompatibleWith(MediaType other) {
		return super.isCompatibleWith(other);
	}

	/**
	 * Return a replica of this instance with the quality value of the given MediaType.
	 * <p>
	 *  以给定的MediaType的质量值返回此实例的副本
	 * 
	 * 
	 * @return the same instance if the given MediaType doesn't have a quality value, or a new one otherwise
	 */
	public MediaType copyQualityValue(MediaType mediaType) {
		if (!mediaType.getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
			return this;
		}
		Map<String, String> params = new LinkedHashMap<String, String>(getParameters());
		params.put(PARAM_QUALITY_FACTOR, mediaType.getParameters().get(PARAM_QUALITY_FACTOR));
		return new MediaType(this, params);
	}

	/**
	 * Return a replica of this instance with its quality value removed.
	 * <p>
	 *  返回此实例的副本,其质量值被删除
	 * 
	 * 
	 * @return the same instance if the media type doesn't contain a quality value, or a new one otherwise
	 */
	public MediaType removeQualityValue() {
		if (!getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
			return this;
		}
		Map<String, String> params = new LinkedHashMap<String, String>(getParameters());
		params.remove(PARAM_QUALITY_FACTOR);
		return new MediaType(this, params);
	}


	/**
	 * Parse the given String value into a {@code MediaType} object,
	 * with this method name following the 'valueOf' naming convention
	 * (as supported by {@link org.springframework.core.convert.ConversionService}.
	 * <p>
	 *  将给定的String值解析为{@code MediaType}对象,该方法名称遵循'valueOf'命名约定(由{@link orgspringframeworkcoreconvertConversionService}
	 * 支持)。
	 * 
	 * 
	 * @param value the string to parse
	 * @throws InvalidMediaTypeException if the media type value cannot be parsed
	 * @see #parseMediaType(String)
	 */
	public static MediaType valueOf(String value) {
		return parseMediaType(value);
	}

	/**
	 * Parse the given String into a single {@code MediaType}.
	 * <p>
	 *  将给定的字符串解析为单个{@code MediaType}
	 * 
	 * 
	 * @param mediaType the string to parse
	 * @return the media type
	 * @throws InvalidMediaTypeException if the media type value cannot be parsed
	 */
	public static MediaType parseMediaType(String mediaType) {
		MimeType type;
		try {
			type = MimeTypeUtils.parseMimeType(mediaType);
		}
		catch (InvalidMimeTypeException ex) {
			throw new InvalidMediaTypeException(ex);
		}
		try {
			return new MediaType(type.getType(), type.getSubtype(), type.getParameters());
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidMediaTypeException(mediaType, ex.getMessage());
		}
	}

	/**
	 * Parse the given comma-separated string into a list of {@code MediaType} objects.
	 * <p>This method can be used to parse an Accept or Content-Type header.
	 * <p>
	 * 将给定的逗号分隔字符串解析为{@code MediaType}对象的列表<p>此方法可用于解析Accept或Content-Type标题
	 * 
	 * 
	 * @param mediaTypes the string to parse
	 * @return the list of media types
	 * @throws InvalidMediaTypeException if the media type value cannot be parsed
	 */
	public static List<MediaType> parseMediaTypes(String mediaTypes) {
		if (!StringUtils.hasLength(mediaTypes)) {
			return Collections.emptyList();
		}
		String[] tokens = StringUtils.tokenizeToStringArray(mediaTypes, ",");
		List<MediaType> result = new ArrayList<MediaType>(tokens.length);
		for (String token : tokens) {
			result.add(parseMediaType(token));
		}
		return result;
	}

	/**
	 * Parse the given list of (potentially) comma-separated strings into a
	 * list of {@code MediaType} objects.
	 * <p>This method can be used to parse an Accept or Content-Type header.
	 * <p>
	 *  将(可能)逗号分隔的字符串的给定列表解析为{@code MediaType}对象的列表<p>此方法可用于解析Accept或Content-Type头
	 * 
	 * 
	 * @param mediaTypes the string to parse
	 * @return the list of media types
	 * @throws InvalidMediaTypeException if the media type value cannot be parsed
	 * @since 4.3.2
	 */
	public static List<MediaType> parseMediaTypes(List<String> mediaTypes) {
		if (CollectionUtils.isEmpty(mediaTypes)) {
			return Collections.<MediaType>emptyList();
		}
		else if (mediaTypes.size() == 1) {
			return parseMediaTypes(mediaTypes.get(0));
		}
		else {
			List<MediaType> result = new ArrayList<MediaType>(8);
			for (String mediaType : mediaTypes) {
				result.addAll(parseMediaTypes(mediaType));
			}
			return result;
		}
	}

	/**
	 * Return a string representation of the given list of {@code MediaType} objects.
	 * <p>This method can be used to for an {@code Accept} or {@code Content-Type} header.
	 * <p>
	 *  返回{@code MediaType}对象的给定列表的字符串表示<p>此方法可用于{@code Accept}或{@code Content-Type}标题
	 * 
	 * 
	 * @param mediaTypes the media types to create a string representation for
	 * @return the string representation
	 */
	public static String toString(Collection<MediaType> mediaTypes) {
		return MimeTypeUtils.toString(mediaTypes);
	}

	/**
	 * Sorts the given list of {@code MediaType} objects by specificity.
	 * <p>Given two media types:
	 * <ol>
	 * <li>if either media type has a {@linkplain #isWildcardType() wildcard type}, then the media type without the
	 * wildcard is ordered before the other.</li>
	 * <li>if the two media types have different {@linkplain #getType() types}, then they are considered equal and
	 * remain their current order.</li>
	 * <li>if either media type has a {@linkplain #isWildcardSubtype() wildcard subtype}, then the media type without
	 * the wildcard is sorted before the other.</li>
	 * <li>if the two media types have different {@linkplain #getSubtype() subtypes}, then they are considered equal
	 * and remain their current order.</li>
	 * <li>if the two media types have different {@linkplain #getQualityValue() quality value}, then the media type
	 * with the highest quality value is ordered before the other.</li>
	 * <li>if the two media types have a different amount of {@linkplain #getParameter(String) parameters}, then the
	 * media type with the most parameters is ordered before the other.</li>
	 * </ol>
	 * <p>For example:
	 * <blockquote>audio/basic &lt; audio/* &lt; *&#047;*</blockquote>
	 * <blockquote>audio/* &lt; audio/*;q=0.7; audio/*;q=0.3</blockquote>
	 * <blockquote>audio/basic;level=1 &lt; audio/basic</blockquote>
	 * <blockquote>audio/basic == text/html</blockquote>
	 * <blockquote>audio/basic == audio/wave</blockquote>
	 * <p>
	 *  给定列表的{@code MediaType}对象按特定性排序<p>给定两种媒体类型：
	 * <ol>
	 * <li>如果任何一种媒体类型都有{@linkplain #isWildcardType()通配符类型},那么没有通配符的媒体类型在另一个</li> <li>之前被排序,如果两种媒体类型有不同的{@linkplain# getType()类型}
	 * ,那么如果任一媒体类型具有{@linkplain #isWildcardSubtype()通配符子类型},那么它们被认为是相等的并保持其当前的顺序</li> <li>,那么没有通配符的媒体类型将被排序另
	 * 一个</li> <li>如果两种媒体类型具有不同的{@linkplain #getSubtype()子类型},那么如果两种媒体类型不同,则它们被认为是相等的,并保持其当前的顺序</li> <li> {@linkplain #getQualityValue()quality value}
	 * ,那么具有最高质量值的媒体类型在另一个之前被排序</li> <li>如果两种媒体类型具有不同数量的{@linkplain #getParameter(String)参数},则具有最多参数的媒体类型在另一
	 * 个</li>。
	 * </ol>
	 * <p>例如：<blockquote> audio / basic&lt;音频/ * * / * </blockquote> <blockquote> audio / *&lt;音频/ *; Q = 07
	 * ; audio / *; q = 03 </blockquote> <blockquote> audio / basic; level = 1&lt;音频/基本</blockquote> <blockquote>
	 * 音频/基本== text / html </blockquote> <blockquote>音频/基本==音频/波</blockquote>。
	 * 
	 * 
	 * @param mediaTypes the list of media types to be sorted
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.2">HTTP 1.1: Semantics
	 * and Content, section 5.3.2</a>
	 */
	public static void sortBySpecificity(List<MediaType> mediaTypes) {
		Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
		if (mediaTypes.size() > 1) {
			Collections.sort(mediaTypes, SPECIFICITY_COMPARATOR);
		}
	}

	/**
	 * Sorts the given list of {@code MediaType} objects by quality value.
	 * <p>Given two media types:
	 * <ol>
	 * <li>if the two media types have different {@linkplain #getQualityValue() quality value}, then the media type
	 * with the highest quality value is ordered before the other.</li>
	 * <li>if either media type has a {@linkplain #isWildcardType() wildcard type}, then the media type without the
	 * wildcard is ordered before the other.</li>
	 * <li>if the two media types have different {@linkplain #getType() types}, then they are considered equal and
	 * remain their current order.</li>
	 * <li>if either media type has a {@linkplain #isWildcardSubtype() wildcard subtype}, then the media type without
	 * the wildcard is sorted before the other.</li>
	 * <li>if the two media types have different {@linkplain #getSubtype() subtypes}, then they are considered equal
	 * and remain their current order.</li>
	 * <li>if the two media types have a different amount of {@linkplain #getParameter(String) parameters}, then the
	 * media type with the most parameters is ordered before the other.</li>
	 * </ol>
	 * <p>
	 *  通过质量值对给定的{@code MediaType}对象列表进行排序<p>给定两种媒体类型：
	 * <ol>
	 * <li>如果两种媒体类型具有不同的{@linkplain #getQualityValue()质量值},则质量最高的媒体类型在另一个</li> <li>之前排序,如果任一媒体类型具有{@ linkplain #isWildcardType()通配符类型}
	 * ,则如果两个媒体类型具有不同的{@linkplain #getType()类型}),那么没有通配符的媒体类型将在另一个</li> <li>之前排序,那么它们被视为相等并且保留其当前订单</li> <li>
	 * ,如果任一媒体类型具有{@linkplain #isWildcardSubtype()通配符子类型},则没有通配符的媒体类型将在另一个</li> <li>之前排序,如果两种媒体类型具有不同的{@linkplain #getSubtype()子类型}
	 * ,那么它们被认为是相等的,并保持其当前顺序</li> <li>如果两种媒体类型具有不同数量的{@linkplain #getParameter(String)参数},则具有最多参数的媒体类型在另一个</li>
	 * 。
	 * </ol>
	 * 
	 * @param mediaTypes the list of media types to be sorted
	 * @see #getQualityValue()
	 */
	public static void sortByQualityValue(List<MediaType> mediaTypes) {
		Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
		if (mediaTypes.size() > 1) {
			Collections.sort(mediaTypes, QUALITY_VALUE_COMPARATOR);
		}
	}

	/**
	 * Sorts the given list of {@code MediaType} objects by specificity as the
	 * primary criteria and quality value the secondary.
	 * <p>
	 * 将特定的{@code MediaType}对象的给定列表排序为主要条件,次要品质值排序
	 * 
	 * 
	 * @see MediaType#sortBySpecificity(List)
	 * @see MediaType#sortByQualityValue(List)
	 */
	public static void sortBySpecificityAndQuality(List<MediaType> mediaTypes) {
		Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
		if (mediaTypes.size() > 1) {
			Collections.sort(mediaTypes, new CompoundComparator<MediaType>(
					MediaType.SPECIFICITY_COMPARATOR, MediaType.QUALITY_VALUE_COMPARATOR));
		}
	}


	/**
	 * Comparator used by {@link #sortByQualityValue(List)}.
	 * <p>
	 *  {@link #sortByQualityValue(List)}使用的比较器
	 * 
	 */
	public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR = new Comparator<MediaType>() {

		@Override
		public int compare(MediaType mediaType1, MediaType mediaType2) {
			double quality1 = mediaType1.getQualityValue();
			double quality2 = mediaType2.getQualityValue();
			int qualityComparison = Double.compare(quality2, quality1);
			if (qualityComparison != 0) {
				return qualityComparison;  // audio/*;q=0.7 < audio/*;q=0.3
			}
				return qualityComparison;  // audio/* <p>
				return qualityComparison;  // audio/*  }
				return qualityComparison;  // audio/* 
				return qualityComparison;  // audio/* 
			else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // */* < audio/*
				return 1;
			}
			else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // * <p>
			else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // *  返回1; }
			else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // * 
			else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // * 
			else if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) { // audio/* > */*
				return -1;
			}
			else if (!mediaType1.getType().equals(mediaType2.getType())) { // audio/basic == text/html
				return 0;
			}
			else { // mediaType1.getType().equals(mediaType2.getType())
				if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) { // audio/* < audio/basic
					return 1;
				}
				else if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) { // audio/basic > audio/*
					return -1;
				}
				else if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) { // audio/basic == audio/wave
					return 0;
				}
				else {
					int paramsSize1 = mediaType1.getParameters().size();
					int paramsSize2 = mediaType2.getParameters().size();
					return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1)); // audio/basic;level=1 < audio/basic
				}
			}
		}
	};


	/**
	 * Comparator used by {@link #sortBySpecificity(List)}.
	 * <p>
	 * 返回-1; } else if(！mediaType1getType()equals(mediaType2getType())){// audio / basic == text / html return 0; }
	 *  else {// mediaType1getType()equals(mediaType2getType())if(mediaType1isWildcardSubtype()&&！mediaType2isWildcardSubtype()){// audio / * <audio / basic return 1; } else if(mediaType2isWildcardSubtype()&&！mediaType1isWildcardSubtype()){// audio / basic> audio / * return -1; }
	 *  else if(！mediaType1getSubtype()equals(mediaType2getSubtype())){// audio / basic == audio / wave return 0; }
	 *  else {int paramsSize1 = mediaType1getParameters()size(); int paramsSize2 = mediaType2getParameters()size(); return(paramsSize2 <paramsSize1?-1：(paramsSize2 == paramsSize1?0：1)); // audio / basic; level = 1 <audio / basic}}}};。
	 */
	public static final Comparator<MediaType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MediaType>() {

		@Override
		protected int compareParameters(MediaType mediaType1, MediaType mediaType2) {
			double quality1 = mediaType1.getQualityValue();
			double quality2 = mediaType2.getQualityValue();
			int qualityComparison = Double.compare(quality2, quality1);
			if (qualityComparison != 0) {
				return qualityComparison;  // audio/*;q=0.7 < audio/*;q=0.3
			}
			return super.compareParameters(mediaType1, mediaType2);
		}
	};

}
