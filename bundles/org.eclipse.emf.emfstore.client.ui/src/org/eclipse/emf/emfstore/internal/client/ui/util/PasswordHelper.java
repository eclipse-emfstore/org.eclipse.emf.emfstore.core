/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.util;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;

/**
 * Helper class for validation user passwords.
 */
public final class PasswordHelper {

	/**
	 * The {@link PasswordHelper} instance.
	 */
	public static final PasswordHelper INSTANCE = new PasswordHelper();

	private Pattern pattern;
	private String patternExplanation;

	private PasswordHelper() {
		final ESExtensionPoint showPasswordControls = new ESExtensionPoint(
			"org.eclipse.emf.emfstore.client.ui.showPasswordControls"); //$NON-NLS-1$

		final String pattern = showPasswordControls.getAttribute("pattern"); //$NON-NLS-1$
		if (pattern != null) {
			try {
				final Pattern compiledPattern = Pattern.compile(pattern);
				this.pattern = compiledPattern;
			} catch (final PatternSyntaxException ex) {
				ModelUtil.logWarning(Messages.PasswordHelper_PatternInvalid, ex);
			}
		}

		final String patternExplanation = showPasswordControls.getAttribute("patternExplanation"); //$NON-NLS-1$
		if (patternExplanation != null && !StringUtils.isBlank(patternExplanation)) {
			this.patternExplanation = patternExplanation;
		} else {
			this.patternExplanation = Messages.PasswordHelper_PasswordDoesNotMatchDefault;
		}
	}

	/**
	 * @param pwd the password to check
	 * @return <code>true</code> if password will be accepted, <code>false</code> otherwise
	 */
	public boolean matchesPattern(String pwd) {
		if (pattern == null) {
			return true;
		}
		return pattern.matcher(pwd).matches();
	}

	/**
	 * @return the string explaining the expected pattern
	 */
	public String getInvalidPatternMessage() {
		return patternExplanation;
	}

}
