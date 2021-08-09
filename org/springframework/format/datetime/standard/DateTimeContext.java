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

package org.springframework.format.datetime.standard;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.UsesJava8;

/**
 * A context that holds user-specific <code>java.time</code> (JSR-310) settings
 * such as the user's Chronology (calendar system) and time zone.
 * A {@code null} property value indicate the user has not specified a setting.
 *
 * <p>
 *  包含用户特定的<code> javatime </code>(JSR-310)设置(例如用户的年表(日历系统)和时区A {@code null}属性值)的上下文表示用户尚未指定设置
 * 
 * 
 * @author Juergen Hoeller
 * @since 4.0
 * @see DateTimeContextHolder
 */
@UsesJava8
public class DateTimeContext {

	private Chronology chronology;

	private ZoneId timeZone;


	/**
	 * Set the user's chronology (calendar system).
	 * <p>
	 * 设置用户的年表(日历系统)
	 * 
	 */
	public void setChronology(Chronology chronology) {
		this.chronology = chronology;
	}

	/**
	 * Return the user's chronology (calendar system), if any.
	 * <p>
	 *  返回用户的年表(日历系统)(如果有的话)
	 * 
	 */
	public Chronology getChronology() {
		return this.chronology;
	}

	/**
	 * Set the user's time zone.
	 * <p>Alternatively, set a {@link TimeZoneAwareLocaleContext} on
	 * {@link LocaleContextHolder}. This context class will fall back to
	 * checking the locale context if no setting has been provided here.
	 * <p>
	 *  设置用户的时区<p>或者,在{@link LocaleContextHolder}上设置{@link TimeZoneAwareLocaleContext}如果此处未提供设置,则此上下文类将返回到检查
	 * 区域设置上下文。
	 * 
	 * 
	 * @see org.springframework.context.i18n.LocaleContextHolder#getTimeZone()
	 * @see org.springframework.context.i18n.LocaleContextHolder#setLocaleContext
	 */
	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Return the user's time zone, if any.
	 * <p>
	 *  返回用户的时区(如果有)
	 * 
	 */
	public ZoneId getTimeZone() {
		return this.timeZone;
	}


	/**
	 * Get the DateTimeFormatter with the this context's settings
	 * applied to the base {@code formatter}.
	 * <p>
	 *  获取DateTimeFormatter,将此上下文的设置应用于基础{@code formatter}
	 * 
	 * @param formatter the base formatter that establishes default
	 * formatting rules, generally context-independent
	 * @return the contextual DateTimeFormatter
	 */
	public DateTimeFormatter getFormatter(DateTimeFormatter formatter) {
		if (this.chronology != null) {
			formatter = formatter.withChronology(this.chronology);
		}
		if (this.timeZone != null) {
			formatter = formatter.withZone(this.timeZone);
		}
		else {
			LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				TimeZone timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
				if (timeZone != null) {
					formatter = formatter.withZone(timeZone.toZoneId());
				}
			}
		}
		return formatter;
	}

}
