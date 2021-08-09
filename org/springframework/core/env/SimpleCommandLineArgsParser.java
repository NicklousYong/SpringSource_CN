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

/**
 * Parses a {@code String[]} of command line arguments in order to populate a
 * {@link CommandLineArgs} object.
 *
 * <h3>Working with option arguments</h3>
 * Option arguments must adhere to the exact syntax:
 * <pre class="code">--optName[=optValue]</pre>
 * That is, options must be prefixed with "{@code --}", and may or may not specify a value.
 * If a value is specified, the name and value must be separated <em>without spaces</em>
 * by an equals sign ("=").
 *
 * <h4>Valid examples of option arguments</h4>
 * <pre class="code">
 * --foo
 * --foo=bar
 * --foo="bar then baz"
 * --foo=bar,baz,biz</pre>
 *
 * <h4>Invalid examples of option arguments</h4>
 * <pre class="code">
 * -foo
 * --foo bar
 * --foo = bar
 * --foo=bar --foo=baz --foo=biz</pre>
 *
 * <h3>Working with non-option arguments</h3>
 * Any and all arguments specified at the command line without the "{@code --}" option
 * prefix will be considered as "non-option arguments" and made available through the
 * {@link CommandLineArgs#getNonOptionArgs()} method.
 *
 * <p>
 *  解析一个{@code String []}的命令行参数,以填充{@link CommandLineArgs}对象
 * 
 * <h3>使用选项参数</h3>选项参数必须遵守确切的语法：<pre class ="code">  -  optName [= optValue] </pre>也就是说,选项必须以"{@代码 - }",
 * 并且可能指定或不指定值如果指定了值,则必须使用等号("=")将名称和值与<空格</em>分开,。
 * 
 *  <h4>选项参数的有效示例</h4>
 * <pre class="code">
 *  --foo --foo = bar --foo ="bar then baz"--foo = bar,baz,biz </pre>
 * 
 *  <h4>无效的选项参数示例</h4>
 * 
 * @author Chris Beams
 * @since 3.1
 */
class SimpleCommandLineArgsParser {

	/**
	 * Parse the given {@code String} array based on the rules described {@linkplain
	 * SimpleCommandLineArgsParser above}, returning a fully-populated
	 * {@link CommandLineArgs} object.
	 * <p>
	 * <pre class="code">
	 *  -foo --foo bar --foo = bar --foo = bar --foo = baz --foo = biz </pre>
	 * 
	 * <h3>使用非选项参数</h3>在没有"{@code  - }"选项前缀的命令行中指定的任何和所有参数将被视为"非选项参数",并通过{ @link CommandLineArgs#getNonOptionArgs()}
	 * 方法。
	 * 
	 * @param args command line arguments, typically from a {@code main()} method
	 */
	public CommandLineArgs parse(String... args) {
		CommandLineArgs commandLineArgs = new CommandLineArgs();
		for (String arg : args) {
			if (arg.startsWith("--")) {
				String optionText = arg.substring(2, arg.length());
				String optionName;
				String optionValue = null;
				if (optionText.contains("=")) {
					optionName = optionText.substring(0, optionText.indexOf("="));
					optionValue = optionText.substring(optionText.indexOf("=")+1, optionText.length());
				}
				else {
					optionName = optionText;
				}
				if (optionName.isEmpty() || (optionValue != null && optionValue.isEmpty())) {
					throw new IllegalArgumentException("Invalid argument syntax: " + arg);
				}
				commandLineArgs.addOptionArg(optionName, optionValue);
			}
			else {
				commandLineArgs.addNonOptionArg(arg);
			}
		}
		return commandLineArgs;
	}

}
