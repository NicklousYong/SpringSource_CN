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

package org.springframework.context.support;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link MessageSourceResolvable} interface.
 * Offers an easy way to store all the necessary values needed to resolve
 * a message via a {@link org.springframework.context.MessageSource}.
 *
 * <p>
 *  {@link MessageSourceResolvable}界面的默认实现提供了一种简单的方法来存储通过{@link orgspringframeworkcontextMessageSource}解
 * 析消息所需的所有必需值。
 * 
 * 
 * @author Juergen Hoeller
 * @since 13.02.2004
 * @see org.springframework.context.MessageSource#getMessage(MessageSourceResolvable, java.util.Locale)
 */
@SuppressWarnings("serial")
public class DefaultMessageSourceResolvable implements MessageSourceResolvable, Serializable {

	private final String[] codes;

	private final Object[] arguments;

	private final String defaultMessage;


	/**
	 * Create a new DefaultMessageSourceResolvable.
	 * <p>
	 * 创建一个新的DefaultMessageSourceResolvable
	 * 
	 * 
	 * @param code the code to be used to resolve this message
	 */
	public DefaultMessageSourceResolvable(String code) {
		this(new String[] {code}, null, null);
	}

	/**
	 * Create a new DefaultMessageSourceResolvable.
	 * <p>
	 *  创建一个新的DefaultMessageSourceResolvable
	 * 
	 * 
	 * @param codes the codes to be used to resolve this message
	 */
	public DefaultMessageSourceResolvable(String[] codes) {
		this(codes, null, null);
	}

	/**
	 * Create a new DefaultMessageSourceResolvable.
	 * <p>
	 *  创建一个新的DefaultMessageSourceResolvable
	 * 
	 * 
	 * @param codes the codes to be used to resolve this message
	 * @param defaultMessage the default message to be used to resolve this message
	 */
	public DefaultMessageSourceResolvable(String[] codes, String defaultMessage) {
		this(codes, null, defaultMessage);
	}

	/**
	 * Create a new DefaultMessageSourceResolvable.
	 * <p>
	 *  创建一个新的DefaultMessageSourceResolvable
	 * 
	 * 
	 * @param codes the codes to be used to resolve this message
	 * @param arguments the array of arguments to be used to resolve this message
	 */
	public DefaultMessageSourceResolvable(String[] codes, Object[] arguments) {
		this(codes, arguments, null);
	}

	/**
	 * Create a new DefaultMessageSourceResolvable.
	 * <p>
	 *  创建一个新的DefaultMessageSourceResolvable
	 * 
	 * 
	 * @param codes the codes to be used to resolve this message
	 * @param arguments the array of arguments to be used to resolve this message
	 * @param defaultMessage the default message to be used to resolve this message
	 */
	public DefaultMessageSourceResolvable(String[] codes, Object[] arguments, String defaultMessage) {
		this.codes = codes;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * Copy constructor: Create a new instance from another resolvable.
	 * <p>
	 *  复制构造函数：从另一个可解析的方法创建一个新的实例
	 * 
	 * 
	 * @param resolvable the resolvable to copy from
	 */
	public DefaultMessageSourceResolvable(MessageSourceResolvable resolvable) {
		this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
	}


	@Override
	public String[] getCodes() {
		return this.codes;
	}

	/**
	 * Return the default code of this resolvable, that is,
	 * the last one in the codes array.
	 * <p>
	 *  返回此可解析的默认代码,即代码数组中的最后一个代码
	 * 
	 */
	public String getCode() {
		return (this.codes != null && this.codes.length > 0 ? this.codes[this.codes.length - 1] : null);
	}

	@Override
	public Object[] getArguments() {
		return this.arguments;
	}

	@Override
	public String getDefaultMessage() {
		return this.defaultMessage;
	}


	/**
	 * Build a default String representation for this MessageSourceResolvable:
	 * including codes, arguments, and default message.
	 * <p>
	 *  为此MessageSourceResolvable构建默认的String表示形式：包括代码,参数和默认消息
	 * 
	 */
	protected final String resolvableToString() {
		StringBuilder result = new StringBuilder();
		result.append("codes [").append(StringUtils.arrayToDelimitedString(this.codes, ","));
		result.append("]; arguments [").append(StringUtils.arrayToDelimitedString(this.arguments, ","));
		result.append("]; default message [").append(this.defaultMessage).append(']');
		return result.toString();
	}

	/**
	 * Default implementation exposes the attributes of this MessageSourceResolvable.
	 * To be overridden in more specific subclasses, potentially including the
	 * resolvable content through {@code resolvableToString()}.
	 * <p>
	 *  默认实现公开了MessageSourceResolvable的属性要在更具体的子类中被覆盖,可能通过{@code resolvableToString()}包含可解析的内容。
	 * 
	 * @see #resolvableToString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + ": " + resolvableToString();
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MessageSourceResolvable)) {
			return false;
		}
		MessageSourceResolvable otherResolvable = (MessageSourceResolvable) other;
		return (ObjectUtils.nullSafeEquals(getCodes(), otherResolvable.getCodes()) &&
				ObjectUtils.nullSafeEquals(getArguments(), otherResolvable.getArguments()) &&
				ObjectUtils.nullSafeEquals(getDefaultMessage(), otherResolvable.getDefaultMessage()));
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(getCodes());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getArguments());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getDefaultMessage());
		return hashCode;
	}

}
