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

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/**
 * Represents a MIME Type, as originally defined in RFC 2046 and subsequently used in
 * other Internet protocols including HTTP.
 *
 * <p>This class, however, does not contain support for the q-parameters used
 * in HTTP content negotiation. Those can be found in the sub-class
 * {@code org.springframework.http.MediaType} in the {@code spring-web} module.
 *
 * <p>Consists of a {@linkplain #getType() type} and a {@linkplain #getSubtype() subtype}.
 * Also has functionality to parse media types from a string using
 * {@link #valueOf(String)}. For more parsing options see {@link MimeTypeUtils}.
 *
 * <p>
 *  表示MIME类型,如RFC 2046中最初定义的,随后在其他Internet协议(包括HTTP)中使用
 * 
 * <p>然而,此类不包含对HTTP内容协商中使用的q参数的支持。
 * 这些可以在{@code spring-web}模块的子类{@code orgspringframeworkhttpMediaType}中找到。
 * 
 *  <p>包含{@linkplain #getType()类型}和{@linkplain #getSubtype()子类型}还具有使用{@link #valueOf(String)}从字符串中解析媒体类型
 * 的功能。
 * 有关更多解析选项请参阅{@link MimeTypeUtils}。
 * 
 * 
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.0
 * @see MimeTypeUtils
 */
public class MimeType implements Comparable<MimeType>, Serializable {

	private static final long serialVersionUID = 4085923477777865903L;


	protected static final String WILDCARD_TYPE = "*";

	private static final String PARAM_CHARSET = "charset";

	private static final BitSet TOKEN;

	static {
		// variable names refer to RFC 2616, section 2.2
		BitSet ctl = new BitSet(128);
		for (int i = 0; i <= 31; i++) {
			ctl.set(i);
		}
		ctl.set(127);

		BitSet separators = new BitSet(128);
		separators.set('(');
		separators.set(')');
		separators.set('<');
		separators.set('>');
		separators.set('@');
		separators.set(',');
		separators.set(';');
		separators.set(':');
		separators.set('\\');
		separators.set('\"');
		separators.set('/');
		separators.set('[');
		separators.set(']');
		separators.set('?');
		separators.set('=');
		separators.set('{');
		separators.set('}');
		separators.set(' ');
		separators.set('\t');

		TOKEN = new BitSet(128);
		TOKEN.set(0, 128);
		TOKEN.andNot(ctl);
		TOKEN.andNot(separators);
	}


	private final String type;

	private final String subtype;

	private final Map<String, String> parameters;


	/**
	 * Create a new {@code MimeType} for the given primary type.
	 * <p>The {@linkplain #getSubtype() subtype} is set to <code>"&#42;"</code>,
	 * and the parameters are empty.
	 * <p>
	 *  为给定的主类型创建新的{@code MimeType} <p>将{@linkplain #getSubtype()子类型}设置为<code>"*"</code>,参数为空
	 * 
	 * 
	 * @param type the primary type
	 * @throws IllegalArgumentException if any of the parameters contains illegal characters
	 */
	public MimeType(String type) {
		this(type, WILDCARD_TYPE);
	}

	/**
	 * Create a new {@code MimeType} for the given primary type and subtype.
	 * <p>The parameters are empty.
	 * <p>
	 *  为给定的主要类型和子类型创建新的{@code MimeType} <p>参数为空
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @throws IllegalArgumentException if any of the parameters contains illegal characters
	 */
	public MimeType(String type, String subtype) {
		this(type, subtype, Collections.<String, String>emptyMap());
	}

	/**
	 * Create a new {@code MimeType} for the given type, subtype, and character set.
	 * <p>
	 *  为给定的类型,子类型和字符集创建一个新的{@code MimeType}
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @param charset the character set
	 * @throws IllegalArgumentException if any of the parameters contains illegal characters
	 */
	public MimeType(String type, String subtype, Charset charset) {
		this(type, subtype, Collections.singletonMap(PARAM_CHARSET, charset.name()));
	}

	/**
	 * Copy-constructor that copies the type, subtype, parameters of the given {@code MimeType},
	 * and allows to set the specified character set.
	 * <p>
	 * 复制构造函数,用于复制给定{@code MimeType}的类型,子类型,参数,并允许设置指定的字符集
	 * 
	 * 
	 * @param other the other media type
	 * @param charset the character set
	 * @throws IllegalArgumentException if any of the parameters contains illegal characters
	 * @since 4.3
	 */
	public MimeType(MimeType other, Charset charset) {
		this(other.getType(), other.getSubtype(), addCharsetParameter(charset, other.getParameters()));
	}

