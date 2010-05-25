/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2009-2010, Red Hat, Inc. and individual contributors
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
 *******************************************************************************/
package org.jboss.test.selenium.listener;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jboss.test.selenium.encapsulated.FrameLocator;
import org.jboss.test.selenium.encapsulated.JavaScript;
import org.jboss.test.selenium.framework.AjaxSelenium;
import org.jboss.test.selenium.framework.internal.Contextual;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * Class determined to logging into Selenium Server's logs server.log via the Selenium.getEval(String) method which will
 * evaluate JavaScript comment.
 * 
 * You must rewrite the Selenium selenium property to allow logging facility.
 * 
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>, <a href="mailto:ppitonak@redhat.com">Pavol Pitonak</a>
 * @version $Revision$
 * 
 */
public class SeleniumLoggingTestListener extends TestListenerAdapter implements Contextual {

    /**
     * Mapping of the status ids to string equivalents
     */
    protected static final TreeMap<Integer, String> STATUSES = new TreeMap<Integer, String>() {
        private static final long serialVersionUID = 1L;

        {
            put(ITestResult.FAILURE, "Failure");
            put(ITestResult.SKIP, "Skip");
            put(ITestResult.STARTED, "Started");
            put(ITestResult.SUCCESS, "Success");
            put(ITestResult.SUCCESS_PERCENTAGE_FAILURE, "FailurePercentage");
        }
    };

    /**
     * Must be specified to allow the logging facility
     */
    private AjaxSelenium selenium = AjaxSelenium.getCurrentContext(this);

    @Override
    public void onTestStart(ITestResult result) {
        logStatus(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logStatus(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logStatus(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logStatus(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logStatus(result);
    }

    /**
     * This method will output method name and status into Selenium Server's log server.log via the
     * Selenium.getEval(String) method which will evaluate JavaScript comment
     * 
     * @param result
     *            from the fine-grained listener's method such as onTestFailure(ITestResult)
     */
    private void logStatus(ITestResult result) {
        final String hashes = "##########";
        final String methodName = getMethodName(result);
        final String status = STATUSES.get(result.getStatus());

        String message = String.format("%s %s: %s %s", hashes, status.toUpperCase(), methodName, hashes);
        String line = StringUtils.repeat("#", message.length());

        if (selenium != null) {
            JavaScript eval = new JavaScript(String.format("/*\n%s\n%s\n%s\n*/", line, message, line));
            try {
                selenium.selectFrame(new FrameLocator("relative=top"));
                selenium.getEval(eval);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get method name from ITestResult
     * 
     * @param result
     *            from the fine-grained listener's method such as onTestFailure(ITestResult)
     * @return the method name in current context
     */
    protected static String getMethodName(ITestResult result) {
        String methodName = result.getMethod().toString();

        Matcher matcher = Pattern.compile(".*\\.(.*\\..*)\\(.*\\)").matcher(methodName);
        if (matcher.lookingAt()) {
            methodName = matcher.group(1);
        }
        return methodName;
    }
}