/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.aop;

/**
 * Interface supplying the information necessary to describe an introduction.
 *
 * <p>{@link IntroductionAdvisor IntroductionAdvisors} must implement this
 * interface. If an {@link org.aopalliance.aop.Advice} implements this,
 * it may be used as an introduction without an {@link IntroductionAdvisor}.
 * In this case, the advice is self-describing, providing not only the
 * necessary behavior, but describing the interfaces it introduces.
 *
 * <p>
 *  界面提供描述介绍所需的信息
 * 
 * <p> {@ link IntroductionAdvisor简介顾问}必须实现这个界面如果一个{@link orgaopallianceaopAdvice}实现了这一点,它可能被用作没有{@link IntroductionAdvisor}
 * 的介绍。
 * 在这种情况下,建议是自我描述的,不提供只是必要的行为,但描述了它介绍的接口。
 * 
 * @author Rod Johnson
 * @since 1.1.1
 */
public interface IntroductionInfo {

	/**
	 * Return the additional interfaces introduced by this Advisor or Advice.
	 * <p>
	 * 
	 * 
	 * @return the introduced interfaces
	 */
	Class<?>[] getInterfaces();

}
