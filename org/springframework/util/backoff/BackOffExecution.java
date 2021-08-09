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

package org.springframework.util.backoff;

/**
 * Represent a particular back-off execution.
 *
 * <p>Implementations do not need to be thread safe.
 *
 * <p>
 *  代表一个特定的后退执行
 * 
 *  <p>实现不需要线程安全
 * 
 * 
 * @author Stephane Nicoll
 * @since 4.1
 * @see BackOff
 */
public interface BackOffExecution {

	/**
	 * Return value of {@link #nextBackOff()} that indicates that the operation
	 * should not be retried.
	 * <p>
	 *  {@link #nextBackOff()}的返回值,表示不应重试该操作
	 * 
	 */
	long STOP = -1;

	/**
	 * Return the number of milliseconds to wait before retrying the operation
	 * or {@link #STOP} ({@value #STOP}) to indicate that no further attempt
	 * should be made for the operation.
	 * <p>
	 * 返回重试操作之前要等待的毫秒数,或{@link #STOP}({@value #STOP}),以表明不应再进行操作
	 */
	long nextBackOff();

}
