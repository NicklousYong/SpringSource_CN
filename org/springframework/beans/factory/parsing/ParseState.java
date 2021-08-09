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

package org.springframework.beans.factory.parsing;

import java.util.Stack;

/**
 * Simple {@link Stack}-based structure for tracking the logical position during
 * a parsing process. {@link Entry entries} are added to the stack at
 * each point during the parse phase in a reader-specific manner.
 *
 * <p>Calling {@link #toString()} will render a tree-style view of the current logical
 * position in the parse phase. This representation is intended for use in
 * error messages.
 *
 * <p>
 *  用于在解析过程中跟踪逻辑位置的简单{@link Stack}结构{@link条目条目}在解析阶段的每个点以特定于读者的方式添加到堆栈中
 * 
 * <p>调用{@link #toString()}将在分析阶段渲染当前逻辑位置的树状视图。此表示旨在用于错误消息
 * 
 * 
 * @author Rob Harrop
 * @since 2.0
 */
public final class ParseState {

	/**
	 * Tab character used when rendering the tree-style representation.
	 * <p>
	 *  渲染树型表示时使用的Tab字符
	 * 
	 */
	private static final char TAB = '\t';

	/**
	 * Internal {@link Stack} storage.
	 * <p>
	 *  内部{@link Stack}存储
	 * 
	 */
	private final Stack<Entry> state;


	/**
	 * Create a new {@code ParseState} with an empty {@link Stack}.
	 * <p>
	 *  使用空的{@link Stack}创建一个新的{@code ParseState}
	 * 
	 */
	public ParseState() {
		this.state = new Stack<Entry>();
	}

	/**
	 * Create a new {@code ParseState} whose {@link Stack} is a {@link Object#clone clone}
	 * of that of the passed in {@code ParseState}.
	 * <p>
	 *  创建一个新的{@code ParseState},其{@link Stack}是{@code ParseState}中传递的{@link对象#克隆克隆}
	 * 
	 */
	@SuppressWarnings("unchecked")
	private ParseState(ParseState other) {
		this.state = (Stack<Entry>) other.state.clone();
	}


	/**
	 * Add a new {@link Entry} to the {@link Stack}.
	 * <p>
	 *  向{@link Stack}添加新的{@link条目}
	 * 
	 */
	public void push(Entry entry) {
		this.state.push(entry);
	}

	/**
	 * Remove an {@link Entry} from the {@link Stack}.
	 * <p>
	 *  从{@link堆栈}中删除{@link条目}
	 * 
	 */
	public void pop() {
		this.state.pop();
	}

	/**
	 * Return the {@link Entry} currently at the top of the {@link Stack} or
	 * {@code null} if the {@link Stack} is empty.
	 * <p>
	 *  如果{@link Stack}为空,则返回目前位于{@link Stack}或{@code null}顶部的{@link条目}
	 * 
	 */
	public Entry peek() {
		return this.state.empty() ? null : this.state.peek();
	}

	/**
	 * Create a new instance of {@link ParseState} which is an independent snapshot
	 * of this instance.
	 * <p>
	 *  创建一个新的{@link ParseState}实例,它是该实例的独立快照
	 * 
	 */
	public ParseState snapshot() {
		return new ParseState(this);
	}


	/**
	 * Returns a tree-style representation of the current {@code ParseState}.
	 * <p>
	 * 返回当前{@code ParseState}的树型表示
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < this.state.size(); x++) {
			if (x > 0) {
				sb.append('\n');
				for (int y = 0; y < x; y++) {
					sb.append(TAB);
				}
				sb.append("-> ");
			}
			sb.append(this.state.get(x));
		}
		return sb.toString();
	}


	/**
	 * Marker interface for entries into the {@link ParseState}.
	 * <p>
	 *  标记界面,用于输入{@link ParseState}
	 */
	public interface Entry {

	}

}
