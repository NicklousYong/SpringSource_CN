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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation that indicates the session attributes that a specific handler uses.
 *
 * <p>This will typically list the names of model attributes which should be
 * transparently stored in the session or some conversational storage,
 * serving as form-backing beans. <b>Declared at the type level</b>, applying
 * to the model attributes that the annotated handler class operates on.
 *
 * <p><b>NOTE:</b> Session attributes as indicated using this annotation
 * correspond to a specific handler's model attributes, getting transparently
 * stored in a conversational session. Those attributes will be removed once
 * the handler indicates completion of its conversational session. Therefore,
 * use this facility for such conversational attributes which are supposed
 * to be stored in the session <i>temporarily</i> during the course of a
 * specific handler's conversation.
 *
 * <p>For permanent session attributes, e.g. a user authentication object,
 * use the traditional {@code session.setAttribute} method instead.
 * Alternatively, consider using the attribute management capabilities of the
 * generic {@link org.springframework.web.context.request.WebRequest} interface.
 *
 * <p><b>NOTE:</b> When using controller interfaces (e.g. for AOP proxying),
 * make sure to consistently put <i>all</i> your mapping annotations &mdash;
 * such as {@code @RequestMapping} and {@code @SessionAttributes} &mdash; on
 * the controller <i>interface</i> rather than on the implementation class.
 *
 * <p>
 *  指示特定处理程序使用的会话属性的注释
 * 
 * 这通常将列出应该透明地存储在会话中的模型属性的名称,或者作为表单支持bean的一些会话存储(b)在类型级别上声明</b>,应用于模型属性注释处理程序类操作
 * 
 *  <p> <b>注意：</b>使用此注释指示的会话属性对应于特定处理程序的模型属性,透明地存储在会话会话中。
 * 一旦处理程序指示会话会话完成,这些属性将被删除因此,在特定处理程序的对话过程中,将此设施用于此类会话属性,这些会话属性应该临时存储在会话<i>中。
 * 
 * <p>对于永久会话属性,例如用户验证对象,请使用传统的{@code sessionsetAttribute}方法。
 * 或者,考虑使用通用{@link orgspringframeworkwebcontextrequestWebRequest}界面的属性管理功能。
 * 
 *  <p> <b>注意：</b>当使用控制器接口(例如用于AOP代理)时,请确保始终将<i>全部</i>的映射注释&mdash;例如{@code @RequestMapping}和{@code @SessionAttributes}
 * &mdash;在控制器<i>界面</i>而不是实现类上。
 * 
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 2.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SessionAttributes {

	/**
	 * Alias for {@link #names}.
	 * <p>
	 * 
	 */
	@AliasFor("names")
	String[] value() default {};

	/**
	 * The names of session attributes in the model that should be stored in the
	 * session or some conversational storage.
	 * <p><strong>Note</strong>: This indicates the <em>model attribute names</em>.
	 * The <em>session attribute names</em> may or may not match the model attribute
	 * names. Applications should therefore not rely on the session attribute
	 * names but rather operate on the model only.
	 * <p>
	 *  {@link #names}的别名
	 * 
	 * 
	 * @since 4.2
	 */
	@AliasFor("value")
	String[] names() default {};

	/**
	 * The types of session attributes in the model that should be stored in the
	 * session or some conversational storage.
	 * <p>All model attributes of these types will be stored in the session,
	 * regardless of attribute name.
	 * <p>
	 * 模型中应存储在会话中的会话属性的名称或某些会话存储<p> <strong>注意</strong>：这表示模型属性名称</em>会话属性名称</em>可能与模型属性名称匹配也可能不匹配应用程序应该不依赖于
	 * 会话属性名称,而只能在模型上运行。
	 * 
	 */
	Class<?>[] types() default {};

}
