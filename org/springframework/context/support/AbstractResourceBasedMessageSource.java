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

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Abstract base class for {@code MessageSource} implementations based on
 * resource bundle conventions, such as {@link ResourceBundleMessageSource}
 * and {@link ReloadableResourceBundleMessageSource}. Provides common
 * configuration methods and corresponding semantic definitions.
 *
 * <p>
 * 基于资源束约束的{@code MessageSource}实现的抽象基类,例如{@link ResourceBundleMessageSource}和{@link ReloadableResourceBundleMessageSource}
 * 提供常见的配置方法和相应的语义定义。
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.3
 * @see ResourceBundleMessageSource
 * @see ReloadableResourceBundleMessageSource
 */
public abstract class AbstractResourceBasedMessageSource extends AbstractMessageSource {

	private final Set<String> basenameSet = new LinkedHashSet<String>(4);

	private String defaultEncoding;

	private boolean fallbackToSystemLocale = true;

	private long cacheMillis = -1;


	/**
	 * Set a single basename, following the basic ResourceBundle convention
	 * of not specifying file extension or language codes. The resource location
	 * format is up to the specific {@code MessageSource} implementation.
	 * <p>Regular and XMl properties files are supported: e.g. "messages" will find
	 * a "messages.properties", "messages_en.properties" etc arrangement as well
	 * as "messages.xml", "messages_en.xml" etc.
	 * <p>
	 *  设置单个基本名称,遵循不指定文件扩展名或语言代码的基本ResourceBundle约定资源位置格式取决于特定的{@code MessageSource}实现<p>支持常规和XMl属性文件：例如"消息"
	 * 将会找到"messagesproperties","messages_enproperties"等安排以及"messagesxml","messages_enxml"等。
	 * 
	 * 
	 * @param basename the single basename
	 * @see #setBasenames
	 * @see org.springframework.core.io.ResourceEditor
	 * @see java.util.ResourceBundle
	 */
	public void setBasename(String basename) {
		setBasenames(basename);
	}

	/**
	 * Set an array of basenames, each following the basic ResourceBundle convention
	 * of not specifying file extension or language codes. The resource location
	 * format is up to the specific {@code MessageSource} implementation.
	 * <p>Regular and XMl properties files are supported: e.g. "messages" will find
	 * a "messages.properties", "messages_en.properties" etc arrangement as well
	 * as "messages.xml", "messages_en.xml" etc.
	 * <p>The associated resource bundles will be checked sequentially when resolving
	 * a message code. Note that message definitions in a <i>previous</i> resource
	 * bundle will override ones in a later bundle, due to the sequential lookup.
	 * <p>Note: In contrast to {@link #addBasenames}, this replaces existing entries
	 * with the given names and can therefore also be used to reset the configuration.
	 * <p>
	 * 设置一个基础名称数组,每个都是基本的ResourceBundle约定,不指定文件扩展名或语言代码资源位置格式取决于特定的{@code MessageSource}实现<p>支持常规和XMl属性文件：例如
	 * "消息"将找到"messagesproperties","messages_enproperties"等安排以及"messagesxml","messages_enxml"等<p>在解析消息代码时,相关
	 * 联的资源束将被顺序检查。
	 * 注意,<i>注意：与{@link #addBasenames}相反,这将替换具有给定名称的现有条目,因此也可以用于重置组态。
	 * 
	 * 
	 * @param basenames an array of basenames
	 * @see #setBasename
	 * @see java.util.ResourceBundle
	 */
	public void setBasenames(String... basenames) {
		this.basenameSet.clear();
		addBasenames(basenames);
	}

	/**
	 * Add the specified basenames to the existing basename configuration.
	 * <p>Note: If a given basename already exists, the position of its entry
	 * will remain as in the original set. New entries will be added at the
	 * end of the list, to be searched after existing basenames.
	 * <p>
	 * 将指定的基础名称添加到现有的基本名称配置<p>注意：如果给定的基本名称已存在,则其条目的位置将保持与原始集合中的新条目将在列表的末尾添加,以便在现有的基础名称之后进行搜索基本名称
	 * 
	 * 
	 * @since 4.3
	 * @see #setBasenames
	 * @see java.util.ResourceBundle
	 */
	public void addBasenames(String... basenames) {
		if (!ObjectUtils.isEmpty(basenames)) {
			for (String basename : basenames) {
				Assert.hasText(basename, "Basename must not be empty");
				this.basenameSet.add(basename.trim());
			}
		}
	}

	/**
	 * Return this {@code MessageSource}'s basename set, containing entries
	 * in the order of registration.
	 * <p>Calling code may introspect this set as well as add or remove entries.
	 * <p>
	 *  返回此{@code MessageSource}的基本名称集,其中包含按照注册顺序的条目<p>调用代码可以内省这个集合以及添加或删除条目
	 * 
	 * 
	 * @since 4.3
	 * @see #addBasenames
	 */
	public Set<String> getBasenameSet() {
		return this.basenameSet;
	}

	/**
	 * Set the default charset to use for parsing properties files.
	 * Used if no file-specific charset is specified for a file.
	 * <p>Default is none, using the {@code java.util.Properties}
	 * default encoding: ISO-8859-1.
	 * <p>Only applies to classic properties files, not to XML files.
	 * <p>
	 *  设置用于解析属性文件的默认字符集如果没有为文件指定特定于文件的字符集,则使用<p>默认值为none,使用{@code javautilProperties}默认编码：ISO-8859-1 <p>仅适用
	 * 于经典属性文件,而不是XML文件。
	 * 
	 * 
	 * @param defaultEncoding the default charset
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Return the default charset to use for parsing properties files, if any.
	 * <p>
	 *  返回用于解析属性文件的默认字符集(如果有)
	 * 
	 * 
	 * @since 4.3
	 */
	protected String getDefaultEncoding() {
		return this.defaultEncoding;
	}

