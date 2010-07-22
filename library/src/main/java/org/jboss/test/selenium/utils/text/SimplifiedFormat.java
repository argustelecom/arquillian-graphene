/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.selenium.utils.text;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * Formats using simplified MessageFormat syntax: {} are used as placeholders in order of arguments; {number} are
 * placeholders with given argument number.
 * </p>
 * 
 * <p>
 * When filling the placeholders by given arguments are compelete, rest of the numbered placeholders are decreased by
 * number of arguments in previous format action.
 * </p>
 * 
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 */
public final class SimplifiedFormat {

    private static final Pattern PATTERN = Pattern.compile("\\{([0-9]+)\\}");

    private SimplifiedFormat() {
    }

    /**
     * Parametrize given string with arguments, using {} or {number} (e.g. {0}, {1}, ...) as placeholders.
     * 
     * @param format
     *            string to format
     * @param args
     *            used to formatting given format string
     * @return string formatted using given arguments
     */
    public static String format(String message, Object... args) {
        String result = message;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toString();
            result = StringUtils.replaceOnce(result, "{}", arg);
            result = StringUtils.replace(result, "{" + i + "}", arg);
        }

        Matcher matcher = PATTERN.matcher(result);
        int delta = args.length;
        List<Integer> found = new LinkedList<Integer>();
        while (matcher.find()) {
            int value = Integer.valueOf(matcher.group(1));
            found.add(value);
        }

        for (int value : found) {
            int replacement = value - delta;
            result = StringUtils.replaceOnce(result, "{" + value + "}", "{" + replacement + "}");
        }

        return result;
    }
}
