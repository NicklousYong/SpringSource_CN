/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link HierarchicalMessageSource} interface,
 * implementing common handling of message variants, making it easy
 * to implement a specific strategy for a concrete MessageSource.
 *
 * <p>Subclasses must implement the abstract {@link #resolveCode}
 * method. For efficient resolution of messages without arguments, the
 * {@link #resolveCodeWithoutArguments} method should be overridden
 * as well, resolving messages without a MessageFormat being involved.
 *
 * <p><b>Note:</b> By default, message texts are only parsed through
 * MessageFormat if arguments have been passed in for the message. In case
 * of no arguments, message texts will be returned as-is. As a consequence,
 * you should only use MessageFormat escaping for messages with actual
 * arguments, and keep all other messages unescaped. If you prefer to
 * escape all messages, set the "alwaysUseMessageFormat" flag to "true".
 *
 * <p>Supports not only MessageSourceResolvables as primary messages
 * but also resolution of message arguments that are in turn
 * MessageSourceResolvables themselves.
 *
 * <p>This class does not implement caching of messages per code, thus
 * subclasses can dynamically change messages over time. Subclasses are
 * encouraged to cache their messages in a modification-aware fashion,
 * allowing for hot deployment of updated messages.
 *
 * <p>
 *  抽象实现{@link HierarchicalMessageSource}接口,实现消息变体的常见处理,使得轻松实现具体MessageSource的具体策略
 * 
 * <p>子类必须实现抽象{@link #resolveCode}方法为了有效地解析没有参数的消息,应该覆盖{@link #resolveCodeWithoutArguments}方法,解决不涉及Messa
 * geFormat的消息。
 * 
 *  <p> <b>注意：</b>默认情况下,消息文本只能通过MessageFormat进行解析,如果参数已经被传递给消息如果没有参数,则消息文本将被原样返回。
 * 因此,您只能使用MessageFormat转义具有实际参数的消息,并保留所有其他消息未转义如果您希望转义所有消息,请将"alwaysUseMessageFormat"标志设置为"true"。
 * 
 * <p>不仅支持MessageSourceResolvables作为主消息,还支持消息参数的解析,反过来MessageSourceResolvables本身
 * 
 *  <p>此类不实现每个代码的消息缓存,因此子类可以随着时间动态地更改消息子类被鼓励以修改感知的方式缓存其消息,从而允许更新消息的热部署
 * 
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @see #resolveCode(String, java.util.Locale)
 * @see #resolveCodeWithoutArguments(String, java.util.Locale)
 * @see #setAlwaysUseMessageFormat
 * @see java.text.MessageFormat
 */
public abstract class AbstractMessageSource extends MessageSourceSupport implements HierarchicalMessageSource {

	private MessageSource parentMessageSource;

	private Properties commonMessages;

	private boolean useCodeAsDefaultMessage = false;


	@Override
	public void setParentMessageSource(MessageSource parent) {
		this.parentMessageSource = parent;
	}

	@Override
	public MessageSource getParentMessageSource() {
		return this.parentMessageSource;
	}

	/**
	 * Specify locale-independent common messages, with the message code as key
	 * and the full message String (may contain argument placeholders) as value.
	 * <p>May also link to an externally defined Properties object, e.g. defined
	 * through a {@link org.springframework.beans.factory.config.PropertiesFactoryBean}.
	 * <p>
	 *  指定与区域设置无关的公共消息,消息代码为关键字,完整消息字符串(可能包含参数占位符)为值<p>也可链接到外部定义的Properties对象,例如通过{@link orgspringframeworkbeansfactoryconfigPropertiesFactoryBean}
	 * 定义。
	 * 
	 */
	public void setCommonMessages(Properties commonMessages) {
		this.commonMessages = commonMessages;
	}

	/**
	 * Return a Properties object defining locale-independent common messages, if any.
	 * <p>
	 * 返回一个定义与区域设置无关的公共消息(如果有的话)的Properties对象
	 * 
	 */
	protected Properties getCommonMessages() {
		return this.commonMessages;
	}

	/**
	 * Set whether to use the message code as default message instead of
	 * throwing a NoSuchMessageException. Useful for development and debugging.
	 * Default is "false".
	 * <p>Note: In case of a MessageSourceResolvable with multiple codes
	 * (like a FieldError) and a MessageSource that has a parent MessageSource,
	 * do <i>not</i> activate "useCodeAsDefaultMessage" in the <i>parent</i>:
	 * Else, you'll get the first code returned as message by the parent,
	 * without attempts to check further codes.
	 * <p>To be able to work with "useCodeAsDefaultMessage" turned on in the parent,
	 * AbstractMessageSource and AbstractApplicationContext contain special checks
	 * to delegate to the internal {@link #getMessageInternal} method if available.
	 * In general, it is recommended to just use "useCodeAsDefaultMessage" during
	 * development and not rely on it in production in the first place, though.
	 * <p>
	 * 设置是否使用消息代码作为默认消息,而不是抛出NoSuchMessageException有用于开发和调试有用于开发和调试默认值为"false"<p>注意：如果具有多个代码(如FieldError)和Me
	 * ssageSource具有父级的MessageSourceResolvable MessageSource,do <i>不</i>在<i>父项</i>中激活"useCodeAsDefaultMessag
	 * e"：否则,您将获得父项返回的第一个代码作为消息,而不尝试检查其他代码<p >为了能够在父项中打开"useCodeAsDefaultMessage",AbstractMessageSource和Abst
	 * ractApplicationContext包含特殊的检查,以委托内部{@link #getMessageInternal}方法(如果可用)一般来说,建议在开发过程中使用"useCodeAsDefaul
	 * tMessage",而不是首先依赖于生产,尽管。
	 * 
	 * 
	 * @see #getMessage(String, Object[], Locale)
	 * @see org.springframework.validation.FieldError
	 */
	public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
		this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
	}

	/**
	 * Return whether to use the message code as default message instead of
	 * throwing a NoSuchMessageException. Useful for development and debugging.
	 * Default is "false".
	 * <p>Alternatively, consider overriding the {@link #getDefaultMessage}
	 * method to return a custom fallback message for an unresolvable code.
	 * <p>
	 * 返回是否使用消息代码作为默认消息,而不是抛出NoSuchMessageException有用于开发和调试有用于开发和调试默认为"false"<p>或者,考虑覆盖{@link #getDefaultMessage}
	 * 方法以返回自定义回退消息,以解决不可解析的代码。
	 * 
	 * 
	 * @see #getDefaultMessage(String)
	 */
	protected boolean isUseCodeAsDefaultMessage() {
		return this.useCodeAsDefaultMessage;
	}


	@Override
	public final String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		String msg = getMessageInternal(code, args, locale);
		if (msg != null) {
			return msg;
		}
		if (defaultMessage == null) {
			String fallback = getDefaultMessage(code);
			if (fallback != null) {
				return fallback;
			}
		}
		return renderDefaultMessage(defaultMessage, args, locale);
	}

	@Override
	public final String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		String msg = getMessageInternal(code, args, locale);
		if (msg != null) {
			return msg;
		}
		String fallback = getDefaultMessage(code);
		if (fallback != null) {
			return fallback;
		}
		throw new NoSuchMessageException(code, locale);
	}

	@Override
	public final String getMessage(MessageSourceResolvable resolvable, Locale locale)
			throws NoSuchMessageException {

		String[] codes = resolvable.getCodes();
		if (codes == null) {
			codes = new String[0];
		}
		for (String code : codes) {
			String msg = getMessageInternal(code, resolvable.getArguments(), locale);
			if (msg != null) {
				return msg;
			}
		}
		String defaultMessage = resolvable.getDefaultMessage();
		if (defaultMessage != null) {
			return renderDefaultMessage(defaultMessage, resolvable.getArguments(), locale);
		}
		if (codes.length > 0) {
			String fallback = getDefaultMessage(codes[0]);
			if (fallback != null) {
				return fallback;
			}
		}
		throw new NoSuchMessageException(codes.length > 0 ? codes[codes.length - 1] : null, locale);
	}


	/**
	 * Resolve the given code and arguments as message in the given Locale,
	 * returning {@code null} if not found. Does <i>not</i> fall back to
	 * the code as default message. Invoked by {@code getMessage} methods.
	 * <p>
	 *  将给定的代码和参数解析为给定的区域设置中的消息,返回{@code null}(如果未找到)<i>不</i>返回到默认消息的代码{@code getMessage}方法
	 * 
	 * 
	 * @param code the code to lookup up, such as 'calculator.noRateSet'
	 * @param args array of arguments that will be filled in for params
	 * within the message
	 * @param locale the Locale in which to do the lookup
	 * @return the resolved message, or {@code null} if not found
	 * @see #getMessage(String, Object[], String, Locale)
	 * @see #getMessage(String, Object[], Locale)
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see #setUseCodeAsDefaultMessage
	 */
	protected String getMessageInternal(String code, Object[] args, Locale locale) {
		if (code == null) {
			return null;
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		Object[] argsToUse = args;

		if (!isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
			// Optimized resolution: no arguments to apply,
			// therefore no MessageFormat needs to be involved.
			// Note that the default implementation still uses MessageFormat;
			// this can be overridden in specific subclasses.
			String message = resolveCodeWithoutArguments(code, locale);
			if (message != null) {
				return message;
			}
		}

		else {
			// Resolve arguments eagerly, for the case where the message
			// is defined in a parent MessageSource but resolvable arguments
			// are defined in the child MessageSource.
			argsToUse = resolveArguments(args, locale);

			MessageFormat messageFormat = resolveCode(code, locale);
			if (messageFormat != null) {
				synchronized (messageFormat) {
					return messageFormat.format(argsToUse);
				}
			}
		}

		// Check locale-independent common messages for the given message code.
		Properties commonMessages = getCommonMessages();
		if (commonMessages != null) {
			String commonMessage = commonMessages.getProperty(code);
			if (commonMessage != null) {
				return formatMessage(commonMessage, args, locale);
			}
		}

		// Not found -> check parent, if any.
		return getMessageFromParent(code, argsToUse, locale);
	}

	/**
	 * Try to retrieve the given message from the parent MessageSource, if any.
	 * <p>
	 *  尝试从父MessageSource(如果有)检索给定的消息
	 * 
	 * 
	 * @param code the code to lookup up, such as 'calculator.noRateSet'
	 * @param args array of arguments that will be filled in for params
	 * within the message
	 * @param locale the Locale in which to do the lookup
	 * @return the resolved message, or {@code null} if not found
	 * @see #getParentMessageSource()
	 */
	protected String getMessageFromParent(String code, Object[] args, Locale locale) {
		MessageSource parent = getParentMessageSource();
		if (parent != null) {
			if (parent instanceof AbstractMessageSource) {
				// Call internal method to avoid getting the default code back
				// in case of "useCodeAsDefaultMessage" being activated.
				return ((AbstractMessageSource) parent).getMessageInternal(code, args, locale);
			}
			else {
				// Check parent MessageSource, returning null if not found there.
				return parent.getMessage(code, args, null, locale);
			}
		}
		// Not found in parent either.
		return null;
	}

	/**
	 * Return a fallback default message for the given code, if any.
	 * <p>Default is to return the code itself if "useCodeAsDefaultMessage" is activated,
	 * or return no fallback else. In case of no fallback, the caller will usually
	 * receive a NoSuchMessageException from {@code getMessage}.
	 * <p>
	 * 返回给定代码的回退默认消息,如果任何<p>默认值是返回代码本身,如果"useCodeAsDefaultMessage"被激活,或返回没有后备否否如果没有后备,调用者通常会收到一个NoSuchMessag
	 * eException从{@代码getMessage}。
	 * 
	 * 
	 * @param code the message code that we couldn't resolve
	 * and that we didn't receive an explicit default message for
	 * @return the default message to use, or {@code null} if none
	 * @see #setUseCodeAsDefaultMessage
	 */
	protected String getDefaultMessage(String code) {
		if (isUseCodeAsDefaultMessage()) {
			return code;
		}
		return null;
	}


	/**
	 * Searches through the given array of objects, finds any MessageSourceResolvable
	 * objects and resolves them.
	 * <p>Allows for messages to have MessageSourceResolvables as arguments.
	 * <p>
	 *  通过给定的对象数组搜索,找到任何MessageSourceResolvable对象并解析它们<p>允许邮件将MessageSourceResolvables作为参数
	 * 
	 * 
	 * @param args array of arguments for a message
	 * @param locale the locale to resolve through
	 * @return an array of arguments with any MessageSourceResolvables resolved
	 */
	@Override
	protected Object[] resolveArguments(Object[] args, Locale locale) {
		if (args == null) {
			return new Object[0];
		}
		List<Object> resolvedArgs = new ArrayList<Object>(args.length);
		for (Object arg : args) {
			if (arg instanceof MessageSourceResolvable) {
				resolvedArgs.add(getMessage((MessageSourceResolvable) arg, locale));
			}
			else {
				resolvedArgs.add(arg);
			}
		}
		return resolvedArgs.toArray(new Object[resolvedArgs.size()]);
	}

	/**
	 * Subclasses can override this method to resolve a message without arguments
	 * in an optimized fashion, i.e. to resolve without involving a MessageFormat.
	 * <p>The default implementation <i>does</i> use MessageFormat, through
	 * delegating to the {@link #resolveCode} method. Subclasses are encouraged
	 * to replace this with optimized resolution.
	 * <p>Unfortunately, {@code java.text.MessageFormat} is not implemented
	 * in an efficient fashion. In particular, it does not detect that a message
	 * pattern doesn't contain argument placeholders in the first place. Therefore,
	 * it is advisable to circumvent MessageFormat for messages without arguments.
	 * <p>
	 * 子类可以覆盖此方法以优化的方式解析不带参数的消息,即不涉及MessageFormat即可解决<p>默认实现<i>使用MessageFormat,通过委托{@link #resolveCode}方法鼓励子
	 * 类将其替换为优化的分辨率<p>不幸的是,{@code javatextMessageFormat}没有以有效的方式实现特别是,它没有检测到消息模式首先不包含参数占位符因此,建议对没有参数的消息规避Mes
	 * sageFormat。
	 * 
	 * 
	 * @param code the code of the message to resolve
	 * @param locale the Locale to resolve the code for
	 * (subclasses are encouraged to support internationalization)
	 * @return the message String, or {@code null} if not found
	 * @see #resolveCode
	 * @see java.text.MessageFormat
	 */
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		MessageFormat messageFormat = resolveCode(code, locale);
		if (messageFormat != null) {
			synchronized (messageFormat) {
				return messageFormat.format(new Object[0]);
			}
		}
		return null;
	}

	/**
	 * Subclasses must implement this method to resolve a message.
	 * <p>Returns a MessageFormat instance rather than a message String,
	 * to allow for appropriate caching of MessageFormats in subclasses.
	 * <p><b>Subclasses are encouraged to provide optimized resolution
	 * for messages without arguments, not involving MessageFormat.</b>
	 * See the {@link #resolveCodeWithoutArguments} javadoc for details.
	 * <p>
	 * 
	 * @param code the code of the message to resolve
	 * @param locale the Locale to resolve the code for
	 * (subclasses are encouraged to support internationalization)
	 * @return the MessageFormat for the message, or {@code null} if not found
	 * @see #resolveCodeWithoutArguments(String, java.util.Locale)
	 */
	protected abstract MessageFormat resolveCode(String code, Locale locale);

}