	/**
	 * Set whether to fall back to the system Locale if no files for a specific
	 * Locale have been found. Default is "true"; if this is turned off, the only
	 * fallback will be the default file (e.g. "messages.properties" for
	 * basename "messages").
	 * <p>Falling back to the system Locale is the default behavior of
	 * {@code java.util.ResourceBundle}. However, this is often not desirable
	 * in an application server environment, where the system Locale is not relevant
	 * to the application at all: set this flag to "false" in such a scenario.
	 * <p>
	 * 设置是否回退到系统Locale如果没有找到特定语言环境的文件默认为"true";如果这是关闭的,唯一的回退将是默认文件(例如,basename"messages"的"消息属性")<p>回到系统区域设置是
	 * {@code javautilResourceBundle}的默认行为。
	 * 但是,这通常不是在应用程序服务器环境中,系统区域设置与应用程序无关：在这种情况下将此标志设置为"false"。
	 * 
	 */
	public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
		this.fallbackToSystemLocale = fallbackToSystemLocale;
	}

	/**
	 * Return whether to fall back to the system Locale if no files for a specific
	 * Locale have been found.
	 * <p>
	 *  如果没有找到特定语言环境的文件,则返回是否回退到系统区域设置
	 * 
	 * 
	 * @since 4.3
	 */
	protected boolean isFallbackToSystemLocale() {
		return this.fallbackToSystemLocale;
	}

	/**
	 * Set the number of seconds to cache loaded properties files.
	 * <ul>
	 * <li>Default is "-1", indicating to cache forever (just like
	 * {@code java.util.ResourceBundle}).
	 * <li>A positive number will cache loaded properties files for the given
	 * number of seconds. This is essentially the interval between refresh checks.
	 * Note that a refresh attempt will first check the last-modified timestamp
	 * of the file before actually reloading it; so if files don't change, this
	 * interval can be set rather low, as refresh attempts will not actually reload.
	 * <li>A value of "0" will check the last-modified timestamp of the file on
	 * every message access. <b>Do not use this in a production environment!</b>
	 * </ul>
	 * <p><b>Note that depending on your ClassLoader, expiration might not work reliably
	 * since the ClassLoader may hold on to a cached version of the bundle file.</b>
	 * Prefer {@link ReloadableResourceBundleMessageSource} over
	 * {@link ResourceBundleMessageSource} in such a scenario, in combination with
	 * a non-classpath location.
	 * <p>
	 *  设置缓存加载属性文件的秒数
	 * <ul>
	 * <li>默认值为"-1",表示永远缓存(就像{@code javautilResourceBundle})<li>正数将缓存加载的属性文件给定的秒数这本质上是刷新检查之间的间隔注意刷新尝试将在实际重新加
	 * 载之前首先检查文件的最后修改时间戳;所以如果文件没有改变,这个时间间隔可以设置得相当低,因为刷新尝试将不会实际重新加载<li>值为"0"将检查每个消息访问时文件的最后修改时间戳。
	 * 不要在生产环境中使用它！</b>。
	 * </ul>
	 * <p> <b>请注意,根据您的ClassLoader,由于ClassLoader可能会通过{@link ResourceBundleMessageSource}在{@link ResourceBundleMessageSource}
	 * 中持有该捆绑文件的缓存版本</b>优先{@link ReloadableResourceBundleMessageSource}一个场景,与非类路径位置相结合。
	 * 
	 */
	public void setCacheSeconds(int cacheSeconds) {
		this.cacheMillis = (cacheSeconds * 1000);
	}

	/**
	 * Set the number of milliseconds to cache loaded properties files.
	 * Note that it is common to set seconds instead: {@link #setCacheSeconds}.
	 * <ul>
	 * <li>Default is "-1", indicating to cache forever (just like
	 * {@code java.util.ResourceBundle}).
	 * <li>A positive number will cache loaded properties files for the given
	 * number of milliseconds. This is essentially the interval between refresh checks.
	 * Note that a refresh attempt will first check the last-modified timestamp
	 * of the file before actually reloading it; so if files don't change, this
	 * interval can be set rather low, as refresh attempts will not actually reload.
	 * <li>A value of "0" will check the last-modified timestamp of the file on
	 * every message access. <b>Do not use this in a production environment!</b>
	 * </ul>
	 * <p>
	 *  设置缓存加载的属性文件的毫秒数注意,通常设置秒：{@link #setCacheSeconds}
	 * <ul>
	 * <li>默认值为"-1",表示永久缓存(就像{@code javautilResourceBundle})<li>正数将缓存加载的属性文件给定的毫秒数本质上是刷新检查之间的间隔注意刷新尝试将在实际重新加
	 * 载之前首先检查文件的最后修改时间戳;所以如果文件没有改变,这个时间间隔可以设置得相当低,因为刷新尝试将不会实际重新加载<li>值为"0"将检查每个消息访问时文件的最后修改时间戳。
	 * 不要在生产环境中使用它！</b>。
	 * 
	 * @since 4.3
	 * @see #setCacheSeconds
	 */
	public void setCacheMillis(long cacheMillis) {
		this.cacheMillis = cacheMillis;
	}

	/**
	 * Return the number of milliseconds to cache loaded properties files.
	 * <p>
	 * </ul>
	 * 
	 * @since 4.3
	 */
	protected long getCacheMillis() {
		return this.cacheMillis;
	}

}
