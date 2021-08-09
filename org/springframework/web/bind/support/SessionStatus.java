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

package org.springframework.web.bind.support;

/**
 * Simple interface that can be injected into handler methods, allowing them to
 * signal that their session processing is complete. The handler invoker may
 * then follow up with appropriate cleanup, e.g. of session attributes which
 * have been implicitly created during this handler's processing (according to
 * the
 * {@link org.springframework.web.bind.annotation.SessionAttributes @SessionAttributes}
 * annotation).
 *
 * <p>
 * 简单的界面,可以被注入到处理程序方法中,允许它们发信号通知它们的会话处理已经完成。
 * 处理程序调用者可以跟随适当的清理,例如在处理程序处理过程中隐式创建的会话属性(根据{@链接orgspringframeworkwebbindannotationSessionAttributes @SessionAttributes}
 * 注释)。
 * 简单的界面,可以被注入到处理程序方法中,允许它们发信号通知它们的会话处理已经完成。
 * 
 * 
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see org.springframework.web.bind.annotation.SessionAttributes
 */
public interface SessionStatus {

	/**
	 * Mark the current handler's session processing as complete, allowing for
	 * cleanup of session attributes.
	 * <p>
	 *  将当前处理程序的会话处理标记为完整,允许清除会话属性
	 * 
	 */
	void setComplete();

	/**
	 * Return whether the current handler's session processing has been marked
	 * as complete.
	 * <p>
	 *  返回当前处理程序的会话处理是否已被标记为完成
	 */
	boolean isComplete();

}
