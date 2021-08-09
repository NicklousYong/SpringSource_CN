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

package org.springframework.web.servlet.i18n;

import java.util.Locale;

import org.springframework.web.servlet.LocaleResolver;

/**
 * Abstract base class for {@link LocaleResolver} implementations.
 * Provides support for a default locale.
 *
 * <p>
 *  {@link LocaleResolver}实现的抽象基类提供对默认语言环境的支持
 * 
 * 
 * @author Juergen Hoeller
 * @since 1.2.9
 * @see #setDefaultLocale
 */
public abstract class AbstractLocaleResolver implements LocaleResolver {

	private Locale defaultLocale;


	/**
	 * Set a default Locale that this resolver will return if no other locale found.
	 * <p>
	 *  如果没有找到其他语言环境,请设置此解析器将返回的默认语言环境
	 * 
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Return the default Locale that this resolver is supposed to fall back to, if any.
	 * <p>
	 * 返回此解析器应该返回的默认区域设置(如果有)
	 */
	protected Locale getDefaultLocale() {
		return this.defaultLocale;
	}

}
