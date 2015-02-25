/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.startup;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.FeatureNotFoundException;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.CloseableIterable;

/**
 * Validates the serverspace in three different ways. First it resolves all proxies, then checks whether all ME have ids
 * and it is checked whether the changes generate the corret projectstate.
 *
 * @author wesendon
 */
public class EmfStoreValidator {

	private static final String CHANGES_FEATURE_NAME = "changes"; //$NON-NLS-1$

	private static final String PROJECTSTATE_FEATURE_NAME = "projectState"; //$NON-NLS-1$

	private final ServerSpace serverSpace;

	private List<String> excludedProjects;

	/**
	 * Default constructor.
	 *
	 * @param serverSpace serverspace
	 */
	public EmfStoreValidator(ServerSpace serverSpace) {
		this.serverSpace = serverSpace;
		excludedProjects = new ArrayList<String>();
	}

	/**
	 * Option for resolving all proxies. The validation therefore uses
	 * {@link EcoreUtil#resolveAll(org.eclipse.emf.ecore.EObject)}
	 */
	public static final int RESOLVEALL = 1;

	/**
	 * Option for checking all modelelement ids in projecstate and changes. Every modelelement and changeoperation has
	 * to have a modelelement id.
	 */
	public static final int MODELELEMENTID = 2;

	/**
	 * Option for the change test. The initial projecstate is loaded and all changes are applied, until the next
	 * projecstate is reached. Then the calculated state and the saved state are comparesd.
	 */
	public static final int PROJECTGENERATION = 4;

	private long timeMillis;

	/**
	 * Runs the validation. You can configure the validation by a bitmask, therefore use the value: {@link #RESOLVEALL},
	 * {@link #MODELELEMENTID} and {@link #PROJECTGENERATION}.
	 *
	 * @param options options
	 * @param throwException allows you to prevent that an exception is thrown if validation failes
	 * @throws FatalESException in case of failure
	 */
	public void validate(int options, boolean throwException) throws FatalESException {
		boolean errors = true;
		if ((options & RESOLVEALL) == RESOLVEALL) {
			errors = validateResolveAll() && errors;
		}
		if ((options & MODELELEMENTID) == MODELELEMENTID) {
			errors = validateModelelementId() && errors;
		}
		// Not effieciently possible with branches
		// if ((options & PROJECTGENERATION) == PROJECTGENERATION) {
		// errors = validateProjectGeneration() && errors;
		// }

		if (!errors && throwException) {
			throw new FatalESException(Messages.EmfStoreValidator_ValidationFailed);
		}
	}

	/**
	 * Runs the validation. You can configure the validation by a bitmask, therefore use the value: {@link #RESOLVEALL},
	 * {@link #MODELELEMENTID} and {@link #PROJECTGENERATION}.
	 *
	 * @param options options
	 * @throws FatalESException in case of failure
	 */
	public void validate(int options) throws FatalESException {
		validate(options, true);
	}

	/**
	 * {@link #RESOLVEALL}.
	 */
	private boolean validateResolveAll() {
		start(Messages.EmfStoreValidator_ResolvingAllElements);
		EcoreUtil.resolveAll(serverSpace.eResource().getResourceSet());
		final EList<Resource.Diagnostic> errors = new BasicEList<Resource.Diagnostic>();
		final EList<Resource> resources = serverSpace.eResource().getResourceSet().getResources();
		for (final Resource currentResource : resources) {
			errors.addAll(currentResource.getErrors());
		}
		removeAcceptedErrors(errors);
		errors(errors);
		stop();
		return errors.size() == 0;
	}

	private void removeAcceptedErrors(EList<Diagnostic> errors) {
		final Iterator<Diagnostic> iterator = errors.iterator();
		while (iterator.hasNext()) {
			final Resource.Diagnostic diagnostic = iterator.next();

			// removes errors for projectState and changePackage references in versions.
			if (diagnostic instanceof FeatureNotFoundException) {
				final FeatureNotFoundException featureNotFoundException = (FeatureNotFoundException) diagnostic;
				if (featureNotFoundException.getObject() instanceof Version) {
					if (featureNotFoundException.getName().equals(PROJECTSTATE_FEATURE_NAME)
						|| featureNotFoundException.getName().equals(CHANGES_FEATURE_NAME)) {
						iterator.remove();
					}
				}
			}
		}
	}

