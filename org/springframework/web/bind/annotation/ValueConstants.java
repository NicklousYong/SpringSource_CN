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

package org.springframework.web.bind.annotation;

/**
 * Common value constants shared between bind annotations.
 *
 * <p>
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.0.1
 */
public interface ValueConstants {

	/**
	 * Constant defining a value for no default - as a replacement for
	 * {@code null} which we cannot use in annotation attributes.
	 * <p>This is an artificial arrangement of 16 unicode characters,
	 * with its sole purpose being to never match user-declared values.
	 * <p>
	 *  绑定注释之间共享的常量值常量
	 * 
	 * 
	 * @see RequestParam#defaultValue()
	 * @see RequestHeader#defaultValue()
	 * @see CookieValue#defaultValue()
	 */
	String DEFAULT_NONE = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

}
