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

package org.springframework.web.util;

import org.springframework.util.Assert;

/**
 * Utility class for HTML escaping. Escapes and unescapes
 * based on the W3C HTML 4.01 recommendation, handling
 * character entity references.
 *
 * <p>Reference:
 * <a href="http://www.w3.org/TR/html4/charset.html">http://www.w3.org/TR/html4/charset.html</a>
 *
 * <p>For a comprehensive set of String escaping utilities,
 * consider Apache Commons Lang and its StringEscapeUtils class.
 * We are not using that class here to avoid a runtime dependency
 * on Commons Lang just for HTML escaping. Furthermore, Spring's
 * HTML escaping is more flexible and 100% HTML 4.0 compliant.
 *
 * <p>
 *  HTML转义的实用程序类基于W3C HTML 401推荐的转义和解映射,处理字符实体引用
 * 
 * <p>参考：<a href=\"http://wwww3org/TR/html4/charsethtml\"> http：// wwww3org / TR / html4 / charsethtml </a>
 * 。
 * 
 *  <p>对于一组全面的String转义实用程序,请考虑Apache Commons Lang及其StringEscapeUtils类我们没有使用该类来避免对Commons Lang的运行时依赖关系,仅用
 * 于HTML转义。
 * 此外,Spring的HTML转义更灵活,100％符合HTML 40。
 * 
 * 
 * @author Juergen Hoeller
 * @author Martin Kersten
 * @author Craig Andrews
 * @since 01.03.2003
 */
public abstract class HtmlUtils {

	/**
	 * Shared instance of pre-parsed HTML character entity references.
	 * <p>
	 *  预先解析的HTML字符实体引用的共享实例
	 * 
	 */
	private static final HtmlCharacterEntityReferences characterEntityReferences =
			new HtmlCharacterEntityReferences();


	/**
	 * Turn special characters into HTML character references.
	 * Handles complete character set defined in HTML 4.01 recommendation.
	 * <p>Escapes all special characters to their corresponding
	 * entity reference (e.g. {@code &lt;}).
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 *  将特殊字符转换为HTML字符引用处理HTML 401中定义的完整字符集建议<p>将所有特殊字符转发到其相应的实体引用(例如{@code <})参考：
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 *  HTTP：// wwww3org / TR / HTML4 / SGML / entitieshtml
	 * </a>
	 * 
	 * @param input the (unescaped) input string
	 * @return the escaped string
	 */
	public static String htmlEscape(String input) {
		return htmlEscape(input, WebUtils.DEFAULT_CHARACTER_ENCODING);
	}

