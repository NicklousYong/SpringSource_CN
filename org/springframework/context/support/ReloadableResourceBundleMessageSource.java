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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/**
 * Spring-specific {@link org.springframework.context.MessageSource} implementation
 * that accesses resource bundles using specified basenames, participating in the
 * Spring {@link org.springframework.context.ApplicationContext}'s resource loading.
 *
 * <p>In contrast to the JDK-based {@link ResourceBundleMessageSource}, this class uses
 * {@link java.util.Properties} instances as its custom data structure for messages,
 * loading them via a {@link org.springframework.util.PropertiesPersister} strategy
 * from Spring {@link Resource} handles. This strategy is not only capable of
 * reloading files based on timestamp changes, but also of loading properties files
 * with a specific character encoding. It will detect XML property files as well.
 *
 * <p>Note that the basenames set as {@link #setBasenames "basenames"} property
 * are treated in a slightly different fashion than the "basenames" property of
 * {@link ResourceBundleMessageSource}. It follows the basic ResourceBundle rule of not
 * specifying file extension or language codes, but can refer to any Spring resource
 * location (instead of being restricted to classpath resources). With a "classpath:"
 * prefix, resources can still be loaded from the classpath, but "cacheSeconds" values
 * other than "-1" (caching forever) might not work reliably in this case.
 *
 * <p>For a typical web application, message files could be placed in {@code WEB-INF}:
 * e.g. a "WEB-INF/messages" basename would find a "WEB-INF/messages.properties",
 * "WEB-INF/messages_en.properties" etc arrangement as well as "WEB-INF/messages.xml",
 * "WEB-INF/messages_en.xml" etc. Note that message definitions in a <i>previous</i>
 * resource bundle will override ones in a later bundle, due to sequential lookup.

 * <p>This MessageSource can easily be used outside of an
 * {@link org.springframework.context.ApplicationContext}: it will use a
 * {@link org.springframework.core.io.DefaultResourceLoader} as default,
 * simply getting overridden with the ApplicationContext's resource loader
 * if running in a context. It does not have any other specific dependencies.
 *
 * <p>Thanks to Thomas Achleitner for providing the initial implementation of
 * this message source!
 *
 * <p>
 * Spring特定的{@link orgspringframeworkcontextMessageSource}实现,使用指定的基础名称访问资源包,参与Spring {@link orgspringframeworkcontextApplicationContext}
 * 的资源加载。
 * 
 *  <p>与基于JDK的{@link ResourceBundleMessageSource}相反,此类使用{@link javautilProperties}实例作为消息的自定义数据结构,通过Sprin
 * g {@link Resource}的{@link orgspringframeworkutilPropertiesPersister}策略加载它们。
 * 句柄此策略不仅可以根据时间戳更改重新加载文件,还可以加载具有特定字符编码的属性文件。它还将检测XML属性文件。
 * 
 * <p>请注意,设置为{@link #setBasenames"basenames"}属性的基本名称以与{@link ResourceBundleMessageSource}的"basenames"属性略
 * 有不同的方式处理。
 * 它遵循不指定文件扩展名的基本ResourceBundle规则,语言代码,但可以引用任何Spring资源位置(而不是限于类路径资源)使用"classpath："前缀,仍然可以从类路径加载资源,但除"-1"
 * 之外的"cacheSeconds"值(缓存)永远)在这种情况下可能无法可靠地工作。
 * 
 * <p>对于典型的Web应用程序,消息文件可以放置在{@code WEB-INF}中：例如,"WEB-INF / messages"基本名称将找到"WEB-INF / messagesproperties
 * ","WEB-INF / messages_enproperties "等等安排以及"WEB-INF / messagesxml","WEB-INF / messages_enxml"等注意,以前的</i>
 * 资源束中的消息定义将覆盖以后的包中的消息定义,因为顺序查找。
 * 
 *  <p>这个MessageSource可以很容易地在{@link orgspringframeworkcontextApplicationContext}之外使用：它将使用{@link orgspringframeworkcoreioDefaultResourceLoader}
 * 作为默认值,如果在上下文中运行,只需被ApplicationContext的资源加载器覆盖。
 * 它没有任何其他特定的依赖。
 * 
 * 感谢Thomas Achleitner提供了这个消息源的初始实现！
 * 
 * 
 * @author Juergen Hoeller
 * @see #setCacheSeconds
 * @see #setBasenames
 * @see #setDefaultEncoding
 * @see #setFileEncodings
 * @see #setPropertiesPersister
 * @see #setResourceLoader
 * @see org.springframework.util.DefaultPropertiesPersister
 * @see org.springframework.core.io.DefaultResourceLoader
 * @see ResourceBundleMessageSource
 * @see java.util.ResourceBundle
 */
