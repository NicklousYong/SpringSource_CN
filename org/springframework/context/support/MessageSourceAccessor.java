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

package org.springframework.context.support;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Helper class for easy access to messages from a MessageSource,
 * providing various overloaded getMessage methods.
 *
 * <p>Available from ApplicationObjectSupport, but also reusable
 * as a standalone helper to delegate to in application objects.
 *
 * <p>
 *  Helper类可以方便地从MessageSource访问消息,从而提供各种重载的getMessage方法
 * 
 * <p>可从ApplicationObjectSupport获得,但也可以作为独立的助手重用,以便委托给应用程序对象
 * 
 * 
 * @author Juergen Hoeller
 * @since 23.10.2003
 * @see ApplicationObjectSupport#getMessageSourceAccessor
 */
public class MessageSourceAccessor {

	private final MessageSource messageSource;

	private final Locale defaultLocale;

	/**
	 * Create a new MessageSourceAccessor, using LocaleContextHolder's locale
	 * as default locale.
	 * <p>
	 *  创建一个新的MessageSourceAccessor,使用LocaleContextHolder的语言环境作为默认语言环境
	 * 
	 * 
	 * @param messageSource the MessageSource to wrap
	 * @see org.springframework.context.i18n.LocaleContextHolder#getLocale()
	 */
	public MessageSourceAccessor(MessageSource messageSource) {
		this.messageSource = messageSource;
		this.defaultLocale = null;
	}

	/**
	 * Create a new MessageSourceAccessor, using the given default locale.
	 * <p>
	 *  使用给定的默认语言环境创建一个新的MessageSourceAccessor
	 * 
	 * 
	 * @param messageSource the MessageSource to wrap
	 * @param defaultLocale the default locale to use for message access
	 */
	public MessageSourceAccessor(MessageSource messageSource, Locale defaultLocale) {
		this.messageSource = messageSource;
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Return the default locale to use if no explicit locale has been given.
	 * <p>The default implementation returns the default locale passed into the
	 * corresponding constructor, or LocaleContextHolder's locale as fallback.
	 * Can be overridden in subclasses.
	 * <p>
	 *  返回默认语言环境以使用,如果没有给出显式语言环境。<p>默认实现返回传递给相应构造函数的默认语言环境,或者LocaleContextHolder的语言环境作为备用可以在子类中被覆盖
	 * 
	 * 
	 * @see #MessageSourceAccessor(org.springframework.context.MessageSource, java.util.Locale)
	 * @see org.springframework.context.i18n.LocaleContextHolder#getLocale()
	 */
	protected Locale getDefaultLocale() {
		return (this.defaultLocale != null ? this.defaultLocale : LocaleContextHolder.getLocale());
	}

	/**
	 * Retrieve the message for the given code and the default Locale.
	 * <p>
	 *  检索给定代码和默认区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getMessage(String code, String defaultMessage) {
		return this.messageSource.getMessage(code, null, defaultMessage, getDefaultLocale());
	}

	/**
	 * Retrieve the message for the given code and the given Locale.
	 * <p>
	 *  检索给定代码和给定的区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param defaultMessage String to return if the lookup fails
	 * @param locale Locale in which to do lookup
	 * @return the message
	 */
	public String getMessage(String code, String defaultMessage, Locale locale) {
		return this.messageSource.getMessage(code, null, defaultMessage, locale);
	}

	/**
	 * Retrieve the message for the given code and the default Locale.
	 * <p>
	 *  检索给定代码和默认区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @return the message
	 */
	public String getMessage(String code, Object[] args, String defaultMessage) {
		return this.messageSource.getMessage(code, args, defaultMessage, getDefaultLocale());
	}

	/**
	 * Retrieve the message for the given code and the given Locale.
	 * <p>
	 *  检索给定代码和给定的区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param defaultMessage String to return if the lookup fails
	 * @param locale Locale in which to do lookup
	 * @return the message
	 */
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return this.messageSource.getMessage(code, args, defaultMessage, locale);
	}

	/**
	 * Retrieve the message for the given code and the default Locale.
	 * <p>
	 * 检索给定代码和默认区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code) throws NoSuchMessageException {
		return this.messageSource.getMessage(code, null, getDefaultLocale());
	}

	/**
	 * Retrieve the message for the given code and the given Locale.
	 * <p>
	 *  检索给定代码和给定的区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param locale Locale in which to do lookup
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code, Locale locale) throws NoSuchMessageException {
		return this.messageSource.getMessage(code, null, locale);
	}

	/**
	 * Retrieve the message for the given code and the default Locale.
	 * <p>
	 *  检索给定代码和默认区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code, Object[] args) throws NoSuchMessageException {
		return this.messageSource.getMessage(code, args, getDefaultLocale());
	}

	/**
	 * Retrieve the message for the given code and the given Locale.
	 * <p>
	 *  检索给定代码和给定的区域设置的消息
	 * 
	 * 
	 * @param code code of the message
	 * @param args arguments for the message, or {@code null} if none
	 * @param locale Locale in which to do lookup
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return this.messageSource.getMessage(code, args, locale);
	}

	/**
	 * Retrieve the given MessageSourceResolvable (e.g. an ObjectError instance)
	 * in the default Locale.
	 * <p>
	 *  在默认语言环境中检索给定的MessageSourceResolvable(例如ObjectError实例)
	 * 
	 * 
	 * @param resolvable the MessageSourceResolvable
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
		return this.messageSource.getMessage(resolvable, getDefaultLocale());
	}

	/**
	 * Retrieve the given MessageSourceResolvable (e.g. an ObjectError instance)
	 * in the given Locale.
	 * <p>
	 *  在给定的区域设置中检索给定的MessageSourceResolvable(例如ObjectError实例)
	 * 
	 * @param resolvable the MessageSourceResolvable
	 * @param locale Locale in which to do lookup
	 * @return the message
	 * @throws org.springframework.context.NoSuchMessageException if not found
	 */
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return this.messageSource.getMessage(resolvable, locale);
	}

}
