/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Julian Sommerfeldt - initial APi and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.internal.diff;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfstore.fuzzy.emf.diff.spi.TestRunProvider;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.FuzzyUtil;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun;

/**
 * An implementation of {@link TestRunProvider} to provide {@link TestRun}s
 * created by the CI-Server Hudson.
 * 
 * @author Julian Sommerfeldt
 * 
 */
public class HudsonTestRunProvider extends TestRunProvider {

	private static final String JOB = "job/"; //$NON-NLS-1$

	private static SAXReader saxReader = new SAXReader();

	private static String jobUrl;

	private String hudsonUrl;

	private final int firstBuildNumber;

	private final int secondBuildNumber;

	/**
	 * The prefix for hudson peroperties.
	 */
	public static final String PROP_HUDSON = ".hudson"; //$NON-NLS-1$

	/**
	 * The hudson url property.
	 */
	public static final String PROP_URL = ".url"; //$NON-NLS-1$

	/**
	 * The hudson artifact folder property.
	 */
	public static final String PROP_ARTIFACT_FOLDER = ".artifact.folder"; //$NON-NLS-1$

	/**
	 * The hudson port property.
	 */
	public static final String PROP_PORT = ".port"; //$NON-NLS-1$

	/**
	 * The name of the hudson job property.
	 */
	public static final String PROP_JOB = ".job"; //$NON-NLS-1$

	/**
	 * The property of the hudson diff job.
	 */
	public static final String PROP_DIFF_JOB = ".diffjob"; //$NON-NLS-1$

	private static final String LAST_BUILD = "lastBuild"; //$NON-NLS-1$

	/**
	 * An array containing all valid states of a hudson build. Valid means it
	 * can be used for creating diffs.
	 */
	public static final String[] VALID_STATES = new String[] { "SUCCESS", //$NON-NLS-1$
		"UNSTABLE" }; //$NON-NLS-1$

	private static final String ARTIFACT = FuzzyUtil.getProperty(PROP_HUDSON
		+ PROP_ARTIFACT_FOLDER, "/artifact/"); //$NON-NLS-1$

	/**
	 * Standard constructor using the last build and the build before the last
	 * build for reading testruns.
	 * 
	 * @throws DocumentException
	 *             If it cannot read the buildnumbers correctly from hudson.
	 * @throws IOException
	 *             If it cannot read the buildnumbers correctly from hudson.
	 */
	public HudsonTestRunProvider() throws DocumentException, IOException {
		initProperties();

		firstBuildNumber = getLastValidBuildNumber(
			Integer.parseInt(getFirstElementValue(jobUrl + LAST_BUILD
				+ "/api/xml?tree=number")), jobUrl); //$NON-NLS-1$
		secondBuildNumber = getLastValidBuildNumber(firstBuildNumber - 1,
			jobUrl);
	}

	/**
	 * Constructor using tow special numbers for testruns.
	 * 
	 * @param firstBuildNumber
	 *            The number of the first build (first from the last one
	 *            backwards, so it is later than the second one).
	 * @param secondBuildNumber
	 *            The number of the second build.
	 */
	public HudsonTestRunProvider(int firstBuildNumber, int secondBuildNumber) {
		initProperties();

		this.firstBuildNumber = firstBuildNumber;
		this.secondBuildNumber = secondBuildNumber;
	}

	private void initProperties() {
		hudsonUrl = getHudsonUrl();
		jobUrl = hudsonUrl + JOB
			+ FuzzyUtil.getProperty(PROP_HUDSON + PROP_JOB, "Explorer") //$NON-NLS-1$
			+ "/"; //$NON-NLS-1$
	}

	private static String getHudsonUrl() {
		final String port = FuzzyUtil.getProperty(PROP_HUDSON + PROP_PORT, null);
		return FuzzyUtil
			.getProperty(PROP_HUDSON + PROP_URL, "http://localhost") //$NON-NLS-1$
			+ (port != null ? ":" + port : ""); // + "/"; //$NON-NLS-1$ //$NON-NLS-2$ 
	}

