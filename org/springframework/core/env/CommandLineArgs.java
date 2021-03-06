/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple representation of command line arguments, broken into "option arguments" and
 * "non-option arguments".
 *
 * <p>
 *  命令行参数的简单表示,分为"选项参数"和"非选项参数"
 * 
 * 
 * @author Chris Beams
 * @since 3.1
 * @see SimpleCommandLineArgsParser
 */
class CommandLineArgs {

	private final Map<String, List<String>> optionArgs = new HashMap<String, List<String>>();
	private final List<String> nonOptionArgs = new ArrayList<String>();

	/**
	 * Add an option argument for the given option name and add the given value to the
	 * list of values associated with this option (of which there may be zero or more).
	 * The given value may be {@code null}, indicating that the option was specified
	 * without an associated value (e.g. "--foo" vs. "--foo=bar").
	 * <p>
	 * 添加给定选项名称的选项参数,并将给定值添加到与此选项(可能为零或更多)相关联的值列表中。
	 * 给定值可能为{@code null},表示该选项已指定没有关联的值(例如"--foo"vs"--foo = bar")。
	 * 
	 */
	public void addOptionArg(String optionName, String optionValue) {
		if (!this.optionArgs.containsKey(optionName)) {
			this.optionArgs.put(optionName, new ArrayList<String>());
		}
		if (optionValue != null) {
			this.optionArgs.get(optionName).add(optionValue);
		}
	}

	/**
	 * Return the set of all option arguments present on the command line.
	 * <p>
	 *  返回命令行中存在的所有选项参数的集合
	 * 
	 */
	public Set<String> getOptionNames() {
		return Collections.unmodifiableSet(this.optionArgs.keySet());
	}

	/**
	 * Return whether the option with the given name was present on the command line.
	 * <p>
	 *  返回命令行中是否存在具有给定名称的选项
	 * 
	 */
	public boolean containsOption(String optionName) {
		return this.optionArgs.containsKey(optionName);
	}

	/**
	 * Return the list of values associated with the given option. {@code null} signifies
	 * that the option was not present; empty list signifies that no values were associated
	 * with this option.
	 * <p>
	 *  返回与给定选项{@code null}相关联的值列表,表示该选项不存在;空列表表示没有值与此选项相关联
	 * 
	 */
	public List<String> getOptionValues(String optionName) {
		return this.optionArgs.get(optionName);
	}

	/**
	 * Add the given value to the list of non-option arguments.
	 * <p>
	 *  将给定值添加到非选项参数列表中
	 * 
	 */
	public void addNonOptionArg(String value) {
		this.nonOptionArgs.add(value);
	}

	/**
	 * Return the list of non-option arguments specified on the command line.
	 * <p>
	 *  返回在命令行中指定的非选项参数的列表
	 */
	public List<String> getNonOptionArgs() {
		return Collections.unmodifiableList(this.nonOptionArgs);
	}

}
