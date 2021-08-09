/***** Lobxxx Translate Finished ******/
/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.format.datetime.joda;

import java.util.TimeZone;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;

/**
 * A context that holds user-specific Joda-Time settings such as the user's
 * Chronology (calendar system) and time zone.
 *
 * <p>A {@code null} property value indicate the user has not specified a setting.
 *
 * <p>
 *  包含用户特定的Joda-Time设置的上下文,例如用户的年表(日历系统)和时区
 * 
 *  <p> {@code null}属性值表示用户尚未指定设置
 * 
 * 
 * @author Keith Donald
 * @since 3.0
 * @see JodaTimeContextHolder
 */
public class JodaTimeContext {

	private Chronology chronology;

	private DateTimeZone timeZone;


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
	public void setTimeZone(DateTimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Return the user's time zone, if any.
	 * <p>
	 *  返回用户的时区(如果有)
	 * 
	 */
	public DateTimeZone getTimeZone() {
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
					formatter = formatter.withZone(DateTimeZone.forTimeZone(timeZone));
				}
			}
		}
		return formatter;
	}

}
