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

package org.springframework.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.MimeType.SpecificityComparator;

/**
 * Miscellaneous {@link MimeType} utility methods.
 *
 * <p>
 *  其他{@link MimeType}实用程序方法
 * 
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public abstract class MimeTypeUtils {

	private static final byte[] BOUNDARY_CHARS =
			new byte[] {'-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
					'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
					'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
					'V', 'W', 'X', 'Y', 'Z'};

	private static final Random RND = new Random();

	private static Charset US_ASCII = Charset.forName("US-ASCII");


	/**
	 * Public constant mime type that includes all media ranges (i.e. "&#42;/&#42;").
	 * <p>
	 *  包含所有媒体范围的公共常量MIME类型(即"* / *")
	 * 
	 */
	public static final MimeType ALL;

	/**
	 * A String equivalent of {@link MimeTypeUtils#ALL}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#ALL}的String
	 * 
	 */
	public static final String ALL_VALUE = "*/*";

	/**
	 *  Public constant mime type for {@code application/atom+xml}.
	 * <p>
	 * / **公共常量MIME类型为{@code application / atom + xml}
	 * 
	 */
	public final static MimeType APPLICATION_ATOM_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_ATOM_XML}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#APPLICATION_ATOM_XML}的String
	 * 
	 */
	public final static String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

	/**
	 * Public constant mime type for {@code application/x-www-form-urlencoded}.
	 * <p>
	 *  公共常量MIME类型为{@code application / x-www-form-urlencoded}
	 * 
	 * 
	 *  */
	public final static MimeType APPLICATION_FORM_URLENCODED;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_FORM_URLENCODED}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#APPLICATION_FORM_URLENCODED}的String
	 * 
	 */
	public final static String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

	/**
	 * Public constant mime type for {@code application/json}.
	 * <p>
	 *  公共常量MIME类型为{@code application / json}
	 * 
	 * 
	 * */
	public final static MimeType APPLICATION_JSON;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_JSON}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#APPLICATION_JSON}的String
	 * 
	 */
	public final static String APPLICATION_JSON_VALUE = "application/json";

	/**
	 * Public constant mime type for {@code application/octet-stream}.
	 * <p>
	 *  公共常量MIME类型为{@code application / octet-stream}
	 * 
	 * 
	 *  */
	public final static MimeType APPLICATION_OCTET_STREAM;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_OCTET_STREAM}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#APPLICATION_OCTET_STREAM}的String
	 * 
	 */
	public final static String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

	/**
	 * Public constant mime type for {@code application/xhtml+xml}.
	 * <p>
	 *  针对{@code application / xhtml + xml}的公共常量MIME类型
	 * 
	 * 
	 *  */
	public final static MimeType APPLICATION_XHTML_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XHTML_XML}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#APPLICATION_XHTML_XML}的String
	 * 
	 */
	public final static String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

	/**
	 * Public constant mime type for {@code application/xml}.
	 * <p>
	 *  公共常量MIME类型为{@code application / xml}
	 * 
	 */
	public final static MimeType APPLICATION_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#APPLICATION_XML}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#APPLICATION_XML}的String
	 * 
	 */
	public final static String APPLICATION_XML_VALUE = "application/xml";

	/**
	 * Public constant mime type for {@code image/gif}.
	 * <p>
	 * 公共常量MIME类型为{@code image / gif}
	 * 
	 */
	public final static MimeType IMAGE_GIF;

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_GIF}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#IMAGE_GIF}的String
	 * 
	 */
	public final static String IMAGE_GIF_VALUE = "image/gif";

	/**
	 * Public constant mime type for {@code image/jpeg}.
	 * <p>
	 *  公共常量MIME类型为{@code image / jpeg}
	 * 
	 */
	public final static MimeType IMAGE_JPEG;

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_JPEG}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#IMAGE_JPEG}的String
	 * 
	 */
	public final static String IMAGE_JPEG_VALUE = "image/jpeg";

	/**
	 * Public constant mime type for {@code image/png}.
	 * <p>
	 *  公共常量MIME类型为{@code image / png}
	 * 
	 */
	public final static MimeType IMAGE_PNG;

	/**
	 * A String equivalent of {@link MimeTypeUtils#IMAGE_PNG}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#IMAGE_PNG}的String
	 * 
	 */
	public final static String IMAGE_PNG_VALUE = "image/png";

	/**
	 * Public constant mime type for {@code multipart/form-data}.
	 * <p>
	 *  公共常量MIME类型为{@code multipart / form-data}
	 * 
	 * 
	 *  */
	public final static MimeType MULTIPART_FORM_DATA;

	/**
	 * A String equivalent of {@link MimeTypeUtils#MULTIPART_FORM_DATA}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#MULTIPART_FORM_DATA}的String
	 * 
	 */
	public final static String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

	/**
	 * Public constant mime type for {@code text/html}.
	 * <p>
	 *  {@code text / html}的公共常量MIME类型
	 * 
	 * 
	 *  */
	public final static MimeType TEXT_HTML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_HTML}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#TEXT_HTML}的String
	 * 
	 */
	public final static String TEXT_HTML_VALUE = "text/html";

	/**
	 * Public constant mime type for {@code text/plain}.
	 * <p>
	 *  公共常量MIME类型为{@code text / plain}
	 * 
	 * 
	 *  */
	public final static MimeType TEXT_PLAIN;

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_PLAIN}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#TEXT_PLAIN}的String
	 * 
	 */
	public final static String TEXT_PLAIN_VALUE = "text/plain";

	/**
	 * Public constant mime type for {@code text/xml}.
	 * <p>
	 *  公共常量MIME类型为{@code text / xml}
	 * 
	 * 
	 *  */
	public final static MimeType TEXT_XML;

	/**
	 * A String equivalent of {@link MimeTypeUtils#TEXT_XML}.
	 * <p>
	 *  相当于{@link MimeTypeUtils#TEXT_XML}的String
	 * 
	 */
	public final static String TEXT_XML_VALUE = "text/xml";


	static {
		ALL = MimeType.valueOf(ALL_VALUE);
		APPLICATION_ATOM_XML = MimeType.valueOf(APPLICATION_ATOM_XML_VALUE);
		APPLICATION_FORM_URLENCODED = MimeType.valueOf(APPLICATION_FORM_URLENCODED_VALUE);
		APPLICATION_JSON = MimeType.valueOf(APPLICATION_JSON_VALUE);
		APPLICATION_OCTET_STREAM = MimeType.valueOf(APPLICATION_OCTET_STREAM_VALUE);
		APPLICATION_XHTML_XML = MimeType.valueOf(APPLICATION_XHTML_XML_VALUE);
		APPLICATION_XML = MimeType.valueOf(APPLICATION_XML_VALUE);
		IMAGE_GIF = MimeType.valueOf(IMAGE_GIF_VALUE);
		IMAGE_JPEG = MimeType.valueOf(IMAGE_JPEG_VALUE);
		IMAGE_PNG = MimeType.valueOf(IMAGE_PNG_VALUE);
		MULTIPART_FORM_DATA = MimeType.valueOf(MULTIPART_FORM_DATA_VALUE);
		TEXT_HTML = MimeType.valueOf(TEXT_HTML_VALUE);
		TEXT_PLAIN = MimeType.valueOf(TEXT_PLAIN_VALUE);
		TEXT_XML = MimeType.valueOf(TEXT_XML_VALUE);
	}


	/**
	 * Parse the given String into a single {@code MimeType}.
	 * <p>
	 * 将给定的字符串解析为单个{@code MimeType}
	 * 
	 * 
	 * @param mimeType the string to parse
	 * @return the mime type
	 * @throws InvalidMimeTypeException if the string cannot be parsed
	 */
	public static MimeType parseMimeType(String mimeType) {
		if (!StringUtils.hasLength(mimeType)) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}
		String[] parts = StringUtils.tokenizeToStringArray(mimeType, ";");
		if (parts.length == 0) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}

		String fullType = parts[0].trim();
		// java.net.HttpURLConnection returns a *; q=.2 Accept header
		if (MimeType.WILDCARD_TYPE.equals(fullType)) {
			fullType = "*/*";
		}
		int subIndex = fullType.indexOf('/');
		if (subIndex == -1) {
			throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
		}
		if (subIndex == fullType.length() - 1) {
			throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
		}
		String type = fullType.substring(0, subIndex);
		String subtype = fullType.substring(subIndex + 1, fullType.length());
		if (MimeType.WILDCARD_TYPE.equals(type) && !MimeType.WILDCARD_TYPE.equals(subtype)) {
			fullType = "* <p>
			fullType = "*  } int subIndex = fullTypeindexOf('/'); if(subIndex == -1){throw new InvalidMimeTypeException(mimeType,"不包含'/'"); }
			fullType = "*  if(subIndex == fullTypelength() -  1){throw new InvalidMimeTypeException(mimeType,"在'/'之后不包含子类型) } S
			fullType = "* tring type = fullTypesubstring(0,subIndex); String subtype = fullTypesubstring(subIndex + 1,fullTypel
			fullType = "* ength()); if(MimeTypeWILDCARD_TYPEequals(type)&&！MimeTypeWILDCARD_TYPEequals(subtype)){。
			fullType = "* 
			fullType = "* 
			throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
		}

		Map<String, String> parameters = null;
		if (parts.length > 1) {
			parameters = new LinkedHashMap<String, String>(parts.length - 1);
			for (int i = 1; i < parts.length; i++) {
				String parameter = parts[i];
				int eqIndex = parameter.indexOf('=');
				if (eqIndex != -1) {
					String attribute = parameter.substring(0, eqIndex);
					String value = parameter.substring(eqIndex + 1, parameter.length());
					parameters.put(attribute, value);
				}
			}
		}

		try {
			return new MimeType(type, subtype, parameters);
		}
		catch (UnsupportedCharsetException ex) {
			throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidMimeTypeException(mimeType, ex.getMessage());
		}
	}

	/**
	 * Parse the given, comma-separated string into a list of {@code MimeType} objects.
	 * <p>
	 *  }
	 * 
	 * Map <String,String> parameters = null; if(partslength> 1){parameters = new LinkedHashMap <String,String>(partslength  -  1); for(int i = 1; i <partslength; i ++){String parameter = parts [i]; int eqIndex = parameterindexOf('='); if(eqIndex！= -1){String attribute = parametersubstring(0,eqIndex); String value = parametersubstring(eqIndex + 1,parameterlength()); parametersput(attribute,value); }}}。
	 * 
	 *  尝试{return new MimeType(type,subtype,parameters); } catch(UnsupportedCharsetException ex){throw new InvalidMimeTypeException(mimeType,"unsupported charset'"+ exgetCharsetName()+"'"); }
	 *  catch(IllegalArgumentException ex){throw new InvalidMimeTypeException(mimeType,exgetMessage()); }}。
	 * 
	 * / **将给定的逗号分隔的字符串解析为{@code MimeType}对象的列表
	 * 
	 * 
	 * @param mimeTypes the string to parse
	 * @return the list of mime types
	 * @throws IllegalArgumentException if the string cannot be parsed
	 */
	public static List<MimeType> parseMimeTypes(String mimeTypes) {
		if (!StringUtils.hasLength(mimeTypes)) {
			return Collections.emptyList();
		}
		String[] tokens = StringUtils.tokenizeToStringArray(mimeTypes, ",");
		List<MimeType> result = new ArrayList<MimeType>(tokens.length);
		for (String token : tokens) {
			result.add(parseMimeType(token));
		}
		return result;
	}

	/**
	 * Return a string representation of the given list of {@code MimeType} objects.
	 * <p>
	 *  返回{@code MimeType}对象的给定列表的字符串表示形式
	 * 
	 * 
	 * @param mimeTypes the string to parse
	 * @return the list of mime types
	 * @throws IllegalArgumentException if the String cannot be parsed
	 */
	public static String toString(Collection<? extends MimeType> mimeTypes) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<? extends MimeType> iterator = mimeTypes.iterator(); iterator.hasNext();) {
			MimeType mimeType = iterator.next();
			mimeType.appendTo(builder);
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}


	/**
	 * Sorts the given list of {@code MimeType} objects by specificity.
	 * <p>Given two mime types:
	 * <ol>
	 * <li>if either mime type has a {@linkplain MimeType#isWildcardType() wildcard type},
	 * then the mime type without the wildcard is ordered before the other.</li>
	 * <li>if the two mime types have different {@linkplain MimeType#getType() types},
	 * then they are considered equal and remain their current order.</li>
	 * <li>if either mime type has a {@linkplain MimeType#isWildcardSubtype() wildcard subtype}
	 * , then the mime type without the wildcard is sorted before the other.</li>
	 * <li>if the two mime types have different {@linkplain MimeType#getSubtype() subtypes},
	 * then they are considered equal and remain their current order.</li>
	 * <li>if the two mime types have a different amount of
	 * {@linkplain MimeType#getParameter(String) parameters}, then the mime type with the most
	 * parameters is ordered before the other.</li>
	 * </ol>
	 * <p>For example: <blockquote>audio/basic &lt; audio/* &lt; *&#047;*</blockquote>
	 * <blockquote>audio/basic;level=1 &lt; audio/basic</blockquote>
	 * <blockquote>audio/basic == text/html</blockquote> <blockquote>audio/basic ==
	 * audio/wave</blockquote>
	 * <p>
	 *  通过特定方式对{@code MimeType}对象的给定列表进行排序给定两种MIME类型：
	 * <ol>
	 * 如果任何mime类型具有{@linkplain MimeType#isWildcardType()通配符类型},则如果两个MIME类型具有不同的{@linkplain,则没有通配符的MIME类型将在另一个</li> <li>之前排序MimeType#getType()types}
	 * ,那么如果mime类型有一个{@linkplain MimeType#isWildcardSubtype()通配符子类型},那么它们被认为是相等的,并保持其当前的顺序</li> <li>,那么没有通配符
	 * 的mime类型如果两个MIME类型具有不同的{@linkplain MimeType#getSubtype()子类型},则被排序在另一个</li> <li>之前,则它们被视为相等并保持其当前顺序</li>
	 *  <li>如果两个MIME类型具有不同数量的{@linkplain MimeType#getParameter(String)参数},则具有最多参数的mime类型在其他</li>。
	 * </ol>
	 * <p>例如：<blockquote> audio / basic&lt;音频/ * * / * </blockquote> <blockquote> audio / basic; level = 1&l
	 * t;音频/基本</blockquote> <blockquote>音频/基本== text / html </blockquote> <blockquote>音频/基本==音频/波</blockquote>
	 * 。
	 * 
	 * @param mimeTypes the list of mime types to be sorted
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.3.2">HTTP 1.1: Semantics
	 * and Content, section 5.3.2</a>
	 */
	public static void sortBySpecificity(List<MimeType> mimeTypes) {
		Assert.notNull(mimeTypes, "'mimeTypes' must not be null");
		if (mimeTypes.size() > 1) {
			Collections.sort(mimeTypes, SPECIFICITY_COMPARATOR);
		}
	}

	/**
	 * Generate a random MIME boundary as bytes, often used in multipart mime types.
	 * <p>
	 * 
	 */
	public static byte[] generateMultipartBoundary() {
		byte[] boundary = new byte[RND.nextInt(11) + 30];
		for (int i = 0; i < boundary.length; i++) {
			boundary[i] = BOUNDARY_CHARS[RND.nextInt(BOUNDARY_CHARS.length)];
		}
		return boundary;
	}

	/**
	 * Generate a random MIME boundary as String, often used in multipart mime types.
	 * <p>
	 *  生成随机MIME边界作为字节,通常用于多部分mime类型
	 * 
	 */
	public static String generateMultipartBoundaryString() {
		return new String(generateMultipartBoundary(), US_ASCII);
	}



	/**
	 * Comparator used by {@link #sortBySpecificity(List)}.
	 * <p>
	 *  生成随机MIME边界为String,通常用于多部分mime类型
	 * 
	 */
	public static final Comparator<MimeType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MimeType>();

}
