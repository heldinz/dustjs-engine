/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.akquinet.dustjs;

import java.net.URL;

public class DustOptions {
	
	public static final String CHARSET_OPTION = "charset";
	public static final String DUST_OPTION = "dust";

	private String charset;
	private URL dust;
	
	public String getCharset() {
		if (charset == null) {
			return "UTF-8";
		}
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public URL getDust() {
		if (dust == null) {
			return getClass().getClassLoader().getResource("META-INF/dust.js");
		}
		return dust;
	}
	
	public void setDust(URL dust) {
		this.dust = dust;
	}
	
}