	/**
	 * Copy-constructor that copies the type and subtype of the given {@code MimeType},
	 * and allows for different parameter.
	 * <p>
	 *  复制构造函数,它复制给定的{@code MimeType}的类型和子类型,并允许不同的参数
	 * 
	 * 
	 * @param other the other media type
	 * @param parameters the parameters, may be {@code null}
	 * @throws IllegalArgumentException if any of the parameters contains illegal characters
	 */
	public MimeType(MimeType other, Map<String, String> parameters) {
		this(other.getType(), other.getSubtype(), parameters);
	}

	/**
	 * Create a new {@code MimeType} for the given type, subtype, and parameters.
	 * <p>
	 *  为给定的类型,子类型和参数创建一个新的{@code MimeType}
	 * 
	 * 
	 * @param type the primary type
	 * @param subtype the subtype
	 * @param parameters the parameters, may be {@code null}
	 * @throws IllegalArgumentException if any of the parameters contains illegal characters
	 */
	public MimeType(String type, String subtype, Map<String, String> parameters) {
		Assert.hasLength(type, "type must not be empty");
		Assert.hasLength(subtype, "subtype must not be empty");
		checkToken(type);
		checkToken(subtype);
		this.type = type.toLowerCase(Locale.ENGLISH);
		this.subtype = subtype.toLowerCase(Locale.ENGLISH);
		if (!CollectionUtils.isEmpty(parameters)) {
			Map<String, String> map = new LinkedCaseInsensitiveMap<String>(parameters.size(), Locale.ENGLISH);
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				String attribute = entry.getKey();
				String value = entry.getValue();
				checkParameters(attribute, value);
				map.put(attribute, value);
			}
			this.parameters = Collections.unmodifiableMap(map);
		}
		else {
			this.parameters = Collections.emptyMap();
		}
	}

	/**
	 * Checks the given token string for illegal characters, as defined in RFC 2616,
	 * section 2.2.
	 * <p>
	 *  检查给定的令牌字符串的非法字符,如RFC 2616第22节所定义
	 * 
	 * 
	 * @throws IllegalArgumentException in case of illegal characters
	 * @see <a href="http://tools.ietf.org/html/rfc2616#section-2.2">HTTP 1.1, section 2.2</a>
	 */
	private void checkToken(String token) {
		for (int i = 0; i < token.length(); i++ ) {
			char ch = token.charAt(i);
			if (!TOKEN.get(ch)) {
				throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
			}
		}
	}

	protected void checkParameters(String attribute, String value) {
		Assert.hasLength(attribute, "parameter attribute must not be empty");
		Assert.hasLength(value, "parameter value must not be empty");
		checkToken(attribute);
		if (PARAM_CHARSET.equals(attribute)) {
			value = unquote(value);
			Charset.forName(value);
		}
		else if (!isQuotedString(value)) {
			checkToken(value);
		}
	}

	private boolean isQuotedString(String s) {
		if (s.length() < 2) {
			return false;
		}
		else {
			return ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
		}
	}

	protected String unquote(String s) {
		if (s == null) {
			return null;
		}
		return isQuotedString(s) ? s.substring(1, s.length() - 1) : s;
	}

	/**
	 * Indicates whether the {@linkplain #getType() type} is the wildcard character
	 * <code>&#42;</code> or not.
	 * <p>
	 *  指示{@linkplain #getType()类型}是否为通配符<code> * </code>
	 * 
	 */
	public boolean isWildcardType() {
		return WILDCARD_TYPE.equals(getType());
	}

	/**
	 * Indicates whether the {@linkplain #getSubtype() subtype} is the wildcard
	 * character <code>&#42;</code> or the wildcard character followed by a suffix
	 * (e.g. <code>&#42;+xml</code>).
	 * <p>
	 *  指示{@linkplain #getSubtype()子类型}是通配符<code> * </code>还是通配符后跟后缀(例如<code> * + xml </code>)
	 * 
	 * 
	 * @return whether the subtype is a wildcard
	 */
	public boolean isWildcardSubtype() {
		return WILDCARD_TYPE.equals(getSubtype()) || getSubtype().startsWith("*+");
	}

	/**
	 * Indicates whether this media type is concrete, i.e. whether neither the type
	 * nor the subtype is a wildcard character <code>&#42;</code>.
	 * <p>
	 * 指示此媒体类型是否具体,即类型和子类型是否都是通配符<code> * </code>
	 * 
	 * 
	 * @return whether this media type is concrete
	 */
	public boolean isConcrete() {
		return !isWildcardType() && !isWildcardSubtype();
	}

	/**
	 * Return the primary type.
	 * <p>
	 *  返回主类型
	 * 
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Return the subtype.
	 * <p>
	 *  返回子类型
	 * 
	 */
	public String getSubtype() {
		return this.subtype;
	}

	/**
	 * Return the character set, as indicated by a {@code charset} parameter, if any.
	 * <p>
	 *  返回字符集,如{@code charset}参数所示
	 * 
	 * 
	 * @return the character set, or {@code null} if not available
	 * @since 4.3
	 */
	public Charset getCharset() {
		String charSet = getParameter(PARAM_CHARSET);
		return (charSet != null ? Charset.forName(unquote(charSet)) : null);
	}

	/**
	 * Return the character set, as indicated by a {@code charset} parameter, if any.
	 * <p>
	 *  返回字符集,如{@code charset}参数所示
	 * 
	 * 
	 * @return the character set, or {@code null} if not available
	 * @deprecated as of Spring 4.3, in favor of {@link #getCharset()} with its name
	 * aligned with the Java return type name
	 */
	@Deprecated
	public Charset getCharSet() {
		return getCharset();
	}

	/**
	 * Return a generic parameter value, given a parameter name.
	 * <p>
	 *  给定一个参数名,返回一个通用参数值
	 * 
	 * 
	 * @param name the parameter name
	 * @return the parameter value, or {@code null} if not present
	 */
	public String getParameter(String name) {
		return this.parameters.get(name);
	}

	/**
	 * Return all generic parameter values.
	 * <p>
	 *  返回所有通用参数值
	 * 
	 * 
	 * @return a read-only map (possibly empty, never {@code null})
	 */
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	/**
	 * Indicate whether this {@code MediaType} includes the given media type.
	 * <p>For instance, {@code text/*} includes {@code text/plain} and {@code text/html},
	 * and {@code application/*+xml} includes {@code application/soap+xml}, etc. This
	 * method is <b>not</b> symmetric.
	 * <p>
	 *  指示此{@code MediaType}是否包含给定的媒体类型<p>例如,{@code text / *}包括{@code text / plain}和{@code text / html}和{@code application / * + xml}
	 * 包括{@code application / soap + xml}等。
	 * 这个方法是<b>不</b>对称。
	 * 
	 * 
	 * @param other the reference media type with which to compare
	 * @return {@code true} if this media type includes the given media type;
	 * {@code false} otherwise
	 */
	public boolean includes(MimeType other) {
		if (other == null) {
			return false;
		}
		if (this.isWildcardType()) {
			// */* includes anything
			return true;
		}
		else if (getType().equals(other.getType())) {
			if (getSubtype().equals(other.getSubtype())) {
				return true;
			}
			if (this.isWildcardSubtype()) {
				// wildcard with suffix, e.g. application/*+xml
				int thisPlusIdx = getSubtype().indexOf('+');
				if (thisPlusIdx == -1) {
					return true;
				}
				else {
					// application/*+xml includes application/soap+xml
					int otherPlusIdx = other.getSubtype().indexOf('+');
					if (otherPlusIdx != -1) {
						String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
						String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
						String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
						if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && WILDCARD_TYPE.equals(thisSubtypeNoSuffix)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Indicate whether this {@code MediaType} is compatible with the given media type.
	 * <p>For instance, {@code text/*} is compatible with {@code text/plain},
	 * {@code text/html}, and vice versa. In effect, this method is similar to
	 * {@link #includes}, except that it <b>is</b> symmetric.
	 * <p>
	 * 返回真} else if(getType()equals(othergetType())){if(getSubtype()equals(othergetSubtype())){return true; }
	 *  if(thisisWildcardSubtype()){//具有后缀的通配符,例如application / * + xml int thisPlusIdx = getSubtype()indexOf('+'); if(thisPlusIdx == -1){return true; }
	 *  else {// application / * + xml include application / soap + xml int otherPlusIdx = othergetSubtype()indexOf('+'); if(otherPlusIdx！= -1){String thisSubtypeNoSuffix = getSubtype()substring(0,thisPlusIdx); String thisSubtypeSuffix = getSubtype()substring(thisPlusIdx + 1); String otherSubtypeSuffix = othergetSubtype()substring(otherPlusIdx + 1); if(thisSubtypeSuffixequals(otherSubtypeSuffix)&& WILDCARD_TYPEequals(thisSubtypeNoSuffix)){return true; }
	 * }}}}} return false; }。
	 * 
	 * / **指示此{@code MediaType}是否与给定的媒体类型<p>兼容。
	 * 例如,{@code text / *}与{@code text / plain},{@code text / html}兼容,反之亦然此方法与{@link #includes}类似,只是它<b>是对称的
	 * 。
	 * / **指示此{@code MediaType}是否与给定的媒体类型<p>兼容。
	 * 
	 * 
	 * @param other the reference media type with which to compare
	 * @return {@code true} if this media type is compatible with the given media type;
	 * {@code false} otherwise
	 */
	public boolean isCompatibleWith(MimeType other) {
		if (other == null) {
			return false;
		}
		if (isWildcardType() || other.isWildcardType()) {
			return true;
		}
		else if (getType().equals(other.getType())) {
			if (getSubtype().equals(other.getSubtype())) {
				return true;
			}
			// wildcard with suffix? e.g. application/*+xml
			if (this.isWildcardSubtype() || other.isWildcardSubtype()) {

				int thisPlusIdx = getSubtype().indexOf('+');
				int otherPlusIdx = other.getSubtype().indexOf('+');

				if (thisPlusIdx == -1 && otherPlusIdx == -1) {
					return true;
				}
				else if (thisPlusIdx != -1 && otherPlusIdx != -1) {
					String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
					String otherSubtypeNoSuffix = other.getSubtype().substring(0, otherPlusIdx);

					String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
					String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);

					if (thisSubtypeSuffix.equals(otherSubtypeSuffix) &&
							(WILDCARD_TYPE.equals(thisSubtypeNoSuffix) || WILDCARD_TYPE.equals(otherSubtypeNoSuffix))) {
						return true;
					}
				}
			}
		}
		return false;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MimeType)) {
			return false;
		}
		MimeType otherType = (MimeType) other;
		return (this.type.equalsIgnoreCase(otherType.type) &&
				this.subtype.equalsIgnoreCase(otherType.subtype) &&
				parametersAreEqual(otherType));
	}

	/**
	 * Determine if the parameters in this {@code MimeType} and the supplied
	 * {@code MimeType} are equal, performing case-insensitive comparisons
	 * for {@link Charset}s.
	 * <p>
	 *  if(thisisWildcardSubtype()|| otherisWildcardSubtype()){
	 * 
	 *  int thisPlusIdx = getSubtype()indexOf('+'); int otherPlusIdx = othergetSubtype()indexOf('+');
	 * 
	 *  if(thisPlusIdx == -1 && otherPlusIdx == -1){return true; } else if(thisPlusIdx！= -1 && otherPlusIdx！
	 * = -1){String thisSubtypeNoSuffix = getSubtype()substring(0,thisPlusIdx); String otherSubtypeNoSuffix = othergetSubtype()substring(0,otherPlusIdx);。
	 * 
	 * String thisSubtypeSuffix = getSubtype()substring(thisPlusIdx + 1); String otherSubtypeSuffix = otherg
	 * etSubtype()substring(otherPlusIdx + 1);。
	 * 
	 *  if(thisSubtypeSuffixequals(otherSubtypeSuffix)&&(WILDCARD_TYPEequals(thisSubtypeNoSuffix)|| WILDCARD
	 * _TYPEequals(otherSubtypeNoSuffix))){return true; }}}} return false; }。
	 * 
	 *  @Override public boolean equals(Object other){if(this == other){return true; } if(！(other instanceof
	 *  MimeType)){return false; } MimeType otherType =(MimeType)other; return(thistypeequalsIgnoreCase(othe
	 * 
	 * @since 4.2
	 */
	private boolean parametersAreEqual(MimeType other) {
		if (this.parameters.size() != other.parameters.size()) {
			return false;
		}

		for (String key : this.parameters.keySet()) {
			if (!other.parameters.containsKey(key)) {
				return false;
			}

			if (PARAM_CHARSET.equals(key)) {
				if (!ObjectUtils.nullSafeEquals(getCharset(), other.getCharset())) {
					return false;
				}
			}
			else if (!ObjectUtils.nullSafeEquals(this.parameters.get(key), other.parameters.get(key))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.type.hashCode();
		result = 31 * result + this.subtype.hashCode();
		result = 31 * result + this.parameters.hashCode();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		appendTo(builder);
		return builder.toString();
	}

	protected void appendTo(StringBuilder builder) {
		builder.append(this.type);
		builder.append('/');
		builder.append(this.subtype);
		appendTo(this.parameters, builder);
	}

	private void appendTo(Map<String, String> map, StringBuilder builder) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			builder.append(';');
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
		}
	}

	/**
	 * Compares this {@code MediaType} to another alphabetically.
	 * <p>
	 * rTypetype)&& thissubtypeequalsIgnoreCase(otherTypesubtype)&& parametersAreEqual(otherType)); }。
	 * 
	 * / **确定此{@code MimeType}和提供的{@code MimeType}中的参数是否相等,对{@link Charset}执行不区分大小写的比较
	 * 
	 * 
	 * @param other media type to compare to
	 * @see MimeTypeUtils#sortBySpecificity(List)
	 */
	@Override
	public int compareTo(MimeType other) {
		int comp = getType().compareToIgnoreCase(other.getType());
		if (comp != 0) {
			return comp;
		}
		comp = getSubtype().compareToIgnoreCase(other.getSubtype());
		if (comp != 0) {
			return comp;
		}
		comp = getParameters().size() - other.getParameters().size();
		if (comp != 0) {
			return comp;
		}
		TreeSet<String> thisAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		thisAttributes.addAll(getParameters().keySet());
		TreeSet<String> otherAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		otherAttributes.addAll(other.getParameters().keySet());
		Iterator<String> thisAttributesIterator = thisAttributes.iterator();
		Iterator<String> otherAttributesIterator = otherAttributes.iterator();
		while (thisAttributesIterator.hasNext()) {
			String thisAttribute = thisAttributesIterator.next();
			String otherAttribute = otherAttributesIterator.next();
			comp = thisAttribute.compareToIgnoreCase(otherAttribute);
			if (comp != 0) {
				return comp;
			}
			String thisValue = getParameters().get(thisAttribute);
			String otherValue = other.getParameters().get(otherAttribute);
			if (otherValue == null) {
				otherValue = "";
			}
			comp = thisValue.compareTo(otherValue);
			if (comp != 0) {
				return comp;
			}
		}
		return 0;
	}


	/**
	 * Parse the given String value into a {@code MimeType} object,
	 * with this method name following the 'valueOf' naming convention
	 * (as supported by {@link org.springframework.core.convert.ConversionService}.
	 * <p>
	 *  将此{@code MediaType}与其他字母顺序进行比较
	 * 
	 * 
	 * @see MimeTypeUtils#parseMimeType(String)
	 */
	public static MimeType valueOf(String value) {
		return MimeTypeUtils.parseMimeType(value);
	}

	private static Map<String, String> addCharsetParameter(Charset charset, Map<String, String> parameters) {
		Map<String, String> map = new LinkedHashMap<String, String>(parameters);
		map.put(PARAM_CHARSET, charset.name());
		return map;
	}


	public static class SpecificityComparator<T extends MimeType> implements Comparator<T> {

		@Override
		public int compare(T mimeType1, T mimeType2) {
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // */* < audio/*
				return 1;
			}
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // * <p>
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // *  将给定的String值解析为{@code MimeType}对象,该方法名称遵循'valueOf'命名约定(由{@link orgspringframeworkcoreconvertConversionService}
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // * 支持)。
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // * 
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // * 
			else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/* > */*
				return -1;
			}
			else if (!mimeType1.getType().equals(mimeType2.getType())) { // audio/basic == text/html
				return 0;
			}
			else { // mediaType1.getType().equals(mediaType2.getType())
				if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/* < audio/basic
					return 1;
				}
				else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic > audio/*
					return -1;
				}
				else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // audio/basic == audio/wave
					return 0;
				}
				else { // mediaType2.getSubtype().equals(mediaType2.getSubtype())
					return compareParameters(mimeType1, mimeType2);
				}
			}
		}

		protected int compareParameters(T mimeType1, T mimeType2) {
			int paramsSize1 = mimeType1.getParameters().size();
			int paramsSize2 = mimeType2.getParameters().size();
			return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1)); // audio/basic;level=1 < audio/basic
		}
	}

}
