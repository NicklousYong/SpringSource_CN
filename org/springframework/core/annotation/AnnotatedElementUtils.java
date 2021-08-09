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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * General utility methods for finding annotations, meta-annotations, and
 * repeatable annotations on {@link AnnotatedElement AnnotatedElements}.
 *
 * <p>{@code AnnotatedElementUtils} defines the public API for Spring's
 * meta-annotation programming model with support for <em>annotation attribute
 * overrides</em>. If you do not need support for annotation attribute
 * overrides, consider using {@link AnnotationUtils} instead.
 *
 * <p>Note that the features of this class are not provided by the JDK's
 * introspection facilities themselves.
 *
 * <h3>Annotation Attribute Overrides</h3>
 * <p>Support for meta-annotations with <em>attribute overrides</em> in
 * <em>composed annotations</em> is provided by all variants of the
 * {@code getMergedAnnotationAttributes()}, {@code getMergedAnnotation()},
 * {@code getAllMergedAnnotations()}, {@code getMergedRepeatableAnnotations()},
 * {@code findMergedAnnotationAttributes()}, {@code findMergedAnnotation()},
 * {@code findAllMergedAnnotations()}, and {@code findMergedRepeatableAnnotations()}
 * methods.
 *
 * <h3>Find vs. Get Semantics</h3>
 * <p>The search algorithms used by methods in this class follow either
 * <em>find</em> or <em>get</em> semantics. Consult the javadocs for each
 * individual method for details on which search algorithm is used.
 *
 * <p><strong>Get semantics</strong> are limited to searching for annotations
 * that are either <em>present</em> on an {@code AnnotatedElement} (i.e.,
 * declared locally or {@linkplain java.lang.annotation.Inherited inherited})
 * or declared within the annotation hierarchy <em>above</em> the
 * {@code AnnotatedElement}.
 *
 * <p><strong>Find semantics</strong> are much more exhaustive, providing
 * <em>get semantics</em> plus support for the following:
 *
 * <ul>
 * <li>Searching on interfaces, if the annotated element is a class
 * <li>Searching on superclasses, if the annotated element is a class
 * <li>Resolving bridged methods, if the annotated element is a method
 * <li>Searching on methods in interfaces, if the annotated element is a method
 * <li>Searching on methods in superclasses, if the annotated element is a method
 * </ul>
 *
 * <h3>Support for {@code @Inherited}</h3>
 * <p>Methods following <em>get semantics</em> will honor the contract of
 * Java's {@link java.lang.annotation.Inherited @Inherited} annotation except
 * that locally declared annotations (including custom composed annotations)
 * will be favored over inherited annotations. In contrast, methods following
 * <em>find semantics</em> will completely ignore the presence of
 * {@code @Inherited} since the <em>find</em> search algorithm manually
 * traverses type and method hierarchies and thereby implicitly supports
 * annotation inheritance without the need for {@code @Inherited}.
 *
 * <p>
 *  在{@link AnnotatedElement AnnotatedElements}上查找注释,元注释和可重复注释的一般实用方法
 * 
 * <p> {@ code AnnotatedElementUtils}定义了Spring的元注释编程模型的公共API,支持<em>注释属性覆盖</em>如果不需要支持注释属性覆盖,请考虑使用{@link AnnotationUtils}
 * 代替。
 * 
 *  <p>请注意,JDK的内省设施本身不提供此类的功能
 * 
 * <h3>注释属性覆盖</h3> <p>在<em>组合注释</em>中支持使用<em>属性覆盖</em>的元注释由{@code getMergedAnnotationAttributes( )},{@code getMergedAnnotation()}
 * ,{@code getAllMergedAnnotations()},{@code getMergedRepeatableAnnotations()},{@code findMergedAnnotationAttributes()}
 * ,{@code findMergedAnnotation()},{@code findAllMergedAnnotations()} ,和{@code findMergedRepeatableAnnotations()}
 * 方法。
 * 
 *  <h3>查找vs获取语义</h3> <p>此类中的方法使用的搜索算法遵循<em> find </em>或<em>获取</em>语义查询每个单独方法的javadocs有关使用哪种搜索算法的详细信息
 * 
 * <p> <strong>获取语义</strong>仅限于在{@code AnnotatedElement}上搜索<em>存在</em>的注释(即本地声明或{@linkplain javalangannotationInherited inherited}
 * )或在<em>上方的注释层次结构</em>中声明了{@code AnnotatedElement}。
 * 
 *  <p> <strong>查找语义</strong>非常详尽,提供<em>获取语义</em>以及对以下内容的支持：
 * 
 * <ul>
 *  <li>在接口上搜索,如果注释元素是类<li>搜索超类,如果注释元素是类<li>解析桥接方法,如果注释元素是方法<li>在界面中搜索方法如果注释元素是方法<li>在超类中搜索方法,如果注释元素是方法。
 * </ul>
 * 
 * <h3>支持{@code @Inherited} </h3> <p> <em>获取语义</em>之后的方法将符合Java的{@link javalangannotationInherited @Inherited}
 * 注释的约定,但本地声明的注释(包括自定义编辑的注释)将被遗留在继承的注释上。
 * 相比之下,<em> find语义</em>之后的方法将完全忽略{@code @Inherited}的存在,因为<em>搜索算法手动遍历类型和方法层次结构,从而隐含地支持注释继承,而不需要{@code @Inherited}
 * 。
 * 
 * 
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 4.0
 * @see AliasFor
 * @see AnnotationAttributes
 * @see AnnotationUtils
 * @see BridgeMethodResolver
 */
public class AnnotatedElementUtils {

	/**
	 * {@code null} constant used to denote that the search algorithm should continue.
	 * <p>
	 *  {@code null}常量,用于表示搜索算法应该继续
	 * 
	 */
	private static final Boolean CONTINUE = null;

	private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

	private static final Processor<Boolean> alwaysTrueAnnotationProcessor = new AlwaysTrueBooleanAnnotationProcessor();


