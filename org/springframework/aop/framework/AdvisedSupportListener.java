/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.aop.framework;

/**
 * Listener to be registered on {@link ProxyCreatorSupport} objects
 * Allows for receiving callbacks on activation and change of advice.
 *
 * <p>
 *  在{@link ProxyCreatorSupport}对象上注册的侦听器允许在激活和更改建议时接收回调
 * 
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ProxyCreatorSupport#addListener
 */
public interface AdvisedSupportListener {

	/**
	 * Invoked when the first proxy is created.
	 * <p>
	 *  创建第一个代理时调用
	 * 
	 * 
	 * @param advised the AdvisedSupport object
	 */
	void activated(AdvisedSupport advised);

	/**
	 * Invoked when advice is changed after a proxy is created.
	 * <p>
	 * 创建代理后更改建议时调用
	 * 
	 * @param advised the AdvisedSupport object
	 */
	void adviceChanged(AdvisedSupport advised);

}
