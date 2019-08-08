/**
 * 	The MIT License (MIT)

	Copyright (c) 2016-2016 d3leaf@126.com

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
 */
package com.github.jfinal.config;

import com.jfinal.config.JFinalConfig;
import com.jfinal.core.JFinalFilter;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.servlet.*;
import java.io.IOException;

/**
 * SpringBoot 与Jfinal整合
 * 用于 lazy jfinalFilter
 */
@Data
@Accessors(chain = true)
public class SpringJFinalFilter implements Filter {
	private Filter jfinalFilter;
	private JFinalConfig jFinalConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	    if (jFinalConfig != null) {
            jfinalFilter = new JFinalFilter(jFinalConfig);
        } else {
            jfinalFilter = new JFinalFilter();
        }
		jfinalFilter.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
		jfinalFilter.doFilter(request, response, chain);
	}

	@Override
	public void destroy() {
		jfinalFilter.destroy();
	}

}