	/**
	 * {@link #MODELELEMENTID}.
	 */
	private boolean validateModelelementId() {
		start(Messages.EmfStoreValidator_CheckingModelElementIds);
		final List<String> errors = new ArrayList<String>();
		for (final ProjectHistory projectHistory : serverSpace.getProjects()) {
			if (isExcluded(projectHistory)) {
				continue;
			}
			System.out.println(
				MessageFormat.format(Messages.EmfStoreValidator_CheckingProject,
					projectHistory.getProjectId().getId()));
			for (final Version version : projectHistory.getVersions()) {
				if (version.getChanges() != null) {
					final CloseableIterable<AbstractOperation> operations = version.getChanges().operations();
					try {
						for (final AbstractOperation abstractOperation : operations.iterable()) {
							if (!(abstractOperation instanceof CompositeOperation)
								&& (abstractOperation.getModelElementId() == null
								|| abstractOperation.getModelElementId().getId() == null)) {
								errors.add(
									MessageFormat.format(
										Messages.EmfStoreValidator_ChangeOperation_Has_No_ModelElementId,
										projectHistory.getProjectId(),
										version.getPrimarySpec().getIdentifier()));
							}
						}
					} finally {
						operations.close();
					}
				}
				if (version.getProjectState() != null) {
					for (final EObject me : version.getProjectState().getAllModelElements()) {
						final ModelElementId modelElementId = ModelUtil.getProject(me).getModelElementId(me);
						if (modelElementId == null || modelElementId.getId() == null) {
							errors.add(
								MessageFormat.format(
									Messages.EmfStoreValidator_ModelElement_Has_No_ModelElementId,
									projectHistory.getProjectId(),
									version.getPrimarySpec().getIdentifier()));
						}
					}
				}
			}
		}
		errors(errors);
		stop();
		return errors.size() == 0;
	}

	/**
	 * Note: This validation has been deactivated since the introduction of branch support. With branches this can't be
	 * done efficiently anymore, we have to discuss alternatives.
	 *
	 * {@value #PROJECTGENERATION}.
	 */
	@SuppressWarnings("unused")
	private boolean validateProjectGeneration() {
		start(Messages.EmfStoreValidator_ProjectGenerationCompare);
		final List<String> errors = new ArrayList<String>();
		for (final ProjectHistory history : serverSpace.getProjects()) {
			if (isExcluded(history)) {
				continue;
			}
			System.out.println(
				MessageFormat.format(
					Messages.EmfStoreValidator_CheckingProject, history.getProjectId().getId()));
			// history = (ProjectHistory) ModelUtil.clone(history);
			Project state = null;

			for (final Version version : history.getVersions()) {

				if (version.getProjectState() != null && state == null) {
					state = ModelUtil.clone(version.getProjectState());
				} else {

					version.getChanges().apply(state, true);

					if (version.getProjectState() != null) {
						final int[] compare = linearCompare(version.getProjectState(), state);
						if (compare[0] == 0) {
							errors.add(
								MessageFormat.format(
									Messages.EmfStoreValidator_ProjectVersionCompareFailed,
									history.getProjectId().getId(),
									version.getPrimarySpec().getIdentifier()));
							// debug(history, state, version);
						}
						state = ModelUtil.clone(version.getProjectState());
					}
				}
			}
		}
		errors(errors);
		stop();
		return errors.size() == 0;
	}

	/**
	 * Allows to exclude projects from validation aside from {@link #RESOLVEALL}.
	 *
	 * @param excludedProjects list of project id as string
	 */
	public void setExcludedProjects(List<String> excludedProjects) {
		if (excludedProjects != null) {
			this.excludedProjects = excludedProjects;
		}
	}

	private boolean isExcluded(ProjectHistory projectHistory) {
		return excludedProjects.contains(projectHistory.getProjectId().getId());
	}

	private void start(String str) {
		timeMillis = System.currentTimeMillis();
		System.out.println(Messages.EmfStoreValidator_Validation + str);
	}

	private void stop() {
		System.out.println(
			MessageFormat.format(
				Messages.EmfStoreValidator_ValidationDuration, System.currentTimeMillis() - timeMillis));
	}

	private void errors(Collection<? extends Object> errorList) {
		System.out.println(Messages.EmfStoreValidator_Errors + errorList.size());
		for (final Object obj : errorList) {
			System.out.println(obj);
		}
	}

	private static int[] linearCompare(Project projectA, Project projectB) {
		final int[] result = new int[5];
		final int areEqual = 0;
		final int differencePosition = 1;
		final int character = 2;
		final int lineNum = 3;
		final int colNum = 4;
		result[areEqual] = 1;
		String stringA = StringUtils.EMPTY;
		String stringB = StringUtils.EMPTY;

		try {
			stringA = ModelUtil.eObjectToString(projectA, ModelUtil.getChecksumSaveOptions());
			stringB = ModelUtil.eObjectToString(projectB, ModelUtil.getChecksumSaveOptions());
		} catch (final SerializationException e) {
			ModelUtil.logException(e);
			result[areEqual] = 0;
			return result;
		}

		final int length = Math.min(stringA.length(), stringB.length());
		for (int index = 0; index < length; index++) {
			if (stringA.charAt(index) != stringB.charAt(index)) {
				result[areEqual] = 0;
				result[differencePosition] = index;
				result[character] = stringA.charAt(index);
				final int lineNumber = getLineNum(stringA, index);
				result[lineNum] = lineNumber;
				result[colNum] = getColNum(stringA, index);
				break;
			}
		}
		return result;
	}

	private static int getColNum(String stringA, int index) {
		int pos = index;
		int j = 0;
		for (int i = 0; i < index; i++) {
			j++;
			if (stringA.charAt(i) == '\n') {
				pos -= j;
				j = 0;
			}
		}
		return pos;
	}

	private static int getLineNum(String stringA, int index) {
		int lineNum = 1;
		for (int i = 0; i < index; i++) {
			if (stringA.charAt(i) == '\n') {
				lineNum++;
			}
		}
		return lineNum;
	}

}
