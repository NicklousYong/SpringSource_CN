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

package org.springframework.web.servlet.view.tiles3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.PreparerException;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.NoSuchPreparerException;

import org.springframework.web.context.WebApplicationContext;

/**
 * Tiles {@link org.apache.tiles.preparer.PreparerFactory} implementation
 * that expects preparer class names and builds preparer instances for those,
 * creating them through the Spring ApplicationContext in order to apply
 * Spring container callbacks and configured Spring BeanPostProcessors.
 *
 * <p>
 *  除非适用法律要求或以书面形式同意,根据许可证分发的软件以"按原样"分发,不附带任何明示或暗示的担保或条件,请参阅许可证中有关特定语言的权限和限制许可证
 * 
 * 
 * @author Juergen Hoeller
 * @since 3.2
 * @see SpringBeanPreparerFactory
 */
public class SimpleSpringPreparerFactory extends AbstractSpringPreparerFactory {

	/** Cache of shared ViewPreparer instances: bean name -> bean instance */
	private final Map<String, ViewPreparer> sharedPreparers = new ConcurrentHashMap<String, ViewPreparer>(16);


	@Override
	protected ViewPreparer getPreparer(String name, WebApplicationContext context) throws TilesException {
		// Quick check on the concurrent map first, with minimal locking.
		ViewPreparer preparer = this.sharedPreparers.get(name);
		if (preparer == null) {
			synchronized (this.sharedPreparers) {
				preparer = this.sharedPreparers.get(name);
				if (preparer == null) {
					try {
						Class<?> beanClass = context.getClassLoader().loadClass(name);
						if (!ViewPreparer.class.isAssignableFrom(beanClass)) {
							throw new PreparerException(
									"Invalid preparer class [" + name + "]: does not implement ViewPreparer interface");
						}
						preparer = (ViewPreparer) context.getAutowireCapableBeanFactory().createBean(beanClass);
						this.sharedPreparers.put(name, preparer);
					}
					catch (ClassNotFoundException ex) {
						throw new NoSuchPreparerException("Preparer class [" + name + "] not found", ex);
					}
				}
			}
		}
		return preparer;
	}

}
