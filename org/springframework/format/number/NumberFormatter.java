/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.format.number;

/**
 * A general-purpose Number formatter.
 *
 * <p>
 *  通用号码格式化程序
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @deprecated as of Spring 4.2, in favor of the more clearly named
 * {@link NumberStyleFormatter}
 */
@Deprecated
public class NumberFormatter extends NumberStyleFormatter {

	/**
	 * Create a new NumberFormatter without a pattern.
	 * <p>
	 *  创建一个没有模式的新的NumberFormatter
	 * 
	 */
	public NumberFormatter() {
	}

	/**
	 * Create a new NumberFormatter with the specified pattern.
	 * <p>
	 *  用指定的模式创建一个新的NumberFormatter
	 * 
	 * @param pattern the format pattern
	 * @see #setPattern
	 */
	public NumberFormatter(String pattern) {
		super(pattern);
	}

}
