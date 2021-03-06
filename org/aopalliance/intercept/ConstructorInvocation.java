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

package org.aopalliance.intercept;

import java.lang.reflect.Constructor;

/**
 * Description of an invocation to a constuctor, given to an
 * interceptor upon constructor-call.
 *
 * <p>A constructor invocation is a joinpoint and can be intercepted
 * by a constructor interceptor.
 *
 * <p>
 * 
 * @author Rod Johnson
 * @see ConstructorInterceptor
 */
public interface ConstructorInvocation extends Invocation {

    /**
     * Get the constructor being called.
     * <p>This method is a friendly implementation of the
     * {@link Joinpoint#getStaticPart()} method (same result).
     * <p>
     *  在构造器调用时给予一个拦截器的对一个构造器的调用的描述
     * 
     *  <p>构造函数调用是一个连接点,可以被构造函数拦截器拦截
     * 
     * 
     * @return the constructor being called
     */
    Constructor<?> getConstructor();

}
