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
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.util.ClassUtils;

/**
 * {@link org.springframework.context.MessageSource} implementation that
 * accesses resource bundles using specified basenames. This class relies
 * on the underlying JDK's {@link java.util.ResourceBundle} implementation,
 * in combination with the JDK's standard message parsing provided by
 * {@link java.text.MessageFormat}.
 *
 * <p>This MessageSource caches both the accessed ResourceBundle instances and
 * the generated MessageFormats for each message. It also implements rendering of
 * no-arg messages without MessageFormat, as supported by the AbstractMessageSource
 * base class. The caching provided by this MessageSource is significantly faster
 * than the built-in caching of the {@code java.util.ResourceBundle} class.
 *
 * <p>The basenames follow {@link java.util.ResourceBundle} conventions: essentially,
 * a fully-qualified classpath location. If it doesn't contain a package qualifier
 * (such as {@code org.mypackage}), it will be resolved from the classpath root.
 * Note that the JDK's standard ResourceBundle treats dots as package separators:
 * This means that "test.theme" is effectively equivalent to "test/theme".
 *
 * <p>
 * 使用指定的基础名访问资源束的{@link orgspringframeworkcontextMessageSource}实现此类依赖于底层的JDK的{@link javautilResourceBundle}
 * 实现,结合JDK由{@link javatextMessageFormat}提供的标准消息解析,。
 * 
 *  这个MessageSource缓存了每个消息访问的ResourceBundle实例和生成的MessageFormats它还实现了AbstractMessageSource基类支持的没有MessageF
 * ormat的无参数消息的呈现。
 * 此MessageSource提供的缓存比内建的缓存{@code javautilResourceBundle}类。
 * 
 * <p>基础名称遵循{@link javautilResourceBundle}约定：本质上是一个完全限定的类路径位置如果它不包含包限定符(例如{@code orgmypackage})),它将从类路径r
 * oot解析注意, JDK的标准ResourceBundle将点视为包分隔符：这意味着"testtheme"实际上等同于"test / theme"。
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setBasenames
 * @see ReloadableResourceBundleMessageSource
 * @see java.util.ResourceBundle
 * @see java.text.MessageFormat
 */
public class ResourceBundleMessageSource extends AbstractResourceBasedMessageSource implements BeanClassLoaderAware {

	private ClassLoader bundleClassLoader;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	/**
	 * Cache to hold loaded ResourceBundles.
	 * This Map is keyed with the bundle basename, which holds a Map that is
	 * keyed with the Locale and in turn holds the ResourceBundle instances.
	 * This allows for very efficient hash lookups, significantly faster
	 * than the ResourceBundle class's own cache.
	 * <p>
	 *  缓存来保存加载的ResourceBundles此映射与bundle basename相关联,它包含与Locale相关联的Map,并依次保存ResourceBundle实例。
	 * 这允许非常有效的哈希查找,比ResourceBundle类自己的缓存快得多。
	 * 
	 */
	private final Map<String, Map<Locale, ResourceBundle>> cachedResourceBundles =
			new HashMap<String, Map<Locale, ResourceBundle>>();

	/**
	 * Cache to hold already generated MessageFormats.
	 * This Map is keyed with the ResourceBundle, which holds a Map that is
	 * keyed with the message code, which in turn holds a Map that is keyed
	 * with the Locale and holds the MessageFormat values. This allows for
	 * very efficient hash lookups without concatenated keys.
	 * <p>
	 * 缓存保存已生成的MessageFormats此Map与ResourceBundle绑定,ResourceBundle包含一个Map,该Map与消息代码相关联,该Map又连接了一个Maped,该Map与L
	 * ocale关联并保存MessageFormat值。
	 * 这允许非常有效的哈希查找没有连接键。
	 * 
	 * 
	 * @see #getMessageFormat
	 */
	private final Map<ResourceBundle, Map<String, Map<Locale, MessageFormat>>> cachedBundleMessageFormats =
			new HashMap<ResourceBundle, Map<String, Map<Locale, MessageFormat>>>();


	/**
	 * Set the ClassLoader to load resource bundles with.
	 * <p>Default is the containing BeanFactory's
	 * {@link org.springframework.beans.factory.BeanClassLoaderAware bean ClassLoader},
	 * or the default ClassLoader determined by
	 * {@link org.springframework.util.ClassUtils#getDefaultClassLoader()}
	 * if not running within a BeanFactory.
	 * <p>
	 *  设置ClassLoader以使用<p>加载资源束。
	 * 默认是包含BeanFactory的{@link orgspringframeworkbeansfactoryBeanClassLoaderAware bean ClassLoader},或者如果不在Be
	 * anFactory中运行,则由{@link orgspringframeworkutilClassUtils#getDefaultClassLoader()}确定的默认ClassLoader。
	 *  设置ClassLoader以使用<p>加载资源束。
	 * 
	 */
	public void setBundleClassLoader(ClassLoader classLoader) {
		this.bundleClassLoader = classLoader;
	}

