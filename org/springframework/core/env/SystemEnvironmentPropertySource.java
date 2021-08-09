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

package org.springframework.core.env;

import java.util.Map;

import org.springframework.util.Assert;

/**
 * Specialization of {@link MapPropertySource} designed for use with
 * {@linkplain AbstractEnvironment#getSystemEnvironment() system environment variables}.
 * Compensates for constraints in Bash and other shells that do not allow for variables
 * containing the period character and/or hyphen character; also allows for uppercase
 * variations on property names for more idiomatic shell use.
 *
 * <p>For example, a call to {@code getProperty("foo.bar")} will attempt to find a value
 * for the original property or any 'equivalent' property, returning the first found:
 * <ul>
 * <li>{@code foo.bar} - the original name</li>
 * <li>{@code foo_bar} - with underscores for periods (if any)</li>
 * <li>{@code FOO.BAR} - original, with upper case</li>
 * <li>{@code FOO_BAR} - with underscores and upper case</li>
 * </ul>
 * Any hyphen variant of the above would work as well, or even mix dot/hyphen variants.
 *
 * <p>The same applies for calls to {@link #containsProperty(String)}, which returns
 * {@code true} if any of the above properties are present, otherwise {@code false}.
 *
 * <p>This feature is particularly useful when specifying active or default profiles as
 * environment variables. The following is not allowable under Bash:
 *
 * <pre class="code">spring.profiles.active=p1 java -classpath ... MyApp</pre>
 *
 * However, the following syntax is permitted and is also more conventional:
 *
 * <pre class="code">SPRING_PROFILES_ACTIVE=p1 java -classpath ... MyApp</pre>
 *
 * <p>Enable debug- or trace-level logging for this class (or package) for messages
 * explaining when these 'property name resolutions' occur.
 *
 * <p>This property source is included by default in {@link StandardEnvironment}
 * and all its subclasses.
 *
 * <p>
 * 专为{@link MapPropertySource}设计的{@linkplain AbstractEnvironment#getSystemEnvironment()系统环境变量}的补充}补偿Bash
 * 和其他不允许包含句点和/或连字符的变量的shell的约束;还允许属性名称上的大写变体用于更常用的shell使用。
 * 
 *  <p>例如,调用{@code getProperty("foobar")}将尝试找到原始属性或任何"等效"属性的值,返回第一个找到的值：
 * <ul>
 *  <li> {@ code foobar}  - 原始名称</li> <li> {@ code foo_bar}  - 带有下划线(如果有)</li> <li> {@ code FOOBAR}  - 原
 * 始,案例</li> <li> {@ code FOO_BAR}  - 带下划线和大写</li>。
 * </ul>
 * 上述的任何连字符变体也可以工作,甚至混合点/连字符变体
 * 
 *  <p>同样适用于对{@link #containsProperty(String)}的调用,如果存在任何上述属性,则返回{@code true},否则{@code false}
 * 
 *  <p>当将活动或默认配置文件指定为环境变量时,此功能特别有用Bash以下是不允许的：
 * 
 *  <pre class ="code"> springprofilesactive = p1 java -classpath MyApp </pre>
 * 
 *  但是,允许使用以下语法,而且更为常规：
 * 
 * 
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see StandardEnvironment
 * @see AbstractEnvironment#getSystemEnvironment()
 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
 */
public class SystemEnvironmentPropertySource extends MapPropertySource {

	/**
	 * Create a new {@code SystemEnvironmentPropertySource} with the given name and
	 * delegating to the given {@code MapPropertySource}.
	 * <p>
	 *  <pre class ="code"> SPRING_PROFILES_ACTIVE = p1 java -classpath MyApp </pre>
	 * 
	 *  <p>为此类(或程序包)启用调试或跟踪级别日志记录,以解释何时发生这些"属性名称解析"
	 * 
	 * <p>此属性源默认包含在{@link StandardEnvironment}及其所有子类中
	 * 
	 */
	public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}


	/**
	 * Return {@code true} if a property with the given name or any underscore/uppercase variant
	 * thereof exists in this property source.
	 * <p>
	 *  用给定的名称创建一个新的{@code SystemEnvironmentPropertySource}并委托给给定的{@code MapPropertySource}
	 * 
	 */
	@Override
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	/**
	 * This implementation returns {@code true} if a property with the given name or
	 * any underscore/uppercase variant thereof exists in this property source.
	 * <p>
	 *  如果在此属性源中存在具有给定名称或任何下划线/大写变体的属性,则返回{@code true}
	 * 
	 */
	@Override
	public Object getProperty(String name) {
		String actualName = resolvePropertyName(name);
		if (logger.isDebugEnabled() && !name.equals(actualName)) {
			logger.debug(String.format("PropertySource [%s] does not contain '%s', but found equivalent '%s'",
					getName(), name, actualName));
		}
		return super.getProperty(actualName);
	}

	/**
	 * Check to see if this property source contains a property with the given name, or
	 * any underscore / uppercase variation thereof. Return the resolved name if one is
	 * found or otherwise the original name. Never returns {@code null}.
	 * <p>
	 *  如果在此属性源中存在具有给定名称或任何下划线/大写变体的属性,则此实现将返回{@code true}
	 * 
	 */
	private String resolvePropertyName(String name) {
		Assert.notNull(name, "Property name must not be null");
		String resolvedName = checkPropertyName(name);
		if (resolvedName != null) {
			return resolvedName;
		}
		String uppercasedName = name.toUpperCase();
		if (!name.equals(uppercasedName)) {
			resolvedName = checkPropertyName(uppercasedName);
			if (resolvedName != null) {
				return resolvedName;
			}
		}
		return name;
	}

	private String checkPropertyName(String name) {
		// Check name as-is
		if (containsKey(name)) {
			return name;
		}
		// Check name with just dots replaced
		String noDotName = name.replace('.', '_');
		if (!name.equals(noDotName) && containsKey(noDotName)) {
			return noDotName;
		}
		// Check name with just hyphens replaced
		String noHyphenName = name.replace('-', '_');
		if (!name.equals(noHyphenName) && containsKey(noHyphenName)) {
			return noHyphenName;
		}
		// Check name with dots and hyphens replaced
		String noDotNoHyphenName = noDotName.replace('-', '_');
		if (!noDotName.equals(noDotNoHyphenName) && containsKey(noDotNoHyphenName)) {
			return noDotNoHyphenName;
		}
		// Give up
		return null;
	}

	private boolean containsKey(String name) {
		return (isSecurityManagerPresent() ? this.source.keySet().contains(name) : this.source.containsKey(name));
	}

	protected boolean isSecurityManagerPresent() {
		return (System.getSecurityManager() != null);
	}

}
