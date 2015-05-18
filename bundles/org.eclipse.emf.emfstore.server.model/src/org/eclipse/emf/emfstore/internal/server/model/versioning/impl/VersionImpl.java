/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.emfstore.common.ESResourceSetProvider;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESPriorityComparator;
import org.eclipse.emf.emfstore.internal.common.ResourceFactoryRegistry;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.impl.ProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Version</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getPrimarySpec <em>Primary Spec
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getTagSpecs <em>Tag Specs</em>}
 * </li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getNextVersion <em>Next Version
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getPreviousVersion <em>Previous
 * Version</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getLogMessage <em>Log Message
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getAncestorVersion <em>Ancestor
 * Version</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getBranchedVersions <em>
 * Branched Versions</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getMergedToVersion <em>Merged
 * To Version</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.server.model.versioning.impl.VersionImpl#getMergedFromVersion <em>Merged
 * From Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class VersionImpl extends EObjectImpl implements Version {

	/**
	 * File extension for main file: emfstore project state.
	 */
	public static final String FILE_EXTENSION_PROJECTSTATE = ".ups"; //$NON-NLS-1$

	/**
	 * File extension for main file: emfstore change package.
	 */
	public static final String FILE_EXTENSION_CHANGEPACKAGE = ".ucp"; //$NON-NLS-1$

	/**
	 * File prefix for file: changepackage.
	 */
	public static final String FILE_PREFIX_CHANGEPACKAGE = "changepackage-"; //$NON-NLS-1$

	/**
	 * File prefix for file: projectstate.
	 */
	public static final String FILE_PREFIX_PROJECTSTATE = "projectstate-"; //$NON-NLS-1$

	/**
	 * The EMFStore URI segment for a changepackage.
	 */
	public static final String CHANGEPACKAGES_SEGMENT = "changepackages"; //$NON-NLS-1$

	/**
	 * The EMFStore URI segment for a projectstate.
	 */
	public static final String PROJECTSTATES_SEGMENT = "projectstates"; //$NON-NLS-1$

	// SoftReferences acting as a simple cache for project state and ChangePackage
	private SoftReference<Resource> projectStateResource = new SoftReference<Resource>(null);
	private SoftReference<Resource> changePackageResource = new SoftReference<Resource>(null);

	private static ResourceFactoryRegistry resourceFactoryRegistry = new ResourceFactoryRegistry();

	/**
	 * The cached value of the '{@link #getPrimarySpec() <em>Primary Spec</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getPrimarySpec()
	 * @generated
	 * @ordered
	 */
	protected PrimaryVersionSpec primarySpec;

	/**
	 * The cached value of the '{@link #getTagSpecs() <em>Tag Specs</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getTagSpecs()
	 * @generated
	 * @ordered
	 */
	protected EList<TagVersionSpec> tagSpecs;

	/**
	 * The cached value of the '{@link #getNextVersion() <em>Next Version</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getNextVersion()
	 * @generated
	 * @ordered
	 */
	protected Version nextVersion;

	/**
	 * The cached value of the '{@link #getPreviousVersion() <em>Previous Version</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getPreviousVersion()
	 * @generated
	 * @ordered
	 */
	protected Version previousVersion;

	/**
	 * The cached value of the '{@link #getLogMessage() <em>Log Message</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getLogMessage()
	 * @generated
	 * @ordered
	 */
	protected LogMessage logMessage;

	/**
	 * The cached value of the '{@link #getAncestorVersion() <em>Ancestor Version</em>}' reference.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getAncestorVersion()
	 * @generated
	 * @ordered
	 */
	protected Version ancestorVersion;

	/**
	 * The cached value of the '{@link #getBranchedVersions() <em>Branched Versions</em>}' reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getBranchedVersions()
	 * @generated
	 * @ordered
	 */
	protected EList<Version> branchedVersions;

	/**
	 * The cached value of the '{@link #getMergedToVersion() <em>Merged To Version</em>}' reference list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @see #getMergedToVersion()
	 * @generated
	 * @ordered
	 */
	protected EList<Version> mergedToVersion;

	/**
	 * The cached value of the '{@link #getMergedFromVersion() <em>Merged From Version</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @see #getMergedFromVersion()
	 * @generated
	 * @ordered
	 */
	protected EList<Version> mergedFromVersion;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected VersionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return VersioningPackage.Literals.VERSION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PrimaryVersionSpec getPrimarySpec() {
		if (primarySpec != null && primarySpec.eIsProxy()) {
			final InternalEObject oldPrimarySpec = (InternalEObject) primarySpec;
			primarySpec = (PrimaryVersionSpec) eResolveProxy(oldPrimarySpec);
			if (primarySpec != oldPrimarySpec) {
				final InternalEObject newPrimarySpec = (InternalEObject) primarySpec;
				NotificationChain msgs = oldPrimarySpec.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.VERSION__PRIMARY_SPEC, null, null);
				if (newPrimarySpec.eInternalContainer() == null) {
					msgs = newPrimarySpec.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- VersioningPackage.VERSION__PRIMARY_SPEC, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, VersioningPackage.VERSION__PRIMARY_SPEC,
						oldPrimarySpec, primarySpec));
				}
			}
		}
		return primarySpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public PrimaryVersionSpec basicGetPrimarySpec() {
		return primarySpec;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetPrimarySpec(PrimaryVersionSpec newPrimarySpec, NotificationChain msgs) {
		final PrimaryVersionSpec oldPrimarySpec = primarySpec;
		primarySpec = newPrimarySpec;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.VERSION__PRIMARY_SPEC, oldPrimarySpec, newPrimarySpec);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setPrimarySpec(PrimaryVersionSpec newPrimarySpec) {
		if (newPrimarySpec != primarySpec) {
			NotificationChain msgs = null;
			if (primarySpec != null) {
				msgs = ((InternalEObject) primarySpec).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.VERSION__PRIMARY_SPEC, null, msgs);
			}
			if (newPrimarySpec != null) {
				msgs = ((InternalEObject) newPrimarySpec).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.VERSION__PRIMARY_SPEC, null, msgs);
			}
			msgs = basicSetPrimarySpec(newPrimarySpec, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.VERSION__PRIMARY_SPEC,
				newPrimarySpec, newPrimarySpec));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<TagVersionSpec> getTagSpecs() {
		if (tagSpecs == null) {
			tagSpecs = new EObjectContainmentEList.Resolving<TagVersionSpec>(TagVersionSpec.class, this,
				VersioningPackage.VERSION__TAG_SPECS);
		}
		return tagSpecs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Version getNextVersion() {
		if (nextVersion != null && nextVersion.eIsProxy()) {
			final InternalEObject oldNextVersion = (InternalEObject) nextVersion;
			nextVersion = (Version) eResolveProxy(oldNextVersion);
			if (nextVersion != oldNextVersion) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, VersioningPackage.VERSION__NEXT_VERSION,
						oldNextVersion, nextVersion));
				}
			}
		}
		return nextVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Version basicGetNextVersion() {
		return nextVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetNextVersion(Version newNextVersion, NotificationChain msgs) {
		final Version oldNextVersion = nextVersion;
		nextVersion = newNextVersion;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.VERSION__NEXT_VERSION, oldNextVersion, newNextVersion);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setNextVersion(Version newNextVersion) {
		if (newNextVersion != nextVersion) {
			NotificationChain msgs = null;
			if (nextVersion != null) {
				msgs = ((InternalEObject) nextVersion).eInverseRemove(this,
					VersioningPackage.VERSION__PREVIOUS_VERSION, Version.class, msgs);
			}
			if (newNextVersion != null) {
				msgs = ((InternalEObject) newNextVersion).eInverseAdd(this,
					VersioningPackage.VERSION__PREVIOUS_VERSION, Version.class, msgs);
			}
			msgs = basicSetNextVersion(newNextVersion, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.VERSION__NEXT_VERSION,
				newNextVersion, newNextVersion));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Version getPreviousVersion() {
		if (previousVersion != null && previousVersion.eIsProxy()) {
			final InternalEObject oldPreviousVersion = (InternalEObject) previousVersion;
			previousVersion = (Version) eResolveProxy(oldPreviousVersion);
			if (previousVersion != oldPreviousVersion) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.VERSION__PREVIOUS_VERSION, oldPreviousVersion, previousVersion));
				}
			}
		}
		return previousVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Version basicGetPreviousVersion() {
		return previousVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetPreviousVersion(Version newPreviousVersion, NotificationChain msgs) {
		final Version oldPreviousVersion = previousVersion;
		previousVersion = newPreviousVersion;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.VERSION__PREVIOUS_VERSION, oldPreviousVersion, newPreviousVersion);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setPreviousVersion(Version newPreviousVersion) {
		if (newPreviousVersion != previousVersion) {
			NotificationChain msgs = null;
			if (previousVersion != null) {
				msgs = ((InternalEObject) previousVersion).eInverseRemove(this,
					VersioningPackage.VERSION__NEXT_VERSION, Version.class, msgs);
			}
			if (newPreviousVersion != null) {
				msgs = ((InternalEObject) newPreviousVersion).eInverseAdd(this,
					VersioningPackage.VERSION__NEXT_VERSION, Version.class, msgs);
			}
			msgs = basicSetPreviousVersion(newPreviousVersion, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.VERSION__PREVIOUS_VERSION,
				newPreviousVersion, newPreviousVersion));
		}
	}

	// begin of custom code
	/**
	 * Loads the XMI IDs from the given resource and returns them in a map
	 * together with the object each ID belongs to.
	 *
	 * @param resource
	 *            the resource from which to load the ID mappings
	 * @return a map consisting of object/id mappings, if the resource doesn't
	 *         contain an eobject/id mapping null will be returned
	 */
	private EMap<EObject, String> loadIdsFromResourceForEObjects(Set<EObject> modelElements, XMIResource xmiResource) {

		EMap<EObject, String> eObjectToIdMap;

		if (xmiResource != null) {
			// guess a rough initial size by looking at the size of the contents
			eObjectToIdMap = new BasicEMap<EObject, String>(xmiResource.getContents().size());

			for (final EObject eObject : modelElements) {
				final String objId = xmiResource.getID(eObject);
				if (objId != null) {
					eObjectToIdMap.put(eObject, objId);
				}
			}

			return eObjectToIdMap;
		}

		return null;
	}

	// end of custom code

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage getLogMessage() {
		if (logMessage != null && logMessage.eIsProxy()) {
			final InternalEObject oldLogMessage = (InternalEObject) logMessage;
			logMessage = (LogMessage) eResolveProxy(oldLogMessage);
			if (logMessage != oldLogMessage) {
				final InternalEObject newLogMessage = (InternalEObject) logMessage;
				NotificationChain msgs = oldLogMessage.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.VERSION__LOG_MESSAGE, null, null);
				if (newLogMessage.eInternalContainer() == null) {
					msgs = newLogMessage.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- VersioningPackage.VERSION__LOG_MESSAGE, null, msgs);
				}
				if (msgs != null) {
					msgs.dispatch();
				}
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, VersioningPackage.VERSION__LOG_MESSAGE,
						oldLogMessage, logMessage));
				}
			}
		}
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LogMessage basicGetLogMessage() {
		return logMessage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetLogMessage(LogMessage newLogMessage, NotificationChain msgs) {
		final LogMessage oldLogMessage = logMessage;
		logMessage = newLogMessage;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.VERSION__LOG_MESSAGE, oldLogMessage, newLogMessage);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setLogMessage(LogMessage newLogMessage) {
		if (newLogMessage != logMessage) {
			NotificationChain msgs = null;
			if (logMessage != null) {
				msgs = ((InternalEObject) logMessage).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.VERSION__LOG_MESSAGE, null, msgs);
			}
			if (newLogMessage != null) {
				msgs = ((InternalEObject) newLogMessage).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- VersioningPackage.VERSION__LOG_MESSAGE, null, msgs);
			}
			msgs = basicSetLogMessage(newLogMessage, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.VERSION__LOG_MESSAGE,
				newLogMessage, newLogMessage));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Version getAncestorVersion() {
		if (ancestorVersion != null && ancestorVersion.eIsProxy()) {
			final InternalEObject oldAncestorVersion = (InternalEObject) ancestorVersion;
			ancestorVersion = (Version) eResolveProxy(oldAncestorVersion);
			if (ancestorVersion != oldAncestorVersion) {
				if (eNotificationRequired()) {
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						VersioningPackage.VERSION__ANCESTOR_VERSION, oldAncestorVersion, ancestorVersion));
				}
			}
		}
		return ancestorVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Version basicGetAncestorVersion() {
		return ancestorVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetAncestorVersion(Version newAncestorVersion, NotificationChain msgs) {
		final Version oldAncestorVersion = ancestorVersion;
		ancestorVersion = newAncestorVersion;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				VersioningPackage.VERSION__ANCESTOR_VERSION, oldAncestorVersion, newAncestorVersion);
			if (msgs == null) {
				msgs = notification;
			} else {
				msgs.add(notification);
			}
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public void setAncestorVersion(Version newAncestorVersion) {
		if (newAncestorVersion != ancestorVersion) {
			NotificationChain msgs = null;
			if (ancestorVersion != null) {
				msgs = ((InternalEObject) ancestorVersion).eInverseRemove(this,
					VersioningPackage.VERSION__BRANCHED_VERSIONS, Version.class, msgs);
			}
			if (newAncestorVersion != null) {
				msgs = ((InternalEObject) newAncestorVersion).eInverseAdd(this,
					VersioningPackage.VERSION__BRANCHED_VERSIONS, Version.class, msgs);
			}
			msgs = basicSetAncestorVersion(newAncestorVersion, msgs);
			if (msgs != null) {
				msgs.dispatch();
			}
		} else if (eNotificationRequired()) {
			eNotify(new ENotificationImpl(this, Notification.SET, VersioningPackage.VERSION__ANCESTOR_VERSION,
				newAncestorVersion, newAncestorVersion));
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<Version> getBranchedVersions() {
		if (branchedVersions == null) {
			branchedVersions = new EObjectWithInverseResolvingEList<Version>(Version.class, this,
				VersioningPackage.VERSION__BRANCHED_VERSIONS, VersioningPackage.VERSION__ANCESTOR_VERSION);
		}
		return branchedVersions;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<Version> getMergedToVersion() {
		if (mergedToVersion == null) {
			mergedToVersion = new EObjectWithInverseResolvingEList.ManyInverse<Version>(Version.class, this,
				VersioningPackage.VERSION__MERGED_TO_VERSION, VersioningPackage.VERSION__MERGED_FROM_VERSION);
		}
		return mergedToVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public EList<Version> getMergedFromVersion() {
		if (mergedFromVersion == null) {
			mergedFromVersion = new EObjectWithInverseResolvingEList.ManyInverse<Version>(Version.class, this,
				VersioningPackage.VERSION__MERGED_FROM_VERSION, VersioningPackage.VERSION__MERGED_TO_VERSION);
		}
		return mergedFromVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case VersioningPackage.VERSION__NEXT_VERSION:
			if (nextVersion != null) {
				msgs = ((InternalEObject) nextVersion).eInverseRemove(this,
					VersioningPackage.VERSION__PREVIOUS_VERSION, Version.class, msgs);
			}
			return basicSetNextVersion((Version) otherEnd, msgs);
		case VersioningPackage.VERSION__PREVIOUS_VERSION:
			if (previousVersion != null) {
				msgs = ((InternalEObject) previousVersion).eInverseRemove(this,
					VersioningPackage.VERSION__NEXT_VERSION, Version.class, msgs);
			}
			return basicSetPreviousVersion((Version) otherEnd, msgs);
		case VersioningPackage.VERSION__ANCESTOR_VERSION:
			if (ancestorVersion != null) {
				msgs = ((InternalEObject) ancestorVersion).eInverseRemove(this,
					VersioningPackage.VERSION__BRANCHED_VERSIONS, Version.class, msgs);
			}
			return basicSetAncestorVersion((Version) otherEnd, msgs);
		case VersioningPackage.VERSION__BRANCHED_VERSIONS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getBranchedVersions()).basicAdd(otherEnd, msgs);
		case VersioningPackage.VERSION__MERGED_TO_VERSION:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getMergedToVersion()).basicAdd(otherEnd, msgs);
		case VersioningPackage.VERSION__MERGED_FROM_VERSION:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getMergedFromVersion())
				.basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case VersioningPackage.VERSION__PRIMARY_SPEC:
			return basicSetPrimarySpec(null, msgs);
		case VersioningPackage.VERSION__TAG_SPECS:
			return ((InternalEList<?>) getTagSpecs()).basicRemove(otherEnd, msgs);
		case VersioningPackage.VERSION__NEXT_VERSION:
			return basicSetNextVersion(null, msgs);
		case VersioningPackage.VERSION__PREVIOUS_VERSION:
			return basicSetPreviousVersion(null, msgs);
		case VersioningPackage.VERSION__LOG_MESSAGE:
			return basicSetLogMessage(null, msgs);
		case VersioningPackage.VERSION__ANCESTOR_VERSION:
			return basicSetAncestorVersion(null, msgs);
		case VersioningPackage.VERSION__BRANCHED_VERSIONS:
			return ((InternalEList<?>) getBranchedVersions()).basicRemove(otherEnd, msgs);
		case VersioningPackage.VERSION__MERGED_TO_VERSION:
			return ((InternalEList<?>) getMergedToVersion()).basicRemove(otherEnd, msgs);
		case VersioningPackage.VERSION__MERGED_FROM_VERSION:
			return ((InternalEList<?>) getMergedFromVersion()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case VersioningPackage.VERSION__PRIMARY_SPEC:
			if (resolve) {
				return getPrimarySpec();
			}
			return basicGetPrimarySpec();
		case VersioningPackage.VERSION__TAG_SPECS:
			return getTagSpecs();
		case VersioningPackage.VERSION__NEXT_VERSION:
			if (resolve) {
				return getNextVersion();
			}
			return basicGetNextVersion();
		case VersioningPackage.VERSION__PREVIOUS_VERSION:
			if (resolve) {
				return getPreviousVersion();
			}
			return basicGetPreviousVersion();
		case VersioningPackage.VERSION__LOG_MESSAGE:
			if (resolve) {
				return getLogMessage();
			}
			return basicGetLogMessage();
		case VersioningPackage.VERSION__ANCESTOR_VERSION:
			if (resolve) {
				return getAncestorVersion();
			}
			return basicGetAncestorVersion();
		case VersioningPackage.VERSION__BRANCHED_VERSIONS:
			return getBranchedVersions();
		case VersioningPackage.VERSION__MERGED_TO_VERSION:
			return getMergedToVersion();
		case VersioningPackage.VERSION__MERGED_FROM_VERSION:
			return getMergedFromVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case VersioningPackage.VERSION__PRIMARY_SPEC:
			setPrimarySpec((PrimaryVersionSpec) newValue);
			return;
		case VersioningPackage.VERSION__TAG_SPECS:
			getTagSpecs().clear();
			getTagSpecs().addAll((Collection<? extends TagVersionSpec>) newValue);
			return;
		case VersioningPackage.VERSION__NEXT_VERSION:
			setNextVersion((Version) newValue);
			return;
		case VersioningPackage.VERSION__PREVIOUS_VERSION:
			setPreviousVersion((Version) newValue);
			return;
		case VersioningPackage.VERSION__LOG_MESSAGE:
			setLogMessage((LogMessage) newValue);
			return;
		case VersioningPackage.VERSION__ANCESTOR_VERSION:
			setAncestorVersion((Version) newValue);
			return;
		case VersioningPackage.VERSION__BRANCHED_VERSIONS:
			getBranchedVersions().clear();
			getBranchedVersions().addAll((Collection<? extends Version>) newValue);
			return;
		case VersioningPackage.VERSION__MERGED_TO_VERSION:
			getMergedToVersion().clear();
			getMergedToVersion().addAll((Collection<? extends Version>) newValue);
			return;
		case VersioningPackage.VERSION__MERGED_FROM_VERSION:
			getMergedFromVersion().clear();
			getMergedFromVersion().addAll((Collection<? extends Version>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case VersioningPackage.VERSION__PRIMARY_SPEC:
			setPrimarySpec((PrimaryVersionSpec) null);
			return;
		case VersioningPackage.VERSION__TAG_SPECS:
			getTagSpecs().clear();
			return;
		case VersioningPackage.VERSION__NEXT_VERSION:
			setNextVersion((Version) null);
			return;
		case VersioningPackage.VERSION__PREVIOUS_VERSION:
			setPreviousVersion((Version) null);
			return;
		case VersioningPackage.VERSION__LOG_MESSAGE:
			setLogMessage((LogMessage) null);
			return;
		case VersioningPackage.VERSION__ANCESTOR_VERSION:
			setAncestorVersion((Version) null);
			return;
		case VersioningPackage.VERSION__BRANCHED_VERSIONS:
			getBranchedVersions().clear();
			return;
		case VersioningPackage.VERSION__MERGED_TO_VERSION:
			getMergedToVersion().clear();
			return;
		case VersioningPackage.VERSION__MERGED_FROM_VERSION:
			getMergedFromVersion().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case VersioningPackage.VERSION__PRIMARY_SPEC:
			return primarySpec != null;
		case VersioningPackage.VERSION__TAG_SPECS:
			return tagSpecs != null && !tagSpecs.isEmpty();
		case VersioningPackage.VERSION__NEXT_VERSION:
			return nextVersion != null;
		case VersioningPackage.VERSION__PREVIOUS_VERSION:
			return previousVersion != null;
		case VersioningPackage.VERSION__LOG_MESSAGE:
			return logMessage != null;
		case VersioningPackage.VERSION__ANCESTOR_VERSION:
			return ancestorVersion != null;
		case VersioningPackage.VERSION__BRANCHED_VERSIONS:
			return branchedVersions != null && !branchedVersions.isEmpty();
		case VersioningPackage.VERSION__MERGED_TO_VERSION:
			return mergedToVersion != null && !mergedToVersion.isEmpty();
		case VersioningPackage.VERSION__MERGED_FROM_VERSION:
			return mergedFromVersion != null && !mergedFromVersion.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getProjectState()
	 */
	public Project getProjectState() {
		final Resource resource = getProjectStateResource();
		if (resource == null || resource.getContents().size() < 1) {
			return null;
		}
		final Project project = (Project) resource.getContents().get(0);
		return project;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#getChanges()
	 */
	public AbstractChangePackage getChanges() {
		final Resource resource = getChangePackageResource();
		if (resource == null || resource.getContents().size() < 1) {
			return null;
		}
		final AbstractChangePackage changePackage = (AbstractChangePackage) resource.getContents().get(0);
		return changePackage;
	}

	/**
	 * Returns a resource containing the associated project state. If a resource is not in memory one will be loaded.
	 *
	 * @return the resource
	 */
	private Resource getProjectStateResource() {
		Resource result = projectStateResource.get();
		if (projectStateResource.get() == null || !projectStateResource.get().isLoaded()) {
			try {
				result = loadResourceForURI(getProjectURI());
			} catch (final IOException ioe) {
				result = null;
				if (!(ioe instanceof FileNotFoundException) || getPrimarySpec().getIdentifier() == 0) {
					ModelUtil.logException(ioe);
				}
			}

			if (result != null && result.getContents().size() > 0) {
				final Project project = (Project) result.getContents().get(0);
				initProjectStateAfterLoad((ProjectImpl) project);
			}

			setProjectStateResource(result);
		}

		// make sure resource has all needed handlers
		if (result != null && result.getResourceSet() == null) {
			addResourceToResourceSet(result);
		}

		return result;
	}

	// builds the idToEObjectMap after a project state is loaded
	private void initProjectStateAfterLoad(ProjectImpl project) {
		final Resource resource = project.eResource();
		if (resource instanceof XMIResource) {
			final Set<EObject> allContainedModelElements = ModelUtil.getAllContainedModelElements(project, false);
			final EMap<EObject, String> eObjectToIdMap = loadIdsFromResourceForEObjects(allContainedModelElements,
				(XMIResource) resource);

			// create reverse mapping
			final Map<String, EObject> idToEObjectMap = new LinkedHashMap<String, EObject>(eObjectToIdMap.size());

			for (final Map.Entry<EObject, String> entry : eObjectToIdMap.entrySet()) {
				idToEObjectMap.put(entry.getValue(), entry.getKey());
			}

			project.initMapping(eObjectToIdMap.map(), idToEObjectMap);
		}
	}

	/**
	 * Returns a resource containing the associated ChangePackage. If a resource is not in memory one will be loaded.
	 *
	 * @return the resource
	 */
	private Resource getChangePackageResource() {
		Resource result = changePackageResource.get();
		if (changePackageResource.get() == null || !changePackageResource.get().isLoaded()) {
			try {
				result = loadResourceForURI(getChangePackageURI());
			} catch (final IOException e) {
				result = null;
				if (getPrimarySpec().getIdentifier() > 0) {
					ModelUtil.logException(e);
				}
			}
			setChangeResource(result);
		}

		// make sure resource has all needed handlers
		if (result != null && result.getResourceSet() == null) {
			addResourceToResourceSet(result);
		}

		return result;
	}

	/**
	 * Loads a resource for the given URI.
	 *
	 * @param uri the URI
	 * @return the loaded resource
	 * @throws IOException - in case the resource could not be read.
	 */
	private Resource loadResourceForURI(URI uri) throws IOException {

		Resource resource = null;

		if (eResource() != null && eResource().getResourceSet() != null) {
			resource = eResource().getResourceSet().createResource(uri);
		}

		if (resource == null) {
			resourceFactoryRegistry.createResource(uri);
		}
		resource.load(ModelUtil.getResourceLoadOptions());
		return resource;
	}

	/**
	 * Adds a resource to a new ResourceSet which can handle EMFStore URIs.
	 */
	// TODO: ME, does this and its callers really belong here?
	private void addResourceToResourceSet(Resource resource) {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint(
			"org.eclipse.emf.emfstore.server.resourceSetProvider", //$NON-NLS-1$
			true, new ESPriorityComparator("priority", true)); //$NON-NLS-1$

		final ESResourceSetProvider resourceSetProvider = extensionPoint.getElementWithHighestPriority().getClass(
			"class", //$NON-NLS-1$
			ESResourceSetProvider.class);

		final ResourceSet resourceSet = resourceSetProvider.getResourceSet();
		resourceSet.getResources().add(resource);
	}

	/**
	 * allows to retrieve the URI for the resource containing the project state associated with this version.
	 *
	 * @return the uri for the project state resource
	 */
	private URI getProjectURI() {
		return getBaseURI() == null ? null : getBaseURI().appendSegment(PROJECTSTATES_SEGMENT).appendSegment(
			Integer.toString(getPrimarySpec().getIdentifier()));
	}

	/**
	 * allows to retrieve the URI for the resource containing the ChangePackege associated with this version.
	 *
	 * @return the uri for the ChangePackage resource
	 */
	private URI getChangePackageURI() {
		return getBaseURI() == null ? null : getBaseURI().appendSegment(CHANGEPACKAGES_SEGMENT).appendSegment(
			Integer.toString(getPrimarySpec().getIdentifier()));
	}

	/**
	 * allows to retrieve the Base-URI of this version, i.e. the URI of the resource
	 * containing the version trimmed by its last segment.
	 *
	 * @return the base URI
	 */
	private URI getBaseURI() {
		return eResource() == null ? null : eResource().getURI().trimSegments(2);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#setChangeResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	public void setChangeResource(Resource resource) {
		if (resource == null) {
			changePackageResource = new SoftReference<Resource>(null);
		} else {
			final ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet != null && resourceSet == eResource().getResourceSet()) {
				// remove the resource from its containing resourceSet in order
				// to remove the strong referencing.
				resourceSet.getResources().remove(resource);
			}
			changePackageResource = new SoftReference<Resource>(resource);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.Version#setProjectStateResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	public void setProjectStateResource(Resource resource) {
		if (resource == null) {
			projectStateResource = new SoftReference<Resource>(null);
		} else {
			final ResourceSet resourceSet = resource.getResourceSet();
			if (resourceSet != null && resourceSet == eResource().getResourceSet()) {
				// remove the resource from its containing resourceSet in order
				// to remove the strong referencing.
				resourceSet.getResources().remove(resource);
			}
			projectStateResource = new SoftReference<Resource>(resource);
		}
	}

} // VersionImpl