public class ReloadableResourceBundleMessageSource extends AbstractResourceBasedMessageSource implements ResourceLoaderAware {

	private static final String PROPERTIES_SUFFIX = ".properties";

	private static final String XML_SUFFIX = ".xml";


	private Properties fileEncodings;

	private boolean concurrentRefresh = true;

	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	/** Cache to hold filename lists per Locale */
	private final ConcurrentMap<String, Map<Locale, List<String>>> cachedFilenames =
			new ConcurrentHashMap<String, Map<Locale, List<String>>>();

	/** Cache to hold already loaded properties per filename */
	private final ConcurrentMap<String, PropertiesHolder> cachedProperties =
			new ConcurrentHashMap<String, PropertiesHolder>();

	/** Cache to hold merged loaded properties per locale */
	private final ConcurrentMap<Locale, PropertiesHolder> cachedMergedProperties =
			new ConcurrentHashMap<Locale, PropertiesHolder>();


	/**
	 * Set per-file charsets to use for parsing properties files.
	 * <p>Only applies to classic properties files, not to XML files.
	 * <p>
	 *  设置每个文件字符集用于解析属性文件<p>仅适用于经典属性文件,而不适用于XML文件
	 * 
	 * 
	 * @param fileEncodings Properties with filenames as keys and charset
	 * names as values. Filenames have to match the basename syntax,
	 * with optional locale-specific components: e.g. "WEB-INF/messages"
	 * or "WEB-INF/messages_en".
	 * @see #setBasenames
	 * @see org.springframework.util.PropertiesPersister#load
	 */
	public void setFileEncodings(Properties fileEncodings) {
		this.fileEncodings = fileEncodings;
	}

	/**
	 * Specify whether to allow for concurrent refresh behavior, i.e. one thread
	 * locked in a refresh attempt for a specific cached properties file whereas
	 * other threads keep returning the old properties for the time being, until
	 * the refresh attempt has completed.
	 * <p>Default is "true": this behavior is new as of Spring Framework 4.1,
	 * minimizing contention between threads. If you prefer the old behavior,
	 * i.e. to fully block on refresh, switch this flag to "false".
	 * <p>
	 *  指定是否允许并发刷新行为,即一个线程锁定特定缓存属性文件的刷新尝试,而其他线程暂时返回旧属性,直到刷新尝试完成<p>默认值为"true" ：这个行为是Spring Framework 41中的新特性,
	 * 最小化线程之间的争用如果您喜欢旧的行为,即完全阻止刷新,请将此标志切换为"false"。
	 * 
	 * 
	 * @since 4.1
	 * @see #setCacheSeconds
	 */
	public void setConcurrentRefresh(boolean concurrentRefresh) {
		this.concurrentRefresh = concurrentRefresh;
	}

	/**
	 * Set the PropertiesPersister to use for parsing properties files.
	 * <p>The default is a DefaultPropertiesPersister.
	 * <p>
	 *  设置PropertiesPersister用于解析属性文件<p>默认值为DefaultPropertiesPersister
	 * 
	 * 
	 * @see org.springframework.util.DefaultPropertiesPersister
	 */
	public void setPropertiesPersister(PropertiesPersister propertiesPersister) {
		this.propertiesPersister =
				(propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister());
	}

