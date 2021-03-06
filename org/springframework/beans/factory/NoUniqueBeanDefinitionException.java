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

package org.springframework.beans.factory;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.util.StringUtils;

/**
 * Exception thrown when a {@code BeanFactory} is asked for a bean instance for which
 * multiple matching candidates have been found when only one matching bean was expected.
 *
 * <p>
 *  当一个{@code BeanFactory}被要求在一个bean实例中被发现时发生异常,当一个bean实例只有一个匹配的bean被发现时已经找到了多个匹配的候选者
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.2.1
 * @see BeanFactory#getBean(Class)
 */
@SuppressWarnings("serial")
public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException {

	private int numberOfBeansFound;

	private Collection<String> beanNamesFound;


	/**
	 * Create a new {@code NoUniqueBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoUniqueBeanDefinitionException}
	 * 
	 * 
	 * @param type required type of the non-unique bean
	 * @param numberOfBeansFound the number of matching beans
	 * @param message detailed message describing the problem
	 */
	public NoUniqueBeanDefinitionException(Class<?> type, int numberOfBeansFound, String message) {
		super(type, message);
		this.numberOfBeansFound = numberOfBeansFound;
	}

	/**
	 * Create a new {@code NoUniqueBeanDefinitionException}.
	 * <p>
	 * 创建一个新的{@code NoUniqueBeanDefinitionException}
	 * 
	 * 
	 * @param type required type of the non-unique bean
	 * @param beanNamesFound the names of all matching beans (as a Collection)
	 */
	public NoUniqueBeanDefinitionException(Class<?> type, Collection<String> beanNamesFound) {
		this(type, beanNamesFound.size(), "expected single matching bean but found " + beanNamesFound.size() + ": " +
				StringUtils.collectionToCommaDelimitedString(beanNamesFound));
		this.beanNamesFound = beanNamesFound;
	}

	/**
	 * Create a new {@code NoUniqueBeanDefinitionException}.
	 * <p>
	 *  创建一个新的{@code NoUniqueBeanDefinitionException}
	 * 
	 * 
	 * @param type required type of the non-unique bean
	 * @param beanNamesFound the names of all matching beans (as an array)
	 */
	public NoUniqueBeanDefinitionException(Class<?> type, String... beanNamesFound) {
		this(type, Arrays.asList(beanNamesFound));
	}


	/**
	 * Return the number of beans found when only one matching bean was expected.
	 * For a NoUniqueBeanDefinitionException, this will usually be higher than 1.
	 * <p>
	 *  返回当预期只有一个匹配的bean时发现的bean数对于NoUniqueBeanDefinitionException,通常会高于1
	 * 
	 * 
	 * @see #getBeanType()
	 */
	@Override
	public int getNumberOfBeansFound() {
		return this.numberOfBeansFound;
	}

	/**
	 * Return the names of all beans found when only one matching bean was expected.
	 * Note that this may be {@code null} if not specified at construction time.
	 * <p>
	 *  返回当预期只有一个匹配的bean时发现的所有bean的名称请注意,如果在施工时没有指定,这可能是{@code null}
	 * 
	 * @since 4.3
	 * @see #getBeanType()
	 */
	public Collection<String> getBeanNamesFound() {
		return this.beanNamesFound;
	}

}
