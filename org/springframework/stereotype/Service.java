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
 * Indicates that an annotated class is a "Service", originally defined by Domain-Driven
 * Design (Evans, 2003) as "an operation offered as an interface that stands alone in the
 * model, with no encapsulated state."
 *
 * <p>May also indicate that a class is a "Business Service Facade" (in the Core J2EE
 * patterns sense), or something similar. This annotation is a general-purpose stereotype
 * and individual teams may narrow their semantics and use as appropriate.
 *
 * <p>This annotation serves as a specialization of {@link Component @Component},
 * allowing for implementation classes to be autodetected through classpath scanning.
 *
 * <p>
 *  表示一个注释类是"服务",最初由域驱动设计(Evans,2003)定义为"作为在模型中独立的界面提供的操作,没有封装状态"
 * 
 * <p>也可以指出,一个类是"业务服务门面"(在核心J2EE模式意义上),或类似的东西这个注释是一个通用的刻板印象,个别的团队可能会缩小他们的语义并适当地使用
 * 
 *  <p>此注释用作{@link Component @Component}的专业化,允许通过类路径扫描自动检测实现类
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see Component
 * @see Repository
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * to be turned into a Spring bean in case of an autodetected component.
	 * <p>
	 * 
	 * 
	 * @return the suggested component name, if any
	 */
	String value() default "";

}