	/**
	 * Set the ResourceLoader to use for loading bundle properties files.
	 * <p>The default is a DefaultResourceLoader. Will get overridden by the
	 * ApplicationContext if running in a context, as it implements the
	 * ResourceLoaderAware interface. Can be manually overridden when
	 * running outside of an ApplicationContext.
	 * <p>
	 * 设置ResourceLoader用于加载bundle属性文件<p>默认值为DefaultResourceLoader如果在上下文中运行,将被ApplicationContext覆盖,因为它实现了Reso
	 * urceLoaderAware接口可以在运行ApplicationContext之外手动覆盖。
	 * 
	 * 
	 * @see org.springframework.core.io.DefaultResourceLoader
	 * @see org.springframework.context.ResourceLoaderAware
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
	}


	/**
	 * Resolves the given message code as key in the retrieved bundle files,
	 * returning the value found in the bundle as-is (without MessageFormat parsing).
	 * <p>
	 *  将给定的消息代码解析为检索到的包文件中的关键字,按原样返回捆绑包中的值(不使用MessageFormat解析)
	 * 
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		if (getCacheMillis() < 0) {
			PropertiesHolder propHolder = getMergedProperties(locale);
			String result = propHolder.getProperty(code);
			if (result != null) {
				return result;
			}
		}
		else {
			for (String basename : getBasenameSet()) {
				List<String> filenames = calculateAllFilenames(basename, locale);
				for (String filename : filenames) {
					PropertiesHolder propHolder = getProperties(filename);
					String result = propHolder.getProperty(code);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Resolves the given message code as key in the retrieved bundle files,
	 * using a cached MessageFormat instance per message code.
	 * <p>
	 *  将给定的消息代码解析为检索到的包文件中的密钥,每个消息代码使用缓存的MessageFormat实例
	 * 
	 */
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		if (getCacheMillis() < 0) {
			PropertiesHolder propHolder = getMergedProperties(locale);
			MessageFormat result = propHolder.getMessageFormat(code, locale);
			if (result != null) {
				return result;
			}
		}
		else {
			for (String basename : getBasenameSet()) {
				List<String> filenames = calculateAllFilenames(basename, locale);
				for (String filename : filenames) {
					PropertiesHolder propHolder = getProperties(filename);
					MessageFormat result = propHolder.getMessageFormat(code, locale);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}


	/**
	 * Get a PropertiesHolder that contains the actually visible properties
	 * for a Locale, after merging all specified resource bundles.
	 * Either fetches the holder from the cache or freshly loads it.
	 * <p>Only used when caching resource bundle contents forever, i.e.
	 * with cacheSeconds < 0. Therefore, merged properties are always
	 * cached forever.
	 * <p>
	 * 在合并所有指定的资源束之后,获取包含Locale实际可见属性的PropertiesHolder从缓存中提取持有者或新加载它<p>仅在缓存资源束内容时使用,即使用cacheSeconds <0因此,合并属性始终被缓存。
	 * 
	 */
	protected PropertiesHolder getMergedProperties(Locale locale) {
		PropertiesHolder mergedHolder = this.cachedMergedProperties.get(locale);
		if (mergedHolder != null) {
			return mergedHolder;
		}
		Properties mergedProps = newProperties();
		mergedHolder = new PropertiesHolder(mergedProps, -1);
		String[] basenames = StringUtils.toStringArray(getBasenameSet());
		for (int i = basenames.length - 1; i >= 0; i--) {
			List<String> filenames = calculateAllFilenames(basenames[i], locale);
			for (int j = filenames.size() - 1; j >= 0; j--) {
				String filename = filenames.get(j);
				PropertiesHolder propHolder = getProperties(filename);
				if (propHolder.getProperties() != null) {
					mergedProps.putAll(propHolder.getProperties());
				}
			}
		}
		PropertiesHolder existing = this.cachedMergedProperties.putIfAbsent(locale, mergedHolder);
		if (existing != null) {
			mergedHolder = existing;
		}
		return mergedHolder;
	}

	/**
	 * Calculate all filenames for the given bundle basename and Locale.
	 * Will calculate filenames for the given Locale, the system Locale
	 * (if applicable), and the default file.
	 * <p>
	 *  计算给定束基础名称和区域设置的所有文件名将计算给定区域设置的文件名,系统区域设置(如果适用)和默认文件
	 * 
	 * 
	 * @param basename the basename of the bundle
	 * @param locale the locale
	 * @return the List of filenames to check
	 * @see #setFallbackToSystemLocale
	 * @see #calculateFilenamesForLocale
	 */
	protected List<String> calculateAllFilenames(String basename, Locale locale) {
		Map<Locale, List<String>> localeMap = this.cachedFilenames.get(basename);
		if (localeMap != null) {
			List<String> filenames = localeMap.get(locale);
			if (filenames != null) {
				return filenames;
			}
		}
		List<String> filenames = new ArrayList<String>(7);
		filenames.addAll(calculateFilenamesForLocale(basename, locale));
		if (isFallbackToSystemLocale() && !locale.equals(Locale.getDefault())) {
			List<String> fallbackFilenames = calculateFilenamesForLocale(basename, Locale.getDefault());
			for (String fallbackFilename : fallbackFilenames) {
				if (!filenames.contains(fallbackFilename)) {
					// Entry for fallback locale that isn't already in filenames list.
					filenames.add(fallbackFilename);
				}
			}
		}
		filenames.add(basename);
		if (localeMap == null) {
			localeMap = new ConcurrentHashMap<Locale, List<String>>();
			Map<Locale, List<String>> existing = this.cachedFilenames.putIfAbsent(basename, localeMap);
			if (existing != null) {
				localeMap = existing;
			}
		}
		localeMap.put(locale, filenames);
		return filenames;
	}

	/**
	 * Calculate the filenames for the given bundle basename and Locale,
	 * appending language code, country code, and variant code.
	 * E.g.: basename "messages", Locale "de_AT_oo" -> "messages_de_AT_OO",
	 * "messages_de_AT", "messages_de".
	 * <p>Follows the rules defined by {@link java.util.Locale#toString()}.
	 * <p>
	 * 计算给定束基础名称和区域设置的文件名,附加语言代码,国家代码和变体代码例如：basename"messages",Locale"de_AT_oo" - >"messages_de_AT_OO","mes
	 * sages_de_AT","messages_de"<p>由{@link javautilLocale#toString()}定义的规则。
	 * 
	 * 
	 * @param basename the basename of the bundle
	 * @param locale the locale
	 * @return the List of filenames to check
	 */
	protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
		List<String> result = new ArrayList<String>(3);
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();
		StringBuilder temp = new StringBuilder(basename);

		temp.append('_');
		if (language.length() > 0) {
			temp.append(language);
			result.add(0, temp.toString());
		}

		temp.append('_');
		if (country.length() > 0) {
			temp.append(country);
			result.add(0, temp.toString());
		}

		if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
			temp.append('_').append(variant);
			result.add(0, temp.toString());
		}

		return result;
	}


