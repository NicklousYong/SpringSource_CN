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

package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.aspectj.AspectInstanceFactory;

/**
 * Subinterface of {@link org.springframework.aop.aspectj.AspectInstanceFactory}
 * that returns {@link AspectMetadata} associated with AspectJ-annotated classes.
 *
 * <p>Ideally, AspectInstanceFactory would include this method itself, but because
 * AspectMetadata uses Java-5-only {@link org.aspectj.lang.reflect.AjType},
 * we need to split out this subinterface.
 *
 * <p>
 *  {@link orgspringframeworkaopaspectjAspectInstanceFactory}的子界面,返回与AspectJ注释类相关联的{@link AspectMetadata}
 * 。
 * 
 * <p>理想情况下,AspectInstanceFactory本身将包含此方法,但是由于AspectMetadata使用仅Java-5 {@link orgaspectjlangreflectAjType}
 * ,因此我们需要将此子界面。
 * 
 * 
 * @author Rod Johnson
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjType
 */
public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {

	/**
	 * Return the AspectJ AspectMetadata for this factory's aspect.
	 * <p>
	 * 
	 * @return the aspect metadata
	 */
	AspectMetadata getAspectMetadata();

	/**
	 * Return the best possible creation mutex for this factory.
	 * <p>
	 *  返回此工厂方面的AspectJ AspectMetadata
	 * 
	 * 
	 * @return the mutex object (never {@code null})
	 * @since 4.3
	 */
	Object getAspectCreationMutex();

}