	/**
	 * Return the ClassLoader to load resource bundles with.
	 * <p>Default is the containing BeanFactory's bean ClassLoader.
	 * <p>
	 *  返回ClassLoader加载资源束与<p>默认是包含BeanFactory的bean ClassLoader
	 * 
	 * 
	 * @see #setBundleClassLoader
	 */
	protected ClassLoader getBundleClassLoader() {
		return (this.bundleClassLoader != null ? this.bundleClassLoader : this.beanClassLoader);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
	}


	/**
	 * Resolves the given message code as key in the registered resource bundles,
	 * returning the value found in the bundle as-is (without MessageFormat parsing).
	 * <p>
	 * 将给定的消息代码解析为已注册的资源包中的密钥,然后返回捆绑包中的值(不使用MessageFormat解析)
	 * 
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		Set<String> basenames = getBasenameSet();
		for (String basename : basenames) {
			ResourceBundle bundle = getResourceBundle(basename, locale);
			if (bundle != null) {
				String result = getStringOrNull(bundle, code);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	/**
	 * Resolves the given message code as key in the registered resource bundles,
	 * using a cached MessageFormat instance per message code.
	 * <p>
	 *  将给定的消息代码解析为注册资源包中的密钥,每个消息代码使用缓存的MessageFormat实例
	 * 
	 */
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		Set<String> basenames = getBasenameSet();
		for (String basename : basenames) {
			ResourceBundle bundle = getResourceBundle(basename, locale);
			if (bundle != null) {
				MessageFormat messageFormat = getMessageFormat(bundle, code, locale);
				if (messageFormat != null) {
					return messageFormat;
				}
			}
		}
		return null;
	}


	/**
	 * Return a ResourceBundle for the given basename and code,
	 * fetching already generated MessageFormats from the cache.
	 * <p>
	 *  为给定的基本名称和代码返回一个ResourceBundle,从缓存中提取已生成的MessageFormats
	 * 
	 * 
	 * @param basename the basename of the ResourceBundle
	 * @param locale the Locale to find the ResourceBundle for
	 * @return the resulting ResourceBundle, or {@code null} if none
	 * found for the given basename and Locale
	 */
	protected ResourceBundle getResourceBundle(String basename, Locale locale) {
		if (getCacheMillis() >= 0) {
			// Fresh ResourceBundle.getBundle call in order to let ResourceBundle
			// do its native caching, at the expense of more extensive lookup steps.
			return doGetBundle(basename, locale);
		}
		else {
			// Cache forever: prefer locale cache over repeated getBundle calls.
			synchronized (this.cachedResourceBundles) {
				Map<Locale, ResourceBundle> localeMap = this.cachedResourceBundles.get(basename);
				if (localeMap != null) {
					ResourceBundle bundle = localeMap.get(locale);
					if (bundle != null) {
						return bundle;
					}
				}
				try {
					ResourceBundle bundle = doGetBundle(basename, locale);
					if (localeMap == null) {
						localeMap = new HashMap<Locale, ResourceBundle>();
						this.cachedResourceBundles.put(basename, localeMap);
					}
					localeMap.put(locale, bundle);
					return bundle;
				}
				catch (MissingResourceException ex) {
					if (logger.isWarnEnabled()) {
						logger.warn("ResourceBundle [" + basename + "] not found for MessageSource: " + ex.getMessage());
					}
					// Assume bundle not found
					// -> do NOT throw the exception to allow for checking parent message source.
					return null;
				}
			}
		}
	}

	/**
	 * Obtain the resource bundle for the given basename and Locale.
	 * <p>
	 *  获取给定基础名称和区域设置的资源包
	 * 
	 * 
	 * @param basename the basename to look for
	 * @param locale the Locale to look for
	 * @return the corresponding ResourceBundle
	 * @throws MissingResourceException if no matching bundle could be found
	 * @see java.util.ResourceBundle#getBundle(String, Locale, ClassLoader)
	 * @see #getBundleClassLoader()
	 */
	protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
		return ResourceBundle.getBundle(basename, locale, getBundleClassLoader(), new MessageSourceControl());
	}

	/**
	 * Load a property-based resource bundle from the given reader.
	 * <p>The default implementation returns a {@link PropertyResourceBundle}.
	 * <p>
	 *  从给定的读者加载基于属性的资源包<p>默认实现返回一个{@link PropertyResourceBundle}
	 * 
	 * 
	 * @param reader the reader for the target resource
	 * @return the fully loaded bundle
	 * @throws IOException in case of I/O failure
	 * @since 4.2
	 * @see PropertyResourceBundle#PropertyResourceBundle(Reader)
	 */
	protected ResourceBundle loadBundle(Reader reader) throws IOException {
		return new PropertyResourceBundle(reader);
	}

	/**
	 * Return a MessageFormat for the given bundle and code,
	 * fetching already generated MessageFormats from the cache.
	 * <p>
	 *  为给定的包和代码返回一个MessageFormat,从缓存中提取已生成的MessageFormats
	 * 
	 * 
	 * @param bundle the ResourceBundle to work on
	 * @param code the message code to retrieve
	 * @param locale the Locale to use to build the MessageFormat
	 * @return the resulting MessageFormat, or {@code null} if no message
	 * defined for the given code
	 * @throws MissingResourceException if thrown by the ResourceBundle
	 */
	protected MessageFormat getMessageFormat(ResourceBundle bundle, String code, Locale locale)
			throws MissingResourceException {

		synchronized (this.cachedBundleMessageFormats) {
			Map<String, Map<Locale, MessageFormat>> codeMap = this.cachedBundleMessageFormats.get(bundle);
			Map<Locale, MessageFormat> localeMap = null;
			if (codeMap != null) {
				localeMap = codeMap.get(code);
				if (localeMap != null) {
					MessageFormat result = localeMap.get(locale);
					if (result != null) {
						return result;
					}
				}
			}

			String msg = getStringOrNull(bundle, code);
			if (msg != null) {
				if (codeMap == null) {
					codeMap = new HashMap<String, Map<Locale, MessageFormat>>();
					this.cachedBundleMessageFormats.put(bundle, codeMap);
				}
				if (localeMap == null) {
					localeMap = new HashMap<Locale, MessageFormat>();
					codeMap.put(code, localeMap);
				}
				MessageFormat result = createMessageFormat(msg, locale);
				localeMap.put(locale, result);
				return result;
			}

			return null;
		}
	}

	/**
	 * Efficiently retrieve the String value for the specified key,
	 * or return {@code null} if not found.
	 * <p>As of 4.2, the default implementation checks {@code containsKey}
	 * before it attempts to call {@code getString} (which would require
	 * catching {@code MissingResourceException} for key not found).
	 * <p>Can be overridden in subclasses.
	 * <p>
	 * 有效地检索指定键的String值,或者如果没有找到则返回{@code null} <p>从42开始,默认实现在尝试调用{@code getString}之前检查{@code containsKey}(这
	 * 将需要捕获未找到密钥的{@code MissingResourceException})<p>可以在子类中覆盖。
	 * 
	 * 
	 * @param bundle the ResourceBundle to perform the lookup in
	 * @param key the key to look up
	 * @return the associated value, or {@code null} if none
	 * @since 4.2
	 * @see ResourceBundle#getString(String)
	 * @see ResourceBundle#containsKey(String)
	 */
	protected String getStringOrNull(ResourceBundle bundle, String key) {
		if (bundle.containsKey(key)) {
			try {
				return bundle.getString(key);
			}
			catch (MissingResourceException ex){
				// Assume key not found for some other reason
				// -> do NOT throw the exception to allow for checking parent message source.
			}
		}
		return null;
	}

	/**
	 * Show the configuration of this MessageSource.
	 * <p>
	 *  显示此MessageSource的配置
	 * 
	 */
	@Override
	public String toString() {
		return getClass().getName() + ": basenames=" + getBasenameSet();
	}


	/**
	 * Custom implementation of Java 6's {@code ResourceBundle.Control},
	 * adding support for custom file encodings, deactivating the fallback to the
	 * system locale and activating ResourceBundle's native cache, if desired.
	 * <p>
	 *  自定义实现Java 6的{@code ResourceBundleControl},添加对自定义文件编码的支持,如果需要,还原对系统区域设置的后备并激活ResourceBundle的本机缓存
	 */
	private class MessageSourceControl extends ResourceBundle.Control {

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {

			// Special handling of default encoding
			if (format.equals("java.properties")) {
				String bundleName = toBundleName(baseName, locale);
				final String resourceName = toResourceName(bundleName, "properties");
				final ClassLoader classLoader = loader;
				final boolean reloadFlag = reload;
				InputStream stream;
				try {
					stream = AccessController.doPrivileged(
							new PrivilegedExceptionAction<InputStream>() {
								@Override
								public InputStream run() throws IOException {
									InputStream is = null;
									if (reloadFlag) {
										URL url = classLoader.getResource(resourceName);
										if (url != null) {
											URLConnection connection = url.openConnection();
											if (connection != null) {
												connection.setUseCaches(false);
												is = connection.getInputStream();
											}
										}
									}
									else {
										is = classLoader.getResourceAsStream(resourceName);
									}
									return is;
								}
							});
				}
				catch (PrivilegedActionException ex) {
					throw (IOException) ex.getException();
				}
				if (stream != null) {
					String encoding = getDefaultEncoding();
					if (encoding == null) {
						encoding = "ISO-8859-1";
					}
					try {
						return loadBundle(new InputStreamReader(stream, encoding));
					}
					finally {
						stream.close();
					}
				}
				else {
					return null;
				}
			}
			else {
				// Delegate handling of "java.class" format to standard Control
				return super.newBundle(baseName, locale, format, loader, reload);
			}
		}

		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			return (isFallbackToSystemLocale() ? super.getFallbackLocale(baseName, locale) : null);
		}

		@Override
		public long getTimeToLive(String baseName, Locale locale) {
			long cacheMillis = getCacheMillis();
			return (cacheMillis >= 0 ? cacheMillis : super.getTimeToLive(baseName, locale));
		}

		@Override
		public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
			if (super.needsReload(baseName, locale, format, loader, bundle, loadTime)) {
				cachedBundleMessageFormats.remove(bundle);
				return true;
			}
			else {
				return false;
			}
		}
	}

}
