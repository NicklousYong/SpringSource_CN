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

package org.springframework.beans.factory.config;

import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;

/**
 * Subclass of PropertyPlaceholderConfigurer that supports JDK 1.4's
 * Preferences API ({@code java.util.prefs}).
 *
 * <p>Tries to resolve placeholders as keys first in the user preferences,
 * then in the system preferences, then in this configurer's properties.
 * Thus, behaves like PropertyPlaceholderConfigurer if no corresponding
 * preferences defined.
 *
 * <p>Supports custom paths for the system and user preferences trees. Also
 * supports custom paths specified in placeholders ("myPath/myPlaceholderKey").
 * Uses the respective root node if not specified.
 *
 * <p>
 *  PropertyPlaceholderConfigurer的子类支持JDK 14的Preferences API({@code javautilprefs})
 * 
 * <p>尝试将占位符解析为用户首选项中的键,然后在系统首选项中,然后在此配置程序的属性中解析占位符。因此,如果没有定义相应的首选项,则表现为PropertyPlaceholderConfigurer
 * 
 *  <p>支持系统和用户首选项树的自定义路径还支持占位符中指定的自定义路径("myPath / myPlaceholderKey")如果未指定,请使用相应的根节点
 * 
 * 
 * @author Juergen Hoeller
 * @since 16.02.2004
 * @see #setSystemTreePath
 * @see #setUserTreePath
 * @see java.util.prefs.Preferences
 */
public class PreferencesPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {

	private String systemTreePath;

	private String userTreePath;

	private Preferences systemPrefs;

	private Preferences userPrefs;


	/**
	 * Set the path in the system preferences tree to use for resolving
	 * placeholders. Default is the root node.
	 * <p>
	 *  设置系统首选项树中用于解析占位符的路径Default是根节点
	 * 
	 */
	public void setSystemTreePath(String systemTreePath) {
		this.systemTreePath = systemTreePath;
	}

	/**
	 * Set the path in the system preferences tree to use for resolving
	 * placeholders. Default is the root node.
	 * <p>
	 *  设置系统首选项树中用于解析占位符的路径Default是根节点
	 * 
	 */
	public void setUserTreePath(String userTreePath) {
		this.userTreePath = userTreePath;
	}


	/**
	 * This implementation eagerly fetches the Preferences instances
	 * for the required system and user tree nodes.
	 * <p>
	 *  该实现方式热切地获取所需系统和用户树节点的Preferences实例
	 * 
	 */
	@Override
	public void afterPropertiesSet() {
		this.systemPrefs = (this.systemTreePath != null) ?
				Preferences.systemRoot().node(this.systemTreePath) : Preferences.systemRoot();
		this.userPrefs = (this.userTreePath != null) ?
				Preferences.userRoot().node(this.userTreePath) : Preferences.userRoot();
	}

	/**
	 * This implementation tries to resolve placeholders as keys first
	 * in the user preferences, then in the system preferences, then in
	 * the passed-in properties.
	 * <p>
	 * 此实现尝试将用户首选中的占位符解析为用户首选项,然后在系统首选项中,然后在传入的属性中
	 * 
	 */
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props) {
		String path = null;
		String key = placeholder;
		int endOfPath = placeholder.lastIndexOf('/');
		if (endOfPath != -1) {
			path = placeholder.substring(0, endOfPath);
			key = placeholder.substring(endOfPath + 1);
		}
		String value = resolvePlaceholder(path, key, this.userPrefs);
		if (value == null) {
			value = resolvePlaceholder(path, key, this.systemPrefs);
			if (value == null) {
				value = props.getProperty(placeholder);
			}
		}
		return value;
	}

	/**
	 * Resolve the given path and key against the given Preferences.
	 * <p>
	 *  根据给定的首选项解析给定的路径和键
	 * 
	 * @param path the preferences path (placeholder part before '/')
	 * @param key the preferences key (placeholder part after '/')
	 * @param preferences the Preferences to resolve against
	 * @return the value for the placeholder, or {@code null} if none found
	 */
	protected String resolvePlaceholder(String path, String key, Preferences preferences) {
		if (path != null) {
			 // Do not create the node if it does not exist...
			try {
				if (preferences.nodeExists(path)) {
					return preferences.node(path).get(key, null);
				}
				else {
					return null;
				}
			}
			catch (BackingStoreException ex) {
				throw new BeanDefinitionStoreException("Cannot access specified node path [" + path + "]", ex);
			}
		}
		else {
			return preferences.get(key, null);
		}
	}

}
