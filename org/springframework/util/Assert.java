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

package org.springframework.util;

import java.util.Collection;
import java.util.Map;

/**
 * Assertion utility class that assists in validating arguments.
 *
 * <p>Useful for identifying programmer errors early and clearly at runtime.
 *
 * <p>For example, if the contract of a public method states it does not
 * allow {@code null} arguments, {@code Assert} can be used to validate that
 * contract. Doing this clearly indicates a contract violation when it
 * occurs and protects the class's invariants.
 *
 * <p>Typically used to validate method arguments rather than configuration
 * properties, to check for cases that are usually programmer errors rather
 * than configuration errors. In contrast to configuration initialization
 * code, there is usually no point in falling back to defaults in such methods.
 *
 * <p>This class is similar to JUnit's assertion library. If an argument value is
 * deemed invalid, an {@link IllegalArgumentException} is thrown (typically).
 * For example:
 *
 * <pre class="code">
 * Assert.notNull(clazz, "The class must not be null");
 * Assert.isTrue(i > 0, "The value must be greater than zero");</pre>
 *
 * <p>Mainly for internal use within the framework; consider
 * <a href="http://commons.apache.org/proper/commons-lang/">Apache's Commons Lang</a>
 * for a more comprehensive suite of {@code String} utilities.
 *
 * <p>
 *  有助于验证参数的Assertion实用程序类
 * 
 *  <p>可用于在运行时早期和清楚地识别程序员错误
 * 
 * 例如,如果公共方法的合同规定它不允许{@code null}参数,{@code Assert}可以用于验证合同。这样做明确地表明合同违规发生时,保护类的不变量
 * 
 *  <p>通常用于验证方法参数而不是配置属性,以检查通常是程序员错误而不是配置错误的情况与配置初始化代码相反,这种方法通常没有任何意义上的缺省值
 * 
 *  <p>此类与JUnit的断言库类似。如果参数值被视为无效,则抛出{@link IllegalArgumentException}(通常)。例如：
 * 
 * <pre class="code">
 * AssertnotNull(clazz,"class must not be null"); AssertisTrue(i> 0,"该值必须大于零"); </pre>
 * 
 *  <p>主要用于框架内部使用;考虑<a href=\"http://commonsapacheorg/proper/commons-lang/\"> Apache的Commons Lang </a>,提
 * 供更全面的{@code String}实用程序套件。
 * 
 * 
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Colin Sampaleanu
 * @author Rob Harrop
 * @author Sam Brannen
 * @since 1.1.2
 */
public abstract class Assert {

