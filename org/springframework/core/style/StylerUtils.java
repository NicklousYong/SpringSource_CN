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

package org.springframework.core.style;

/**
 * Simple utility class to allow for convenient access to value
 * styling logic, mainly to support descriptive logging messages.
 *
 * <p>For more sophisticated needs, use the {@link ValueStyler} abstraction
 * directly. This class simply uses a shared {@link DefaultValueStyler}
 * instance underneath.
 *
 * <p>
 *  简单的实用程序类,允许方便地访问值样式逻辑,主要用于支持描述性日志消息
 * 
 * <p>对于更复杂的需求,请直接使用{@link ValueStyler}抽象方法此类仅使用下面的共享{@link DefaultValueStyler}实例
 * 
 * 
 * @author Keith Donald
 * @since 1.2.2
 * @see ValueStyler
 * @see DefaultValueStyler
 */
public abstract class StylerUtils {

	/**
	 * Default ValueStyler instance used by the {@code style} method.
	 * Also available for the {@link ToStringCreator} class in this package.
	 * <p>
	 *  {@code style}方法使用的默认ValueStyler实例此包中的{@link ToStringCreator}类也可用
	 * 
	 */
	static final ValueStyler DEFAULT_VALUE_STYLER = new DefaultValueStyler();

	/**
	 * Style the specified value according to default conventions.
	 * <p>
	 *  根据默认约定调整指定的值
	 * 
	 * @param value the Object value to style
	 * @return the styled String
	 * @see DefaultValueStyler
	 */
	public static String style(Object value) {
		return DEFAULT_VALUE_STYLER.style(value);
	}

}