	private static int getLastValidBuildNumber(int maxBuildNumber, String jobUrl)
		throws MalformedURLException, DocumentException {
		if (maxBuildNumber < 0) {
			throw new RuntimeException(
				Messages.HudsonTestRunProvider_Not_Enough_Valid_Builds);
		}
		if (isValidBuild(maxBuildNumber, jobUrl)) {
			return maxBuildNumber;
		}

		return getLastValidBuildNumber(maxBuildNumber - 1, jobUrl);
	}

	private static boolean isValidBuild(int buildNumber, String jobUrl)
		throws MalformedURLException, DocumentException {
		final String result = getFirstElementValue(jobUrl + buildNumber
			+ "/api/xml?tree=result"); //$NON-NLS-1$
		for (final String valid : VALID_STATES) {
			if (valid.equals(result)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static String getFirstElementValue(String url)
		throws MalformedURLException, DocumentException {
		Document doc;
		try {
			doc = saxReader.read(new URL(url));
		} catch (final DocumentException ex) {
			throw new DocumentException(
				MessageFormat.format(
					Messages.HudsonTestRunProvider_ReadFailed, url), ex);
		}
		final List<Element> elements = doc.getRootElement().elements();
		if (elements.size() == 0) {
			throw new RuntimeException(
				Messages.HudsonTestRunProvider_No_Elements_In_Result + url);
		}
		return elements.get(0).getText();
	}

	@Override
	public TestRun[] getTestRuns() throws IOException {

		final TestRun[] runs = new TestRun[2];

		Resource resource = getTestRunResource(firstBuildNumber);
		if (!FuzzyUtil.resourceExists(resource)) {
			throw new RuntimeException(Messages.HudsonTestRunProvider_No_TestRunFile_For_1st_Run);
		}
		resource.load(null);

		runs[0] = getTestRun(resource);

		resource = getTestRunResource(secondBuildNumber);
		if (!FuzzyUtil.resourceExists(resource)) {
			throw new RuntimeException(
				MessageFormat.format(
					Messages.HudsonTestRunProvider_No_TestRunFile_For_2nd_Run, resource.getURI()));
		}
		resource.load(null);

		runs[1] = getTestRun(resource);

		return runs;
	}

	private Resource getTestRunResource(int buildNumber) {
		return FuzzyUtil.createResource(jobUrl + buildNumber + ARTIFACT
			+ FuzzyUtil.FUZZY_FOLDER + FuzzyUtil.RUN_FOLDER
			+ getTestConfig().getId() + FuzzyUtil.FILE_SUFFIX);
	}

	/**
	 * @return All {@link TestConfig} which are loadable via this {@link HudsonTestRunProvider}.
	 */
	public List<TestConfig> getAllConfigs() {
		final Resource resource = FuzzyUtil.createResource(jobUrl + firstBuildNumber
			+ ARTIFACT + FuzzyUtil.FUZZY_FOLDER
			+ FuzzyUtil.TEST_CONFIG_FILE);
		try {
			resource.load(null);
		} catch (final IOException e) {
			throw new RuntimeException(Messages.HudsonTestRunProvider_Could_Not_Load_Config_File, e);
		}
		final List<TestConfig> configs = new ArrayList<TestConfig>();
		for (final EObject obj : resource.getContents()) {
			if (obj instanceof TestConfig) {
				configs.add((TestConfig) obj);
			}
		}
		return configs;
	}

	/**
	 * @return The diff resource created by hudson.
	 * @throws DocumentException
	 *             in case an error occurs during obtainment of the resource.
	 * @throws MalformedURLException
	 *             in case an error occurs during obtainment of the resource.
	 */
	public static Resource getDiffResource() throws MalformedURLException,
		DocumentException {
		final String diffJobUrl = getHudsonUrl() + JOB
			+ FuzzyUtil.getProperty(PROP_HUDSON + PROP_DIFF_JOB, "Diff") //$NON-NLS-1$
			+ "/"; //$NON-NLS-1$
		final int lastValidNumber = getLastValidBuildNumber(
			Integer.parseInt(getFirstElementValue(diffJobUrl + LAST_BUILD
				+ "/api/xml?tree=number")), diffJobUrl); //$NON-NLS-1$
		return FuzzyUtil.createResource(diffJobUrl + lastValidNumber + ARTIFACT
			+ FuzzyUtil.FUZZY_FOLDER + "diff" + FuzzyUtil.FILE_SUFFIX); //$NON-NLS-1$
	}
}