	/**
	 * Assert a boolean expression, throwing {@code IllegalArgumentException}
	 * if the test result is {@code false}.
	 * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
	 * <p>
	 *  如果测试结果为{@code false},则抛出{@code IllegalArgumentException},这样就会抛出{@code IllegalArgumentException} <pre class ="code">
	 *  AssertisTrue(i> 0,"该值必须大于零"); </pre >。
	 * 
	 * 
	 * @param expression a boolean expression
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert a boolean expression, throwing {@code IllegalArgumentException}
	 * if the test result is {@code false}.
	 * <pre class="code">Assert.isTrue(i &gt; 0);</pre>
	 * <p>
	 *  如果测试结果为{@code false},则抛出一个布尔表达式抛出{@code IllegalArgumentException} <pre class ="code"> AssertisTrue(i
	 * > 0); </pre>。
	 * 
	 * 
	 * @param expression a boolean expression
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	/**
	 * Assert that an object is {@code null} .
	 * <pre class="code">Assert.isNull(value, "The value must be null");</pre>
	 * <p>
	 *  断言一个对象是{@code null} <pre class ="code"> AssertisNull(value,"该值必须为null"); </pre>
	 * 
	 * 
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is not {@code null}
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an object is {@code null} .
	 * <pre class="code">Assert.isNull(value);</pre>
	 * <p>
	 * 断言一个对象是{@code null} <pre class ="code"> AssertisNull(value); </pre>
	 * 
	 * 
	 * @param object the object to check
	 * @throws IllegalArgumentException if the object is not {@code null}
	 */
	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}

	/**
	 * Assert that an object is not {@code null} .
	 * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
	 * <p>
	 *  断言一个对象不是{@code null} <pre class ="code"> AssertnotNull(clazz,"class must not be null"); </pre>
	 * 
	 * 
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an object is not {@code null} .
	 * <pre class="code">Assert.notNull(clazz);</pre>
	 * <p>
	 *  断言一个对象不是{@code null} <pre class ="code"> AssertnotNull(clazz); </pre>
	 * 
	 * 
	 * @param object the object to check
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - this argument is required; it must not be null");
	}

	/**
	 * Assert that the given String is not empty; that is,
	 * it must not be {@code null} and not the empty String.
	 * <pre class="code">Assert.hasLength(name, "Name must not be empty");</pre>
	 * <p>
	 *  断言给定的String不为空;也就是说,它不能是{@code null},而不是空的String <pre class ="code"> AsserthasLength(name,"Name must
	 *  not be empty"); </pre>。
	 * 
	 * 
	 * @param text the String to check
	 * @param message the exception message to use if the assertion fails
	 * @see StringUtils#hasLength
	 * @throws IllegalArgumentException if the text is empty
	 */
	public static void hasLength(String text, String message) {
		if (!StringUtils.hasLength(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that the given String is not empty; that is,
	 * it must not be {@code null} and not the empty String.
	 * <pre class="code">Assert.hasLength(name);</pre>
	 * <p>
	 *  断言给定的String不为空;也就是说,它不能是{@code null},而不是空的String <pre class ="code"> AsserthasLength(name); </pre>
	 * 
	 * 
	 * @param text the String to check
	 * @see StringUtils#hasLength
	 * @throws IllegalArgumentException if the text is empty
	 */
	public static void hasLength(String text) {
		hasLength(text,
				"[Assertion failed] - this String argument must have length; it must not be null or empty");
	}

	/**
	 * Assert that the given String has valid text content; that is, it must not
	 * be {@code null} and must contain at least one non-whitespace character.
	 * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
	 * <p>
	 * 声明给定的String具有有效的文本内容;也就是说,它不能是{@code null},并且必须包含至少一个非空格字符<pre class ="code"> AsserthasText(name,"'na
	 * me'不能为空"); </pre>。
	 * 
	 * 
	 * @param text the String to check
	 * @param message the exception message to use if the assertion fails
	 * @see StringUtils#hasText
	 * @throws IllegalArgumentException if the text does not contain valid text content
	 */
	public static void hasText(String text, String message) {
		if (!StringUtils.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that the given String has valid text content; that is, it must not
	 * be {@code null} and must contain at least one non-whitespace character.
	 * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
	 * <p>
	 *  声明给定的String具有有效的文本内容;也就是说,它不能是{@code null},并且必须包含至少一个非空格字符<pre class ="code"> AsserthasText(name,"'n
	 * ame'不能为空"); </pre>。
	 * 
	 * 
	 * @param text the String to check
	 * @see StringUtils#hasText
	 * @throws IllegalArgumentException if the text does not contain valid text content
	 */
	public static void hasText(String text) {
		hasText(text,
				"[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
	}

	/**
	 * Assert that the given text does not contain the given substring.
	 * <pre class="code">Assert.doesNotContain(name, "rod", "Name must not contain 'rod'");</pre>
	 * <p>
	 *  断言给定的文本不包含给定的子串<pre class ="code"> AssertdoesNotContain(name,"rod","Name must not contains'rod'"); </pre>
	 * 。
	 * 
	 * 
	 * @param textToSearch the text to search
	 * @param substring the substring to find within the text
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the text contains the substring
	 */
	public static void doesNotContain(String textToSearch, String substring, String message) {
		if (StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) &&
				textToSearch.contains(substring)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that the given text does not contain the given substring.
	 * <pre class="code">Assert.doesNotContain(name, "rod");</pre>
	 * <p>
	 *  断言给定的文本不包含给定的子串<pre class ="code"> AssertdoesNotContain(name,"rod"); </pre>
	 * 
	 * 
	 * @param textToSearch the text to search
	 * @param substring the substring to find within the text
	 * @throws IllegalArgumentException if the text contains the substring
	 */
	public static void doesNotContain(String textToSearch, String substring) {
		doesNotContain(textToSearch, substring,
				"[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
	}

	/**
	 * Assert that an array has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(array, "The array must have elements");</pre>
	 * <p>
	 * 断言数组有元素;也就是说,它不能是{@code null},并且必须有至少一个元素<pre class ="code"> AssertnotEmpty(array,"数组必须有元素"); </pre>。
	 * 
	 * 
	 * @param array the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array is {@code null} or has no elements
	 */
	public static void notEmpty(Object[] array, String message) {
		if (ObjectUtils.isEmpty(array)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an array has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(array);</pre>
	 * <p>
	 *  断言数组有元素;也就是说,它不能是{@code null},并且必须至少有一个元素<pre class ="code"> AssertnotEmpty(array); </pre>
	 * 
	 * 
	 * @param array the array to check
	 * @throws IllegalArgumentException if the object array is {@code null} or has no elements
	 */
	public static void notEmpty(Object[] array) {
		notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
	}

	/**
	 * Assert that an array has no null elements.
	 * Note: Does not complain if the array is empty!
	 * <pre class="code">Assert.noNullElements(array, "The array must have non-null elements");</pre>
	 * <p>
	 *  声明数组没有null元素注意：不要抱怨数组是空的！ <pre class ="code"> AssertnoNullElements(array,"数组必须有非空元素"); </pre>
	 * 
	 * 
	 * @param array the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array contains a {@code null} element
	 */
	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	/**
	 * Assert that an array has no null elements.
	 * Note: Does not complain if the array is empty!
	 * <pre class="code">Assert.noNullElements(array);</pre>
	 * <p>
	 *  声明数组没有null元素注意：不要抱怨数组是空的！ <pre class ="code"> AssertnoNullElements(array); </pre>
	 * 
	 * 
	 * @param array the array to check
	 * @throws IllegalArgumentException if the object array contains a {@code null} element
	 */
	public static void noNullElements(Object[] array) {
		noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
	}

	/**
	 * Assert that a collection has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(collection, "Collection must have elements");</pre>
	 * <p>
	 * 断言一个集合有元素;也就是说,它不能是{@code null},并且必须至少有一个元素<pre class ="code"> AssertnotEmpty(collection,"Collection 
	 * must have elements"); </pre>。
	 * 
	 * 
	 * @param collection the collection to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the collection is {@code null} or has no elements
	 */
	public static void notEmpty(Collection<?> collection, String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that a collection has elements; that is, it must not be
	 * {@code null} and must have at least one element.
	 * <pre class="code">Assert.notEmpty(collection, "Collection must have elements");</pre>
	 * <p>
	 *  断言一个集合有元素;也就是说,它不能是{@code null},并且必须至少有一个元素<pre class ="code"> AssertnotEmpty(collection,"Collection
	 *  must have elements"); </pre>。
	 * 
	 * 
	 * @param collection the collection to check
	 * @throws IllegalArgumentException if the collection is {@code null} or has no elements
	 */
	public static void notEmpty(Collection<?> collection) {
		notEmpty(collection,
				"[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
	}

	/**
	 * Assert that a Map has entries; that is, it must not be {@code null}
	 * and must have at least one entry.
	 * <pre class="code">Assert.notEmpty(map, "Map must have entries");</pre>
	 * <p>
	 *  断言地图有条目;也就是说,它不能是{@code null},并且必须至少有一个条目<pre class ="code"> AssertnotEmpty(map,"Map must have entri
	 * es"); </pre>。
	 * 
	 * 
	 * @param map the map to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the map is {@code null} or has no entries
	 */
	public static void notEmpty(Map<?, ?> map, String message) {
		if (CollectionUtils.isEmpty(map)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that a Map has entries; that is, it must not be {@code null}
	 * and must have at least one entry.
	 * <pre class="code">Assert.notEmpty(map);</pre>
	 * <p>
	 *  断言地图有条目;也就是说,它不能是{@code null},并且必须至少有一个条目<pre class ="code"> AssertnotEmpty(map); </pre>
	 * 
	 * 
	 * @param map the map to check
	 * @throws IllegalArgumentException if the map is {@code null} or has no entries
	 */
	public static void notEmpty(Map<?, ?> map) {
		notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
	}

	/**
	 * Assert that the provided object is an instance of the provided class.
	 * <pre class="code">Assert.instanceOf(Foo.class, foo);</pre>
	 * <p>
	 * 声明所提供的对象是提供的类的一个实例<pre class ="code"> AssertinstanceOf(Fooclass,foo); </pre>
	 * 
	 * 
	 * @param clazz the required class
	 * @param obj the object to check
	 * @throws IllegalArgumentException if the object is not an instance of clazz
	 * @see Class#isInstance
	 */
	public static void isInstanceOf(Class<?> clazz, Object obj) {
		isInstanceOf(clazz, obj, "");
	}

	/**
	 * Assert that the provided object is an instance of the provided class.
	 * <pre class="code">Assert.instanceOf(Foo.class, foo);</pre>
	 * <p>
	 *  声明所提供的对象是提供的类的一个实例<pre class ="code"> AssertinstanceOf(Fooclass,foo); </pre>
	 * 
	 * 
	 * @param type the type to check against
	 * @param obj the object to check
	 * @param message a message which will be prepended to the message produced by
	 * the function itself, and which may be used to provide context. It should
	 * normally end in ":" or "." so that the generated message looks OK when
	 * appended to it.
	 * @throws IllegalArgumentException if the object is not an instance of clazz
	 * @see Class#isInstance
	 */
	public static void isInstanceOf(Class<?> type, Object obj, String message) {
		notNull(type, "Type to check against must not be null");
		if (!type.isInstance(obj)) {
			throw new IllegalArgumentException(
					(StringUtils.hasLength(message) ? message + " " : "") +
					"Object of class [" + (obj != null ? obj.getClass().getName() : "null") +
					"] must be an instance of " + type);
		}
	}

	/**
	 * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
	 * <pre class="code">Assert.isAssignable(Number.class, myClass);</pre>
	 * <p>
	 *  断言{@code superTypeisAssignableFrom(subType)}是{@code true} <pre class ="code"> AssertisAssignable(Num
	 * berclass,myClass); </pre>。
	 * 
	 * 
	 * @param superType the super type to check
	 * @param subType the sub type to check
	 * @throws IllegalArgumentException if the classes are not assignable
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType) {
		isAssignable(superType, subType, "");
	}

	/**
	 * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
	 * <pre class="code">Assert.isAssignable(Number.class, myClass);</pre>
	 * <p>
	 *  断言{@code superTypeisAssignableFrom(subType)}是{@code true} <pre class ="code"> AssertisAssignable(Num
	 * berclass,myClass); </pre>。
	 * 
	 * 
	 * @param superType the super type to check against
	 * @param subType the sub type to check
	 * @param message a message which will be prepended to the message produced by
	 * the function itself, and which may be used to provide context. It should
	 * normally end in ":" or "." so that the generated message looks OK when
	 * appended to it.
	 * @throws IllegalArgumentException if the classes are not assignable
	 */
	public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
		notNull(superType, "Type to check against must not be null");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException((StringUtils.hasLength(message) ? message + " " : "") +
					subType + " is not assignable to " + superType);
		}
	}

	/**
	 * Assert a boolean expression, throwing {@code IllegalStateException}
	 * if the test result is {@code false}. Call isTrue if you wish to
	 * throw IllegalArgumentException on an assertion failure.
	 * <pre class="code">Assert.state(id == null, "The id property must not already be initialized");</pre>
	 * <p>
	 * 如果测试结果为{@code false},则抛出{@code IllegalStateException}如果您希望在断言失败时抛出IllegalArgumentException,则抛出{@code IllegalStateException}
	 *  <pre class ="code"> Assertstate(id == null,"The id属性不能被初始化"); </pre>。
	 * 
	 * 
	 * @param expression a boolean expression
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalStateException if expression is {@code false}
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Assert a boolean expression, throwing {@link IllegalStateException}
	 * if the test result is {@code false}.
	 * <p>Call {@link #isTrue(boolean)} if you wish to
	 * throw {@link IllegalArgumentException} on an assertion failure.
	 * <pre class="code">Assert.state(id == null);</pre>
	 * <p>
	 *  如果您希望将{@link IllegalArgumentException}抛出一个断言失败,则声明一个布尔表达式,如果测试结果为{@code false} <p>调用{@link #isTrue(boolean)}
	 * ,则抛出{@link IllegalStateException} class ="code"> Assertstate(id == null); </pre>。
	 * 
	 * @param expression a boolean expression
	 * @throws IllegalStateException if the supplied expression is {@code false}
	 */
	public static void state(boolean expression) {
		state(expression, "[Assertion failed] - this state invariant must be true");
	}

}