	/**
	 * Get a PropertiesHolder for the given filename, either from the
	 * cache or freshly loaded.
	 * <p>
	 *  从缓存或新加载中获取给定文件名的PropertiesHolder
	 * 
	 * 
	 * @param filename the bundle filename (basename + Locale)
	 * @return the current PropertiesHolder for the bundle
	 */
	protected PropertiesHolder getProperties(String filename) {
		PropertiesHolder propHolder = this.cachedProperties.get(filename);
		long originalTimestamp = -2;

		if (propHolder != null) {
			originalTimestamp = propHolder.getRefreshTimestamp();
			if (originalTimestamp == -1 || originalTimestamp > System.currentTimeMillis() - getCacheMillis()) {
				// Up to date
				return propHolder;
			}
		}
		else {
			propHolder = new PropertiesHolder();
			PropertiesHolder existingHolder = this.cachedProperties.putIfAbsent(filename, propHolder);
			if (existingHolder != null) {
				propHolder = existingHolder;
			}
		}

		// At this point, we need to refresh...
		if (this.concurrentRefresh && propHolder.getRefreshTimestamp() >= 0) {
			// A populated but stale holder -> could keep using it.
			if (!propHolder.refreshLock.tryLock()) {
				// Getting refreshed by another thread already ->
				// let's return the existing properties for the time being.
				return propHolder;
			}
		}
		else {
			propHolder.refreshLock.lock();
		}
		try {
			PropertiesHolder existingHolder = this.cachedProperties.get(filename);
			if (existingHolder != null && existingHolder.getRefreshTimestamp() > originalTimestamp) {
				return existingHolder;
			}
			return refreshProperties(filename, propHolder);
		}
		finally {
			propHolder.refreshLock.unlock();
		}
	}