	/**
	 * Turn special characters into HTML character references.
	 * Handles complete character set defined in HTML 4.01 recommendation.
	 * <p>Escapes all special characters to their corresponding
	 * entity reference (e.g. {@code &lt;}) at least as required by the
	 * specified encoding. In other words, if a special character does
	 * not have to be escaped for the given encoding, it may not be.
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 * 将特殊字符转换为HTML字符引用处理HTML 401中定义的完整字符集建议<p>至少按指定编码的要求将所有特殊字符转发到其相应的实体引用(例如{@code <})。
	 * 换句话说,如果一个特殊的字符不需要转义为给定的编码,它可能不是<p>参考：。
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 *  HTTP：// wwww3org / TR / HTML4 / SGML / entitieshtml
	 * </a>
	 * 
	 * @param input the (unescaped) input string
	 * @param encoding the name of a supported {@link java.nio.charset.Charset charset}
	 * @return the escaped string
	 * @since 4.1.2
	 */
	public static String htmlEscape(String input, String encoding) {
		Assert.notNull(encoding, "Encoding is required");
		if (input == null) {
			return null;
		}
		StringBuilder escaped = new StringBuilder(input.length() * 2);
		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			String reference = characterEntityReferences.convertToReference(character, encoding);
			if (reference != null) {
				escaped.append(reference);
			}
			else {
				escaped.append(character);
			}
		}
		return escaped.toString();
	}

	/**
	 * Turn special characters into HTML character references.
	 * Handles complete character set defined in HTML 4.01 recommendation.
	 * <p>Escapes all special characters to their corresponding numeric
	 * reference in decimal format (&#<i>Decimal</i>;).
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 *  将特殊字符转换为HTML字符引用处理HTML 401中定义的完整字符集建议<p>将所有特殊字符转换为十进制格式的对应数字引用(&#<i>十进制</i>;)<p>参考：
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 *  HTTP：// wwww3org / TR / HTML4 / SGML / entitieshtml
	 * </a>
	 * 
	 * @param input the (unescaped) input string
	 * @return the escaped string
	 */
	public static String htmlEscapeDecimal(String input) {
		return htmlEscapeDecimal(input, WebUtils.DEFAULT_CHARACTER_ENCODING);
	}

	/**
	 * Turn special characters into HTML character references.
	 * Handles complete character set defined in HTML 4.01 recommendation.
	 * <p>Escapes all special characters to their corresponding numeric
	 * reference in decimal format (&#<i>Decimal</i>;) at least as required by the
	 * specified encoding. In other words, if a special character does
	 * not have to be escaped for the given encoding, it may not be.
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 * 将特殊字符转换为HTML字符引用处理HTML 401中定义的完整字符集建议<p>至少按照所要求的格式将所有特殊字符转换为十进制格式(&#<i>十进制</i>)中的相应数字引用指定的编码换句话说,如果特定
	 * 字符不必为给定的编码进行转义,则可能不是<p>参考：。
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 *  HTTP：// wwww3org / TR / HTML4 / SGML / entitieshtml
	 * </a>
	 * 
	 * @param input the (unescaped) input string
	 * @param encoding the name of a supported {@link java.nio.charset.Charset charset}
	 * @return the escaped string
	 * @since 4.1.2
	 */
	public static String htmlEscapeDecimal(String input, String encoding) {
		Assert.notNull(encoding, "Encoding is required");
		if (input == null) {
			return null;
		}
		StringBuilder escaped = new StringBuilder(input.length() * 2);
		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if (characterEntityReferences.isMappedToReference(character, encoding)) {
				escaped.append(HtmlCharacterEntityReferences.DECIMAL_REFERENCE_START);
				escaped.append((int) character);
				escaped.append(HtmlCharacterEntityReferences.REFERENCE_END);
			}
			else {
				escaped.append(character);
			}
		}
		return escaped.toString();
	}

	/**
	 * Turn special characters into HTML character references.
	 * Handles complete character set defined in HTML 4.01 recommendation.
	 * <p>Escapes all special characters to their corresponding numeric
	 * reference in hex format (&#x<i>Hex</i>;).
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 *  将特殊字符转换为HTML字符引用处理HTML 401中定义的完整字符集建议<p>将所有特殊字符转换为十六进制格式的对应数字引用(&#x <i> Hex </i>;)<p>参考：
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 *  HTTP：// wwww3org / TR / HTML4 / SGML / entitieshtml
	 * </a>
	 * 
	 * @param input the (unescaped) input string
	 * @return the escaped string
	 */
	public static String htmlEscapeHex(String input) {
		return htmlEscapeHex(input, WebUtils.DEFAULT_CHARACTER_ENCODING);
	}

	/**
	 * Turn special characters into HTML character references.
	 * Handles complete character set defined in HTML 4.01 recommendation.
	 * <p>Escapes all special characters to their corresponding numeric
	 * reference in hex format (&#x<i>Hex</i>;) at least as required by the
	 * specified encoding. In other words, if a special character does
	 * not have to be escaped for the given encoding, it may not be.
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 * 将特殊字符转换为HTML字符引用处理HTML 401中定义的完整字符集建议<p>至少按照十六进制格式(&#x <i> Hex </i>)将所有特殊字符转换为对应的数字引用指定的编码换句话说,如果特定字符
	 * 不必为给定的编码进行转义,则可能不是<p>参考：。
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 *  HTTP：// wwww3org / TR / HTML4 / SGML / entitieshtml
	 * </a>
	 * 
	 * @param input the (unescaped) input string
	 * @param encoding the name of a supported {@link java.nio.charset.Charset charset}
	 * @return the escaped string
	 * @since 4.1.2
	 */
	public static String htmlEscapeHex(String input, String encoding) {
		Assert.notNull(encoding, "Encoding is required");
		if (input == null) {
			return null;
		}
		StringBuilder escaped = new StringBuilder(input.length() * 2);
		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			if (characterEntityReferences.isMappedToReference(character, encoding)) {
				escaped.append(HtmlCharacterEntityReferences.HEX_REFERENCE_START);
				escaped.append(Integer.toString(character, 16));
				escaped.append(HtmlCharacterEntityReferences.REFERENCE_END);
			}
			else {
				escaped.append(character);
			}
		}
		return escaped.toString();
	}

	/**
	 * Turn HTML character references into their plain text UNICODE equivalent.
	 * <p>Handles complete character set defined in HTML 4.01 recommendation
	 * and all reference types (decimal, hex, and entity).
	 * <p>Correctly converts the following formats:
	 * <blockquote>
	 * &amp;#<i>Entity</i>; - <i>(Example: &amp;amp;) case sensitive</i>
	 * &amp;#<i>Decimal</i>; - <i>(Example: &amp;#68;)</i><br>
	 * &amp;#x<i>Hex</i>; - <i>(Example: &amp;#xE5;) case insensitive</i><br>
	 * </blockquote>
	 * Gracefully handles malformed character references by copying original
	 * characters as is when encountered.<p>
	 * <p>Reference:
	 * <a href="http://www.w3.org/TR/html4/sgml/entities.html">
	 * http://www.w3.org/TR/html4/sgml/entities.html
	 * </a>
	 * <p>
	 *  将HTML字符引用转换为纯文本UNICODE等效<p>处理HTML 401推荐和所有引用类型(十进制,十六进制和实体)中定义的完整字符集<p>正确转换以下格式：
	 * <blockquote>
	 * &安培;#<I>实体</i>的; <i>(示例：&amp; amp;))区分大小写</i>&amp;#<i>十进制</i>; -  <i>(示例：&#68;)</i> <br>&amp; #x <i> 
	 * Hex </i>; <i>(示例：&amp;#xE5;)不区分大小写</i> <br>。
	 * </blockquote>
	 * 
	 * @param input the (escaped) input string
	 * @return the unescaped string
	 */
	public static String htmlUnescape(String input) {
		if (input == null) {
			return null;
		}
		return new HtmlCharacterEntityDecoder(characterEntityReferences, input).decode();
	}

}
