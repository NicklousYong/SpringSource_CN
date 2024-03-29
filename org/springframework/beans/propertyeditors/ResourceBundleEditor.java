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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link java.beans.PropertyEditor} implementation for standard JDK
 * {@link java.util.ResourceBundle ResourceBundles}.
 *
 * <p>Only supports conversion <i>from</i> a String, but not <i>to</i> a String.
 *
 * Find below some examples of using this class in a (properly configured)
 * Spring container using XML-based metadata:
 *
 * <pre class="code"> &lt;bean id="errorDialog" class="..."&gt;
 *    &lt;!--
 *        the 'messages' property is of type java.util.ResourceBundle.
 *        the 'DialogMessages.properties' file exists at the root of the CLASSPATH
 *    --&gt;
 *    &lt;property name="messages" value="DialogMessages"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * <pre class="code"> &lt;bean id="errorDialog" class="..."&gt;
 *    &lt;!--
 *        the 'DialogMessages.properties' file exists in the 'com/messages' package
 *    --&gt;
 *    &lt;property name="messages" value="com/messages/DialogMessages"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * <p>A 'properly configured' Spring {@link org.springframework.context.ApplicationContext container}
 * might contain a {@link org.springframework.beans.factory.config.CustomEditorConfigurer}
 * definition such that the conversion can be effected transparently:
 *
 * <pre class="code"> &lt;bean class="org.springframework.beans.factory.config.CustomEditorConfigurer"&gt;
 *    &lt;property name="customEditors"&gt;
 *        &lt;map&gt;
 *            &lt;entry key="java.util.ResourceBundle"&gt;
 *                &lt;bean class="org.springframework.beans.propertyeditors.ResourceBundleEditor"/&gt;
 *            &lt;/entry&gt;
 *        &lt;/map&gt;
 *    &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * <p>Please note that this {@link java.beans.PropertyEditor} is <b>not</b>
 * registered by default with any of the Spring infrastructure.
 *
 * <p>Thanks to David Leal Valmana for the suggestion and initial prototype.
 *
 * <p>
 *  标准JDK的{@link javabeansPropertyEditor}实现{@link javautilResourceBundle ResourceBundles}
 * 
 *  <p>仅支持从</i>一个字符串转换<i>,而不支持将<i>转换为</i>一个字符串
 * 
 * 在下面的一些示例中,使用基于XML的元数据在(正确配置的)Spring容器中使用此类：
 * 
 *  <pre class ="code">&lt; bean id ="errorDialog"class =""&gt; &lt;！ - 'messages'属性的类型为javautilResource
 * Bundle'DialogMessagesproperties'文件存在于CLASSPATH的根目录下 - &gt; &lt; property name ="messages"value ="Dial
 * ogMessages"/&gt; &LT; /豆腐&GT; </PRE>。
 * 
 *  <pre class ="code">&lt; bean id ="errorDialog"class =""&gt; &lt;！ - 'DialogMessagesproperties'文件存在于'
 * com / messages'包中 - &gt; &lt; property name ="messages"value ="com / messages / DialogMessages"/&gt; 
 * &LT; /豆腐&GT; </PRE>。
 * 
 * <p>"正确配置"的Spring {@link orgspringframeworkcontextApplicationContext容器}可能包含{@link orgspringframeworkbeansfactoryconfigCustomEditorConfigurer}
 * 
 * @author Rick Evans
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ResourceBundleEditor extends PropertyEditorSupport {

	/**
	 * The separator used to distinguish between the base name and the locale
	 * (if any) when {@link #setAsText(String) converting from a String}.
	 * <p>
	 * 定义,以便转换可以透明地实现：。
	 * 
	 *  <pre class ="code">&lt; bean class ="orgspringframeworkbeansfactoryconfigCustomEditorConfigurer"&gt;
	 *  &lt; property name ="customEditors"&gt; &LT;地图&GT; &lt; entry key ="javautilResourceBundle"&gt; &lt;
	 *  bean class ="orgspringframeworkbeanspropertyeditorsResourceBundleEditor"/&gt; &LT; /条目&GT; &LT; /地图&
	 * GT; &LT; /性&gt; &LT; /豆腐&GT; </PRE>。
	 * 
	 */
	public static final String BASE_NAME_SEPARATOR = "_";


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Assert.hasText(text, "'text' must not be empty");
		String name = text.trim();

		int separator = name.indexOf(BASE_NAME_SEPARATOR);
		if (separator == -1) {
			setValue(ResourceBundle.getBundle(name));
		}
		else {
			// The name potentially contains locale information
			String baseName = name.substring(0, separator);
			if (!StringUtils.hasText(baseName)) {
				throw new IllegalArgumentException("Invalid ResourceBundle name: '" + text + "'");
			}
			String localeString = name.substring(separator + 1);
			Locale locale = StringUtils.parseLocaleString(localeString);
			setValue((StringUtils.hasText(localeString)) ? ResourceBundle.getBundle(baseName, locale) :
					ResourceBundle.getBundle(baseName));
		}
	}

}