	/**
	 * Refresh the PropertiesHolder for the given bundle filename.
	 * The holder can be {@code null} if not cached before, or a timed-out cache entry
	 * (potentially getting re-validated against the current last-modified timestamp).
	 * <p>
	 *  刷新给定bundle的PropertiesHolder文件名持有者可以是{@code null}(如果没有缓存)或超时高速缓存条目(可能根据当前上次修改的时间戳重新验证)
	 * 
	 * 
	 * @param filename the bundle filename (basename + Locale)
	 * @param propHolder the current PropertiesHolder for the bundle
	 */
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		long refreshTimestamp = (getCacheMillis() < 0 ? -1 : System.currentTimeMillis());

		Resource resource = this.resourceLoader.getResource(filename + PROPERTIES_SUFFIX);
		if (!resource.exists()) {
			resource = this.resourceLoader.getResource(filename + XML_SUFFIX);
		}

		if (resource.exists()) {
			long fileTimestamp = -1;
			if (getCacheMillis() >= 0) {
				// Last-modified timestamp of file will just be read if caching with timeout.
				try {
					fileTimestamp = resource.lastModified();
					if (propHolder != null && propHolder.getFileTimestamp() == fileTimestamp) {
						if (logger.isDebugEnabled()) {
							logger.debug("Re-caching properties for filename [" + filename + "] - file hasn't been modified");
						}
						propHolder.setRefreshTimestamp(refreshTimestamp);
						return propHolder;
					}
				}
				catch (IOException ex) {
					// Probably a class path resource: cache it forever.
					if (logger.isDebugEnabled()) {
						logger.debug(resource + " could not be resolved in the file system - assuming that it hasn't changed", ex);
					}
					fileTimestamp = -1;
				}
			}
			try {
				Properties props = loadProperties(resource, filename);
				propHolder = new PropertiesHolder(props, fileTimestamp);
			}
			catch (IOException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Could not parse properties file [" + resource.getFilename() + "]", ex);
				}
				// Empty holder representing "not valid".
				propHolder = new PropertiesHolder();
			}
		}

		else {
			// Resource does not exist.
			if (logger.isDebugEnabled()) {
				logger.debug("No properties file found for [" + filename + "] - neither plain properties nor XML");
			}
			// Empty holder representing "not found".
			propHolder = new PropertiesHolder();
		}

		propHolder.setRefreshTimestamp(refreshTimestamp);
		this.cachedProperties.put(filename, propHolder);
		return propHolder;
	}

	/**
	 * Load the properties from the given resource.
	 * <p>
	 *  从给定资源加载属性
	 * 
	 * 
	 * @param resource the resource to load from
	 * @param filename the original bundle filename (basename + Locale)
	 * @return the populated Properties instance
	 * @throws IOException if properties loading failed
	 */
	protected Properties loadProperties(Resource resource, String filename) throws IOException {
		InputStream is = resource.getInputStream();
		Properties props = newProperties();
		try {
			if (resource.getFilename().endsWith(XML_SUFFIX)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Loading properties [" + resource.getFilename() + "]");
				}
				this.propertiesPersister.loadFromXml(props, is);
			}
			else {
				String encoding = null;
				if (this.fileEncodings != null) {
					encoding = this.fileEncodings.getProperty(filename);
				}
				if (encoding == null) {
					encoding = getDefaultEncoding();
				}
				if (encoding != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Loading properties [" + resource.getFilename() + "] with encoding '" + encoding + "'");
					}
					this.propertiesPersister.load(props, new InputStreamReader(is, encoding));
				}
				else {
					if (logger.isDebugEnabled()) {
						logger.debug("Loading properties [" + resource.getFilename() + "]");
					}
					this.propertiesPersister.load(props, is);
				}
			}
			return props;
		}
		finally {
			is.close();
		}
	}

	/**
	 * Template method for creating a plain new {@link Properties} instance.
	 * The default implementation simply calls {@link Properties#Properties()}.
	 * <p>Allows for returning a custom {@link Properties} extension in subclasses.
	 * Overriding methods should just instantiate a custom {@link Properties} subclass,
	 * with no further initialization or population to be performed at that point.
	 * <p>
	 * 用于创建纯新的{@link属性}实例的模板方法默认实现只需调用{@link属性#属性()} <p>允许在子类中返回自定义{@link属性}扩展覆盖方法应该只是实例化自定义{@link属性}子类,在此时不
	 * 再进行初始化或填充。
	 * 
	 * 
	 * @return a plain Properties instance
	 * @since 4.2
	 */
	protected Properties newProperties() {
		return new Properties();
	}


	/**
	 * Clear the resource bundle cache.
	 * Subsequent resolve calls will lead to reloading of the properties files.
	 * <p>
	 *  清除资源束缓存后续的解析调用将导致重新加载属性文件
	 * 
	 */
	public void clearCache() {
		logger.debug("Clearing entire resource bundle cache");
		this.cachedProperties.clear();
		this.cachedMergedProperties.clear();
	}

	/**
	 * Clear the resource bundle caches of this MessageSource and all its ancestors.
	 * <p>
	 *  清除此MessageSource及其所有祖先的资源束缓存
	 * 
	 * 
	 * @see #clearCache
	 */
	public void clearCacheIncludingAncestors() {
		clearCache();
		if (getParentMessageSource() instanceof ReloadableResourceBundleMessageSource) {
			((ReloadableResourceBundleMessageSource) getParentMessageSource()).clearCacheIncludingAncestors();
		}
	}


	@Override
	public String toString() {
		return getClass().getName() + ": basenames=" + getBasenameSet();
	}


	/**
	 * PropertiesHolder for caching.
	 * Stores the last-modified timestamp of the source file for efficient
	 * change detection, and the timestamp of the last refresh attempt
	 * (updated every time the cache entry gets re-validated).
	 * <p>
	 *  属性缓存的缓存存储源文件的最后修改的时间戳以进行高效的更改检测,以及最后一次刷新尝试的时间戳(每次缓存条目重新验证时更新)
	 */
	protected class PropertiesHolder {

		private final Properties properties;

		private final long fileTimestamp;

		private volatile long refreshTimestamp = -2;

		private final ReentrantLock refreshLock = new ReentrantLock();

		/** Cache to hold already generated MessageFormats per message code */
		private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats =
				new ConcurrentHashMap<String, Map<Locale, MessageFormat>>();

		public PropertiesHolder() {
			this.properties = null;
			this.fileTimestamp = -1;
		}

		public PropertiesHolder(Properties properties, long fileTimestamp) {
			this.properties = properties;
			this.fileTimestamp = fileTimestamp;
		}

		public Properties getProperties() {
			return this.properties;
		}

		public long getFileTimestamp() {
			return this.fileTimestamp;
		}

		public void setRefreshTimestamp(long refreshTimestamp) {
			this.refreshTimestamp = refreshTimestamp;
		}

		public long getRefreshTimestamp() {
			return this.refreshTimestamp;
		}

		public String getProperty(String code) {
			if (this.properties == null) {
				return null;
			}
			return this.properties.getProperty(code);
		}

		public MessageFormat getMessageFormat(String code, Locale locale) {
			if (this.properties == null) {
				return null;
			}
			Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
			if (localeMap != null) {
				MessageFormat result = localeMap.get(locale);
				if (result != null) {
					return result;
				}
			}
			String msg = this.properties.getProperty(code);
			if (msg != null) {
				if (localeMap == null) {
					localeMap = new ConcurrentHashMap<Locale, MessageFormat>();
					Map<Locale, MessageFormat> existing = this.cachedMessageFormats.putIfAbsent(code, localeMap);
					if (existing != null) {
						localeMap = existing;
					}
				}
				MessageFormat result = createMessageFormat(msg, locale);
				localeMap.put(locale, result);
				return result;
			}
			return null;
		}
	}

}
