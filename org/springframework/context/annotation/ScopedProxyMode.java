/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.context.annotation;

/**
 * Enumerates the various scoped-proxy options.
 *
 * <p>For a more complete discussion of exactly what a scoped proxy is, see the
 * section of the Spring reference documentation entitled '<em>Scoped beans as
 * dependencies</em>'.
 *
 * <p>
 *  枚举各种范围代理选项
 * 
 *  <p>要详细讨论作用域代理的详细信息,请参阅标题为"<em>作为依赖关系的作用域bean"的Spring参考文档的部分</em>
 * 
 * 
 * @author Mark Fisher
 * @since 2.5
 * @see ScopeMetadata
 */
public enum ScopedProxyMode {

	/**
	 * Default typically equals {@link #NO}, unless a different default
	 * has been configured at the component-scan instruction level.
	 * <p>
	 * 默认值通常等于{@link #NO},除非在组件扫描指令级别配置了不同的默认值
	 * 
	 */
	DEFAULT,

	/**
	 * Do not create a scoped proxy.
	 * <p>This proxy-mode is not typically useful when used with a
	 * non-singleton scoped instance, which should favor the use of the
	 * {@link #INTERFACES} or {@link #TARGET_CLASS} proxy-modes instead if it
	 * is to be used as a dependency.
	 * <p>
	 *  不要创建作用域代理<p>与非单例范围实例一起使用时,此代理模式通常不是有用的,该实例应有利于使用{@link #INTERFACES}或{@link #TARGET_CLASS}代理模式而是如果它被用
	 * 作依赖。
	 * 
	 */
	NO,

	/**
	 * Create a JDK dynamic proxy implementing <i>all</i> interfaces exposed by
	 * the class of the target object.
	 * <p>
	 *  创建JDK动态代理,实现由目标对象的类公开的所有</i>接口
	 * 
	 */
	INTERFACES,

	/**
	 * Create a class-based proxy (uses CGLIB).
	 * <p>
	 *  创建一个基于类的代理(使用CGLIB)
	 */
	TARGET_CLASS;

}
