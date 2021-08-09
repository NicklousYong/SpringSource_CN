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
 * Provide a {@link BackOffExecution} that indicates the rate at which
 * an operation should be retried.
 *
 * <p>Users of this interface are expected to use it like this:
 *
 * <pre class="code">
 * BackOffExecution exec = backOff.start();
 *
 * // In the operation recovery/retry loop:
 * long waitInterval = exec.nextBackOff();
 * if (waitInterval == BackOffExecution.STOP) {
 *     // do not retry operation
 * }
 * else {
 *     // sleep, e.g. Thread.sleep(waitInterval)
 *     // retry operation
 * }
 * }</pre>
 *
 * Once the underlying operation has completed successfully,
 * the execution instance can be simply discarded.
 *
 * <p>
 *  提供一个{@link BackOffExecution},指示应重试操作的速率
 * 
 *  <p>此界面的用户预计将使用它：
 * 
 * <pre class="code">
 *  BackOffExecution exec = backOffstart();
 * 
 * //在操作恢复/重试循环中：long waitInterval = execnextBackOff(); if(waitInterval == BackOffExecutionSTOP){//不要重试操作}
 * 
 * @author Stephane Nicoll
 * @since 4.1
 * @see BackOffExecution
 */
public interface BackOff {

	/**
	 * Start a new back off execution.
	 * <p>
	 *  else {// sleep,例如Threadsleep(waitInterval)//重试操作}} </pre>。
	 * 
	 *  一旦底层操作成功完成,就可以简单地丢弃执行实例
	 * 
	 * 
	 * @return a fresh {@link BackOffExecution} ready to be used
	 */
	BackOffExecution start();

}
