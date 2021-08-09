/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.messaging.support;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

/**
 * A base {@link HeaderMapper} implementation
 *
 * <p>
 *  基础{@link HeaderMapper}实施
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 */
public abstract class AbstractHeaderMapper<T> implements HeaderMapper<T> {

	protected final Log logger = LogFactory.getLog(getClass());

	private String inboundPrefix = "";

	private String outboundPrefix = "";


	/**
	 * Specify a prefix to be appended to the message header name for any
	 * user-defined property that is being mapped into the MessageHeaders.
	 * The default is an empty String (no prefix).
	 * <p>
	 *  指定要附加到消息标题名称的前缀,用于映射到MessageHeaders的任何用户定义的属性默认值为空字符串(无前缀)
	 * 
	 */
	public void setInboundPrefix(String inboundPrefix) {
		this.inboundPrefix = (inboundPrefix != null ? inboundPrefix : "");
	}

	/**
	 * Specify a prefix to be appended to the protocol property name for any
	 * user-defined message header that is being mapped into the protocol-specific
	 * Message. The default is an empty String (no prefix).
	 * <p>
	 * 指定要附加到协议属性名称的前缀,用于映射到协议特定消息中的任何用户定义的消息头。缺省值为空字符串(无前缀)
	 * 
	 */
	public void setOutboundPrefix(String outboundPrefix) {
		this.outboundPrefix = (outboundPrefix != null ? outboundPrefix : "");
	}


	/**
	 * Generate the name to use to set the header defined by the specified
	 * {@code headerName} to the protocol specific message.
	 * <p>
	 *  生成用于将指定的{@code headerName}定义的标头设置为协议特定消息的名称
	 * 
	 * 
	 * @see #setOutboundPrefix
	 */
	protected String fromHeaderName(String headerName) {
		String propertyName = headerName;
		if (StringUtils.hasText(this.outboundPrefix) && !propertyName.startsWith(this.outboundPrefix)) {
			propertyName = this.outboundPrefix + headerName;
		}
		return propertyName;
	}

	/**
	 * Generate the name to use to set the header defined by the specified
	 * {@code propertyName} to the {@link MessageHeaders} instance.
	 * <p>
	 *  生成用于将指定的{@code propertyName}定义的标头设置为{@link MessageHeaders}实例的名称
	 * 
	 * 
	 * @see #setInboundPrefix(String)
	 */
	protected String toHeaderName(String propertyName) {
		String headerName = propertyName;
		if (StringUtils.hasText(this.inboundPrefix) && !headerName.startsWith(this.inboundPrefix)) {
			headerName = this.inboundPrefix + propertyName;
		}
		return headerName;
	}

	/**
	 * Return the header value, or {@code null} if it does not exist
	 * or does not match the requested {@code type}.
	 * <p>
	 *  返回标头值,或{@code null}(如果不存在)或不符合请求的{@code类型}
	 */
	protected <V> V getHeaderIfAvailable(Map<String, Object> headers, String name, Class<V> type) {
		Object value = headers.get(name);
		if (value == null) {
			return null;
		}
		if (!type.isAssignableFrom(value.getClass())) {
			if (logger.isWarnEnabled()) {
				logger.warn("Skipping header '" + name + "'expected type [" + type + "], but got [" +
						value.getClass() + "]");
			}
			return null;
		}
		else {
			return type.cast(value);
		}
	}

}
