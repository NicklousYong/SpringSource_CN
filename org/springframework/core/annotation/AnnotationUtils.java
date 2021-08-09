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

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * General utility methods for working with annotations, handling meta-annotations,
 * bridge methods (which the compiler generates for generic declarations) as well
 * as super methods (for optional <em>annotation inheritance</em>).
 *
 * <p>Note that most of the features of this class are not provided by the
 * JDK's introspection facilities themselves.
 *
 * <p>As a general rule for runtime-retained annotations (e.g. for transaction
 * control, authorization, or service exposure), always use the lookup methods
 * on this class (e.g., {@link #findAnnotation(Method, Class)},
 * {@link #getAnnotation(Method, Class)}, and {@link #getAnnotations(Method)})
 * instead of the plain annotation lookup methods in the JDK. You can still
 * explicitly choose between a <em>get</em> lookup on the given class level only
 * ({@link #getAnnotation(Method, Class)}) and a <em>find</em> lookup in the entire
 * inheritance hierarchy of the given method ({@link #findAnnotation(Method, Class)}).
 *
 * <h3>Terminology</h3>
 * The terms <em>directly present</em>, <em>indirectly present</em>, and
 * <em>present</em> have the same meanings as defined in the class-level
 * Javadoc for {@link AnnotatedElement} (in Java 8).
 *
 * <p>An annotation is <em>meta-present</em> on an element if the annotation
 * is declared as a meta-annotation on some other annotation which is
 * <em>present</em> on the element. Annotation {@code A} is <em>meta-present</em>
 * on another annotation if {@code A} is either <em>directly present</em> or
 * <em>meta-present</em> on the other annotation.
 *
 * <h3>Meta-annotation Support</h3>
 * <p>Most {@code find*()} methods and some {@code get*()} methods in this class
 * provide support for finding annotations used as meta-annotations. Consult the
 * javadoc for each method in this class for details. For fine-grained support for
 * meta-annotations with <em>attribute overrides</em> in <em>composed annotations</em>,
 * consider using {@link AnnotatedElementUtils}'s more specific methods instead.
 *
 * <h3>Attribute Aliases</h3>
 * <p>All public methods in this class that return annotations, arrays of
 * annotations, or {@link AnnotationAttributes} transparently support attribute
 * aliases configured via {@link AliasFor @AliasFor}. Consult the various
 * {@code synthesizeAnnotation*(..)} methods for details.
 *
 * <h3>Search Scope</h3>
 * <p>The search algorithms used by methods in this class stop searching for
 * an annotation once the first annotation of the specified type has been
 * found. As a consequence, additional annotations of the specified type will
 * be silently ignored.
 *
 * <p>
 *  用于处理注释,处理元注释,桥接方法(编译器为通用声明生成的)的一般实用方法以及超级方法(用于可选的<em>注释继承</em>)
 * 
 * 注意,这个类的大部分功能都不是由JDK的内省设施本身提供的
 * 
 *  作为运行时保留注释的一般规则(例如用于事务控制,授权或服务暴露),始终使用此类上的查找方法(例如{@link #findAnnotation(Method,Class)},{@链接#getAnnotation(Method,Class)}
 * 和{@link #getAnnotations(Method)}),而不是JDK中的简单注释查找方法您还可以在给定的<em> get </em>查找之间明确选择在给定方法({@link #findAnnotation(Method,Class)}
 * )的整个继承层次结构中,只有类级别({@link #getAnnotation(Method,Class)})和<em> find </em>查找)。
 * 
 * <h3>术语</h3>间接呈现</em>,<em>直接出现<em> <em> <em> <em> <em> <em> <em> <em> <em> <em> {@link AnnotatedElement}
 * 的Javadoc(在Java 8中)。
 * 
 *  <p>如果注释被声明为在元素注释{@code上的<em>存在</em>的某些其他注释上的元注释,那么元素上的注释是<em>元素存在</em>如果{@code A}直接存在</em>或<em>存在</em>
 * ,另一个注释上的A}是<em>元存在</em>。
 * 
 * <h3>元标注支持</h3> <p>此类中的大多数{@code find *()}方法和一些{@code get *()}方法提供了支持查找用作元注释的注释。
 *  javadoc为此类中的每个方法详细信息对于在<em>组合注释<em>中使用<em>属性覆盖</em>的元注释的细粒度支持,请考虑使用{@link AnnotatedElementUtils}更具体的
 * 方法代替。
 * <h3>元标注支持</h3> <p>此类中的大多数{@code find *()}方法和一些{@code get *()}方法提供了支持查找用作元注释的注释。
 * 
 *  <h3>属性别名</h3> <p>此类中返回注释,注释数组或{@link AnnotationAttributes}的所有公共方法透明地支持通过{@link AliasFor @AliasFor}配置
 * 的属性别名查看各种{@ code synthesizeAnnotation *()}方法的详细信息。
 * 
 * <h3>搜索范围</h3> <p>一旦找到指定类型的第一个注释,此类中的方法使用的搜索算法就会停止搜索注释。因此,指定类型的附加注释将默认忽视
 * 
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Mark Fisher
 * @author Chris Beams
 * @author Phillip Webb
 * @since 2.0
 * @see AliasFor
 * @see AnnotationAttributes
 * @see AnnotatedElementUtils
 * @see BridgeMethodResolver
 * @see java.lang.reflect.AnnotatedElement#getAnnotations()
 * @see java.lang.reflect.AnnotatedElement#getAnnotation(Class)
 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotations()
 */
public abstract class AnnotationUtils {

	/**
	 * The attribute name for annotations with a single element.
	 * <p>
	 *  具有单个元素的注释的属性名称
	 * 
	 */
	public static final String VALUE = "value";

	private static final String REPEATABLE_CLASS_NAME = "java.lang.annotation.Repeatable";

	private static final Map<AnnotationCacheKey, Annotation> findAnnotationCache =
			new ConcurrentReferenceHashMap<AnnotationCacheKey, Annotation>(256);

	private static final Map<AnnotationCacheKey, Boolean> metaPresentCache =
			new ConcurrentReferenceHashMap<AnnotationCacheKey, Boolean>(256);

	private static final Map<Class<?>, Boolean> annotatedInterfaceCache =
			new ConcurrentReferenceHashMap<Class<?>, Boolean>(256);

	private static final Map<Class<? extends Annotation>, Boolean> synthesizableCache =
			new ConcurrentReferenceHashMap<Class<? extends Annotation>, Boolean>(256);

	private static final Map<Class<? extends Annotation>, Map<String, List<String>>> attributeAliasesCache =
			new ConcurrentReferenceHashMap<Class<? extends Annotation>, Map<String, List<String>>>(256);

	private static final Map<Class<? extends Annotation>, List<Method>> attributeMethodsCache =
			new ConcurrentReferenceHashMap<Class<? extends Annotation>, List<Method>>(256);

	private static final Map<Method, AliasDescriptor> aliasDescriptorCache =
			new ConcurrentReferenceHashMap<Method, AliasDescriptor>(256);

	private static transient Log logger;


	/**
	 * Get a single {@link Annotation} of {@code annotationType} from the supplied
	 * annotation: either the given annotation itself or a direct meta-annotation
	 * thereof.
	 * <p>Note that this method supports only a single level of meta-annotations.
	 * For support for arbitrary levels of meta-annotations, use one of the
	 * {@code find*()} methods instead.
	 * <p>
	 *  从提供的注释中获取{@code注释类型}的单一{@link注释}：给定的注释本身或其直接元注释<p>请注意,此方法仅支持单级元标注用于支持任意级别的元注释,使用{@code find *()}方法之一
	 * 。
	 * 
	 * 
	 * @param ann the Annotation to check
	 * @param annotationType the annotation type to look for, both locally and as a meta-annotation
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 4.0
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getAnnotation(Annotation ann, Class<A> annotationType) {
		if (annotationType.isInstance(ann)) {
			return synthesizeAnnotation((A) ann);
		}
		Class<? extends Annotation> annotatedElement = ann.annotationType();
		try {
			return synthesizeAnnotation(annotatedElement.getAnnotation(annotationType), annotatedElement);
		}
		catch (Exception ex) {
			handleIntrospectionFailure(annotatedElement, ex);
		}
		return null;
	}

	/**
	 * Get a single {@link Annotation} of {@code annotationType} from the supplied
	 * {@link AnnotatedElement}, where the annotation is either <em>present</em> or
	 * <em>meta-present</em> on the {@code AnnotatedElement}.
	 * <p>Note that this method supports only a single level of meta-annotations.
	 * For support for arbitrary levels of meta-annotations, use
	 * {@link #findAnnotation(AnnotatedElement, Class)} instead.
	 * <p>
	 * 从提供的{@link注释元素}中获取{@code注释类型}的{@link注释},其中注释是<em> <em> <em> <em> <em> <em> </em> @code AnnotatedEleme
	 * nt} <p>请注意,此方法仅支持单级元注释为了支持任意级别的元注释,请使用{@link #findAnnotation(AnnotatedElement,Class)}替代。
	 * 
	 * 
	 * @param annotatedElement the {@code AnnotatedElement} from which to get the annotation
	 * @param annotationType the annotation type to look for, both locally and as a meta-annotation
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 3.1
	 */
	public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
		try {
			A annotation = annotatedElement.getAnnotation(annotationType);
			if (annotation == null) {
				for (Annotation metaAnn : annotatedElement.getAnnotations()) {
					annotation = metaAnn.annotationType().getAnnotation(annotationType);
					if (annotation != null) {
						break;
					}
				}
			}
			return synthesizeAnnotation(annotation, annotatedElement);
		}
		catch (Exception ex) {
			handleIntrospectionFailure(annotatedElement, ex);
		}
		return null;
	}

	/**
	 * Get a single {@link Annotation} of {@code annotationType} from the
	 * supplied {@link Method}, where the annotation is either <em>present</em>
	 * or <em>meta-present</em> on the method.
	 * <p>Correctly handles bridge {@link Method Methods} generated by the compiler.
	 * <p>Note that this method supports only a single level of meta-annotations.
	 * For support for arbitrary levels of meta-annotations, use
	 * {@link #findAnnotation(Method, Class)} instead.
	 * <p>
	 * 从提供的{@link方法}中获取{@code注释类型}的{@link注释},其中注释是<em>存在</em>或<em> </em>元数据</em> <p>正确处理由编译器生成的桥接{@link方法} <p>
	 * 请注意,此方法仅支持单级元标注为了支持任意级别的元注释,请使用{@link #findAnnotation(Method ,Class)}。
	 * 
	 * 
	 * @param method the method to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the first matching annotation, or {@code null} if not found
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod(Method)
	 * @see #getAnnotation(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
		Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
		return getAnnotation((AnnotatedElement) resolvedMethod, annotationType);
	}

	/**
	 * Get all {@link Annotation Annotations} that are <em>present</em> on the
	 * supplied {@link AnnotatedElement}.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>
	 *  获取所提供的{@link注释元素}元标记的所有{@link注释注释} <em>不会<em> </em>
	 * 
	 * 
	 * @param annotatedElement the Method, Constructor or Field to retrieve annotations from
	 * @return the annotations found, an empty array, or {@code null} if not
	 * resolvable (e.g. because nested Class values in annotation attributes
	 * failed to resolve at runtime)
	 * @since 4.0.8
	 * @see AnnotatedElement#getAnnotations()
	 */
	public static Annotation[] getAnnotations(AnnotatedElement annotatedElement) {
		try {
			return synthesizeAnnotationArray(annotatedElement.getAnnotations(), annotatedElement);
		}
		catch (Exception ex) {
			handleIntrospectionFailure(annotatedElement, ex);
		}
		return null;
	}

	/**
	 * Get all {@link Annotation Annotations} that are <em>present</em> on the
	 * supplied {@link Method}.
	 * <p>Correctly handles bridge {@link Method Methods} generated by the compiler.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>
	 * 获取所提供的{@link方法} <p>上的所有{@link注释注释} </em>正确处理由编译器生成的桥接器{@link方法} <p>元标注将< em>不</em>被搜索
	 * 
	 * 
	 * @param method the Method to retrieve annotations from
	 * @return the annotations found, an empty array, or {@code null} if not
	 * resolvable (e.g. because nested Class values in annotation attributes
	 * failed to resolve at runtime)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod(Method)
	 * @see AnnotatedElement#getAnnotations()
	 */
	public static Annotation[] getAnnotations(Method method) {
		try {
			return synthesizeAnnotationArray(BridgeMethodResolver.findBridgedMethod(method).getAnnotations(), method);
		}
		catch (Exception ex) {
			handleIntrospectionFailure(method, ex);
		}
		return null;
	}

	/**
	 * Delegates to {@link #getRepeatableAnnotations(AnnotatedElement, Class, Class)}.
	 * <p>
	 *  代表{@link #getRepeatableAnnotations(AnnotatedElement,Class,Class)}
	 * 
	 * 
	 * @since 4.0
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @deprecated As of Spring Framework 4.2, use {@code getRepeatableAnnotations()}
	 * or {@code getDeclaredRepeatableAnnotations()} instead.
	 */
	@Deprecated
	public static <A extends Annotation> Set<A> getRepeatableAnnotation(Method method,
			Class<? extends Annotation> containerAnnotationType, Class<A> annotationType) {

		return getRepeatableAnnotations(method, annotationType, containerAnnotationType);
	}

	/**
	 * Delegates to {@link #getRepeatableAnnotations(AnnotatedElement, Class, Class)}.
	 * <p>
	 *  代表{@link #getRepeatableAnnotations(AnnotatedElement,Class,Class)}
	 * 
	 * 
	 * @since 4.0
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @deprecated As of Spring Framework 4.2, use {@code getRepeatableAnnotations()}
	 * or {@code getDeclaredRepeatableAnnotations()} instead.
	 */
	@Deprecated
	public static <A extends Annotation> Set<A> getRepeatableAnnotation(AnnotatedElement annotatedElement,
			Class<? extends Annotation> containerAnnotationType, Class<A> annotationType) {

		return getRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType);
	}

	/**
	 * Get the <em>repeatable</em> {@linkplain Annotation annotations} of
	 * {@code annotationType} from the supplied {@link AnnotatedElement}, where
	 * such annotations are either <em>present</em>, <em>indirectly present</em>,
	 * or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getAnnotationsByType(Class)}
	 * with support for automatic detection of a <em>container annotation</em>
	 * declared via @{@link java.lang.annotation.Repeatable} (when running on
	 * Java 8 or higher) and with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * <p>
	 * 从提供的{@link AnnotatedElement}获取{@code注释类型}中的</em>可重复的</em> {@linkplain注释},其中这些注释是<em>存在</em>,<em>间接呈现</em>
	 * 元素</em>元素</em> </em>此方法模仿Java 8的{@link javalangreflectAnnotatedElement#getAnnotationsByType(Class)}的功
	 * 能,支持自动检测<em>通过@ {@ link javalangannotationRepeatable}(在Java 8或更高版本上运行时)声明容器注释</em>以及对元注释的额外支持<p>处理嵌套在
	 * <em>容器注释中的单个注释和注释</em> em> <p>如果提供的元素是{@link方法},正确地处理由编译器生成的</em>桥接方法</em><p>如果提供的元素上的注释不是<em>存在</em>
	 * ,则会搜索元标注。
	 * 
	 * 
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(AnnotatedElement, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getAnnotationsByType
	 */
	public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement,
			Class<A> annotationType) {

		return getRepeatableAnnotations(annotatedElement, annotationType, null);
	}

	/**
	 * Get the <em>repeatable</em> {@linkplain Annotation annotations} of
	 * {@code annotationType} from the supplied {@link AnnotatedElement}, where
	 * such annotations are either <em>present</em>, <em>indirectly present</em>,
	 * or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getAnnotationsByType(Class)}
	 * with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * <p>
	 * 从提供的{@link AnnotatedElement}获取{@code注释类型}中的</em>可重复的</em> {@linkplain注释},其中这些注释是<em>存在</em>,<em>间接呈现</em>
	 * 或<em>元素</em>元素</p> </em>此方法模仿Java 8的{@link javalangreflectAnnotatedElement#getAnnotationsByType(Class)}
	 * 的功能,并附加了对元注释的支持<p>处理嵌套在<em>容器注解中的单个注释和注释</em> <p>正确地处理由编译器生成的</em>桥接方法,如果提供的元素是{@link方法} <p>如果提供的元素上的
	 * 注释不是<em>存在</em>,则会搜索元标注。
	 * 
	 * 
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @param containerAnnotationType the type of the container that holds
	 * the annotations; may be {@code null} if a container is not supported
	 * or if it should be looked up via @{@link java.lang.annotation.Repeatable}
	 * when running on Java 8 or higher
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getAnnotationsByType
	 */
	public static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement,
			Class<A> annotationType, Class<? extends Annotation> containerAnnotationType) {

		Set<A> annotations = getDeclaredRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType);
		if (!annotations.isEmpty()) {
			return annotations;
		}

		if (annotatedElement instanceof Class) {
			Class<?> superclass = ((Class<?>) annotatedElement).getSuperclass();
			if (superclass != null && Object.class != superclass) {
				return getRepeatableAnnotations(superclass, annotationType, containerAnnotationType);
			}
		}

		return getRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType, false);
	}

	/**
	 * Get the declared <em>repeatable</em> {@linkplain Annotation annotations}
	 * of {@code annotationType} from the supplied {@link AnnotatedElement},
	 * where such annotations are either <em>directly present</em>,
	 * <em>indirectly present</em>, or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType(Class)}
	 * with support for automatic detection of a <em>container annotation</em>
	 * declared via @{@link java.lang.annotation.Repeatable} (when running on
	 * Java 8 or higher) and with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * <p>
	 * 从提供的{@link AnnotatedElement}中获取{@code annotationType}中声明的<em>可重复的</em> {@linkplain注释},其中这些注释直接出现<em> 
	 * <em> <em>间接呈现</em>或<meta> </em>元素</p> </em>此方法模仿Java 8的{@link javalangreflectAnnotatedElement#getDeclaredAnnotationsByType(Class)}
	 * 的功能,支持自动检测<通过@ {@ link javalangannotationRepeatable}(当在Java 8或更高版本上运行时)声明并附加了对元注释的支持<p>容器注释</em> <p>处
	 * 理嵌套在<em>容器注释中的单个注释和注释</EM>如果提供的元素是{@link方法} <p>,则正确地处理由编译器生成的</em>桥接方法</em>。
	 * 如果注释不存在,将搜索元标注。</em >在提供的元素上。
	 * 
	 * 
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class)
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(AnnotatedElement, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType
	 */
	public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement,
			Class<A> annotationType) {

		return getDeclaredRepeatableAnnotations(annotatedElement, annotationType, null);
	}

	/**
	 * Get the declared <em>repeatable</em> {@linkplain Annotation annotations}
	 * of {@code annotationType} from the supplied {@link AnnotatedElement},
	 * where such annotations are either <em>directly present</em>,
	 * <em>indirectly present</em>, or <em>meta-present</em> on the element.
	 * <p>This method mimics the functionality of Java 8's
	 * {@link java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType(Class)}
	 * with additional support for meta-annotations.
	 * <p>Handles both single annotations and annotations nested within a
	 * <em>container annotation</em>.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * <p>
	 * 从提供的{@link AnnotatedElement}中获取{@code annotationType}中声明的<em>可重复的</em> {@linkplain注释},其中这些注释直接出现<em> 
	 * <em> <em>间接呈现</em>或元素</em>元素</p> </em>此方法模仿Java 8的{@link javalangreflectAnnotatedElement#getDeclaredAnnotationsByType(Class)}
	 * 的功能,并附加了对元注释的支持< p>处理嵌套在<em>容器注解中的单个注释和注释</em> <p>正确地处理由编译器生成的</em>桥接方法,如果提供的元素是{@link Method} < p>如果
	 * 提供的元素上的注释不是<em>存在</em>,则会搜索元标注。
	 * 
	 * 
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @param containerAnnotationType the type of the container that holds
	 * the annotations; may be {@code null} if a container is not supported
	 * or if it should be looked up via @{@link java.lang.annotation.Repeatable}
	 * when running on Java 8 or higher
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class)
	 * @see #getRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see #getDeclaredRepeatableAnnotations(AnnotatedElement, Class)
	 * @see AnnotatedElementUtils#getMergedRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotationsByType
	 */
	public static <A extends Annotation> Set<A> getDeclaredRepeatableAnnotations(AnnotatedElement annotatedElement,
			Class<A> annotationType, Class<? extends Annotation> containerAnnotationType) {

		return getRepeatableAnnotations(annotatedElement, annotationType, containerAnnotationType, true);
	}

	/**
	 * Perform the actual work for {@link #getRepeatableAnnotations(AnnotatedElement, Class, Class)}
	 * and {@link #getDeclaredRepeatableAnnotations(AnnotatedElement, Class, Class)}.
	 * <p>Correctly handles <em>bridge methods</em> generated by the
	 * compiler if the supplied element is a {@link Method}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>present</em> on the supplied element.
	 * <p>
	 * 执行{@link #getRepeatableAnnotations(AnnotatedElement,Class,Class)}和{@link #getDeclaredRepeatableAnnations(AnnotatedElement,Class,Class))的实际工作}
	 *  <p>正确处理编译器生成的</em>桥接方法</em>如果提供的元素是{@link方法} <p>如果提供的元素上的注释不是<em>存在</em>,则会搜索元标注。
	 * 
	 * 
	 * @param annotatedElement the element to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @param containerAnnotationType the type of the container that holds
	 * the annotations; may be {@code null} if a container is not supported
	 * or if it should be looked up via @{@link java.lang.annotation.Repeatable}
	 * when running on Java 8 or higher
	 * @param declaredMode {@code true} if only declared annotations (i.e.,
	 * directly or indirectly present) should be considered
	 * @return the annotations found or an empty set (never {@code null})
	 * @since 4.2
	 * @see org.springframework.core.BridgeMethodResolver#findBridgedMethod
	 * @see java.lang.annotation.Repeatable
	 */
	private static <A extends Annotation> Set<A> getRepeatableAnnotations(AnnotatedElement annotatedElement,
			Class<A> annotationType, Class<? extends Annotation> containerAnnotationType, boolean declaredMode) {

		Assert.notNull(annotatedElement, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "Annotation type must not be null");

		try {
			if (annotatedElement instanceof Method) {
				annotatedElement = BridgeMethodResolver.findBridgedMethod((Method) annotatedElement);
			}
			return new AnnotationCollector<A>(annotationType, containerAnnotationType, declaredMode).getResult(annotatedElement);
		}
		catch (Exception ex) {
			handleIntrospectionFailure(annotatedElement, ex);
		}
		return Collections.emptySet();
	}

	/**
	 * Find a single {@link Annotation} of {@code annotationType} on the
	 * supplied {@link AnnotatedElement}.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>directly present</em> on the supplied element.
	 * <p><strong>Warning</strong>: this method operates generically on
	 * annotated elements. In other words, this method does not execute
	 * specialized search algorithms for classes or methods. If you require
	 * the more specific semantics of {@link #findAnnotation(Class, Class)}
	 * or {@link #findAnnotation(Method, Class)}, invoke one of those methods
	 * instead.
	 * <p>
	 * 在提供的{@link AnnotatedElement} <p>上查找{@code注释类型}中的{@link注释},如果注释不直接出现<em>,则将搜索元标注</em> p> <strong>警告</strong>
	 * ：此方法通常用注释元素进行操作换句话说,此方法不会针对类或方法执行专门的搜索算法如果您需要更具体的{@link #findAnnotation(Class, Class)}或{@link #findAnnotation(Method,Class)}
	 * ,调用其中一个方法。
	 * 
	 * 
	 * @param annotatedElement the {@code AnnotatedElement} on which to find the annotation
	 * @param annotationType the annotation type to look for, both locally and as a meta-annotation
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 4.2
	 */
	public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
		Assert.notNull(annotatedElement, "AnnotatedElement must not be null");
		if (annotationType == null) {
			return null;
		}

		// Do NOT store result in the findAnnotationCache since doing so could break
		// findAnnotation(Class, Class) and findAnnotation(Method, Class).
		A ann = findAnnotation(annotatedElement, annotationType, new HashSet<Annotation>());
		return synthesizeAnnotation(ann, annotatedElement);
	}

	/**
	 * Perform the search algorithm for {@link #findAnnotation(AnnotatedElement, Class)}
	 * avoiding endless recursion by tracking which annotations have already
	 * been <em>visited</em>.
	 * <p>
	 *  执行{@link #findAnnotation(AnnotatedElement,Class)}的搜索算法,通过跟踪哪些注释已被访问</em>避免无休止的递归
	 * 
	 * 
	 * @param annotatedElement the {@code AnnotatedElement} on which to find the annotation
	 * @param annotationType the annotation type to look for, both locally and as a meta-annotation
	 * @param visited the set of annotations that have already been visited
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 4.2
	 */
	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType, Set<Annotation> visited) {
		try {
			Annotation[] anns = annotatedElement.getDeclaredAnnotations();
			for (Annotation ann : anns) {
				if (ann.annotationType() == annotationType) {
					return (A) ann;
				}
			}
			for (Annotation ann : anns) {
				if (!isInJavaLangAnnotationPackage(ann) && visited.add(ann)) {
					A annotation = findAnnotation((AnnotatedElement) ann.annotationType(), annotationType, visited);
					if (annotation != null) {
						return annotation;
					}
				}
			}
		}
		catch (Exception ex) {
			handleIntrospectionFailure(annotatedElement, ex);
		}
		return null;
	}

	/**
	 * Find a single {@link Annotation} of {@code annotationType} on the supplied
	 * {@link Method}, traversing its super methods (i.e., from superclasses and
	 * interfaces) if the annotation is not <em>directly present</em> on the given
	 * method itself.
	 * <p>Correctly handles bridge {@link Method Methods} generated by the compiler.
	 * <p>Meta-annotations will be searched if the annotation is not
	 * <em>directly present</em> on the method.
	 * <p>Annotations on methods are not inherited by default, so we need to handle
	 * this explicitly.
	 * <p>
	 * 在提供的{@link方法}中查找{@code注释类型}的{@link注释},如果注释不直接存在</em>,则遍历其超类方法(即从超类和接口)给定的方法本身<p>正确处理由编译器生成的桥{@link方法}
	 *  <p>如果注释不直接存在</em>,则将搜索Meta-annotations注释on方法不是默认继承,所以我们需要明确地处理这个方法。
	 * 
	 * 
	 * @param method the method to look for annotations on
	 * @param annotationType the annotation type to look for
	 * @return the first matching annotation, or {@code null} if not found
	 * @see #getAnnotation(Method, Class)
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
		Assert.notNull(method, "Method must not be null");
		if (annotationType == null) {
			return null;
		}

		AnnotationCacheKey cacheKey = new AnnotationCacheKey(method, annotationType);
		A result = (A) findAnnotationCache.get(cacheKey);

		if (result == null) {
			Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
			result = findAnnotation((AnnotatedElement) resolvedMethod, annotationType);

			if (result == null) {
				result = searchOnInterfaces(method, annotationType, method.getDeclaringClass().getInterfaces());
			}

			Class<?> clazz = method.getDeclaringClass();
			while (result == null) {
				clazz = clazz.getSuperclass();
				if (clazz == null || Object.class == clazz) {
					break;
				}
				try {
					Method equivalentMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
					Method resolvedEquivalentMethod = BridgeMethodResolver.findBridgedMethod(equivalentMethod);
					result = findAnnotation((AnnotatedElement) resolvedEquivalentMethod, annotationType);
				}
				catch (NoSuchMethodException ex) {
					// No equivalent method found
				}
				if (result == null) {
					result = searchOnInterfaces(method, annotationType, clazz.getInterfaces());
				}
			}

			if (result != null) {
				result = synthesizeAnnotation(result, method);
				findAnnotationCache.put(cacheKey, result);
			}
		}

		return result;
	}

	private static <A extends Annotation> A searchOnInterfaces(Method method, Class<A> annotationType, Class<?>... ifcs) {
		A annotation = null;
		for (Class<?> iface : ifcs) {
			if (isInterfaceWithAnnotatedMethods(iface)) {
				try {
					Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
					annotation = getAnnotation(equivalentMethod, annotationType);
				}
				catch (NoSuchMethodException ex) {
					// Skip this interface - it doesn't have the method...
				}
				if (annotation != null) {
					break;
				}
			}
		}
		return annotation;
	}

	static boolean isInterfaceWithAnnotatedMethods(Class<?> iface) {
		Boolean found = annotatedInterfaceCache.get(iface);
		if (found != null) {
			return found;
		}
		found = Boolean.FALSE;
		for (Method ifcMethod : iface.getMethods()) {
			try {
				if (ifcMethod.getAnnotations().length > 0) {
					found = Boolean.TRUE;
					break;
				}
			}
			catch (Exception ex) {
				handleIntrospectionFailure(ifcMethod, ex);
			}
		}
		annotatedInterfaceCache.put(iface, found);
		return found;
	}

	/**
	 * Find a single {@link Annotation} of {@code annotationType} on the
	 * supplied {@link Class}, traversing its interfaces, annotations, and
	 * superclasses if the annotation is not <em>directly present</em> on
	 * the given class itself.
	 * <p>This method explicitly handles class-level annotations which are not
	 * declared as {@link java.lang.annotation.Inherited inherited} <em>as well
	 * as meta-annotations and annotations on interfaces</em>.
	 * <p>The algorithm operates as follows:
	 * <ol>
	 * <li>Search for the annotation on the given class and return it if found.
	 * <li>Recursively search through all annotations that the given class declares.
	 * <li>Recursively search through all interfaces that the given class declares.
	 * <li>Recursively search through the superclass hierarchy of the given class.
	 * </ol>
	 * <p>Note: in this context, the term <em>recursively</em> means that the search
	 * process continues by returning to step #1 with the current interface,
	 * annotation, or superclass as the class to look for annotations on.
	 * <p>
	 * 在提供的{@link Class}上查找{@code注释类型}的{@link注释},如果注释不直接在给定的类本身上呈现</em>,则遍历其接口,注释和超类<p>此方法显式处理未声明为{@link javalangannotationInherited inherited}
	 *  <em>的类级别注释,以及接口上的元注释和注释</em> <p>该算法操作如下：。
	 * <ol>
	 *  <li>在给定类中搜索注释,并返回它(如果发现)<li>递归搜索给定类声明的所有注释<li>递归地搜索给定类声明的所有接口<li>递归搜索超类给定类的层次结构
	 * </ol>
	 * <p>注意：在本文中,术语<em>递归</em>意味着搜索过程继续返回步骤#1,当前界面,注释或超类作为类来查找注释
	 * 
	 * 
	 * @param clazz the class to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @return the first matching annotation, or {@code null} if not found
	 */
	public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
		return findAnnotation(clazz, annotationType, true);
	}

	/**
	 * Perform the actual work for {@link #findAnnotation(AnnotatedElement, Class)},
	 * honoring the {@code synthesize} flag.
	 * <p>
	 *  执行{@link #findAnnotation(AnnotatedElement,Class)}的实际工作,遵守{@code综合}标志
	 * 
	 * 
	 * @param clazz the class to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @param synthesize {@code true} if the result should be
	 * {@linkplain #synthesizeAnnotation(Annotation) synthesized}
	 * @return the first matching annotation, or {@code null} if not found
	 * @since 4.2.1
	 */
	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, boolean synthesize) {
		Assert.notNull(clazz, "Class must not be null");
		if (annotationType == null) {
			return null;
		}

		AnnotationCacheKey cacheKey = new AnnotationCacheKey(clazz, annotationType);
		A result = (A) findAnnotationCache.get(cacheKey);
		if (result == null) {
			result = findAnnotation(clazz, annotationType, new HashSet<Annotation>());
			if (result != null && synthesize) {
				result = synthesizeAnnotation(result, clazz);
				findAnnotationCache.put(cacheKey, result);
			}
		}
		return result;
	}

	/**
	 * Perform the search algorithm for {@link #findAnnotation(Class, Class)},
	 * avoiding endless recursion by tracking which annotations have already
	 * been <em>visited</em>.
	 * <p>
	 *  执行{@link #findAnnotation(Class,Class)}的搜索算法,通过跟踪哪些注释已被<em>访问</em>避免无休止的递归
	 * 
	 * 
	 * @param clazz the class to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @param visited the set of annotations that have already been visited
	 * @return the first matching annotation, or {@code null} if not found
	 */
	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, Set<Annotation> visited) {
		try {
			Annotation[] anns = clazz.getDeclaredAnnotations();
			for (Annotation ann : anns) {
				if (ann.annotationType() == annotationType) {
					return (A) ann;
				}
			}
			for (Annotation ann : anns) {
				if (!isInJavaLangAnnotationPackage(ann) && visited.add(ann)) {
					A annotation = findAnnotation(ann.annotationType(), annotationType, visited);
					if (annotation != null) {
						return annotation;
					}
				}
			}
		}
		catch (Exception ex) {
			handleIntrospectionFailure(clazz, ex);
			return null;
		}

		for (Class<?> ifc : clazz.getInterfaces()) {
			A annotation = findAnnotation(ifc, annotationType, visited);
			if (annotation != null) {
				return annotation;
			}
		}

		Class<?> superclass = clazz.getSuperclass();
		if (superclass == null || Object.class == superclass) {
			return null;
		}
		return findAnnotation(superclass, annotationType, visited);
	}

	/**
	 * Find the first {@link Class} in the inheritance hierarchy of the
	 * specified {@code clazz} (including the specified {@code clazz} itself)
	 * on which an annotation of the specified {@code annotationType} is
	 * <em>directly present</em>.
	 * <p>If the supplied {@code clazz} is an interface, only the interface
	 * itself will be checked; the inheritance hierarchy for interfaces will
	 * not be traversed.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>The standard {@link Class} API does not provide a mechanism for
	 * determining which class in an inheritance hierarchy actually declares
	 * an {@link Annotation}, so we need to handle this explicitly.
	 * <p>
	 * 找到指定的{@code clazz}(包括指定的{@code clazz}本身)的继承层次结构中的第一个{@link Class},其中指定的{@code注释类型}的注释直接出现在< / em> <p>
	 * 如果提供的{@code clazz}是一个接口,则只会检查接口本身;接口的继承层次不会被遍历<p>元标注将<em>不</em>被搜索<p>标准的{@link类} API不提供一种确定继承层次结构中的哪个
	 * 类的机制实际上声明了一个{@link注释},所以我们需要明确地处理这个。
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @param clazz the class to check for the annotation on (may be {@code null})
	 * @return the first {@link Class} in the inheritance hierarchy that
	 * declares an annotation of the specified {@code annotationType}, or
	 * {@code null} if not found
	 * @see Class#isAnnotationPresent(Class)
	 * @see Class#getDeclaredAnnotations()
	 * @see #findAnnotationDeclaringClassForTypes(List, Class)
	 * @see #isAnnotationDeclaredLocally(Class, Class)
	 */
	public static Class<?> findAnnotationDeclaringClass(Class<? extends Annotation> annotationType, Class<?> clazz) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		if (clazz == null || Object.class == clazz) {
			return null;
		}
		if (isAnnotationDeclaredLocally(annotationType, clazz)) {
			return clazz;
		}
		return findAnnotationDeclaringClass(annotationType, clazz.getSuperclass());
	}

	/**
	 * Find the first {@link Class} in the inheritance hierarchy of the
	 * specified {@code clazz} (including the specified {@code clazz} itself)
	 * on which at least one of the specified {@code annotationTypes} is
	 * <em>directly present</em>.
	 * <p>If the supplied {@code clazz} is an interface, only the interface
	 * itself will be checked; the inheritance hierarchy for interfaces will
	 * not be traversed.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>The standard {@link Class} API does not provide a mechanism for
	 * determining which class in an inheritance hierarchy actually declares
	 * one of several candidate {@linkplain Annotation annotations}, so we
	 * need to handle this explicitly.
	 * <p>
	 * 在指定的{@code clazz}(包括指定的{@code clazz}本身)的继承层次结构中找到第一个{@link Class},其中至少有一个指定的{@code注释类型}直接出现在</em> <p>
	 * 如果提供的{@code clazz}是一个接口,则只会检查接口本身;接口的继承层次不会被遍历<p>元标注将<em>不</em>被搜索<p>标准的{@link类} API不提供一种确定继承层次结构中的哪个
	 * 类的机制实际上声明了几个候选人{@linkplain注释注释}之一,因此我们需要明确处理。
	 * 
	 * 
	 * @param annotationTypes the annotation types to look for
	 * @param clazz the class to check for the annotations on, or {@code null}
	 * @return the first {@link Class} in the inheritance hierarchy that
	 * declares an annotation of at least one of the specified
	 * {@code annotationTypes}, or {@code null} if not found
	 * @since 3.2.2
	 * @see Class#isAnnotationPresent(Class)
	 * @see Class#getDeclaredAnnotations()
	 * @see #findAnnotationDeclaringClass(Class, Class)
	 * @see #isAnnotationDeclaredLocally(Class, Class)
	 */
	public static Class<?> findAnnotationDeclaringClassForTypes(List<Class<? extends Annotation>> annotationTypes, Class<?> clazz) {
		Assert.notEmpty(annotationTypes, "List of annotation types must not be empty");
		if (clazz == null || Object.class == clazz) {
			return null;
		}
		for (Class<? extends Annotation> annotationType : annotationTypes) {
			if (isAnnotationDeclaredLocally(annotationType, clazz)) {
				return clazz;
			}
		}
		return findAnnotationDeclaringClassForTypes(annotationTypes, clazz.getSuperclass());
	}

	/**
	 * Determine whether an annotation of the specified {@code annotationType}
	 * is declared locally (i.e., <em>directly present</em>) on the supplied
	 * {@code clazz}.
	 * <p>The supplied {@link Class} may represent any type.
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>Note: This method does <strong>not</strong> determine if the annotation
	 * is {@linkplain java.lang.annotation.Inherited inherited}. For greater
	 * clarity regarding inherited annotations, consider using
	 * {@link #isAnnotationInherited(Class, Class)} instead.
	 * <p>
	 * 确定指定的{@code注释类型}的注释是否在本地提供(即,<em>直接呈现</em>)在提供的{@code clazz} <p>提供的{@link类}可以表示任何类型<p>元搜索将<em>不</em>被
	 * 搜索<p>注意：此方法<strong>不</strong>确定注释是否为{@linkplain javalangannotationInherited inherited}为了更加清晰的继承注释请考虑使
	 * 用{@link #isAnnotationInherited(Class,Class)}。
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @param clazz the class to check for the annotation on
	 * @return {@code true} if an annotation of the specified {@code annotationType}
	 * is <em>directly present</em>
	 * @see java.lang.Class#getDeclaredAnnotations()
	 * @see java.lang.Class#getDeclaredAnnotation(Class)
	 * @see #isAnnotationInherited(Class, Class)
	 */
	public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		Assert.notNull(clazz, "Class must not be null");
		try {
			for (Annotation ann : clazz.getDeclaredAnnotations()) {
				if (ann.annotationType() == annotationType) {
					return true;
				}
			}
		}
		catch (Exception ex) {
			handleIntrospectionFailure(clazz, ex);
		}
		return false;
	}

	/**
	 * Determine whether an annotation of the specified {@code annotationType}
	 * is <em>present</em> on the supplied {@code clazz} and is
	 * {@linkplain java.lang.annotation.Inherited inherited} (i.e., not
	 * <em>directly present</em>).
	 * <p>Meta-annotations will <em>not</em> be searched.
	 * <p>If the supplied {@code clazz} is an interface, only the interface
	 * itself will be checked. In accordance with standard meta-annotation
	 * semantics in Java, the inheritance hierarchy for interfaces will not
	 * be traversed. See the {@linkplain java.lang.annotation.Inherited Javadoc}
	 * for the {@code @Inherited} meta-annotation for further details regarding
	 * annotation inheritance.
	 * <p>
	 * 确定提供的{@code clazz}上是否存在指定的{@code注释类型}的注释<em> </em>,并且是{@linkplain javalangannotationInherited inherited}
	 * (即,不直接显示</em> )<p>元搜索将<em>不</em>被搜索<p>如果提供的{@code clazz}是一个接口,则只会检查接口本身是否符合Java中的标准元注释语义,不会遍历接口的继承层次结
	 * 构有关注释继承的更多详细信息,请参阅{@code @Inherited}元注释的{@linkplain javalangannotationInherited Javadoc}。
	 * 
	 * 
	 * @param annotationType the annotation type to look for
	 * @param clazz the class to check for the annotation on
	 * @return {@code true} if an annotation of the specified {@code annotationType}
	 * is <em>present</em> and <em>inherited</em>
	 * @see Class#isAnnotationPresent(Class)
	 * @see #isAnnotationDeclaredLocally(Class, Class)
	 */
	public static boolean isAnnotationInherited(Class<? extends Annotation> annotationType, Class<?> clazz) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isAnnotationPresent(annotationType) && !isAnnotationDeclaredLocally(annotationType, clazz));
	}

	/**
	 * Determine if an annotation of type {@code metaAnnotationType} is
	 * <em>meta-present</em> on the supplied {@code annotationType}.
	 * <p>
	 *  确定所提供的{@code注释类型}中的{@code metaAnnotationType}类型的注释是否是元数据</em>
	 * 
	 * 
	 * @param annotationType the annotation type to search on
	 * @param metaAnnotationType the type of meta-annotation to search for
	 * @return {@code true} if such an annotation is meta-present
	 * @since 4.2.1
	 */
	public static boolean isAnnotationMetaPresent(Class<? extends Annotation> annotationType,
			Class<? extends Annotation> metaAnnotationType) {

		Assert.notNull(annotationType, "Annotation type must not be null");
		if (metaAnnotationType == null) {
			return false;
		}

		AnnotationCacheKey cacheKey = new AnnotationCacheKey(annotationType, metaAnnotationType);
		Boolean metaPresent = metaPresentCache.get(cacheKey);
		if (metaPresent != null) {
			return metaPresent;
		}
		metaPresent = Boolean.FALSE;
		if (findAnnotation(annotationType, metaAnnotationType, false) != null) {
			metaPresent = Boolean.TRUE;
		}
		metaPresentCache.put(cacheKey, metaPresent);
		return metaPresent;
	}

	/**
	 * Determine if the supplied {@link Annotation} is defined in the core JDK
	 * {@code java.lang.annotation} package.
	 * <p>
	 * 确定提供的{@link注释}是否在核心JDK {@code javalangannotation}包中定义
	 * 
	 * 
	 * @param annotation the annotation to check (never {@code null})
	 * @return {@code true} if the annotation is in the {@code java.lang.annotation} package
	 */
	public static boolean isInJavaLangAnnotationPackage(Annotation annotation) {
		Assert.notNull(annotation, "Annotation must not be null");
		return isInJavaLangAnnotationPackage(annotation.annotationType().getName());
	}

	/**
	 * Determine if the {@link Annotation} with the supplied name is defined
	 * in the core JDK {@code java.lang.annotation} package.
	 * <p>
	 *  确定提供的名称的{@link注释}是否在核心JDK {@code javalangannotation}包中定义
	 * 
	 * 
	 * @param annotationType the name of the annotation type to check (never {@code null} or empty)
	 * @return {@code true} if the annotation is in the {@code java.lang.annotation} package
	 * @since 4.2
	 */
	public static boolean isInJavaLangAnnotationPackage(String annotationType) {
		Assert.hasText(annotationType, "annotationType must not be null or empty");
		return annotationType.startsWith("java.lang.annotation");
	}

	/**
	 * Retrieve the given annotation's attributes as a {@link Map}, preserving all
	 * attribute types.
	 * <p>Equivalent to calling {@link #getAnnotationAttributes(Annotation, boolean, boolean)}
	 * with the {@code classValuesAsString} and {@code nestedAnnotationsAsMap} parameters
	 * set to {@code false}.
	 * <p>Note: This method actually returns an {@link AnnotationAttributes} instance.
	 * However, the {@code Map} signature has been preserved for binary compatibility.
	 * <p>
	 *  将给定注释的属性恢复为{@link Map},保留所有属性类型<p>等效于使用{@code classValuesAsString}和{@code nestedAnnotationsAsMap}参数调
	 * 用{@link #getAnnotationAttributes(Annotation,boolean,boolean)}设置为{@code false} <p>注意：此方法实际上返回一个{@link AnnotationAttributes}
	 * 实例但是,{@code Map}签名已保留为二进制兼容性。
	 * 
	 * 
	 * @param annotation the annotation to retrieve the attributes for
	 * @return the Map of annotation attributes, with attribute names as keys and
	 * corresponding attribute values as values (never {@code null})
	 * @see #getAnnotationAttributes(AnnotatedElement, Annotation)
	 * @see #getAnnotationAttributes(Annotation, boolean, boolean)
	 * @see #getAnnotationAttributes(AnnotatedElement, Annotation, boolean, boolean)
	 */
	public static Map<String, Object> getAnnotationAttributes(Annotation annotation) {
		return getAnnotationAttributes(null, annotation);
	}

	/**
	 * Retrieve the given annotation's attributes as a {@link Map}.
	 * <p>Equivalent to calling {@link #getAnnotationAttributes(Annotation, boolean, boolean)}
	 * with the {@code nestedAnnotationsAsMap} parameter set to {@code false}.
	 * <p>Note: This method actually returns an {@link AnnotationAttributes} instance.
	 * However, the {@code Map} signature has been preserved for binary compatibility.
	 * <p>
	 * 将{@code NestedAnnotationsAsMap}参数设置为{@code false} <p>注释,将引用注释的属性作为{@link Map} <p>获取等价于调用{@link #getAnnotationAttributes(Annotation,boolean,boolean)}
	 * } ：该方法实际上返回一个{@link AnnotationAttributes}实例。
	 * 但是,{@code Map}签名已被保留,用于二进制兼容性。
	 * 
	 * 
	 * @param annotation the annotation to retrieve the attributes for
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @return the Map of annotation attributes, with attribute names as keys and
	 * corresponding attribute values as values (never {@code null})
	 * @see #getAnnotationAttributes(Annotation, boolean, boolean)
	 */
	public static Map<String, Object> getAnnotationAttributes(Annotation annotation, boolean classValuesAsString) {
		return getAnnotationAttributes(annotation, classValuesAsString, false);
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>This method provides fully recursive annotation reading capabilities on par with
	 * the reflection-based {@link org.springframework.core.type.StandardAnnotationMetadata}.
	 * <p>
	 *  将给定注释的属性作为{@link AnnotationAttributes}映射获取<p>此方法提供完全递归的注释读取功能,与基于反射的{@link orgspringframeworkcoretypeStandardAnnotationMetadata}
	 * 。
	 * 
	 * 
	 * @param annotation the annotation to retrieve the attributes for
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 * {@link AnnotationAttributes} maps (for compatibility with
	 * {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 * {@code Annotation} instances
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 3.1.1
	 */
	public static AnnotationAttributes getAnnotationAttributes(Annotation annotation, boolean classValuesAsString,
			boolean nestedAnnotationsAsMap) {

		return getAnnotationAttributes(null, annotation, classValuesAsString, nestedAnnotationsAsMap);
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>Equivalent to calling {@link #getAnnotationAttributes(AnnotatedElement, Annotation, boolean, boolean)}
	 * with the {@code classValuesAsString} and {@code nestedAnnotationsAsMap} parameters
	 * set to {@code false}.
	 * <p>
	 * 将给定注释的属性作为{@link AnnotationAttributes}映射获取相对于{@link #getAnnotationAttributes(AnnotatedElement,Annotation,boolean,boolean)}
	 * ,其中{@code classValuesAsString}和{@code nestedAnnotationsAsMap}参数设置为{@code false}。
	 * 
	 * 
	 * @param annotatedElement the element that is annotated with the supplied annotation;
	 * may be {@code null} if unknown
	 * @param annotation the annotation to retrieve the attributes for
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 4.2
	 * @see #getAnnotationAttributes(AnnotatedElement, Annotation, boolean, boolean)
	 */
	public static AnnotationAttributes getAnnotationAttributes(AnnotatedElement annotatedElement, Annotation annotation) {
		return getAnnotationAttributes(annotatedElement, annotation, false, false);
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>This method provides fully recursive annotation reading capabilities on par with
	 * the reflection-based {@link org.springframework.core.type.StandardAnnotationMetadata}.
	 * <p>
	 *  将给定注释的属性作为{@link AnnotationAttributes}映射获取<p>此方法提供完全递归的注释读取功能,与基于反射的{@link orgspringframeworkcoretypeStandardAnnotationMetadata}
	 * 。
	 * 
	 * 
	 * @param annotatedElement the element that is annotated with the supplied annotation;
	 * may be {@code null} if unknown
	 * @param annotation the annotation to retrieve the attributes for
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 * {@link AnnotationAttributes} maps (for compatibility with
	 * {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 * {@code Annotation} instances
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 4.2
	 */
	public static AnnotationAttributes getAnnotationAttributes(AnnotatedElement annotatedElement,
			Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		return getAnnotationAttributes(
				(Object) annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
	}

	private static AnnotationAttributes getAnnotationAttributes(Object annotatedElement,
			Annotation annotation, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		AnnotationAttributes attributes =
				retrieveAnnotationAttributes(annotatedElement, annotation, classValuesAsString, nestedAnnotationsAsMap);
		postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Retrieve the given annotation's attributes as an {@link AnnotationAttributes} map.
	 * <p>This method provides fully recursive annotation reading capabilities on par with
	 * the reflection-based {@link org.springframework.core.type.StandardAnnotationMetadata}.
	 * <p><strong>NOTE</strong>: This variant of {@code getAnnotationAttributes()} is
	 * only intended for use within the framework. The following special rules apply:
	 * <ol>
	 * <li>Default values will be replaced with default value placeholders.</li>
	 * <li>The resulting, merged annotation attributes should eventually be
	 * {@linkplain #postProcessAnnotationAttributes post-processed} in order to
	 * ensure that placeholders have been replaced by actual default values and
	 * in order to enforce {@code @AliasFor} semantics.</li>
	 * </ol>
	 * <p>
	 * 将给定注释的属性作为{@link AnnotationAttributes}映射获取<p>此方法提供与基于反射的{@link orgspringframeworkcoretypeStandardAnnotationMetadata}
	 * 完全递归的注释读取功能<p> <strong>注意</strong>：此变体的{@code getAnnotationAttributes()}仅适用于框架内使用以下特殊规则：。
	 * <ol>
	 *  <li>默认值将替换为默认值占位符</li> <li>生成的合并注释属性最终应为{@linkplain #postProcessAnnotationAttributes post-processed}
	 * },以确保占位符已被实际的默认值替换并且为了执行{@code @AliasFor}语义</li>。
	 * </ol>
	 * 
	 * @param annotatedElement the element that is annotated with the supplied annotation;
	 * may be {@code null} if unknown
	 * @param annotation the annotation to retrieve the attributes for
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 * {@link AnnotationAttributes} maps (for compatibility with
	 * {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 * {@code Annotation} instances
	 * @return the annotation attributes (a specialized Map) with attribute names as keys
	 * and corresponding attribute values as values (never {@code null})
	 * @since 4.2
	 * @see #postProcessAnnotationAttributes
	 */
	static AnnotationAttributes retrieveAnnotationAttributes(Object annotatedElement, Annotation annotation,
			boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		Class<? extends Annotation> annotationType = annotation.annotationType();
		AnnotationAttributes attributes = new AnnotationAttributes(annotationType);

		for (Method method : getAttributeMethods(annotationType)) {
			try {
				Object attributeValue = method.invoke(annotation);
				Object defaultValue = method.getDefaultValue();
				if (defaultValue != null && ObjectUtils.nullSafeEquals(attributeValue, defaultValue)) {
					attributeValue = new DefaultValueHolder(defaultValue);
				}
				attributes.put(method.getName(),
						adaptValue(annotatedElement, attributeValue, classValuesAsString, nestedAnnotationsAsMap));
			}
			catch (Exception ex) {
				if (ex instanceof InvocationTargetException) {
					Throwable targetException = ((InvocationTargetException) ex).getTargetException();
					rethrowAnnotationConfigurationException(targetException);
				}
				throw new IllegalStateException("Could not obtain annotation attribute value for " + method, ex);
			}
		}

		return attributes;
	}

	/**
	 * Adapt the given value according to the given class and nested annotation settings.
	 * <p>Nested annotations will be
	 * {@linkplain #synthesizeAnnotation(Annotation, AnnotatedElement) synthesized}.
	 * <p>
	 * 根据给定的类和嵌套注释设置适应给定值<p>嵌套注释将{@linkplain #synthesizeAnnotation(Annotation,AnnotatedElement))合成}
	 * 
	 * 
	 * @param annotatedElement the element that is annotated, used for contextual
	 * logging; may be {@code null} if unknown
	 * @param value the annotation attribute value
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 * {@link AnnotationAttributes} maps (for compatibility with
	 * {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 * {@code Annotation} instances
	 * @return the adapted value, or the original value if no adaptation is needed
	 */
	static Object adaptValue(Object annotatedElement, Object value, boolean classValuesAsString,
			boolean nestedAnnotationsAsMap) {

		if (classValuesAsString) {
			if (value instanceof Class<?>) {
				return ((Class<?>) value).getName();
			}
			else if (value instanceof Class<?>[]) {
				Class<?>[] clazzArray = (Class<?>[]) value;
				String[] classNames = new String[clazzArray.length];
				for (int i = 0; i < clazzArray.length; i++) {
					classNames[i] = clazzArray[i].getName();
				}
				return classNames;
			}
		}

		if (value instanceof Annotation) {
			Annotation annotation = (Annotation) value;
			if (nestedAnnotationsAsMap) {
				return getAnnotationAttributes(annotatedElement, annotation, classValuesAsString, true);
			}
			else {
				return synthesizeAnnotation(annotation, annotatedElement);
			}
		}

		if (value instanceof Annotation[]) {
			Annotation[] annotations = (Annotation[]) value;
			if (nestedAnnotationsAsMap) {
				AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[annotations.length];
				for (int i = 0; i < annotations.length; i++) {
					mappedAnnotations[i] =
							getAnnotationAttributes(annotatedElement, annotations[i], classValuesAsString, true);
				}
				return mappedAnnotations;
			}
			else {
				return synthesizeAnnotationArray(annotations, annotatedElement);
			}
		}

		// Fallback
		return value;
	}

	/**
	 * Register the annotation-declared default values for the given attributes,
	 * if available.
	 * <p>
	 *  注册给定属性的注释声明的默认值(如果可用)
	 * 
	 * 
	 * @param attributes the annotation attributes to process
	 * @since 4.3.2
	 */
	public static void registerDefaultValues(AnnotationAttributes attributes) {
		// Only do defaults scanning for public annotations; we'd run into
		// IllegalAccessExceptions otherwise, and we don't want to mess with
		// accessibility in a SecurityManager environment.
		Class<? extends Annotation> annotationType = attributes.annotationType();
		if (annotationType != null && Modifier.isPublic(annotationType.getModifiers())) {
			// Check declared default values of attributes in the annotation type.
			for (Method annotationAttribute : getAttributeMethods(annotationType)) {
				String attributeName = annotationAttribute.getName();
				Object defaultValue = annotationAttribute.getDefaultValue();
				if (defaultValue != null && !attributes.containsKey(attributeName)) {
					if (defaultValue instanceof Annotation) {
						defaultValue = getAnnotationAttributes((Annotation) defaultValue, false, true);
					}
					else if (defaultValue instanceof Annotation[]) {
						Annotation[] realAnnotations = (Annotation[]) defaultValue;
						AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[realAnnotations.length];
						for (int i = 0; i < realAnnotations.length; i++) {
							mappedAnnotations[i] = getAnnotationAttributes(realAnnotations[i], false, true);
						}
						defaultValue = mappedAnnotations;
					}
					attributes.put(attributeName, new DefaultValueHolder(defaultValue));
				}
			}
		}
	}

	/**
	 * Post-process the supplied {@link AnnotationAttributes}, preserving nested
	 * annotations as {@code Annotation} instances.
	 * <p>Specifically, this method enforces <em>attribute alias</em> semantics
	 * for annotation attributes that are annotated with {@link AliasFor @AliasFor}
	 * and replaces default value placeholders with their original default values.
	 * <p>
	 *  对所提供的{@link AnnotationAttributes}进行后处理,将嵌套注释保留为{@code注释}实例<p>具体来说,该方法强制使用{@link AliasFor注释的注释属性的<em>属性别名</em>语义@AliasFor}
	 * ,并用原始的默认值替换默认值占位符。
	 * 
	 * 
	 * @param annotatedElement the element that is annotated with an annotation or
	 * annotation hierarchy from which the supplied attributes were created;
	 * may be {@code null} if unknown
	 * @param attributes the annotation attributes to post-process
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @since 4.3.2
	 * @see #postProcessAnnotationAttributes(Object, AnnotationAttributes, boolean, boolean)
	 * @see #getDefaultValue(Class, String)
	 */
	public static void postProcessAnnotationAttributes(Object annotatedElement,
			AnnotationAttributes attributes, boolean classValuesAsString) {

		postProcessAnnotationAttributes(annotatedElement, attributes, classValuesAsString, false);
	}

	/**
	 * Post-process the supplied {@link AnnotationAttributes}.
	 * <p>Specifically, this method enforces <em>attribute alias</em> semantics
	 * for annotation attributes that are annotated with {@link AliasFor @AliasFor}
	 * and replaces default value placeholders with their original default values.
	 * <p>
	 * 具体来说,此方法强制使用{@link AliasFor @AliasFor}注释的注释属性的<em>属性别名</em>语义,并将其原始代码替换为默认值占位符默认值
	 * 
	 * 
	 * @param annotatedElement the element that is annotated with an annotation or
	 * annotation hierarchy from which the supplied attributes were created;
	 * may be {@code null} if unknown
	 * @param attributes the annotation attributes to post-process
	 * @param classValuesAsString whether to convert Class references into Strings (for
	 * compatibility with {@link org.springframework.core.type.AnnotationMetadata})
	 * or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested annotations into
	 * {@link AnnotationAttributes} maps (for compatibility with
	 * {@link org.springframework.core.type.AnnotationMetadata}) or to preserve them as
	 * {@code Annotation} instances
	 * @since 4.2
	 * @see #retrieveAnnotationAttributes(Object, Annotation, boolean, boolean)
	 * @see #getDefaultValue(Class, String)
	 */
	static void postProcessAnnotationAttributes(Object annotatedElement,
			AnnotationAttributes attributes, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		// Abort?
		if (attributes == null) {
			return;
		}

		Class<? extends Annotation> annotationType = attributes.annotationType();

		// Track which attribute values have already been replaced so that we can short
		// circuit the search algorithms.
		Set<String> valuesAlreadyReplaced = new HashSet<String>();

		if (!attributes.validated) {
			// Validate @AliasFor configuration
			Map<String, List<String>> aliasMap = getAttributeAliasMap(annotationType);
			for (String attributeName : aliasMap.keySet()) {
				if (valuesAlreadyReplaced.contains(attributeName)) {
					continue;
				}
				Object value = attributes.get(attributeName);
				boolean valuePresent = (value != null && !(value instanceof DefaultValueHolder));

				for (String aliasedAttributeName : aliasMap.get(attributeName)) {
					if (valuesAlreadyReplaced.contains(aliasedAttributeName)) {
						continue;
					}

					Object aliasedValue = attributes.get(aliasedAttributeName);
					boolean aliasPresent = (aliasedValue != null && !(aliasedValue instanceof DefaultValueHolder));

					// Something to validate or replace with an alias?
					if (valuePresent || aliasPresent) {
						if (valuePresent && aliasPresent) {
							// Since annotation attributes can be arrays, we must use ObjectUtils.nullSafeEquals().
							if (!ObjectUtils.nullSafeEquals(value, aliasedValue)) {
								String elementAsString =
										(annotatedElement != null ? annotatedElement.toString() : "unknown element");
								throw new AnnotationConfigurationException(String.format(
										"In AnnotationAttributes for annotation [%s] declared on %s, " +
										"attribute '%s' and its alias '%s' are declared with values of [%s] and [%s], " +
										"but only one is permitted.", annotationType.getName(), elementAsString,
										attributeName, aliasedAttributeName, ObjectUtils.nullSafeToString(value),
										ObjectUtils.nullSafeToString(aliasedValue)));
							}
						}
						else if (aliasPresent) {
							// Replace value with aliasedValue
							attributes.put(attributeName,
									adaptValue(annotatedElement, aliasedValue, classValuesAsString, nestedAnnotationsAsMap));
							valuesAlreadyReplaced.add(attributeName);
						}
						else {
							// Replace aliasedValue with value
							attributes.put(aliasedAttributeName,
									adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
							valuesAlreadyReplaced.add(aliasedAttributeName);
						}
					}
				}
			}
			attributes.validated = true;
		}

		// Replace any remaining placeholders with actual default values
		for (String attributeName : attributes.keySet()) {
			if (valuesAlreadyReplaced.contains(attributeName)) {
				continue;
			}
			Object value = attributes.get(attributeName);
			if (value instanceof DefaultValueHolder) {
				value = ((DefaultValueHolder) value).defaultValue;
				attributes.put(attributeName,
						adaptValue(annotatedElement, value, classValuesAsString, nestedAnnotationsAsMap));
			}
		}
	}

	/**
	 * Retrieve the <em>value</em> of the {@code value} attribute of a
	 * single-element Annotation, given an annotation instance.
	 * <p>
	 *  检索单元素注释的{@code值}属性的<em>值</em>,给定注释实例
	 * 
	 * 
	 * @param annotation the annotation instance from which to retrieve the value
	 * @return the attribute value, or {@code null} if not found
	 * @see #getValue(Annotation, String)
	 */
	public static Object getValue(Annotation annotation) {
		return getValue(annotation, VALUE);
	}

	/**
	 * Retrieve the <em>value</em> of a named attribute, given an annotation instance.
	 * <p>
	 *  在给定注释实例的情况下,检索命名属性的<em>值</em>
	 * 
	 * 
	 * @param annotation the annotation instance from which to retrieve the value
	 * @param attributeName the name of the attribute value to retrieve
	 * @return the attribute value, or {@code null} if not found
	 * @see #getValue(Annotation)
	 */
	public static Object getValue(Annotation annotation, String attributeName) {
		if (annotation == null || !StringUtils.hasText(attributeName)) {
			return null;
		}
		try {
			Method method = annotation.annotationType().getDeclaredMethod(attributeName);
			ReflectionUtils.makeAccessible(method);
			return method.invoke(annotation);
		}
		catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Retrieve the <em>default value</em> of the {@code value} attribute
	 * of a single-element Annotation, given an annotation instance.
	 * <p>
	 *  检索单元素注释的{@code value}属性的<em>默认值</em>,给定注释实例
	 * 
	 * 
	 * @param annotation the annotation instance from which to retrieve the default value
	 * @return the default value, or {@code null} if not found
	 * @see #getDefaultValue(Annotation, String)
	 */
	public static Object getDefaultValue(Annotation annotation) {
		return getDefaultValue(annotation, VALUE);
	}

	/**
	 * Retrieve the <em>default value</em> of a named attribute, given an annotation instance.
	 * <p>
	 *  给定注释实例,检索命名属性的<em>默认值</em>
	 * 
	 * 
	 * @param annotation the annotation instance from which to retrieve the default value
	 * @param attributeName the name of the attribute value to retrieve
	 * @return the default value of the named attribute, or {@code null} if not found
	 * @see #getDefaultValue(Class, String)
	 */
	public static Object getDefaultValue(Annotation annotation, String attributeName) {
		if (annotation == null) {
			return null;
		}
		return getDefaultValue(annotation.annotationType(), attributeName);
	}

	/**
	 * Retrieve the <em>default value</em> of the {@code value} attribute
	 * of a single-element Annotation, given the {@link Class annotation type}.
	 * <p>
	 * 鉴于{@link类注解类型},检索单元素注释的{@code值}属性的<em>默认值</em>
	 * 
	 * 
	 * @param annotationType the <em>annotation type</em> for which the default value should be retrieved
	 * @return the default value, or {@code null} if not found
	 * @see #getDefaultValue(Class, String)
	 */
	public static Object getDefaultValue(Class<? extends Annotation> annotationType) {
		return getDefaultValue(annotationType, VALUE);
	}

	/**
	 * Retrieve the <em>default value</em> of a named attribute, given the
	 * {@link Class annotation type}.
	 * <p>
	 *  获取{@link类注释类型}的命名属性的<em>默认值</em>
	 * 
	 * 
	 * @param annotationType the <em>annotation type</em> for which the default value should be retrieved
	 * @param attributeName the name of the attribute value to retrieve.
	 * @return the default value of the named attribute, or {@code null} if not found
	 * @see #getDefaultValue(Annotation, String)
	 */
	public static Object getDefaultValue(Class<? extends Annotation> annotationType, String attributeName) {
		if (annotationType == null || !StringUtils.hasText(attributeName)) {
			return null;
		}
		try {
			return annotationType.getDeclaredMethod(attributeName).getDefaultValue();
		}
		catch (Exception ex) {
			return null;
		}
	}

	/**
	 * <em>Synthesize</em> an annotation from the supplied {@code annotation}
	 * by wrapping it in a dynamic proxy that transparently enforces
	 * <em>attribute alias</em> semantics for annotation attributes that are
	 * annotated with {@link AliasFor @AliasFor}.
	 * <p>
	 *  通过将动态代理程序包含在一个动态代理中来合并</em>,该注释透明地强制使用{@link AliasFor @注释的注释属性的<em>属性别名</em>语义AliasFor}
	 * 
	 * 
	 * @param annotation the annotation to synthesize
	 * @return the synthesized annotation, if the supplied annotation is
	 * <em>synthesizable</em>; {@code null} if the supplied annotation is
	 * {@code null}; otherwise, the supplied annotation unmodified
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(Annotation, AnnotatedElement)
	 */
	static <A extends Annotation> A synthesizeAnnotation(A annotation) {
		return synthesizeAnnotation(annotation, null);
	}

	/**
	 * <em>Synthesize</em> an annotation from the supplied {@code annotation}
	 * by wrapping it in a dynamic proxy that transparently enforces
	 * <em>attribute alias</em> semantics for annotation attributes that are
	 * annotated with {@link AliasFor @AliasFor}.
	 * <p>
	 *  通过将动态代理程序包含在一个动态代理中来合并</em>,该注释透明地强制使用{@link AliasFor @注释的注释属性的<em>属性别名</em>语义AliasFor}
	 * 
	 * 
	 * @param annotation the annotation to synthesize
	 * @param annotatedElement the element that is annotated with the supplied
	 * annotation; may be {@code null} if unknown
	 * @return the synthesized annotation if the supplied annotation is
	 * <em>synthesizable</em>; {@code null} if the supplied annotation is
	 * {@code null}; otherwise the supplied annotation unmodified
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @see #synthesizeAnnotation(Class)
	 */
	public static <A extends Annotation> A synthesizeAnnotation(A annotation, AnnotatedElement annotatedElement) {
		return synthesizeAnnotation(annotation, (Object) annotatedElement);
	}

	@SuppressWarnings("unchecked")
	static <A extends Annotation> A synthesizeAnnotation(A annotation, Object annotatedElement) {
		if (annotation == null) {
			return null;
		}
		if (annotation instanceof SynthesizedAnnotation) {
			return annotation;
		}

		Class<? extends Annotation> annotationType = annotation.annotationType();
		if (!isSynthesizable(annotationType)) {
			return annotation;
		}

		DefaultAnnotationAttributeExtractor attributeExtractor =
				new DefaultAnnotationAttributeExtractor(annotation, annotatedElement);
		InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);

		// Can always expose Spring's SynthesizedAnnotation marker since we explicitly check for a
		// synthesizable annotation before (which needs to declare @AliasFor from the same package)
		Class<?>[] exposedInterfaces = new Class<?>[] {annotationType, SynthesizedAnnotation.class};
		return (A) Proxy.newProxyInstance(annotation.getClass().getClassLoader(), exposedInterfaces, handler);
	}

	/**
	 * <em>Synthesize</em> an annotation from the supplied map of annotation
	 * attributes by wrapping the map in a dynamic proxy that implements an
	 * annotation of the specified {@code annotationType} and transparently
	 * enforces <em>attribute alias</em> semantics for annotation attributes
	 * that are annotated with {@link AliasFor @AliasFor}.
	 * <p>The supplied map must contain a key-value pair for every attribute
	 * defined in the supplied {@code annotationType} that is not aliased or
	 * does not have a default value. Nested maps and nested arrays of maps
	 * will be recursively synthesized into nested annotations or nested
	 * arrays of annotations, respectively.
	 * <p>Note that {@link AnnotationAttributes} is a specialized type of
	 * {@link Map} that is an ideal candidate for this method's
	 * {@code attributes} argument.
	 * <p>
	 * 通过将地图包含在实现指定的{@code注释类型}的注释的动态代理中,并透明地强制执行<em>属性别名</em>语义,从注释属性的提供地图合成</em>注释对于使用{@link AliasFor @AliasFor}
	 * 注释的注释属性<p>提供的映射必须包含未提供的{@code注释类型}中定义的每个属性的键值对,该别名不是别名或不具有默认值嵌套地图和嵌套的地图数组将被递归地合成为嵌套注释或嵌套的注释数组<p>请注意,{@link AnnotationAttributes}
	 * 是一种专门的{@link Map}类型,是此方法的{ @code属性}参数。
	 * 
	 * 
	 * @param attributes the map of annotation attributes to synthesize
	 * @param annotationType the type of annotation to synthesize
	 * @param annotatedElement the element that is annotated with the annotation
	 * corresponding to the supplied attributes; may be {@code null} if unknown
	 * @return the synthesized annotation, or {@code null} if the supplied attributes
	 * map is {@code null}
	 * @throws IllegalArgumentException if a required attribute is missing or if an
	 * attribute is not of the correct type
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(Annotation, AnnotatedElement)
	 * @see #synthesizeAnnotation(Class)
	 * @see #getAnnotationAttributes(AnnotatedElement, Annotation)
	 * @see #getAnnotationAttributes(AnnotatedElement, Annotation, boolean, boolean)
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A synthesizeAnnotation(Map<String, Object> attributes,
			Class<A> annotationType, AnnotatedElement annotatedElement) {

		Assert.notNull(annotationType, "annotationType must not be null");
		if (attributes == null) {
			return null;
		}

		MapAnnotationAttributeExtractor attributeExtractor =
				new MapAnnotationAttributeExtractor(attributes, annotationType, annotatedElement);
		InvocationHandler handler = new SynthesizedAnnotationInvocationHandler(attributeExtractor);
		Class<?>[] exposedInterfaces = (canExposeSynthesizedMarker(annotationType) ?
				new Class<?>[] {annotationType, SynthesizedAnnotation.class} : new Class<?>[] {annotationType});
		return (A) Proxy.newProxyInstance(annotationType.getClassLoader(), exposedInterfaces, handler);
	}

	/**
	 * <em>Synthesize</em> an annotation from its default attributes values.
	 * <p>This method simply delegates to
	 * {@link #synthesizeAnnotation(Map, Class, AnnotatedElement)},
	 * supplying an empty map for the source attribute values and {@code null}
	 * for the {@link AnnotatedElement}.
	 * <p>
	 * 从其默认属性值合并</em>注释<p>此方法简单地委托给{@link #synthesizeAnnotation(Map,Class,AnnotatedElement)},为源属性值提供一个空地图,{@code null}
	 * 为{@link注释元素}。
	 * 
	 * 
	 * @param annotationType the type of annotation to synthesize
	 * @return the synthesized annotation
	 * @throws IllegalArgumentException if a required attribute is missing
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @see #synthesizeAnnotation(Annotation, AnnotatedElement)
	 */
	public static <A extends Annotation> A synthesizeAnnotation(Class<A> annotationType) {
		return synthesizeAnnotation(Collections.<String, Object> emptyMap(), annotationType, null);
	}

	/**
	 * <em>Synthesize</em> an array of annotations from the supplied array
	 * of {@code annotations} by creating a new array of the same size and
	 * type and populating it with {@linkplain #synthesizeAnnotation(Annotation)
	 * synthesized} versions of the annotations from the input array.
	 * <p>
	 *  通过创建相同大小和类型的新数组,并使用{@linkplain #synthesizeAnnotation(Annotation)synthesis}版本来填充{@code annotations}数组
	 * 中的注释数组</em>来自输入数组的注释。
	 * 
	 * 
	 * @param annotations the array of annotations to synthesize
	 * @param annotatedElement the element that is annotated with the supplied
	 * array of annotations; may be {@code null} if unknown
	 * @return a new array of synthesized annotations, or {@code null} if
	 * the supplied array is {@code null}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #synthesizeAnnotation(Annotation, AnnotatedElement)
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 */
	static Annotation[] synthesizeAnnotationArray(Annotation[] annotations, Object annotatedElement) {
		if (annotations == null) {
			return null;
		}

		Annotation[] synthesized = (Annotation[]) Array.newInstance(
				annotations.getClass().getComponentType(), annotations.length);
		for (int i = 0; i < annotations.length; i++) {
			synthesized[i] = synthesizeAnnotation(annotations[i], annotatedElement);
		}
		return synthesized;
	}

	/**
	 * <em>Synthesize</em> an array of annotations from the supplied array
	 * of {@code maps} of annotation attributes by creating a new array of
	 * {@code annotationType} with the same size and populating it with
	 * {@linkplain #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * synthesized} versions of the maps from the input array.
	 * <p>
	 * 通过创建一个具有相同大小的{@code注释类型}的新数组,并通过{@linkplain #synthesizeAnnotation()方法填充它),从提供的{@code maps}数组中注释</em>注
	 * 释数组</code> Map,Class,AnnotatedElement)从输入数组中合成}映射的版本。
	 * 
	 * 
	 * @param maps the array of maps of annotation attributes to synthesize
	 * @param annotationType the type of annotations to synthesize; never
	 * {@code null}
	 * @return a new array of synthesized annotations, or {@code null} if
	 * the supplied array is {@code null}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2.1
	 * @see #synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @see #synthesizeAnnotationArray(Annotation[], Object)
	 */
	@SuppressWarnings("unchecked")
	static <A extends Annotation> A[] synthesizeAnnotationArray(Map<String, Object>[] maps, Class<A> annotationType) {
		Assert.notNull(annotationType, "annotationType must not be null");
		if (maps == null) {
			return null;
		}

		A[] synthesized = (A[]) Array.newInstance(annotationType, maps.length);
		for (int i = 0; i < maps.length; i++) {
			synthesized[i] = synthesizeAnnotation(maps[i], annotationType, null);
		}
		return synthesized;
	}

	/**
	 * Get a map of all attribute aliases declared via {@code @AliasFor}
	 * in the supplied annotation type.
	 * <p>The map is keyed by attribute name with each value representing
	 * a list of names of aliased attributes.
	 * <p>For <em>explicit</em> alias pairs such as x and y (i.e., where x
	 * is an {@code @AliasFor("y")} and y is an {@code @AliasFor("x")}, there
	 * will be two entries in the map: {@code x -> (y)} and {@code y -> (x)}.
	 * <p>For <em>implicit</em> aliases (i.e., attributes that are declared
	 * as attribute overrides for the same attribute in the same meta-annotation),
	 * there will be n entries in the map. For example, if x, y, and z are
	 * implicit aliases, the map will contain the following entries:
	 * {@code x -> (y, z)}, {@code y -> (x, z)}, {@code z -> (x, y)}.
	 * <p>An empty return value implies that the annotation does not declare
	 * any attribute aliases.
	 * <p>
	 * 通过提供的注释类型获取通过{@code @AliasFor}声明的所有属性别名的映射<p>地图由属性名称键入,每个值表示别名属性名称列表<p>对于<em>显式< / em>诸如x和y之间的别名对(即,其
	 * 中x是一个{@code @AliasFor("y")},y是一个{@code @AliasFor("x")},将有两个条目映射：{@code x  - >(y)}和{@code y  - >(x)} <p>
	 * 对于<em>隐式</em>别名(即,声明为相同属性的属性属性在同一个元注释中),映射中将有n个条目例如,如果x,y和z是隐式别名,则映射将包含以下条目：{@code x  - >(y,z) },{@code y  - >(x,z)}
	 * ,{@code z  - >(x,y)}<p>空的返回值意味着注释不声明任何属性别名。
	 * 
	 * 
	 * @param annotationType the annotation type to find attribute aliases in
	 * @return a map containing attribute aliases (never {@code null})
	 * @since 4.2
	 */
	static Map<String, List<String>> getAttributeAliasMap(Class<? extends Annotation> annotationType) {
		if (annotationType == null) {
			return Collections.emptyMap();
		}

		Map<String, List<String>> map = attributeAliasesCache.get(annotationType);
		if (map != null) {
			return map;
		}

		map = new LinkedHashMap<String, List<String>>();
		for (Method attribute : getAttributeMethods(annotationType)) {
			List<String> aliasNames = getAttributeAliasNames(attribute);
			if (!aliasNames.isEmpty()) {
				map.put(attribute.getName(), aliasNames);
			}
		}

		attributeAliasesCache.put(annotationType, map);
		return map;
	}

	/**
	 * Check whether we can expose our {@link SynthesizedAnnotation} marker for the given annotation type.
	 * <p>
	 * 检查我们是否可以为给定的注释类型公开我们的{@link SynthesizedAnnotation}标记
	 * 
	 * 
	 * @param annotationType the annotation type that we are about to create a synthesized proxy for
	 */
	private static boolean canExposeSynthesizedMarker(Class<? extends Annotation> annotationType) {
		try {
			return (Class.forName(SynthesizedAnnotation.class.getName(), false, annotationType.getClassLoader()) ==
					SynthesizedAnnotation.class);
		}
		catch (ClassNotFoundException ex) {
			return false;
		}
	}

	/**
	 * Determine if annotations of the supplied {@code annotationType} are
	 * <em>synthesizable</em> (i.e., in need of being wrapped in a dynamic
	 * proxy that provides functionality above that of a standard JDK
	 * annotation).
	 * <p>Specifically, an annotation is <em>synthesizable</em> if it declares
	 * any attributes that are configured as <em>aliased pairs</em> via
	 * {@link AliasFor @AliasFor} or if any nested annotations used by the
	 * annotation declare such <em>aliased pairs</em>.
	 * <p>
	 *  确定提供的{@code注释类型}的注释是否可合成</em>(即,需要包含在提供高于标准JDK注释的功能的动态代理中)<p>具体来说,注释如果它通过{@link AliasFor @AliasFor}声
	 * 明配置为</em>别名的任何属性,或者如果注释使用的任何嵌套注释声明了这样的<em>别名对</EM>。
	 * 
	 * 
	 * @since 4.2
	 * @see SynthesizedAnnotation
	 * @see SynthesizedAnnotationInvocationHandler
	 */
	@SuppressWarnings("unchecked")
	private static boolean isSynthesizable(Class<? extends Annotation> annotationType) {
		Boolean synthesizable = synthesizableCache.get(annotationType);
		if (synthesizable != null) {
			return synthesizable;
		}

		synthesizable = Boolean.FALSE;
		for (Method attribute : getAttributeMethods(annotationType)) {
			if (!getAttributeAliasNames(attribute).isEmpty()) {
				synthesizable = Boolean.TRUE;
				break;
			}
			Class<?> returnType = attribute.getReturnType();
			if (Annotation[].class.isAssignableFrom(returnType)) {
				Class<? extends Annotation> nestedAnnotationType =
						(Class<? extends Annotation>) returnType.getComponentType();
				if (isSynthesizable(nestedAnnotationType)) {
					synthesizable = Boolean.TRUE;
					break;
				}
			}
			else if (Annotation.class.isAssignableFrom(returnType)) {
				Class<? extends Annotation> nestedAnnotationType = (Class<? extends Annotation>) returnType;
				if (isSynthesizable(nestedAnnotationType)) {
					synthesizable = Boolean.TRUE;
					break;
				}
			}
		}

		synthesizableCache.put(annotationType, synthesizable);
		return synthesizable;
	}

	/**
	 * Get the names of the aliased attributes configured via
	 * {@link AliasFor @AliasFor} for the supplied annotation {@code attribute}.
	 * <p>
	 *  获取通过{@link AliasFor @AliasFor}为附加注释{@code属性}配置的别名属性的名称
	 * 
	 * 
	 * @param attribute the attribute to find aliases for
	 * @return the names of the aliased attributes (never {@code null}, though
	 * potentially <em>empty</em>)
	 * @throws IllegalArgumentException if the supplied attribute method is
	 * {@code null} or not from an annotation
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 * @see #getAttributeOverrideName(Method, Class)
	 */
	static List<String> getAttributeAliasNames(Method attribute) {
		Assert.notNull(attribute, "attribute must not be null");
		AliasDescriptor descriptor = AliasDescriptor.from(attribute);
		return (descriptor != null ? descriptor.getAttributeAliasNames() : Collections.<String> emptyList());
	}

	/**
	 * Get the name of the overridden attribute configured via
	 * {@link AliasFor @AliasFor} for the supplied annotation {@code attribute}.
	 * <p>
	 * 获取通过{@link AliasFor @AliasFor}为附加注释{@code属性}配置的重写属性的名称
	 * 
	 * 
	 * @param attribute the attribute from which to retrieve the override;
	 * never {@code null}
	 * @param metaAnnotationType the type of meta-annotation in which the
	 * overridden attribute is allowed to be declared
	 * @return the name of the overridden attribute, or {@code null} if not
	 * found or not applicable for the specified meta-annotation type
	 * @throws IllegalArgumentException if the supplied attribute method is
	 * {@code null} or not from an annotation, or if the supplied meta-annotation
	 * type is {@code null} or {@link Annotation}
	 * @throws AnnotationConfigurationException if invalid configuration of
	 * {@code @AliasFor} is detected
	 * @since 4.2
	 */
	static String getAttributeOverrideName(Method attribute, Class<? extends Annotation> metaAnnotationType) {
		Assert.notNull(attribute, "attribute must not be null");
		Assert.notNull(metaAnnotationType, "metaAnnotationType must not be null");
		Assert.isTrue(Annotation.class != metaAnnotationType,
				"metaAnnotationType must not be [java.lang.annotation.Annotation]");

		AliasDescriptor descriptor = AliasDescriptor.from(attribute);
		return (descriptor != null ? descriptor.getAttributeOverrideName(metaAnnotationType) : null);
	}

	/**
	 * Get all methods declared in the supplied {@code annotationType} that
	 * match Java's requirements for annotation <em>attributes</em>.
	 * <p>All methods in the returned list will be
	 * {@linkplain ReflectionUtils#makeAccessible(Method) made accessible}.
	 * <p>
	 *  获取在提供的{@code注释类型}中声明的所有方法,符合Java对注释属性的要求</em> <p>返回列表中的所有方法将为{@linkplain ReflectionUtils#makeAccessible(Method)。
	 * 
	 * 
	 * @param annotationType the type in which to search for attribute methods;
	 * never {@code null}
	 * @return all annotation attribute methods in the specified annotation
	 * type (never {@code null}, though potentially <em>empty</em>)
	 * @since 4.2
	 */
	static List<Method> getAttributeMethods(Class<? extends Annotation> annotationType) {
		List<Method> methods = attributeMethodsCache.get(annotationType);
		if (methods != null) {
			return methods;
		}

		methods = new ArrayList<Method>();
		for (Method method : annotationType.getDeclaredMethods()) {
			if (isAttributeMethod(method)) {
				ReflectionUtils.makeAccessible(method);
				methods.add(method);
			}
		}

		attributeMethodsCache.put(annotationType, methods);
		return methods;
	}

	/**
	 * Get the annotation with the supplied {@code annotationName} on the
	 * supplied {@code element}.
	 * <p>
	 *  在提供的{@code元素}上使用提供的{@code annotationName}获取注释
	 * 
	 * 
	 * @param element the element to search on
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find
	 * @return the annotation if found; {@code null} otherwise
	 * @since 4.2
	 */
	static Annotation getAnnotation(AnnotatedElement element, String annotationName) {
		for (Annotation annotation : element.getAnnotations()) {
			if (annotation.annotationType().getName().equals(annotationName)) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * Determine if the supplied {@code method} is an annotation attribute method.
	 * <p>
	 *  确定提供的{@code方法}是否是注释属性方法
	 * 
	 * 
	 * @param method the method to check
	 * @return {@code true} if the method is an attribute method
	 * @since 4.2
	 */
	static boolean isAttributeMethod(Method method) {
		return (method != null && method.getParameterTypes().length == 0 && method.getReturnType() != void.class);
	}

	/**
	 * Determine if the supplied method is an "annotationType" method.
	 * <p>
	 *  确定提供的方法是否为"注释类型"方法
	 * 
	 * 
	 * @return {@code true} if the method is an "annotationType" method
	 * @see Annotation#annotationType()
	 * @since 4.2
	 */
	static boolean isAnnotationTypeMethod(Method method) {
		return (method != null && method.getName().equals("annotationType") && method.getParameterTypes().length == 0);
	}

	/**
	 * Resolve the container type for the supplied repeatable {@code annotationType}.
	 * <p>Automatically detects a <em>container annotation</em> declared via
	 * {@link java.lang.annotation.Repeatable}. If the supplied annotation type
	 * is not annotated with {@code @Repeatable}, this method simply returns
	 * {@code null}.
	 * <p>
	 * 解析提供的可重复的{@code注释类型}的容器类型<p>通过{@link javalangannotationRepeatable}自动检测<em>容器注释</em>如果提供的注释类型未使用{@code @Repeatable}
	 * 注释, ,这个方法只需返回{@code null}。
	 * 
	 * 
	 * @since 4.2
	 */
	@SuppressWarnings("unchecked")
	static Class<? extends Annotation> resolveContainerAnnotationType(Class<? extends Annotation> annotationType) {
		try {
			Annotation repeatable = getAnnotation(annotationType, REPEATABLE_CLASS_NAME);
			if (repeatable != null) {
				Object value = getValue(repeatable);
				return (Class<? extends Annotation>) value;
			}
		}
		catch (Exception ex) {
			handleIntrospectionFailure(annotationType, ex);
		}
		return null;
	}

	/**
	 * If the supplied throwable is an {@link AnnotationConfigurationException},
	 * it will be cast to an {@code AnnotationConfigurationException} and thrown,
	 * allowing it to propagate to the caller.
	 * <p>Otherwise, this method does nothing.
	 * <p>
	 *  如果提供的throwable是{@link AnnotationConfigurationException},它将被转换为{@code AnnotationConfigurationException}
	 * 并抛出,允许它传播到调用者<p>否则,此方法不执行任何操作。
	 * 
	 * 
	 * @param ex the throwable to inspect
	 * @since 4.2
	 */
	static void rethrowAnnotationConfigurationException(Throwable ex) {
		if (ex instanceof AnnotationConfigurationException) {
			throw (AnnotationConfigurationException) ex;
		}
	}

	/**
	 * Handle the supplied annotation introspection exception.
	 * <p>If the supplied exception is an {@link AnnotationConfigurationException},
	 * it will simply be thrown, allowing it to propagate to the caller, and
	 * nothing will be logged.
	 * <p>Otherwise, this method logs an introspection failure (in particular
	 * {@code TypeNotPresentExceptions}) before moving on, assuming nested
	 * Class values were not resolvable within annotation attributes and
	 * thereby effectively pretending there were no annotations on the specified
	 * element.
	 * <p>
	 * 处理提供的注释内省异常<p>如果提供的异常是{@link AnnotationConfigurationException},它将被简单地抛出,允许它传播到调用者,并且不会记录任何内容<p>否则,此方法
	 * 记录一个内省在进行移动之前,假定嵌套的Class值不能在注释属性中解析,从而有效假装在指定的元素上没有注释,则失败(特别是{@code TypeNotPresentExceptions}))。
	 * 
	 * 
	 * @param element the element that we tried to introspect annotations on
	 * @param ex the exception that we encountered
	 * @see #rethrowAnnotationConfigurationException
	 */
	static void handleIntrospectionFailure(AnnotatedElement element, Exception ex) {
		rethrowAnnotationConfigurationException(ex);

		Log loggerToUse = logger;
		if (loggerToUse == null) {
			loggerToUse = LogFactory.getLog(AnnotationUtils.class);
			logger = loggerToUse;
		}
		if (element instanceof Class && Annotation.class.isAssignableFrom((Class<?>) element)) {
			// Meta-annotation lookup on an annotation type
			if (loggerToUse.isDebugEnabled()) {
				loggerToUse.debug("Failed to introspect meta-annotations on [" + element + "]: " + ex);
			}
		}
		else {
			// Direct annotation lookup on regular Class, Method, Field
			if (loggerToUse.isInfoEnabled()) {
				loggerToUse.info("Failed to introspect annotations on [" + element + "]: " + ex);
			}
		}
	}


	/**
	 * Cache key for the AnnotatedElement cache.
	 * <p>
	 *  AnnotatedElement缓存的缓存键
	 * 
	 */
	private static final class AnnotationCacheKey implements Comparable<AnnotationCacheKey> {

		private final AnnotatedElement element;

		private final Class<? extends Annotation> annotationType;

		public AnnotationCacheKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {
			this.element = element;
			this.annotationType = annotationType;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof AnnotationCacheKey)) {
				return false;
			}
			AnnotationCacheKey otherKey = (AnnotationCacheKey) other;
			return (this.element.equals(otherKey.element) && this.annotationType.equals(otherKey.annotationType));
		}

		@Override
		public int hashCode() {
			return (this.element.hashCode() * 29 + this.annotationType.hashCode());
		}

		@Override
		public String toString() {
			return "@" + this.annotationType + " on " + this.element;
		}

		@Override
		public int compareTo(AnnotationCacheKey other) {
			int result = this.element.toString().compareTo(other.element.toString());
			if (result == 0) {
				result = this.annotationType.getName().compareTo(other.annotationType.getName());
			}
			return result;
		}
	}


	private static class AnnotationCollector<A extends Annotation> {

		private final Class<A> annotationType;

		private final Class<? extends Annotation> containerAnnotationType;

		private final boolean declaredMode;

		private final Set<AnnotatedElement> visited = new HashSet<AnnotatedElement>();

		private final Set<A> result = new LinkedHashSet<A>();

		AnnotationCollector(Class<A> annotationType, Class<? extends Annotation> containerAnnotationType, boolean declaredMode) {
			this.annotationType = annotationType;
			this.containerAnnotationType = (containerAnnotationType != null ? containerAnnotationType :
					resolveContainerAnnotationType(annotationType));
			this.declaredMode = declaredMode;
		}

		Set<A> getResult(AnnotatedElement element) {
			process(element);
			return Collections.unmodifiableSet(this.result);
		}

		@SuppressWarnings("unchecked")
		private void process(AnnotatedElement element) {
			if (this.visited.add(element)) {
				try {
					Annotation[] annotations = (this.declaredMode ? element.getDeclaredAnnotations() : element.getAnnotations());
					for (Annotation ann : annotations) {
						Class<? extends Annotation> currentAnnotationType = ann.annotationType();
						if (ObjectUtils.nullSafeEquals(this.annotationType, currentAnnotationType)) {
							this.result.add(synthesizeAnnotation((A) ann, element));
						}
						else if (ObjectUtils.nullSafeEquals(this.containerAnnotationType, currentAnnotationType)) {
							this.result.addAll(getValue(element, ann));
						}
						else if (!isInJavaLangAnnotationPackage(ann)) {
							process(currentAnnotationType);
						}
					}
				}
				catch (Exception ex) {
					handleIntrospectionFailure(element, ex);
				}
			}
		}

		@SuppressWarnings("unchecked")
		private List<A> getValue(AnnotatedElement element, Annotation annotation) {
			try {
				List<A> synthesizedAnnotations = new ArrayList<A>();
				for (A anno : (A[]) AnnotationUtils.getValue(annotation)) {
					synthesizedAnnotations.add(synthesizeAnnotation(anno, element));
				}
				return synthesizedAnnotations;
			}
			catch (Exception ex) {
				handleIntrospectionFailure(element, ex);
			}
			// Unable to read value from repeating annotation container -> ignore it.
			return Collections.emptyList();
		}
	}


	/**
	 * {@code AliasDescriptor} encapsulates the declaration of {@code @AliasFor}
	 * on a given annotation attribute and includes support for validating
	 * the configuration of aliases (both explicit and implicit).
	 * <p>
	 *  {@code AliasDescriptor}在给定的注释属性上封装了{@code @AliasFor}的声明,并包括验证别名的配置(显式和隐式)的支持,
	 * 
	 * 
	 * @since 4.2.1
	 * @see #from
	 * @see #getAttributeAliasNames
	 * @see #getAttributeOverrideName
	 */
	private static class AliasDescriptor {

		private final Method sourceAttribute;

		private final Class<? extends Annotation> sourceAnnotationType;

		private final String sourceAttributeName;

		private final Method aliasedAttribute;

		private final Class<? extends Annotation> aliasedAnnotationType;

		private final String aliasedAttributeName;

		private final boolean isAliasPair;

		/**
		 * Create an {@code AliasDescriptor} <em>from</em> the declaration
		 * of {@code @AliasFor} on the supplied annotation attribute and
		 * validate the configuration of {@code @AliasFor}.
		 * <p>
		 * 在提供的注释属性上从</em> {@code @AliasFor}的声明中创建一个{@code AliasDescriptor} <em>,并验证{@code @AliasFor}的配置
		 * 
		 * 
		 * @param attribute the annotation attribute that is annotated with
		 * {@code @AliasFor}
		 * @return an alias descriptor, or {@code null} if the attribute
		 * is not annotated with {@code @AliasFor}
		 * @see #validateAgainst
		 */
		public static AliasDescriptor from(Method attribute) {
			AliasDescriptor descriptor = aliasDescriptorCache.get(attribute);
			if (descriptor != null) {
				return descriptor;
			}

			AliasFor aliasFor = attribute.getAnnotation(AliasFor.class);
			if (aliasFor == null) {
				return null;
			}

			descriptor = new AliasDescriptor(attribute, aliasFor);
			descriptor.validate();
			aliasDescriptorCache.put(attribute, descriptor);
			return descriptor;
		}

		@SuppressWarnings("unchecked")
		private AliasDescriptor(Method sourceAttribute, AliasFor aliasFor) {
			Class<?> declaringClass = sourceAttribute.getDeclaringClass();
			Assert.isTrue(declaringClass.isAnnotation(), "sourceAttribute must be from an annotation");

			this.sourceAttribute = sourceAttribute;
			this.sourceAnnotationType = (Class<? extends Annotation>) declaringClass;
			this.sourceAttributeName = sourceAttribute.getName();

			this.aliasedAnnotationType = (Annotation.class == aliasFor.annotation() ?
					this.sourceAnnotationType : aliasFor.annotation());
			this.aliasedAttributeName = getAliasedAttributeName(aliasFor, sourceAttribute);
			if (this.aliasedAnnotationType == this.sourceAnnotationType &&
					this.aliasedAttributeName.equals(this.sourceAttributeName)) {
				String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] points to " +
						"itself. Specify 'annotation' to point to a same-named attribute on a meta-annotation.",
						sourceAttribute.getName(), declaringClass.getName());
				throw new AnnotationConfigurationException(msg);
			}
			try {
				this.aliasedAttribute = this.aliasedAnnotationType.getDeclaredMethod(this.aliasedAttributeName);
			}
			catch (NoSuchMethodException ex) {
				String msg = String.format(
						"Attribute '%s' in annotation [%s] is declared as an @AliasFor nonexistent attribute '%s' in annotation [%s].",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName,
						this.aliasedAnnotationType.getName());
				throw new AnnotationConfigurationException(msg, ex);
			}

			this.isAliasPair = (this.sourceAnnotationType == this.aliasedAnnotationType);
		}

		private void validate() {
			// Target annotation is not meta-present?
			if (!this.isAliasPair && !isAnnotationMetaPresent(this.sourceAnnotationType, this.aliasedAnnotationType)) {
				String msg = String.format("@AliasFor declaration on attribute '%s' in annotation [%s] declares " +
						"an alias for attribute '%s' in meta-annotation [%s] which is not meta-present.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName,
						this.aliasedAnnotationType.getName());
				throw new AnnotationConfigurationException(msg);
			}

			if (this.isAliasPair) {
				AliasFor mirrorAliasFor = this.aliasedAttribute.getAnnotation(AliasFor.class);
				if (mirrorAliasFor == null) {
					String msg = String.format("Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s].",
							this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName);
					throw new AnnotationConfigurationException(msg);
				}

				String mirrorAliasedAttributeName = getAliasedAttributeName(mirrorAliasFor, this.aliasedAttribute);
				if (!this.sourceAttributeName.equals(mirrorAliasedAttributeName)) {
					String msg = String.format("Attribute '%s' in annotation [%s] must be declared as an @AliasFor [%s], not [%s].",
							this.aliasedAttributeName, this.sourceAnnotationType.getName(), this.sourceAttributeName,
							mirrorAliasedAttributeName);
					throw new AnnotationConfigurationException(msg);
				}
			}

			Class<?> returnType = this.sourceAttribute.getReturnType();
			Class<?> aliasedReturnType = this.aliasedAttribute.getReturnType();
			if (returnType != aliasedReturnType &&
					(!aliasedReturnType.isArray() || returnType != aliasedReturnType.getComponentType())) {
				String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] " +
						"and attribute '%s' in annotation [%s] must declare the same return type.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), this.aliasedAttributeName,
						this.aliasedAnnotationType.getName());
				throw new AnnotationConfigurationException(msg);
			}

			if (this.isAliasPair) {
				validateDefaultValueConfiguration(this.aliasedAttribute);
			}
		}

		private void validateDefaultValueConfiguration(Method aliasedAttribute) {
			Assert.notNull(aliasedAttribute, "aliasedAttribute must not be null");
			Object defaultValue = this.sourceAttribute.getDefaultValue();
			Object aliasedDefaultValue = aliasedAttribute.getDefaultValue();

			if (defaultValue == null || aliasedDefaultValue == null) {
				String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] " +
						"and attribute '%s' in annotation [%s] must declare default values.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(),
						aliasedAttribute.getDeclaringClass().getName());
				throw new AnnotationConfigurationException(msg);
			}

			if (!ObjectUtils.nullSafeEquals(defaultValue, aliasedDefaultValue)) {
				String msg = String.format("Misconfigured aliases: attribute '%s' in annotation [%s] " +
						"and attribute '%s' in annotation [%s] must declare the same default value.",
						this.sourceAttributeName, this.sourceAnnotationType.getName(), aliasedAttribute.getName(),
						aliasedAttribute.getDeclaringClass().getName());
				throw new AnnotationConfigurationException(msg);
			}
		}

		/**
		 * Validate this descriptor against the supplied descriptor.
		 * <p>This method only validates the configuration of default values
		 * for the two descriptors, since other aspects of the descriptors
		 * are validated when they are created.
		 * <p>
		 *  根据提供的描述符验证此描述符<p>此方法仅验证两个描述符的默认值的配置,因为描述符的其他方面在创建时被验证
		 * 
		 */
		private void validateAgainst(AliasDescriptor otherDescriptor) {
			validateDefaultValueConfiguration(otherDescriptor.sourceAttribute);
		}

		/**
		 * Determine if this descriptor represents an explicit override for
		 * an attribute in the supplied {@code metaAnnotationType}.
		 * <p>
		 *  确定此描述符是否表示提供的{@code metaAnnotationType}中的属性的显式覆盖
		 * 
		 * 
		 * @see #isAliasFor
		 */
		private boolean isOverrideFor(Class<? extends Annotation> metaAnnotationType) {
			return (this.aliasedAnnotationType == metaAnnotationType);
		}

		/**
		 * Determine if this descriptor and the supplied descriptor both
		 * effectively represent aliases for the same attribute in the same
		 * target annotation, either explicitly or implicitly.
		 * <p>This method searches the attribute override hierarchy, beginning
		 * with this descriptor, in order to detect implicit and transitively
		 * implicit aliases.
		 * <p>
		 * 确定此描述符和提供的描述符是否有效地表示相同目标注释中相同属性的别名,无论是显式还是隐式的<p>此方法从该描述符开始搜索属性覆盖层次结构,以便检测隐式和传递隐式别名
		 * 
		 * 
		 * @return {@code true} if this descriptor and the supplied descriptor
		 * effectively alias the same annotation attribute
		 * @see #isOverrideFor
		 */
		private boolean isAliasFor(AliasDescriptor otherDescriptor) {
			for (AliasDescriptor lhs = this; lhs != null; lhs = lhs.getAttributeOverrideDescriptor()) {
				for (AliasDescriptor rhs = otherDescriptor; rhs != null; rhs = rhs.getAttributeOverrideDescriptor()) {
					if (lhs.aliasedAttribute.equals(rhs.aliasedAttribute)) {
						return true;
					}
				}
			}
			return false;
		}

		public List<String> getAttributeAliasNames() {
			// Explicit alias pair?
			if (this.isAliasPair) {
				return Collections.singletonList(this.aliasedAttributeName);
			}

			// Else: search for implicit aliases
			List<String> aliases = new ArrayList<String>();
			for (AliasDescriptor otherDescriptor : getOtherDescriptors()) {
				if (this.isAliasFor(otherDescriptor)) {
					this.validateAgainst(otherDescriptor);
					aliases.add(otherDescriptor.sourceAttributeName);
				}
			}
			return aliases;
		}

		private List<AliasDescriptor> getOtherDescriptors() {
			List<AliasDescriptor> otherDescriptors = new ArrayList<AliasDescriptor>();
			for (Method currentAttribute : getAttributeMethods(this.sourceAnnotationType)) {
				if (!this.sourceAttribute.equals(currentAttribute)) {
					AliasDescriptor otherDescriptor = AliasDescriptor.from(currentAttribute);
					if (otherDescriptor != null) {
						otherDescriptors.add(otherDescriptor);
					}
				}
			}
			return otherDescriptors;
		}

		public String getAttributeOverrideName(Class<? extends Annotation> metaAnnotationType) {
			Assert.notNull(metaAnnotationType, "metaAnnotationType must not be null");
			Assert.isTrue(Annotation.class != metaAnnotationType,
					"metaAnnotationType must not be [java.lang.annotation.Annotation]");

			// Search the attribute override hierarchy, starting with the current attribute
			for (AliasDescriptor desc = this; desc != null; desc = desc.getAttributeOverrideDescriptor()) {
				if (desc.isOverrideFor(metaAnnotationType)) {
					return desc.aliasedAttributeName;
				}
			}

			// Else: explicit attribute override for a different meta-annotation
			return null;
		}

		private AliasDescriptor getAttributeOverrideDescriptor() {
			if (this.isAliasPair) {
				return null;
			}
			return AliasDescriptor.from(this.aliasedAttribute);
		}

		/**
		 * Get the name of the aliased attribute configured via the supplied
		 * {@link AliasFor @AliasFor} annotation on the supplied {@code attribute},
		 * or the original attribute if no aliased one specified (indicating that
		 * the reference goes to a same-named attribute on a meta-annotation).
		 * <p>This method returns the value of either the {@code attribute}
		 * or {@code value} attribute of {@code @AliasFor}, ensuring that only
		 * one of the attributes has been declared while simultaneously ensuring
		 * that at least one of the attributes has been declared.
		 * <p>
		 * 获取通过提供的{@code属性}提供的{@link AliasFor @AliasFor}注释配置的别名属性的名称,如果没有指定别名的属性,则指示原始属性(指示引用到同一命名属性一个元注释)<p>此方法
		 * 返回{@code @AliasFor}的{@code属性}或{@code值}属性的值,确保只有一个属性已被声明,同时确保至少有一个属性已被声明。
		 * 
		 * @param aliasFor the {@code @AliasFor} annotation from which to retrieve
		 * the aliased attribute name
		 * @param attribute the attribute that is annotated with {@code @AliasFor}
		 * @return the name of the aliased attribute (never {@code null} or empty)
		 * @throws AnnotationConfigurationException if invalid configuration of
		 * {@code @AliasFor} is detected
		 */
		private String getAliasedAttributeName(AliasFor aliasFor, Method attribute) {
			String attributeName = aliasFor.attribute();
			String value = aliasFor.value();
			boolean attributeDeclared = StringUtils.hasText(attributeName);
			boolean valueDeclared = StringUtils.hasText(value);

			// Ensure user did not declare both 'value' and 'attribute' in @AliasFor
			if (attributeDeclared && valueDeclared) {
				String msg = String.format("In @AliasFor declared on attribute '%s' in annotation [%s], attribute 'attribute' " +
						"and its alias 'value' are present with values of [%s] and [%s], but only one is permitted.",
						attribute.getName(), attribute.getDeclaringClass().getName(), attributeName, value);
				throw new AnnotationConfigurationException(msg);
			}

			// Either explicit attribute name or pointing to same-named attribute by default
			attributeName = (attributeDeclared ? attributeName : value);
			return (StringUtils.hasText(attributeName) ? attributeName.trim() : attribute.getName());
		}

		@Override
		public String toString() {
			return String.format("%s: @%s(%s) is an alias for @%s(%s)", getClass().getSimpleName(),
					this.sourceAnnotationType.getSimpleName(), this.sourceAttributeName,
					this.aliasedAnnotationType.getSimpleName(), this.aliasedAttributeName);
		}
	}


	private static class DefaultValueHolder {

		final Object defaultValue;

		public DefaultValueHolder(Object defaultValue) {
			this.defaultValue = defaultValue;
		}
	}

}
