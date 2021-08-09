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

package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @AliasFor} is an annotation that is used to declare aliases for
 * annotation attributes.
 *
 * <h3>Usage Scenarios</h3>
 * <ul>
 * <li><strong>Explicit aliases within an annotation</strong>: within a single
 * annotation, {@code @AliasFor} can be declared on a pair of attributes to
 * signal that they are interchangeable aliases for each other.</li>
 * <li><strong>Explicit alias for attribute in meta-annotation</strong>: if the
 * {@link #annotation} attribute of {@code @AliasFor} is set to a different
 * annotation than the one that declares it, the {@link #attribute} is
 * interpreted as an alias for an attribute in a meta-annotation (i.e., an
 * explicit meta-annotation attribute override). This enables fine-grained
 * control over exactly which attributes are overridden within an annotation
 * hierarchy. In fact, with {@code @AliasFor} it is even possible to declare
 * an alias for the {@code value} attribute of a meta-annotation.</li>
 * <li><strong>Implicit aliases within an annotation</strong>: if one or
 * more attributes within an annotation are declared as attribute overrides
 * for the same meta-annotation attribute (either directly or transitively),
 * those attributes will be treated as a set of <em>implicit</em> aliases
 * for each other, resulting in behavior analogous to that for explicit
 * aliases within an annotation.</li>
 * </ul>
 *
 * <h3>Usage Requirements</h3>
 * <p>Like with any annotation in Java, the mere presence of {@code @AliasFor}
 * on its own will not enforce alias semantics. For alias semantics to be
 * enforced, annotations must be <em>loaded</em> via the utility methods in
 * {@link AnnotationUtils}. Behind the scenes, Spring will <em>synthesize</em>
 * an annotation by wrapping it in a dynamic proxy that transparently enforces
 * <em>attribute alias</em> semantics for annotation attributes that are
 * annotated with {@code @AliasFor}. Similarly, {@link AnnotatedElementUtils}
 * supports explicit meta-annotation attribute overrides when {@code @AliasFor}
 * is used within an annotation hierarchy. Typically you will not need to
 * manually synthesize annotations on your own since Spring will do that for
 * you transparently when looking up annotations on Spring-managed components.
 *
 * <h3>Implementation Requirements</h3>
 * <ul>
 * <li><strong>Explicit aliases within an annotation</strong>:
 * <ol>
 * <li>Each attribute that makes up an aliased pair must be annotated with
 * {@code @AliasFor}, and either {@link #attribute} or {@link #value} must
 * reference the <em>other</em> attribute in the pair.</li>
 * <li>Aliased attributes must declare the same return type.</li>
 * <li>Aliased attributes must declare a default value.</li>
 * <li>Aliased attributes must declare the same default value.</li>
 * <li>{@link #annotation} should not be declared.</li>
 * </ol>
 * </li>
 * <li><strong>Explicit alias for attribute in meta-annotation</strong>:
 * <ol>
 * <li>The attribute that is an alias for an attribute in a meta-annotation
 * must be annotated with {@code @AliasFor}, and {@link #attribute} must
 * reference the attribute in the meta-annotation.</li>
 * <li>Aliased attributes must declare the same return type.</li>
 * <li>{@link #annotation} must reference the meta-annotation.</li>
 * <li>The referenced meta-annotation must be <em>meta-present</em> on the
 * annotation class that declares {@code @AliasFor}.</li>
 * </ol>
 * </li>
 * <li><strong>Implicit aliases within an annotation</strong>:
 * <ol>
 * <li>Each attribute that belongs to a set of implicit aliases must be
 * annotated with {@code @AliasFor}, and {@link #attribute} must reference
 * the same attribute in the same meta-annotation (either directly or
 * transitively via other explicit meta-annotation attribute overrides
 * within the annotation hierarchy).</li>
 * <li>Aliased attributes must declare the same return type.</li>
 * <li>Aliased attributes must declare a default value.</li>
 * <li>Aliased attributes must declare the same default value.</li>
 * <li>{@link #annotation} must reference an appropriate meta-annotation.</li>
 * <li>The referenced meta-annotation must be <em>meta-present</em> on the
 * annotation class that declares {@code @AliasFor}.</li>
 * </ol>
 * </li>
 * </ul>
 *
 * <h3>Example: Explicit Aliases within an Annotation</h3>
 * <p>In {@code @ContextConfiguration}, {@code value} and {@code locations}
 * are explicit aliases for each other.
 *
 * <pre class="code"> public &#064;interface ContextConfiguration {
 *
 *    &#064;AliasFor("locations")
 *    String[] value() default {};
 *
 *    &#064;AliasFor("value")
 *    String[] locations() default {};
 *
 *    // ...
 * }</pre>
 *
 * <h3>Example: Explicit Alias for Attribute in Meta-annotation</h3>
 * <p>In {@code @XmlTestConfig}, {@code xmlFiles} is an explicit alias for
 * {@code locations} in {@code @ContextConfiguration}. In other words,
 * {@code xmlFiles} overrides the {@code locations} attribute in
 * {@code @ContextConfiguration}.
 *
 * <pre class="code"> &#064;ContextConfiguration
 * public &#064;interface XmlTestConfig {
 *
 *    &#064;AliasFor(annotation = ContextConfiguration.class, attribute = "locations")
 *    String[] xmlFiles();
 * }</pre>
 *
 * <h3>Example: Implicit Aliases within an Annotation</h3>
 * <p>In {@code @MyTestConfig}, {@code value}, {@code groovyScripts}, and
 * {@code xmlFiles} are all explicit meta-annotation attribute overrides for
 * the {@code locations} attribute in {@code @ContextConfiguration}. These
 * three attributes are therefore also implicit aliases for each other.
 *
 * <pre class="code"> &#064;ContextConfiguration
 * public &#064;interface MyTestConfig {
 *
 *    &#064;AliasFor(annotation = ContextConfiguration.class, attribute = "locations")
 *    String[] value() default {};
 *
 *    &#064;AliasFor(annotation = ContextConfiguration.class, attribute = "locations")
 *    String[] groovyScripts() default {};
 *
 *    &#064;AliasFor(annotation = ContextConfiguration.class, attribute = "locations")
 *    String[] xmlFiles() default {};
 * }</pre>
 *
 * <h3>Example: Transitive Implicit Aliases within an Annotation</h3>
 * <p>In {@code @GroovyOrXmlTestConfig}, {@code groovy} is an explicit
 * override for the {@code groovyScripts} attribute in {@code @MyTestConfig};
 * whereas, {@code xml} is an explicit override for the {@code locations}
 * attribute in {@code @ContextConfiguration}. Furthermore, {@code groovy}
 * and {@code xml} are transitive implicit aliases for each other, since they
 * both effectively override the {@code locations} attribute in
 * {@code @ContextConfiguration}.
 *
 * <pre class="code"> &#064;MyTestConfig
 * public &#064;interface GroovyOrXmlTestConfig {
 *
 *    &#064;AliasFor(annotation = MyTestConfig.class, attribute = "groovyScripts")
 *    String[] groovy() default {};
 *
 *    &#064;AliasFor(annotation = ContextConfiguration.class, attribute = "locations")
 *    String[] xml() default {};
 * }</pre>
 *
 * <h3>Spring Annotations Supporting Attribute Aliases</h3>
 * <p>As of Spring Framework 4.2, several annotations within core Spring
 * have been updated to use {@code @AliasFor} to configure their internal
 * attribute aliases. Consult the Javadoc for individual annotations as well
 * as the reference manual for details.
 *
 * <p>
 *  {@code @AliasFor}是一个注释,用于为注释属性声明别名
 * 
 *  <h3>使用场景</h3>
 * <ul>
 * <li> <strong>注释中的显式别名</strong>：在单个注释中,{@code @AliasFor}可以在一对属性上声明,表示它们是互为可互换的别名</li> li> <strong>元注释中
 * 的属性的显式别名</strong>：如果{@code @AliasFor}的{@link #annotation}属性设置为与声明的不同的注释,则{@链接#attribute}被解释为元注释中的属性的别
 * 名(即,显式元注释属性覆盖)这使得精确控制在注释层次结构中覆盖哪些属性的事实上,使用{@代码@AliasFor}甚至可以为元注释的{@code值}属性声明一个别名</li> <li> <strong>注
 * 释中的隐式别名</strong>：如果注释中的一个或多个属性被声明为相同元注释属性(直接或传递性)的属性覆盖,则这些属性将被视为一组<em>隐式</em>别名,导致类似于注释中显式别名的行为</li>。
 * </ul>
 * 
 * <h3>使用要求</h3> <p>与Java中的任何注释一样,仅仅存在{@code @AliasFor}就不会强制执行别名语义要执行别名语义,注释必须是<em>通过{@link AnnotationUtils}
 * 中的实用程序方法加载</em>后,Spring将通过将注释包含在一个透明地强制执行<em>属性别名</em>的动态代理中来</em>使用{@code @AliasFor}注释的注释属性的语义类似地,当{@code @AliasFor}
 * 在注释层次结构中使用时,{@link AnnotatedElementUtils}支持显式元注释属性覆盖通常,您不需要自己手动合成注释,因为Spring在Spring管理的组件上查找注释时会透明地为您做
 * 这些注释。
 * 
 * <h3>实施要求</h3>
 * <ul>
 *  <li> <strong>注释中的显式别名</strong>：
 * <ol>
 *  <li>组成别名对的每个属性必须用{@code @AliasFor}注释,{@link #attribute}或{@link #value}必须引用<em>其他</em>属性别对</li> <li>别
 * 名属性必须声明相同的返回类型</li> <li>别名属性必须声明默认值</li> <li>别名属性必须声明相同的默认值</li> <li> {@ link #annotation}不应声明为</li>。
 * </ol>
 * </li>
 *  <li> <strong>元注释中的属性的显式别名</strong>：
 * <ol>
 * <li>元注释中属性的别名属性必须用{@code @AliasFor}注释,{@link #attribute}必须引用元注释中的属性</li> <li >别名的属性必须声明相同的返回类型</li> <li>
 *  {@ link #annotation}必须引用元注释</li> <li>引用的元注释必须是<em>元存在</em>在声明{@code @AliasFor} </li>的注释类上。
 * </ol>
 * </li>
 *  <li> <strong>注释中的隐式别名</strong>：
 * <ol>
 * 属于一组隐式别名的每个属性必须用{@code @AliasFor}注释,{@link #attribute}必须在同一个元注释中引用相同的属性(直接或通过其他明确的方式传递)别名属性必须声明相同的返回类
 * 型</li> <li>别名属性必须声明默认值</li> <li>别名属性必须声明相同的默认值</li> <li> {@ link #annotation}必须引用适当的元注释</li> <li>引用的元
 * 注释必须是<em>元存在</em>注释类声明{@code @AliasFor} </li>。
 * </ol>
 * </li>
 * </ul>
 * 
 * <h3>示例：注释中的显式别名</h3> <p>在{@code @ContextConfiguration}中,{@code值}和{@code位置}是彼此的显式别名
 * 
 *  <pre class ="code"> public @interface ContextConfiguration {
 * 
 *  @AliasFor("locations")String [] value()default {};
 * 
 *  @AliasFor("value")String [] locations()default {};
 * 
 *  //} </pre>
 * 
 * @author Sam Brannen
 * @since 4.2
 * @see AnnotatedElementUtils
 * @see AnnotationUtils
 * @see AnnotationUtils#synthesizeAnnotation(Annotation, java.lang.reflect.AnnotatedElement)
 * @see SynthesizedAnnotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AliasFor {

	/**
	 * Alias for {@link #attribute}.
	 * <p>Intended to be used instead of {@link #attribute} when {@link #annotation}
	 * is not declared &mdash; for example: {@code @AliasFor("value")} instead of
	 * {@code @AliasFor(attribute = "value")}.
	 * <p>
	 * 
	 *  <h3>示例：Meta-annotation中的属性的显式别名</h3> <p>在{@code @XmlTestConfig}中,{@code xmlFiles}是{@code @ContextConfiguration}
	 * 中{@code位置}的显式别名换句话说,{@code xmlFiles}会覆盖{@code @ContextConfiguration}中的{@code位置}属性。
	 * 
	 *  <pre class ="code"> @ContextConfiguration public @interface XmlTestConfig {
	 * 
	 * @AliasFor(annotation = ContextConfigurationclass,attribute ="locations")String [] xmlFiles(); } </PRE>
	 * 。
	 * 
	 *  <h3>示例：注释中的隐式别名</h3> <p>在{@code @MyTestConfig}中,{@code value},{@code groovyScripts}和{@code xmlFiles}
	 * 都是显式元注释属性覆盖{@code @ContextConfiguration}中的{@code位置}属性这三个属性因此也是彼此的隐式别名。
	 * 
	 *  <pre class ="code"> @ContextConfiguration public @interface MyTestConfig {
	 * 
	 *  @AliasFor(annotation = ContextConfigurationclass,attribute ="locations")String [] value()default {};
	 * 。
	 * 
	 *  @AliasFor(annotation = ContextConfigurationclass,attribute ="locations")String [] groovyScripts()def
	 * ault {};。
	 * 
	 * @AliasFor(annotation = ContextConfigurationclass,attribute ="locations")String [] xmlFiles()default {}
	 * ; } </PRE>。
	 * 
	 *  <h3>示例：注释中的传递隐式别名</h3> <p>在{@code @GroovyOrXmlTestConfig}中,{@code groovy}是{@code @MyTestConfig}中{@code groovyScripts}
	 */
	@AliasFor("attribute")
	String value() default "";

	/**
	 * The name of the attribute that <em>this</em> attribute is an alias for.
	 * <p>
	 * 属性的显式覆盖;而{@code xml}是{@code @ContextConfiguration}中{@code位置}属性的显式覆盖。
	 * 此外,{@code groovy}和{@code xml}是彼此的传递隐式别名,因为它们都有效地覆盖{@code @ContextConfiguration}中的{@code位置}属性。
	 * 
	 *  <pre class ="code"> @MyTestConfig public @interface GroovyOrXmlTestConfig {
	 * 
	 * @AliasFor(annotation = MyTestConfigclass,attribute ="groovyScripts")String [] groovy()default {};
	 * 
	 *  @AliasFor(annotation = ContextConfigurationclass,attribute ="locations")String [] xml()default {}; }
	 *  </PRE>。
	 * 
	 *  <h3>支持属性别名的Spring注释</h3> <p>从Spring Framework 42开始,内核Spring中的几个注释已更新,以使用{@code @AliasFor}配置其内部属性别名查看
	 * Javadoc中的各个注释为以及详细的参考手册。
	 * 
	 * @see #value
	 */
	@AliasFor("value")
	String attribute() default "";

	/**
	 * The type of annotation in which the aliased {@link #attribute} is declared.
	 * <p>Defaults to {@link Annotation}, implying that the aliased attribute is
	 * declared in the same annotation as <em>this</em> attribute.
	 * <p>
	 * 
	 */
	Class<? extends Annotation> annotation() default Annotation.class;

}