	/**
	 * Build an adapted {@link AnnotatedElement} for the given annotations,
	 * typically for use with other methods on {@link AnnotatedElementUtils}.
	 * <p>
	 * 为给定的注释构建一个适应的{@link AnnotatedElement},通常用于{@link AnnotatedElementUtils}上的其他方法
	 * 
	 * 
	 * @param annotations the annotations to expose through the {@code AnnotatedElement}
	 * @since 4.3
	 */
	public static AnnotatedElement forAnnotations(final Annotation... annotations) {
		return new AnnotatedElement() {
			@Override
			@SuppressWarnings("unchecked")
			public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
				for (Annotation ann : annotations) {
					if (ann.annotationType() == annotationClass) {
						return (T) ann;
					}
				}
				return null;
			}
			@Override
			public Annotation[] getAnnotations() {
				return annotations;
			}
			@Override
			public Annotation[] getDeclaredAnnotations() {
				return annotations;
			}
		};
	}

	/**
	 * Get the fully qualified class names of all meta-annotation types
	 * <em>present</em> on the annotation (of the specified {@code annotationType})
	 * on the supplied {@link AnnotatedElement}.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 *  在所提供的{@link AnnotatedElement} <p>上的注释(指定的{@code注释类型})上获取所有元注释类型<em>的完全限定类名。
	 * </em>获得语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type on which to find meta-annotations
	 * @return the names of all meta-annotations present on the annotation,
	 * or {@code null} if not found
	 * @since 4.2
	 * @see #getMetaAnnotationTypes(AnnotatedElement, String)
	 * @see #hasMetaAnnotationTypes
	 */
	public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		return getMetaAnnotationTypes(element, element.getAnnotation(annotationType));
	}

	/**
	 * Get the fully qualified class names of all meta-annotation
	 * types <em>present</em> on the annotation (of the specified
	 * {@code annotationName}) on the supplied {@link AnnotatedElement}.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 *  在提供的{@link AnnotatedElement} <p>的注释(指定的{@code annotationName})上获取所有元注释类型<em>的完全限定类名。
	 * </em>获得语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation
	 * type on which to find meta-annotations
	 * @return the names of all meta-annotations present on the annotation,
	 * or {@code null} if not found
	 * @see #getMetaAnnotationTypes(AnnotatedElement, Class)
	 * @see #hasMetaAnnotationTypes
	 */
	public static Set<String> getMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.hasLength(annotationName, "annotationName must not be null or empty");

		return getMetaAnnotationTypes(element, AnnotationUtils.getAnnotation(element, annotationName));
	}

	private static Set<String> getMetaAnnotationTypes(AnnotatedElement element, Annotation composed) {
		if (composed == null) {
			return null;
		}

		try {
			final Set<String> types = new LinkedHashSet<String>();
			searchWithGetSemantics(composed.annotationType(), null, null, null, new SimpleAnnotationProcessor<Object>(true) {
					@Override
					public Object process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
						types.add(annotation.annotationType().getName());
						return CONTINUE;
					}
				}, new HashSet<AnnotatedElement>(), 1);
			return (!types.isEmpty() ? types : null);
		}
		catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
		}
	}

	/**
	 * Determine if the supplied {@link AnnotatedElement} is annotated with
	 * a <em>composed annotation</em> that is meta-annotated with an
	 * annotation of the specified {@code annotationType}.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 确定提供的{@link AnnotatedElement}是否使用指定的{@code annotationType}的注释进行元注释的<em>组合注释</em>进行注释。
	 * 此方法遵循<em> get语义< / em>,如{@linkplain AnnotatedElementUtils class class javadoc}中所述。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the meta-annotation type to find
	 * @return {@code true} if a matching meta-annotation is present
	 * @since 4.2.3
	 * @see #getMetaAnnotationTypes
	 */
	public static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		return hasMetaAnnotationTypes(element, annotationType, null);
	}

	/**
	 * Determine if the supplied {@link AnnotatedElement} is annotated with a
	 * <em>composed annotation</em> that is meta-annotated with an annotation
	 * of the specified {@code annotationName}.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 *  确定提供的{@link AnnotatedElement}是否使用指定的{@code annotationName}的注释进行元注释的<em>组合注释</em>进行注释。
	 * 此方法遵循<em> get语义< / em>,如{@linkplain AnnotatedElementUtils class class javadoc}中所述。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the
	 * meta-annotation type to find
	 * @return {@code true} if a matching meta-annotation is present
	 * @see #getMetaAnnotationTypes
	 */
	public static boolean hasMetaAnnotationTypes(AnnotatedElement element, String annotationName) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.hasLength(annotationName, "annotationName must not be null or empty");

		return hasMetaAnnotationTypes(element, null, annotationName);
	}

	private static boolean hasMetaAnnotationTypes(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName) {

		return Boolean.TRUE.equals(
			searchWithGetSemantics(element, annotationType, annotationName, new SimpleAnnotationProcessor<Boolean>() {

				@Override
				public Boolean process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
					return (metaDepth > 0 ? Boolean.TRUE : CONTINUE);
				}
			}));
	}

	/**
	 * Determine if an annotation of the specified {@code annotationType}
	 * is <em>present</em> on the supplied {@link AnnotatedElement} or
	 * within the annotation hierarchy <em>above</em> the specified element.
	 * <p>If this method returns {@code true}, then {@link #getMergedAnnotationAttributes}
	 * will return a non-null value.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 确定指定的{@code注释类型}的注释是否在提供的{@link AnnotatedElement}上或</em>上方的注释层次结构<em>中<em> </em>中指定的元素<p>方法返回{@code true}
	 * ,则{@link #getMergedAnnotationAttributes}将返回非空值<p>此方法遵循<em> get语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @return {@code true} if a matching annotation is present
	 * @since 4.2.3
	 * @see #hasAnnotation(AnnotatedElement, Class)
	 */
	public static boolean isAnnotated(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		// Shortcut: directly present on the element, with no processing needed?
		if (element.isAnnotationPresent(annotationType)) {
			return true;
		}

		return Boolean.TRUE.equals(searchWithGetSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
	}

	/**
	 * Determine if an annotation of the specified {@code annotationName} is
	 * <em>present</em> on the supplied {@link AnnotatedElement} or within the
	 * annotation hierarchy <em>above</em> the specified element.
	 * <p>If this method returns {@code true}, then {@link #getMergedAnnotationAttributes}
	 * will return a non-null value.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 确定指定的{@code annotationName}的注释是否在所提供的{@link AnnotatedElement}上或在</em>之前的注释层次结构<em>中存在</em>指定的元素<p>如果方
	 * 法返回{@code true},则{@link #getMergedAnnotationAttributes}将返回非空值<p>此方法遵循<em> get语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @return {@code true} if a matching annotation is present
	 */
	public static boolean isAnnotated(AnnotatedElement element, String annotationName) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.hasLength(annotationName, "annotationName must not be null or empty");

		return Boolean.TRUE.equals(searchWithGetSemantics(element, null, annotationName, alwaysTrueAnnotationProcessor));
	}

	/**
	/* <p>
	/* 
	 * @deprecated As of Spring Framework 4.2, use {@link #getMergedAnnotationAttributes(AnnotatedElement, String)} instead.
	 */
	@Deprecated
	public static AnnotationAttributes getAnnotationAttributes(AnnotatedElement element, String annotationName) {
		return getMergedAnnotationAttributes(element, annotationName);
	}

	/**
	/* <p>
	/* 
	 * @deprecated As of Spring Framework 4.2, use {@link #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)} instead.
	 */
	@Deprecated
	public static AnnotationAttributes getAnnotationAttributes(AnnotatedElement element, String annotationName,
			boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		return getMergedAnnotationAttributes(element, annotationName, classValuesAsString, nestedAnnotationsAsMap);
	}

	/**
	 * Get the first annotation of the specified {@code annotationType} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element} and
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>This method delegates to {@link #getMergedAnnotationAttributes(AnnotatedElement, String)}.
	 * <p>
	 * 在提供的{@code元素}上方的注释层次结构<em>中获取指定的{@code annotationType}的第一个注释,并将该注释的属性与<em>匹配</em>属性的注释合并注释层次结构的级别<p> 
	 * {@ link AliasFor @AliasFor}语义在单个注释和注释层次结构中完全支持<p>此方法委托给{@link #getMergedAnnotationAttributes(AnnotatedElement,String)}
	 * }。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @since 4.2
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 */
	public static AnnotationAttributes getMergedAnnotationAttributes(
			AnnotatedElement element, Class<? extends Annotation> annotationType) {

		Assert.notNull(annotationType, "annotationType must not be null");
		AnnotationAttributes attributes = searchWithGetSemantics(element, annotationType, null,
				new MergedAnnotationAttributesProcessor());
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
		return attributes;
	}

	/**
	 * Get the first annotation of the specified {@code annotationName} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element} and
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>This method delegates to {@link #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)},
	 * supplying {@code false} for {@code classValuesAsString} and {@code nestedAnnotationsAsMap}.
	 * <p>
	 * 在提供的{@code元素}上方的注释层次结构<em>中获取指定的{@code annotationName}的第一个注释,并将该注释的属性与<em>匹配</em>属性的注释合并注释层次结构的级别<p> 
	 * {@ link AliasFor @AliasFor}语义完全支持,单个注释和注释层次结构<p>此方法委托给{@link #getMergedAnnotationAttributes(AnnotatedElement,String,boolean,boolean )}
	 * ,为{@code classValuesAsString}和{@code nestedAnnotationsAsMap}提供{@code false}。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @since 4.2
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String)
	 */
	public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, String annotationName) {
		return getMergedAnnotationAttributes(element, annotationName, false, false);
	}

	/**
	 * Get the first annotation of the specified {@code annotationName} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element} and
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>Attributes from lower levels in the annotation hierarchy override attributes
	 * of the same name from higher levels, and {@link AliasFor @AliasFor} semantics are
	 * fully supported, both within a single annotation and within the annotation hierarchy.
	 * <p>In contrast to {@link #getAllAnnotationAttributes}, the search algorithm used by
	 * this method will stop searching the annotation hierarchy once the first annotation
	 * of the specified {@code annotationName} has been found. As a consequence,
	 * additional annotations of the specified {@code annotationName} will be ignored.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在提供的{@code元素}上方的注释层次结构<em>中获取指定的{@code annotationName}的第一个注释,并将该注释的属性与<em>匹配</em>属性的注释合并注释层次结构的级别<p>注
	 * 释层次中较低级别的属性从较高级别覆盖相同名称的属性,并且完全支持{@link AliasFor @AliasFor}语义,在单个注释和注释层次结构< p>与{@link #getAllAnnotationAttributes}
	 * 相反,一旦找到指定的{@code annotationName}的第一个注释,此方法使用的搜索算法将停止搜索注释层次结构因此,指定的{@code annotationName}的附加注释将被忽略<p>此
	 * 方法遵循<em> get语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @param classValuesAsString whether to convert Class references into Strings or to
	 * preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances
	 * into {@code AnnotationAttributes} maps or to preserve them as Annotation instances
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @since 4.2
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 */
	public static AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element,
			String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		Assert.hasLength(annotationName, "annotationName must not be null or empty");
		AnnotationAttributes attributes = searchWithGetSemantics(element, null, annotationName,
				new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Get the first annotation of the specified {@code annotationType} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element},
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy, and synthesize
	 * the result back into an annotation of the specified {@code annotationType}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>This method delegates to {@link #getMergedAnnotationAttributes(AnnotatedElement, Class)}
	 * and {@link AnnotationUtils#synthesizeAnnotation(Map, Class, AnnotatedElement)}.
	 * <p>
	 * 在提供的{@code元素}上方的注释层次结构<em>中获取指定的{@code annotationType}的第一个注释,将该注释的属性与</em>匹配的属性与<注释层次结构的级别,并将结果合成回指定的{@code annotationType} <p>
	 *  {@ link AliasFor @AliasFor}语义的注释,完全支持在单个注释和注释层次结构<p>此方法委托给{@link #getMergedAnnotationAttributes(AnnotatedElement,Class)}
	 * 和{@link AnnotationUtils#synthesizeAnnotation(Map,Class,AnnotatedElement)}。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @return the merged, synthesized {@code Annotation}, or {@code null} if not found
	 * @since 4.2
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, Class)
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see AnnotationUtils#synthesizeAnnotation(Map, Class, AnnotatedElement)
	 */
	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Assert.notNull(annotationType, "annotationType must not be null");

		// Shortcut: directly present on the element, with no merging needed?
		if (!(element instanceof Class)) {
			// Do not use this shortcut against a Class: Inherited annotations
			// would get preferred over locally declared composed annotations.
			A annotation = element.getAnnotation(annotationType);
			if (annotation != null) {
				return AnnotationUtils.synthesizeAnnotation(annotation, element);
			}
		}

		// Exhaustive retrieval of merged annotation attributes...
		AnnotationAttributes attributes = getMergedAnnotationAttributes(element, annotationType);
		return AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element);
	}

	/**
	 * Get <strong>all</strong> annotations of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the annotation
	 * hierarchy and synthesize the results back into an annotation of the specified
	 * {@code annotationType}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在所提供的{@code元素} </em>之前的注解层次结构<em>中获取<strong>所有</strong>注释{@code annotationType};并且对于发现的每个注释,将该注释的属性与注
	 * 释层级的较低级别的注释中的<em>匹配</em>属性合并,并将结果重新编入指定的{@code注释类型}的注释<链接AliasFor @AliasFor}语义完全支持,单个注释和注释层次结构<p>此方法遵
	 * 循<em>获取语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find; never {@code null}
	 * @return the set of all merged, synthesized {@code Annotations} found, or an empty
	 * set if none were found
	 * @since 4.3
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String)
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> Set<A> getAllMergedAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithGetSemantics(element, annotationType, null, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Get all <em>repeatable annotations</em> of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the annotation
	 * hierarchy and synthesize the results back into an annotation of the specified
	 * {@code annotationType}.
	 * <p>The container type that holds the repeatable annotations will be looked up
	 * via {@link java.lang.annotation.Repeatable}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 获取所提供的{@code元素}之前的注释层次结构<em> </em>中的指定{@code注释类型}的所有<em>可重复注释</em>并且对于发现的每个注释,将该注释的属性与注释层级的较低级别的注释中的<em>
	 * 匹配</em>属性合并,并将结果合成回指定的{@code注释类型}的注释<p>容器通过{@link javalangannotationRepeatable} <p> {@ link AliasFor @AliasFor}
	 * 语义来查找保存可重复注释的类型,完全支持单个注释和注释层次结构中的语义<p>此方法遵循<em>获取语义</em>,如{@linkplain AnnotatedElementUtils class class javadoc}
	 * 中所述。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find; never {@code null}
	 * @return the set of all merged repeatable {@code Annotations} found, or an empty
	 * set if none were found
	 * @since 4.3
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllMergedAnnotations(AnnotatedElement, Class)
	 * @see #getMergedRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @throws IllegalArgumentException if the {@code element} or {@code annotationType}
	 * is {@code null}, or if the container type cannot be resolved
	 */
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		return getMergedRepeatableAnnotations(element, annotationType, null);
	}

	/**
	 * Get all <em>repeatable annotations</em> of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the annotation
	 * hierarchy and synthesize the results back into an annotation of the specified
	 * {@code annotationType}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 获取所提供的{@code元素}之前的注释层次结构<em> </em>中的指定{@code注释类型}的所有<em>可重复注释</em>并且对于发现的每个注释,将该注释的属性与注释层级的较低级别的注释中的<em>
	 * 匹配</em>属性合并,并将结果重新编入指定的{@code注释类型}的注释<链接AliasFor @AliasFor}语义完全支持,单个注释和注释层次结构<p>此方法遵循<em>获取语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find; never {@code null}
	 * @param containerType the type of the container that holds the annotations;
	 * may be {@code null} if the container type should be looked up via
	 * {@link java.lang.annotation.Repeatable}
	 * @return the set of all merged repeatable {@code Annotations} found, or an empty
	 * set if none were found
	 * @since 4.3
	 * @see #getMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllMergedAnnotations(AnnotatedElement, Class)
	 * @throws IllegalArgumentException if the {@code element} or {@code annotationType}
	 * is {@code null}, or if the container type cannot be resolved
	 * @throws AnnotationConfigurationException if the supplied {@code containerType}
	 * is not a valid container annotation for the supplied {@code annotationType}
	 */
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType, Class<? extends Annotation> containerType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		if (containerType == null) {
			containerType = resolveContainerType(annotationType);
		}
		else {
			validateContainerType(annotationType, containerType);
		}

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithGetSemantics(element, annotationType, null, containerType, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Get the annotation attributes of <strong>all</strong> annotations of the specified
	 * {@code annotationName} in the annotation hierarchy above the supplied
	 * {@link AnnotatedElement} and store the results in a {@link MultiValueMap}.
	 * <p>Note: in contrast to {@link #getMergedAnnotationAttributes(AnnotatedElement, String)},
	 * this method does <em>not</em> support attribute overrides.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在提供的{@link AnnotatedElement}上方的注释层次结构中获取指定的{@code annotationName}的<strong>所有</strong>注释属性,并将结果存储在{@link MultiValueMap}
	 *  <p>中注意：与{@link #getMergedAnnotationAttributes(AnnotatedElement,String)})相反,此方法不支持</em>支持属性覆盖<p>此方法遵循
	 * <em>获取语义</em>,如{@ linkplain AnnotatedElementUtils class class javadoc}。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @return a {@link MultiValueMap} keyed by attribute name, containing the annotation
	 * attributes from all annotations found, or {@code null} if not found
	 * @see #getAllAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 */
	public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element, String annotationName) {
		return getAllAnnotationAttributes(element, annotationName, false, false);
	}

	/**
	 * Get the annotation attributes of <strong>all</strong> annotations of
	 * the specified {@code annotationName} in the annotation hierarchy above
	 * the supplied {@link AnnotatedElement} and store the results in a
	 * {@link MultiValueMap}.
	 * <p>Note: in contrast to {@link #getMergedAnnotationAttributes(AnnotatedElement, String)},
	 * this method does <em>not</em> support attribute overrides.
	 * <p>This method follows <em>get semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在提供的{@link AnnotatedElement}上方的注释层次结构中获取指定的{@code annotationName}的<strong>所有</strong>注释属性,并将结果存储在{@link MultiValueMap}
	 *  <p>中注意：与{@link #getMergedAnnotationAttributes(AnnotatedElement,String)})相反,此方法不支持</em>支持属性覆盖<p>此方法遵循
	 * <em>获取语义</em>,如{@ linkplain AnnotatedElementUtils class class javadoc}。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @param classValuesAsString whether to convert Class references into Strings or to
	 * preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances into
	 * {@code AnnotationAttributes} maps or to preserve them as Annotation instances
	 * @return a {@link MultiValueMap} keyed by attribute name, containing the annotation
	 * attributes from all annotations found, or {@code null} if not found
	 */
	public static MultiValueMap<String, Object> getAllAnnotationAttributes(AnnotatedElement element,
			String annotationName, final boolean classValuesAsString, final boolean nestedAnnotationsAsMap) {

		final MultiValueMap<String, Object> attributesMap = new LinkedMultiValueMap<String, Object>();

		searchWithGetSemantics(element, null, annotationName, new SimpleAnnotationProcessor<Object>() {
			@Override
			public Object process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
				AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(
						annotation, classValuesAsString, nestedAnnotationsAsMap);
				for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
					attributesMap.add(entry.getKey(), entry.getValue());
				}
				return CONTINUE;
			}
		});

		return (!attributesMap.isEmpty() ? attributesMap : null);
	}

	/**
	 * Determine if an annotation of the specified {@code annotationType}
	 * is <em>available</em> on the supplied {@link AnnotatedElement} or
	 * within the annotation hierarchy <em>above</em> the specified element.
	 * <p>If this method returns {@code true}, then {@link #findMergedAnnotationAttributes}
	 * will return a non-null value.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 确定指定的{@code注释类型}的注释是否可用</em>在所提供的{@link AnnotatedElement}上或注释分层结构<em> </em>中指定的元素<p>如果方法返回{@code true}
	 * ,则{@link #findMergedAnnotationAttributes}将返回非空值<p>该方法遵循<em> find语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @return {@code true} if a matching annotation is present
	 * @since 4.3
	 * @see #isAnnotated(AnnotatedElement, Class)
	 */
	public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		// Shortcut: directly present on the element, with no processing needed?
		if (element.isAnnotationPresent(annotationType)) {
			return true;
		}

		return Boolean.TRUE.equals(searchWithFindSemantics(element, annotationType, null, alwaysTrueAnnotationProcessor));
	}

	/**
	 * Find the first annotation of the specified {@code annotationType} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element} and
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>Attributes from lower levels in the annotation hierarchy override
	 * attributes of the same name from higher levels, and
	 * {@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>In contrast to {@link #getAllAnnotationAttributes}, the search
	 * algorithm used by this method will stop searching the annotation
	 * hierarchy once the first annotation of the specified
	 * {@code annotationType} has been found. As a consequence, additional
	 * annotations of the specified {@code annotationType} will be ignored.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在所提供的{@code元素}上找到</em>以上的注释层次结构</code>中指定的{@code annotationType}的第一个注释,并将该注释的属性与<em>匹配</em>属性的注释合并注释层
	 * 次结构的级别<p>注释层次中较低级别的属性从较高级别覆盖相同名称的属性,并且完全支持{@link AliasFor @AliasFor}语义,在单个注释和注释层次结构< p>与{@link #getAllAnnotationAttributes}
	 * 相反,一旦找到指定的{@code注释类型}的第一个注释,此方法使用的搜索算法将停止搜索注释层次结构因此,指定的{@code annotationType}的附加注释将被忽略<p>此方法遵循<em> fi
	 * nd语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @param classValuesAsString whether to convert Class references into
	 * Strings or to preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation
	 * instances into {@code AnnotationAttributes} maps or to preserve them
	 * as Annotation instances
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if
	 * not found
	 * @since 4.2
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 */
	public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element,
			Class<? extends Annotation> annotationType, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		AnnotationAttributes attributes = searchWithFindSemantics(element, annotationType, null,
				new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Find the first annotation of the specified {@code annotationName} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element} and
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy.
	 * <p>Attributes from lower levels in the annotation hierarchy override
	 * attributes of the same name from higher levels, and
	 * {@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>In contrast to {@link #getAllAnnotationAttributes}, the search
	 * algorithm used by this method will stop searching the annotation
	 * hierarchy once the first annotation of the specified
	 * {@code annotationName} has been found. As a consequence, additional
	 * annotations of the specified {@code annotationName} will be ignored.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在提供的{@code元素}之上的注释层次结构<em>中找到指定的{@code annotationName}的第一个注释,并将该注释的属性与下面的注释中的<em>匹配</em>属性合并注释层次结构的级别
	 * <p>注释层次中较低级别的属性从较高级别覆盖相同名称的属性,并且完全支持{@link AliasFor @AliasFor}语义,在单个注释和注释层次结构< p>与{@link #getAllAnnotationAttributes}
	 * 相反,一旦找到指定的{@code annotationName}的第一个注释,此方法使用的搜索算法将停止搜索注释层次结构因此,指定的{@code annotationName}的附加注释将被忽略<p>此
	 * 方法遵循<em> find语义</em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @param classValuesAsString whether to convert Class references into Strings or to
	 * preserve them as Class references
	 * @param nestedAnnotationsAsMap whether to convert nested Annotation instances into
	 * {@code AnnotationAttributes} maps or to preserve them as Annotation instances
	 * @return the merged {@code AnnotationAttributes}, or {@code null} if not found
	 * @since 4.2
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 */
	public static AnnotationAttributes findMergedAnnotationAttributes(AnnotatedElement element,
			String annotationName, boolean classValuesAsString, boolean nestedAnnotationsAsMap) {

		AnnotationAttributes attributes = searchWithFindSemantics(element, null, annotationName,
				new MergedAnnotationAttributesProcessor(classValuesAsString, nestedAnnotationsAsMap));
		AnnotationUtils.postProcessAnnotationAttributes(element, attributes, classValuesAsString, nestedAnnotationsAsMap);
		return attributes;
	}

	/**
	 * Find the first annotation of the specified {@code annotationType} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element},
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy, and synthesize
	 * the result back into an annotation of the specified {@code annotationType}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在所提供的{@code元素}之前的</em>上方的注释层次结构<em>中找到指定的{@code annotationType}的第一个注释,将该注释的属性与<em>匹配</em>属性的注释合并注释层次结
	 * 构的级别,并将结果合成回指定的{@code annotationType} <p> {@ link AliasFor @AliasFor}语义的注释,完全支持在单个注释和注释层次结构<p>该方法遵循</em>
	 *  </em>,如{@linkplain AnnotatedElementUtils类级别的javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @return the merged, synthesized {@code Annotation}, or {@code null} if not found
	 * @since 4.2
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see #getMergedAnnotationAttributes(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Assert.notNull(annotationType, "annotationType must not be null");

		// Shortcut: directly present on the element, with no merging needed?
		if (!(element instanceof Class)) {
			// Do not use this shortcut against a Class: Inherited annotations
			// would get preferred over locally declared composed annotations.
			A annotation = element.getAnnotation(annotationType);
			if (annotation != null) {
				return AnnotationUtils.synthesizeAnnotation(annotation, element);
			}
		}

		// Exhaustive retrieval of merged annotation attributes...
		AnnotationAttributes attributes = findMergedAnnotationAttributes(element, annotationType, false, false);
		return AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element);
	}

	/**
	 * Find the first annotation of the specified {@code annotationName} within
	 * the annotation hierarchy <em>above</em> the supplied {@code element},
	 * merge that annotation's attributes with <em>matching</em> attributes from
	 * annotations in lower levels of the annotation hierarchy, and synthesize
	 * the result back into an annotation of the specified {@code annotationName}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both
	 * within a single annotation and within the annotation hierarchy.
	 * <p>This method delegates to {@link #findMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)}
	 * (supplying {@code false} for {@code classValuesAsString} and {@code nestedAnnotationsAsMap})
	 * and {@link AnnotationUtils#synthesizeAnnotation(Map, Class, AnnotatedElement)}.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在提供的{@code元素}之上的注释层次结构<em>中找到指定的{@code annotationName}的第一个注释,将注释的属性与<em>匹配</em>属性的注释合并在较低的注释层次结构的级别,并
	 * 将结果合成到指定的{@code annotationName} <p> {@ link AliasFor @AliasFor}语义的注释中,完全支持在单个注释和注释层次结构<p>这个方法委托给{@link #findMergedAnnotationAttributes(AnnotatedElement,String,boolean,boolean)}
	 * (为{@code classValuesAsString}和{@code nestedAnnotationsAsMap}提供{@code false})和{@link AnnotationUtils#synthesizeAnnotation(Map,Class ,AnnotatedElement)}
	 * <p>这个方法按照{@linkplain AnnotatedElementUtils class class javadoc}中所述的<em>查找语义</em>。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationName the fully qualified class name of the annotation type to find
	 * @return the merged, synthesized {@code Annotation}, or {@code null} if not found
	 * @since 4.2
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findMergedAnnotationAttributes(AnnotatedElement, String, boolean, boolean)
	 * @see AnnotationUtils#synthesizeAnnotation(Map, Class, AnnotatedElement)
	 * @deprecated As of Spring Framework 4.2.3, use {@link #findMergedAnnotation(AnnotatedElement, Class)} instead.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, String annotationName) {
		AnnotationAttributes attributes = findMergedAnnotationAttributes(element, annotationName, false, false);
		return AnnotationUtils.synthesizeAnnotation(attributes, (Class<A>) attributes.annotationType(), element);
	}

	/**
	 * Find <strong>all</strong> annotations of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the annotation
	 * hierarchy and synthesize the results back into an annotation of the specified
	 * {@code annotationType}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在所提供的{@code元素}之前的注释层次结构<em> </em>中查找指定的{@code注释类型}的<strong>所有</strong>注释;并且对于发现的每个注释,将该注释的属性与注释层级的较低级
	 * 别的注释中的<em>匹配</em>属性合并,并将结果重新编入指定的{@code注释类型}的注释<链接AliasFor @AliasFor}语义完全支持,单个注释和注释层次结构<p>此方法遵循<em>查找
	 * 语义</em>,如{@linkplain AnnotatedElementUtils类级别javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find; never {@code null}
	 * @return the set of all merged, synthesized {@code Annotations} found, or an empty
	 * set if none were found
	 * @since 4.3
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #getAllMergedAnnotations(AnnotatedElement, Class)
	 */
	public static <A extends Annotation> Set<A> findAllMergedAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithFindSemantics(element, annotationType, null, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Find all <em>repeatable annotations</em> of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the annotation
	 * hierarchy and synthesize the results back into an annotation of the specified
	 * {@code annotationType}.
	 * <p>The container type that holds the repeatable annotations will be looked up
	 * via {@link java.lang.annotation.Repeatable}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在所提供的{@code元素}之前的注释层次结构<em>中找到指定的{@code注释类型}的所有<em>可重复注释</em>;并且对于发现的每个注释,将该注释的属性与注释层级的较低级别的注释中的<em>匹
	 * 配</em>属性合并,并将结果合成回指定的{@code注释类型}的注释<p>容器可以通过{@link javalangannotationRepeatable}查找包含可重复注释的类型<p> {@ link AliasFor @AliasFor}
	 * 语义在单个注释和注释层次结构中完全支持<p>此方法遵循<em>查找语义</em>,如{@linkplain AnnotatedElementUtils class class javadoc}中所述。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find; never {@code null}
	 * @return the set of all merged repeatable {@code Annotations} found, or an empty
	 * set if none were found
	 * @since 4.3
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 * @see #findMergedRepeatableAnnotations(AnnotatedElement, Class, Class)
	 * @throws IllegalArgumentException if the {@code element} or {@code annotationType}
	 * is {@code null}, or if the container type cannot be resolved
	 */
	public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType) {

		return findMergedRepeatableAnnotations(element, annotationType, null);
	}

	/**
	 * Find all <em>repeatable annotations</em> of the specified {@code annotationType}
	 * within the annotation hierarchy <em>above</em> the supplied {@code element};
	 * and for each annotation found, merge that annotation's attributes with
	 * <em>matching</em> attributes from annotations in lower levels of the annotation
	 * hierarchy and synthesize the results back into an annotation of the specified
	 * {@code annotationType}.
	 * <p>{@link AliasFor @AliasFor} semantics are fully supported, both within a
	 * single annotation and within annotation hierarchies.
	 * <p>This method follows <em>find semantics</em> as described in the
	 * {@linkplain AnnotatedElementUtils class-level javadoc}.
	 * <p>
	 * 在所提供的{@code元素}之前的注释层次结构<em>中找到指定的{@code注释类型}的所有<em>可重复注释</em>;并且对于发现的每个注释,将该注释的属性与注释层级的较低级别的注释中的<em>匹
	 * 配</em>属性合并,并将结果重新编入指定的{@code注释类型}的注释<链接AliasFor @AliasFor}语义完全支持,单个注释和注释层次结构<p>此方法遵循<em>查找语义</em>,如{@linkplain AnnotatedElementUtils类级别javadoc中所述)。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find; never {@code null}
	 * @param containerType the type of the container that holds the annotations;
	 * may be {@code null} if the container type should be looked up via
	 * {@link java.lang.annotation.Repeatable}
	 * @return the set of all merged repeatable {@code Annotations} found, or an empty
	 * set if none were found
	 * @since 4.3
	 * @see #findMergedAnnotation(AnnotatedElement, Class)
	 * @see #findAllMergedAnnotations(AnnotatedElement, Class)
	 * @throws IllegalArgumentException if the {@code element} or {@code annotationType}
	 * is {@code null}, or if the container type cannot be resolved
	 * @throws AnnotationConfigurationException if the supplied {@code containerType}
	 * is not a valid container annotation for the supplied {@code annotationType}
	 */
	public static <A extends Annotation> Set<A> findMergedRepeatableAnnotations(AnnotatedElement element,
			Class<A> annotationType, Class<? extends Annotation> containerType) {

		Assert.notNull(element, "AnnotatedElement must not be null");
		Assert.notNull(annotationType, "annotationType must not be null");

		if (containerType == null) {
			containerType = resolveContainerType(annotationType);
		}
		else {
			validateContainerType(annotationType, containerType);
		}

		MergedAnnotationAttributesProcessor processor = new MergedAnnotationAttributesProcessor(false, false, true);
		searchWithFindSemantics(element, annotationType, null, containerType, processor);
		return postProcessAndSynthesizeAggregatedResults(element, annotationType, processor.getAggregatedResults());
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following
	 * <em>get semantics</em>.
	 * <p>
	 * 在指定的{@code元素}上搜索指定的{@code annotationName}或{@code annotationType}的注释,遵循<em>获取语义</em>
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param processor the processor to delegate to
	 * @return the result of the processor, potentially {@code null}
	 */
	private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Processor<T> processor) {

		return searchWithGetSemantics(element, annotationType, annotationName, null, processor);
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following
	 * <em>get semantics</em>.
	 * <p>
	 *  在指定的{@code元素}上搜索指定的{@code annotationName}或{@code annotationType}的注释,遵循<em>获取语义</em>
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param containerType the type of the container that holds repeatable
	 * annotations, or {@code null} if the annotation is not repeatable
	 * @param processor the processor to delegate to
	 * @return the result of the processor, potentially {@code null}
	 * @since 4.3
	 */
	private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor) {

		try {
			return searchWithGetSemantics(element, annotationType, annotationName, containerType, processor,
					new HashSet<AnnotatedElement>(), 0);
		}
		catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
		}
	}

	/**
	 * Perform the search algorithm for the {@link #searchWithGetSemantics}
	 * method, avoiding endless recursion by tracking which annotated elements
	 * have already been <em>visited</em>.
	 * <p>The {@code metaDepth} parameter is explained in the
	 * {@link Processor#process process()} method of the {@link Processor} API.
	 * <p>
	 *  执行{@link #searchWithGetSemantics}方法的搜索算法,通过跟踪哪些注释元素已被访问避免无休止的递归</em> <p> {@code metaDepth}参数在{@link {@link Processor}
	 *  API的Processor#process process()}方法。
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param containerType the type of the container that holds repeatable
	 * annotations, or {@code null} if the annotation is not repeatable
	 * @param processor the processor to delegate to
	 * @param visited the set of annotated elements that have already been visited
	 * @param metaDepth the meta-depth of the annotation
	 * @return the result of the processor, potentially {@code null}
	 */
	private static <T> T searchWithGetSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth) {

		Assert.notNull(element, "AnnotatedElement must not be null");

		if (visited.add(element)) {
			try {
				// Start searching within locally declared annotations
				List<Annotation> declaredAnnotations = Arrays.asList(element.getDeclaredAnnotations());
				T result = searchWithGetSemanticsInAnnotations(element, declaredAnnotations,
						annotationType, annotationName, containerType, processor, visited, metaDepth);
				if (result != null) {
					return result;
				}

				if (element instanceof Class) { // otherwise getAnnotations doesn't return anything new
					List<Annotation> inheritedAnnotations = new ArrayList<Annotation>();
					for (Annotation annotation : element.getAnnotations()) {
						if (!declaredAnnotations.contains(annotation)) {
							inheritedAnnotations.add(annotation);
						}
					}

					// Continue searching within inherited annotations
					result = searchWithGetSemanticsInAnnotations(element, inheritedAnnotations,
							annotationType, annotationName, containerType, processor, visited, metaDepth);
					if (result != null) {
						return result;
					}
				}
			}
			catch (Exception ex) {
				AnnotationUtils.handleIntrospectionFailure(element, ex);
			}
		}

		return null;
	}

	/**
	 * This method is invoked by {@link #searchWithGetSemantics} to perform
	 * the actual search within the supplied list of annotations.
	 * <p>This method should be invoked first with locally declared annotations
	 * and then subsequently with inherited annotations, thereby allowing
	 * local annotations to take precedence over inherited annotations.
	 * <p>The {@code metaDepth} parameter is explained in the
	 * {@link Processor#process process()} method of the {@link Processor} API.
	 * <p>
	 * 该方法由{@link #searchWithGetSemantics}调用,以在提供的注释列表中执行实际搜索<p>此方法应首先使用本地声明的注释进行调用,然后再使用继承的注释,从而允许本地注释优先于继承
	 * 的注释<p>在{@link Processor} API的{@link Processor#process process()}方法中说明了{@code metaDepth}参数。
	 * 
	 * 
	 * @param element the element that is annotated with the supplied
	 * annotations, used for contextual logging; may be {@code null} if unknown
	 * @param annotations the annotations to search in
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param containerType the type of the container that holds repeatable
	 * annotations, or {@code null} if the annotation is not repeatable
	 * @param processor the processor to delegate to
	 * @param visited the set of annotated elements that have already been visited
	 * @param metaDepth the meta-depth of the annotation
	 * @return the result of the processor, potentially {@code null}
	 * @since 4.2
	 */
	private static <T> T searchWithGetSemanticsInAnnotations(AnnotatedElement element,
			List<Annotation> annotations, Class<? extends Annotation> annotationType, String annotationName,
			Class<? extends Annotation> containerType, Processor<T> processor, Set<AnnotatedElement> visited,
			int metaDepth) {

		// Search in annotations
		for (Annotation annotation : annotations) {
			if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotation)) {
				if (annotation.annotationType() == annotationType ||
						annotation.annotationType().getName().equals(annotationName) ||
						processor.alwaysProcesses()) {
					T result = processor.process(element, annotation, metaDepth);
					if (result != null) {
						if (processor.aggregates() && metaDepth == 0) {
							processor.getAggregatedResults().add(result);
						}
						else {
							return result;
						}
					}
				}
				// Repeatable annotations in container?
				else if (annotation.annotationType() == containerType) {
					for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
						T result = processor.process(element, contained, metaDepth);
						if (result != null) {
							// No need to post-process since repeatable annotations within a
							// container cannot be composed annotations.
							processor.getAggregatedResults().add(result);
						}
					}
				}
			}
		}

		// Recursively search in meta-annotations
		for (Annotation annotation : annotations) {
			if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotation)) {
				T result = searchWithGetSemantics(annotation.annotationType(), annotationType,
						annotationName, containerType, processor, visited, metaDepth + 1);
				if (result != null) {
					processor.postProcess(element, annotation, result);
					if (processor.aggregates() && metaDepth == 0) {
						processor.getAggregatedResults().add(result);
					}
					else {
						return result;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following
	 * <em>find semantics</em>.
	 * <p>
	 *  在指定的{@code元素}上搜索指定的{@code annotationName}或{@code annotationType}的注释,后跟<em>查找语义</em>
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param processor the processor to delegate to
	 * @return the result of the processor, potentially {@code null}
	 * @since 4.2
	 */
	private static <T> T searchWithFindSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Processor<T> processor) {

		return searchWithFindSemantics(element, annotationType, annotationName, null, processor);
	}

	/**
	 * Search for annotations of the specified {@code annotationName} or
	 * {@code annotationType} on the specified {@code element}, following
	 * <em>find semantics</em>.
	 * <p>
	 *  在指定的{@code元素}上搜索指定的{@code annotationName}或{@code annotationType}的注释,后跟<em>查找语义</em>
	 * 
	 * 
	 * @param element the annotated element
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param containerType the type of the container that holds repeatable
	 * annotations, or {@code null} if the annotation is not repeatable
	 * @param processor the processor to delegate to
	 * @return the result of the processor, potentially {@code null}
	 * @since 4.3
	 */
	private static <T> T searchWithFindSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor) {

		if (containerType != null && !processor.aggregates()) {
			throw new IllegalArgumentException(
				"Searches for repeatable annotations must supply an aggregating Processor");
		}

		try {
			return searchWithFindSemantics(
					element, annotationType, annotationName, containerType, processor, new HashSet<AnnotatedElement>(), 0);
		}
		catch (Throwable ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			throw new IllegalStateException("Failed to introspect annotations on " + element, ex);
		}
	}

	/**
	 * Perform the search algorithm for the {@link #searchWithFindSemantics}
	 * method, avoiding endless recursion by tracking which annotated elements
	 * have already been <em>visited</em>.
	 * <p>The {@code metaDepth} parameter is explained in the
	 * {@link Processor#process process()} method of the {@link Processor} API.
	 * <p>
	 * 执行{@link #searchWithFindSemantics}方法的搜索算法,通过跟踪哪些注释元素已被访问避免无休止的递归</em> <p> {@code metaDepth}参数在{@link {@link Processor}
	 *  API的Processor#process process()}方法。
	 * 
	 * 
	 * @param element the annotated element; never {@code null}
	 * @param annotationType the annotation type to find
	 * @param annotationName the fully qualified class name of the annotation
	 * type to find (as an alternative to {@code annotationType})
	 * @param containerType the type of the container that holds repeatable
	 * annotations, or {@code null} if the annotation is not repeatable
	 * @param processor the processor to delegate to
	 * @param visited the set of annotated elements that have already been visited
	 * @param metaDepth the meta-depth of the annotation
	 * @return the result of the processor, potentially {@code null}
	 * @since 4.2
	 */
	private static <T> T searchWithFindSemantics(AnnotatedElement element, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth) {

		Assert.notNull(element, "AnnotatedElement must not be null");

		if (visited.add(element)) {
			try {
				// Locally declared annotations (ignoring @Inherited)
				Annotation[] annotations = element.getDeclaredAnnotations();
				List<T> aggregatedResults = (processor.aggregates() ? new ArrayList<T>() : null);

				// Search in local annotations
				for (Annotation annotation : annotations) {
					if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotation)) {
						if (annotation.annotationType() == annotationType
								|| annotation.annotationType().getName().equals(annotationName)
								|| processor.alwaysProcesses()) {

							T result = processor.process(element, annotation, metaDepth);
							if (result != null) {
								if (processor.aggregates() && metaDepth == 0) {
									aggregatedResults.add(result);
								}
								else {
									return result;
								}
							}
						}
						// Repeatable annotations in container?
						else if (annotation.annotationType() == containerType) {
							for (Annotation contained : getRawAnnotationsFromContainer(element, annotation)) {
								T result = processor.process(element, contained, metaDepth);
								if (result != null) {
									// No need to post-process since repeatable annotations within a
									// container cannot be composed annotations.
									aggregatedResults.add(result);
								}
							}
						}
					}
				}

				// Search in meta annotations on local annotations
				for (Annotation annotation : annotations) {
					if (!AnnotationUtils.isInJavaLangAnnotationPackage(annotation)) {
						T result = searchWithFindSemantics(annotation.annotationType(), annotationType, annotationName,
								containerType, processor, visited, metaDepth + 1);
						if (result != null) {
							processor.postProcess(annotation.annotationType(), annotation, result);
							if (processor.aggregates() && metaDepth == 0) {
								aggregatedResults.add(result);
							}
							else {
								return result;
							}
						}
					}
				}

				if (processor.aggregates()) {
					// Prepend to support top-down ordering within class hierarchies
					processor.getAggregatedResults().addAll(0, aggregatedResults);
				}

				if (element instanceof Method) {
					Method method = (Method) element;

					// Search on possibly bridged method
					Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
					T result = searchWithFindSemantics(resolvedMethod, annotationType, annotationName, containerType,
							processor, visited, metaDepth);
					if (result != null) {
						return result;
					}

					// Search on methods in interfaces declared locally
					Class<?>[] ifcs = method.getDeclaringClass().getInterfaces();
					result = searchOnInterfaces(method, annotationType, annotationName, containerType, processor,
							visited, metaDepth, ifcs);
					if (result != null) {
						return result;
					}

					// Search on methods in class hierarchy and interface hierarchy
					Class<?> clazz = method.getDeclaringClass();
					while (true) {
						clazz = clazz.getSuperclass();
						if (clazz == null || Object.class == clazz) {
							break;
						}

						try {
							Method equivalentMethod = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
							Method resolvedEquivalentMethod = BridgeMethodResolver.findBridgedMethod(equivalentMethod);
							result = searchWithFindSemantics(resolvedEquivalentMethod, annotationType, annotationName,
									containerType, processor, visited, metaDepth);
							if (result != null) {
								return result;
							}
						}
						catch (NoSuchMethodException ex) {
							// No equivalent method found
						}

						// Search on interfaces declared on superclass
						result = searchOnInterfaces(method, annotationType, annotationName, containerType, processor,
								visited, metaDepth, clazz.getInterfaces());
						if (result != null) {
							return result;
						}
					}
				}
				else if (element instanceof Class) {
					Class<?> clazz = (Class<?>) element;

					// Search on interfaces
					for (Class<?> ifc : clazz.getInterfaces()) {
						T result = searchWithFindSemantics(ifc, annotationType, annotationName, containerType,
								processor, visited, metaDepth);
						if (result != null) {
							return result;
						}
					}

					// Search on superclass
					Class<?> superclass = clazz.getSuperclass();
					if (superclass != null && Object.class != superclass) {
						T result = searchWithFindSemantics(superclass, annotationType, annotationName, containerType,
								processor, visited, metaDepth);
						if (result != null) {
							return result;
						}
					}
				}
			}
			catch (Exception ex) {
				AnnotationUtils.handleIntrospectionFailure(element, ex);
			}
		}
		return null;
	}

	private static <T> T searchOnInterfaces(Method method, Class<? extends Annotation> annotationType,
			String annotationName, Class<? extends Annotation> containerType, Processor<T> processor,
			Set<AnnotatedElement> visited, int metaDepth, Class<?>[] ifcs) {

		for (Class<?> iface : ifcs) {
			if (AnnotationUtils.isInterfaceWithAnnotatedMethods(iface)) {
				try {
					Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
					T result = searchWithFindSemantics(equivalentMethod, annotationType, annotationName, containerType,
							processor, visited, metaDepth);
					if (result != null) {
						return result;
					}
				}
				catch (NoSuchMethodException ex) {
					// Skip this interface - it doesn't have the method...
				}
			}
		}

		return null;
	}

	/**
	 * Get the array of raw (unsynthesized) annotations from the {@code value}
	 * attribute of the supplied repeatable annotation {@code container}.
	 * <p>
	 *  从提供的可重复注释{@code容器}的{@code值}属性获取原始(未合成)注释的数组
	 * 
	 * 
	 * @since 4.3
	 */
	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A[] getRawAnnotationsFromContainer(AnnotatedElement element,
			Annotation container) {

		try {
			return (A[]) AnnotationUtils.getValue(container);
		}
		catch (Exception ex) {
			AnnotationUtils.handleIntrospectionFailure(element, ex);
		}
		// Unable to read value from repeating annotation container -> ignore it.
		return (A[]) EMPTY_ANNOTATION_ARRAY;
	}

	/**
	 * Resolve the container type for the supplied repeatable {@code annotationType}.
	 * <p>Delegates to {@link AnnotationUtils#resolveContainerAnnotationType(Class)}.
	 * <p>
	 *  解决提供的可重复{@code注释类型} <p>的代码到{@link AnnotationUtils#resolveContainerAnnotationType(Class)}的容器类型}
	 * 
	 * 
	 * @param annotationType the annotation type to resolve the container for
	 * @return the container type; never {@code null}
	 * @throws IllegalArgumentException if the container type cannot be resolved
	 * @since 4.3
	 */
	private static Class<? extends Annotation> resolveContainerType(Class<? extends Annotation> annotationType) {
		Class<? extends Annotation> containerType = AnnotationUtils.resolveContainerAnnotationType(annotationType);
		if (containerType == null) {
			throw new IllegalArgumentException(
				"annotationType must be a repeatable annotation: failed to resolve container type for "
						+ annotationType.getName());
		}
		return containerType;
	}

	/**
	 * Validate that the supplied {@code containerType} is a proper container
	 * annotation for the supplied repeatable {@code annotationType} (i.e.,
	 * that it declares a {@code value} attribute that holds an array of the
	 * {@code annotationType}).
	 * <p>
	 * 验证所提供的{@code containerType}是提供的可重复{@code注释类型}的适当容器注释(即,它声明一个{@code值}属性,其中包含{@code annotationType}的数组)
	 * 。
	 * 
	 * 
	 * @since 4.3
	 * @throws AnnotationConfigurationException if the supplied {@code containerType}
	 * is not a valid container annotation for the supplied {@code annotationType}
	 */
	private static void validateContainerType(Class<? extends Annotation> annotationType,
			Class<? extends Annotation> containerType) {

		try {
			Method method = containerType.getDeclaredMethod(AnnotationUtils.VALUE);
			Class<?> returnType = method.getReturnType();
			if (!returnType.isArray() || returnType.getComponentType() != annotationType) {
				String msg = String.format(
					"Container type [%s] must declare a 'value' attribute for an array of type [%s]",
					containerType.getName(), annotationType.getName());
				throw new AnnotationConfigurationException(msg);
			}
		}
		catch (Exception ex) {
			AnnotationUtils.rethrowAnnotationConfigurationException(ex);
			String msg = String.format("Invalid declaration of container type [%s] for repeatable annotation [%s]",
				containerType.getName(), annotationType.getName());
			throw new AnnotationConfigurationException(msg, ex);
		}
	}

	/**
	/* <p>
	/* 
	 * @since 4.3
	 */
	private static <A extends Annotation> Set<A> postProcessAndSynthesizeAggregatedResults(AnnotatedElement element,
			Class<A> annotationType, List<AnnotationAttributes> aggregatedResults) {

		Set<A> annotations = new LinkedHashSet<A>();
		for (AnnotationAttributes attributes : aggregatedResults) {
			AnnotationUtils.postProcessAnnotationAttributes(element, attributes, false, false);
			annotations.add(AnnotationUtils.synthesizeAnnotation(attributes, annotationType, element));
		}
		return annotations;
	}


	/**
	 * Callback interface that is used to process annotations during a search.
	 * <p>Depending on the use case, a processor may choose to
	 * {@linkplain #process} a single target annotation, multiple target
	 * annotations, or all annotations discovered by the currently executing
	 * search. The term "target" in this context refers to a matching
	 * annotation (i.e., a specific annotation type that was found during
	 * the search).
	 * <p>Returning a non-null value from the {@link #process}
	 * method instructs the search algorithm to stop searching further;
	 * whereas, returning {@code null} from the {@link #process} method
	 * instructs the search algorithm to continue searching for additional
	 * annotations. One exception to this rule applies to processors
	 * that {@linkplain #aggregates aggregate} results. If an aggregating
	 * processor returns a non-null value, that value will be added to the
	 * list of {@linkplain #getAggregatedResults aggregated results}
	 * and the search algorithm will continue.
	 * <p>Processors can optionally {@linkplain #postProcess post-process}
	 * the result of the {@link #process} method as the search algorithm
	 * goes back down the annotation hierarchy from an invocation of
	 * {@link #process} that returned a non-null value down to the
	 * {@link AnnotatedElement} that was supplied as the starting point to
	 * the search algorithm.
	 * <p>
	 * 用于在搜索期间处理注释的回调接口<p>根据用例,处理器可以选择{@linkplain #process}单个目标注释,多个目标注释或当前正在执行的搜索发现的所有注释。
	 * 在这种情况下,术语"目标"是指匹配注释(即,在搜索期间发现的特定注释类型)<p>从{@link #process}方法返回非空值指示搜索算法停止进一步搜索而从{@link #process}方法返回的{@code null}
	 * 指示搜索算法继续搜索其他注释。
	 * 用于在搜索期间处理注释的回调接口<p>根据用例,处理器可以选择{@linkplain #process}单个目标注释,多个目标注释或当前正在执行的搜索发现的所有注释。
	 * 此规则的一个例外适用于{@linkplain #aggregates aggregate}结果的处理器如果聚合处理器返回非空值,该值将被添加到{@linkplain #getAggregatedResults聚合结果}
	 * 列表中,搜索算法将继续<p>处理器可以选择{@linkplain #postProcess post-process}当搜索算法从{@link #process}的调用返回注释分层结果时,{@link #process}
	 * 方法的结果返回非空值,直到{@link AnnotatedElement}为起点搜索算法。
	 * 用于在搜索期间处理注释的回调接口<p>根据用例,处理器可以选择{@linkplain #process}单个目标注释,多个目标注释或当前正在执行的搜索发现的所有注释。
	 * 
	 * 
	 * @param <T> the type of result returned by the processor
	 */
	private interface Processor<T> {

		/**
		 * Process the supplied annotation.
		 * <p>The supplied annotation will be an actual target annotation
		 * that has been found by the search algorithm, unless this processor
		 * is configured to {@linkplain #alwaysProcesses always process}
		 * annotations in which case it may be some other annotation within an
		 * annotation hierarchy. In the latter case, the {@code metaDepth}
		 * will have a value greater than {@code 0}. In any case, it is
		 * up to concrete implementations of this method to decide what to
		 * do with the supplied annotation.
		 * <p>The {@code metaDepth} parameter represents the depth of the
		 * annotation relative to the first annotated element in the
		 * annotation hierarchy. For example, an annotation that is
		 * <em>present</em> on a non-annotation element will have a depth
		 * of 0; a meta-annotation will have a depth of 1; and a
		 * meta-meta-annotation will have a depth of 2; etc.
		 * <p>
		 * 处理提供的注释<p>提供的注释将是搜索算法找到的实际目标注释,除非该处理器配置为{@linkplain #alwaysProcesses始终处理}注释,在这种情况下,它可能是其他注释注释层次结构在后一种
		 * 情况下,{@code metaDepth}将具有大于{@code 0}的值。
		 * 在任何情况下,由该方法的具体实现决定如何处理提供的注释<p> {@code metaDepth}参数表示注释相对于注释层次结构中第一个注释元素的深度例如,在非注释元素上<em>存在</em>的注释的深度
		 * 为0;元注释的深度为1;元元注释的深度为2;等等。
		 * 
		 * 
		 * @param annotatedElement the element that is annotated with the
		 * supplied annotation, used for contextual logging; may be
		 * {@code null} if unknown
		 * @param annotation the annotation to process
		 * @param metaDepth the meta-depth of the annotation
		 * @return the result of the processing, or {@code null} to continue
		 * searching for additional annotations
		 */
		T process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth);

		/**
		 * Post-process the result returned by the {@link #process} method.
		 * <p>The {@code annotation} supplied to this method is an annotation
		 * that is present in the annotation hierarchy, between the initial
		 * {@link AnnotatedElement} and an invocation of {@link #process}
		 * that returned a non-null value.
		 * <p>
		 * 后处理{@link #process}方法返回的结果<p>提供给此方法的{@code注释}是注释层次结构中的注释,位于初始{@link AnnotatedElement}和调用之间的{@link #process}
		 * 返回非空值。
		 * 
		 * 
		 * @param annotatedElement the element that is annotated with the
		 * supplied annotation, used for contextual logging; may be
		 * {@code null} if unknown
		 * @param annotation the annotation to post-process
		 * @param result the result to post-process
		 */
		void postProcess(AnnotatedElement annotatedElement, Annotation annotation, T result);

		/**
		 * Determine if this processor always processes annotations regardless of
		 * whether or not the target annotation has been found.
		 * <p>
		 *  确定此处理器是否始终处理注释,而不管目标注释是否已被找到
		 * 
		 * 
		 * @return {@code true} if this processor always processes annotations
		 * @since 4.3
		 */
		boolean alwaysProcesses();

		/**
		 * Determine if this processor aggregates the results returned by {@link #process}.
		 * <p>If this method returns {@code true}, then {@link #getAggregatedResults()}
		 * must return a non-null value.
		 * <p>
		 *  确定此处理器是否聚合{@link #process} <p>返回的结果如果此方法返回{@code true},则{@link #getAggregatedResults()}必须返回非空值
		 * 
		 * 
		 * @return {@code true} if this processor supports aggregated results
		 * @see #getAggregatedResults
		 * @since 4.3
		 */
		boolean aggregates();

		/**
		 * Get the list of results aggregated by this processor.
		 * <p>NOTE: the processor does <strong>not</strong> aggregate the results
		 * itself. Rather, the search algorithm that uses this processor is
		 * responsible for asking this processor if it {@link #aggregates} results
		 * and then adding the post-processed results to the list returned by this
		 * method.
		 * <p>
		 * 获取此处理器聚合的结果列表<p>注意：处理器执行<strong>不</strong>聚合结果本身而不是使用此处理器的搜索算法负责询问该处理器是否为{@link#聚合}结果,然后将后处理结果添加到此方法返
		 * 回的列表中。
		 * 
		 * 
		 * @return the list of results aggregated by this processor; never
		 * {@code null} unless {@link #aggregates} returns {@code false}
		 * @see #aggregates
		 * @since 4.3
		 */
		List<T> getAggregatedResults();
	}


	/**
	 * {@link Processor} that {@linkplain #process(AnnotatedElement, Annotation, int)
	 * processes} annotations but does not {@linkplain #postProcess post-process} or
	 * {@linkplain #aggregates aggregate} results.
	 * <p>
	 *  {@link Processor} {@linkplain #process(AnnotatedElement,Annotation,int)处理}注释,但{@linkplain #postProcess post-process}
	 * 或{@linkplain #aggregates aggregate}结果。
	 * 
	 * 
	 * @since 4.2
	 */
	private abstract static class SimpleAnnotationProcessor<T> implements Processor<T> {

		private final boolean alwaysProcesses;

		public SimpleAnnotationProcessor() {
			this(false);
		}

		public SimpleAnnotationProcessor(boolean alwaysProcesses) {
			this.alwaysProcesses = alwaysProcesses;
		}

		@Override
		public final boolean alwaysProcesses() {
			return this.alwaysProcesses;
		}

		@Override
		public final void postProcess(AnnotatedElement annotatedElement, Annotation annotation, T result) {
			// no-op
		}

		@Override
		public final boolean aggregates() {
			return false;
		}

		@Override
		public final List<T> getAggregatedResults() {
			throw new UnsupportedOperationException("SimpleAnnotationProcessor does not support aggregated results");
		}
	}


	/**
	 * {@link SimpleAnnotationProcessor} that always returns {@link Boolean#TRUE} when
	 * asked to {@linkplain #process(AnnotatedElement, Annotation, int) process} an
	 * annotation.
	 * <p>
	 *  {@link SimpleAnnotationProcessor}当被要求{@linkplain #process(AnnotatedElement,Annotation,int)process)时,总是返回{@link Boolean#TRUE}
	 * 一个注释。
	 * 
	 * 
	 * @since 4.3
	 */
	static class AlwaysTrueBooleanAnnotationProcessor extends SimpleAnnotationProcessor<Boolean> {

		@Override
		public final Boolean process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
			return Boolean.TRUE;
		}
	}


	/**
	 * {@link Processor} that gets the {@code AnnotationAttributes} for the
	 * target annotation during the {@link #process} phase and then merges
	 * annotation attributes from lower levels in the annotation hierarchy
	 * during the {@link #postProcess} phase.
	 * <p>A {@code MergedAnnotationAttributesProcessor} may optionally be
	 * configured to {@linkplain #aggregates aggregate} results.
	 * <p>
	 * {@link Processor}在{@link #process}阶段中获取目标注释的{@code AnnotationAttributes},然后在{@link #postProcess}阶段<p>
	 * 中将注释属性从注释层次结构中的较低级别合并可以将{@code MergedAnnotationAttributesProcessor}配置为{@linkplain #aggregates aggregate}
	 * 
	 * @since 4.2
	 * @see AnnotationUtils#retrieveAnnotationAttributes
	 * @see AnnotationUtils#postProcessAnnotationAttributes
	 */
	private static class MergedAnnotationAttributesProcessor implements Processor<AnnotationAttributes> {

		private final boolean classValuesAsString;

		private final boolean nestedAnnotationsAsMap;

		private final boolean aggregates;

		private final List<AnnotationAttributes> aggregatedResults;

		MergedAnnotationAttributesProcessor() {
			this(false, false, false);
		}

		MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap) {
			this(classValuesAsString, nestedAnnotationsAsMap, false);
		}

		MergedAnnotationAttributesProcessor(boolean classValuesAsString, boolean nestedAnnotationsAsMap,
				boolean aggregates) {

			this.classValuesAsString = classValuesAsString;
			this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
			this.aggregates = aggregates;
			this.aggregatedResults = (aggregates ? new ArrayList<AnnotationAttributes>() : null);
		}

		@Override
		public boolean alwaysProcesses() {
			return false;
		}

		@Override
		public boolean aggregates() {
			return this.aggregates;
		}

		@Override
		public List<AnnotationAttributes> getAggregatedResults() {
			return this.aggregatedResults;
		}

		@Override
		public AnnotationAttributes process(AnnotatedElement annotatedElement, Annotation annotation, int metaDepth) {
			return AnnotationUtils.retrieveAnnotationAttributes(annotatedElement, annotation,
					this.classValuesAsString, this.nestedAnnotationsAsMap);
		}

		@Override
		public void postProcess(AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes) {
			annotation = AnnotationUtils.synthesizeAnnotation(annotation, element);
			Class<? extends Annotation> targetAnnotationType = attributes.annotationType();

			// Track which attribute values have already been replaced so that we can short
			// circuit the search algorithms.
			Set<String> valuesAlreadyReplaced = new HashSet<String>();

			for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotation.annotationType())) {
				String attributeName = attributeMethod.getName();
				String attributeOverrideName = AnnotationUtils.getAttributeOverrideName(attributeMethod, targetAnnotationType);

				// Explicit annotation attribute override declared via @AliasFor
				if (attributeOverrideName != null) {
					if (valuesAlreadyReplaced.contains(attributeOverrideName)) {
						continue;
					}

					List<String> targetAttributeNames = new ArrayList<String>();
					targetAttributeNames.add(attributeOverrideName);
					valuesAlreadyReplaced.add(attributeOverrideName);

					// Ensure all aliased attributes in the target annotation are overridden. (SPR-14069)
					List<String> aliases = AnnotationUtils.getAttributeAliasMap(targetAnnotationType).get(attributeOverrideName);
					if (aliases != null) {
						for (String alias : aliases) {
							if (!valuesAlreadyReplaced.contains(alias)) {
								targetAttributeNames.add(alias);
								valuesAlreadyReplaced.add(alias);
							}
						}
					}

					overrideAttributes(element, annotation, attributes, attributeName, targetAttributeNames);
				}
				// Implicit annotation attribute override based on convention
				else if (!AnnotationUtils.VALUE.equals(attributeName) && attributes.containsKey(attributeName)) {
					overrideAttribute(element, annotation, attributes, attributeName, attributeName);
				}
			}
		}

		private void overrideAttributes(AnnotatedElement element, Annotation annotation,
				AnnotationAttributes attributes, String sourceAttributeName, List<String> targetAttributeNames) {

			Object adaptedValue = getAdaptedValue(element, annotation, sourceAttributeName);

			for (String targetAttributeName : targetAttributeNames) {
				attributes.put(targetAttributeName, adaptedValue);
			}
		}

		private void overrideAttribute(AnnotatedElement element, Annotation annotation, AnnotationAttributes attributes,
				String sourceAttributeName, String targetAttributeName) {

			attributes.put(targetAttributeName, getAdaptedValue(element, annotation, sourceAttributeName));
		}

		private Object getAdaptedValue(AnnotatedElement element, Annotation annotation, String sourceAttributeName) {
			Object value = AnnotationUtils.getValue(annotation, sourceAttributeName);
			return AnnotationUtils.adaptValue(element, value, this.classValuesAsString, this.nestedAnnotationsAsMap);
		}
	}

}
