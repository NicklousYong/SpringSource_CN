/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "Repository", originally defined by
 * Domain-Driven Design (Evans, 2003) as "a mechanism for encapsulating storage,
 * retrieval, and search behavior which emulates a collection of objects".
 *
 * <p>Teams implementing traditional J2EE patterns such as "Data Access Object"
 * may also apply this stereotype to DAO classes, though care should be taken to
 * understand the distinction between Data Access Object and DDD-style repositories
 * before doing so. This annotation is a general-purpose stereotype and individual teams
 * may narrow their semantics and use as appropriate.
 *
 * <p>A class thus annotated is eligible for Spring
 * {@link org.springframework.dao.DataAccessException DataAccessException} translation
 * when used in conjunction with a {@link
 * org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
 * PersistenceExceptionTranslationPostProcessor}. The annotated class is also clarified as
 * to its role in the overall application architecture for the purpose of tooling,
 * aspects, etc.
 *
 * <p>As of Spring 2.5, this annotation also serves as a specialization of
 * {@link Component @Component}, allowing for implementation classes to be autodetected
 * through classpath scanning.
 *
 * <p>
 *  表示一个注释类是最初由域驱动设计(Evans,2003)定义为"模拟一个对象集合的封装存储,检索和搜索行为的机制"的"存储库"
 * 
 * 实现传统J2EE模式(如"数据访问对象")的团队也可以将此原型应用于DAO类,但在执行此操作之前,应注意了解数据访问对象和DDD样式存储库之间的区别。
 * 此注释是一般的使用刻板印象和个别团队可能缩小其语义并酌情使用。
 * 
 * <p>如果这样注释的类与{@link orgspringframeworkdaoannotationPersistenceExceptionTranslationPostProcessor PersistenceExceptionTranslationPostProcessor}
 * 结合使用,则可以使用Spring {@link orgspringframeworkdaoDataAccessException DataAccessException}翻译。
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see Component
 * @see Service
 * @see org.springframework.dao.DataAccessException
 * @see org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * <p>
	 * 注释类也针对其在整个应用程序架构中的作用进行了说明,方面等。
	 * 
	 *  <p>从Spring 25开始,此注释还可以作为{@link Component @Component}的专业化,允许通过类路径扫描自动检测实现类
	 * 
	 * 
	 * @return the suggested component name, if any
	 */
	String value() default "";

}
