/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2014 the original author or authors.
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
 * A {@link Condition} that offers more fine-grained control when used with
 * {@code @Configuration}. Allows certain {@link Condition}s to adapt when they match
 * based on the configuration phase. For example, a condition that checks if a bean
 * has already been registered might choose to only be evaluated during the
 * {@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} {@link ConfigurationPhase}.
 *
 * <p>
 * {@link条件}与{@code @Configuration}一起使用时提供更细粒度的控制允许某些{@link条件}在基于配置阶段匹配时进行调整。
 * 例如,检查bean是否为一个条件已经被注册可能选择仅在{@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} {@link ConfigurationPhase}
 * 期间进行评估。
 * {@link条件}与{@code @Configuration}一起使用时提供更细粒度的控制允许某些{@link条件}在基于配置阶段匹配时进行调整。
 * 
 * 
 * @author Phillip Webb
 * @since 4.0
 * @see Configuration
 */
public interface ConfigurationCondition extends Condition {

	/**
	 * Return the {@link ConfigurationPhase} in which the condition should be evaluated.
	 * <p>
	 *  返回应该评估条件的{@link ConfigurationPhase}
	 * 
	 */
	ConfigurationPhase getConfigurationPhase();


	/**
	 * The various configuration phases where the condition could be evaluated.
	 * <p>
	 *  可以评估条件的各种配置阶段
	 * 
	 */
	public static enum ConfigurationPhase {

		/**
		 * The {@link Condition} should be evaluated as a {@code @Configuration}
		 * class is being parsed.
		 * <p>If the condition does not match at this point, the {@code @Configuration}
		 * class will not be added.
		 * <p>
		 *  {@link条件}应该被评估为正在解析的{@code @Configuration}类<p>如果此时条件不匹配,则{@code @Configuration}类将不被添加
		 * 
		 */
		PARSE_CONFIGURATION,

		/**
		 * The {@link Condition} should be evaluated when adding a regular
		 * (non {@code @Configuration}) bean. The condition will not prevent
		 * {@code @Configuration} classes from being added.
		 * <p>At the time that the condition is evaluated, all {@code @Configuration}s
		 * will have been parsed.
		 * <p>
		 * 在添加常规(非{@code @Configuration})bean时,应该评估{@link条件}条件不会阻止添加{@code @Configuration}类<p>在评估条件时,所有{@code @Configuration}
		 * 将被解析。
		 */
		REGISTER_BEAN
	}

}
