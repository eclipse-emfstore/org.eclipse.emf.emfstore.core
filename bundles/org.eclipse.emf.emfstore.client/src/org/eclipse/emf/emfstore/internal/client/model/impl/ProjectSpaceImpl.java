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
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.emfstore.client.util.ClientURIUtil;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESPriorityComparator;
import org.eclipse.emf.emfstore.common.extensionpoint.ESResourceSetProvider;
import org.eclipse.emf.emfstore.internal.client.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.Workspace;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Project Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getProject <em>Project</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getProjectId <em>Project Id</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getProjectName <em>Project Name
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getProjectDescription <em>Project
 * Description </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getUsersession <em>Usersession </em>}
 * </li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getLastUpdated <em>Last Updated
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getBaseVersion <em>Base Version
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getResourceCount <em>Resource Count
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#isDirty <em>Dirty</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getOldLogMessages <em>Old Log
 * Messages</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getLocalOperations <em>Local
 * Operations</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getWaitingUploads <em>Waiting Uploads
 * </em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getProperties <em>Properties</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getChangedSharedProperties <em>
 * Changed Shared Properties</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceImpl#getWorkspace <em>Workspace</em>}</li>
 * </ul>
 * </p>
 * 
 */
public class ProjectSpaceImpl extends ProjectSpaceBase implements ProjectSpace {

	/**
	 * The cached value of the '{@link #getProjectId() <em>Project Id</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectId()
	 * @generated
	 * @ordered
	 */
	protected ProjectId projectId;

	/**
	 * The default value of the '{@link #getProjectName() <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProjectName() <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected String projectName = PROJECT_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getProjectDescription() <em>Project Description</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getProjectDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProjectDescription() <em>Project Description</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getProjectDescription()
	 * @generated
	 * @ordered
	 */
	protected String projectDescription = PROJECT_DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getUsersession() <em>Usersession</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUsersession()
	 * @generated
	 * @ordered
	 */
	protected Usersession usersession;

	/**
	 * The default value of the '{@link #getLastUpdated() <em>Last Updated</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getLastUpdated()
	 * @generated
	 * @ordered
	 */
	protected static final Date LAST_UPDATED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLastUpdated() <em>Last Updated</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getLastUpdated()
	 * @generated
	 * @ordered
	 */
	protected Date lastUpdated = LAST_UPDATED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getBaseVersion() <em>Base Version</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getBaseVersion()
	 * @generated
	 * @ordered
	 */
	protected PrimaryVersionSpec baseVersion;

	/**
	 * The default value of the '{@link #getResourceCount() <em>Resource Count</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getResourceCount()
	 * @generated
	 * @ordered
	 */
	protected static final int RESOURCE_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getResourceCount() <em>Resource Count</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getResourceCount()
	 * @generated
	 * @ordered
	 */
	protected int resourceCount = RESOURCE_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #isDirty() <em>Dirty</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #isDirty()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DIRTY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDirty() <em>Dirty</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #isDirty()
	 * @generated
	 * @ordered
	 */
	protected boolean dirty = DIRTY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOldLogMessages() <em>Old Log Messages</em>}' attribute list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getOldLogMessages()
	 * @generated
	 * @ordered
	 */
	protected EList<String> oldLogMessages;

	/**
	 * The cached value of the '{@link #getWaitingUploads()
	 * <em>Waiting Uploads</em>}' containment reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getWaitingUploads()
	 * @generated
	 * @ordered
	 */
	protected EList<FileIdentifier> waitingUploads;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<EMFStoreProperty> properties;

	/**
	 * The cached value of the '{@link #getChangedSharedProperties() <em>Changed Shared Properties</em>}' reference
	 * list.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getChangedSharedProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<EMFStoreProperty> changedSharedProperties;

	/**
	 * The cached value of the '{@link #getMergedVersion() <em>Merged Version</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @see #getMergedVersion()
	 * @generated
	 * @ordered
	 */
	protected PrimaryVersionSpec mergedVersion;

	private WeakReference<Project> projectReference = new WeakReference<Project>(null);
	private WeakReference<ChangePackage> localChangePackageReference = new WeakReference<ChangePackage>(null);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ProjectSpaceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PrimaryVersionSpec basicGetBaseVersion() {
		return baseVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ProjectId basicGetProjectId() {
		return projectId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Usersession basicGetUsersession() {
		return usersession;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Workspace basicGetWorkspace() {
		if (eContainerFeatureID() != ModelPackage.PROJECT_SPACE__WORKSPACE)
			return null;
		return (Workspace) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetBaseVersion(PrimaryVersionSpec newBaseVersion, NotificationChain msgs) {
		PrimaryVersionSpec oldBaseVersion = baseVersion;
		baseVersion = newBaseVersion;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				ModelPackage.PROJECT_SPACE__BASE_VERSION, oldBaseVersion, newBaseVersion);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetProjectId(ProjectId newProjectId, NotificationChain msgs) {
		ProjectId oldProjectId = projectId;
		projectId = newProjectId;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				ModelPackage.PROJECT_SPACE__PROJECT_ID, oldProjectId, newProjectId);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetWorkspace(Workspace newWorkspace, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newWorkspace, ModelPackage.PROJECT_SPACE__WORKSPACE, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID())
		{
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			return eInternalContainer().eInverseRemove(this, ModelPackage.WORKSPACE__PROJECT_SPACES, Workspace.class,
				msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID)
		{
		case ModelPackage.PROJECT_SPACE__PROJECT_ID:
			if (resolve)
				return getProjectId();
			return basicGetProjectId();
		case ModelPackage.PROJECT_SPACE__PROJECT_NAME:
			return getProjectName();
		case ModelPackage.PROJECT_SPACE__PROJECT_DESCRIPTION:
			return getProjectDescription();
		case ModelPackage.PROJECT_SPACE__USERSESSION:
			if (resolve)
				return getUsersession();
			return basicGetUsersession();
		case ModelPackage.PROJECT_SPACE__LAST_UPDATED:
			return getLastUpdated();
		case ModelPackage.PROJECT_SPACE__BASE_VERSION:
			if (resolve)
				return getBaseVersion();
			return basicGetBaseVersion();
		case ModelPackage.PROJECT_SPACE__RESOURCE_COUNT:
			return getResourceCount();
		case ModelPackage.PROJECT_SPACE__DIRTY:
			return isDirty();
		case ModelPackage.PROJECT_SPACE__OLD_LOG_MESSAGES:
			return getOldLogMessages();
		case ModelPackage.PROJECT_SPACE__WAITING_UPLOADS:
			return getWaitingUploads();
		case ModelPackage.PROJECT_SPACE__PROPERTIES:
			return getProperties();
		case ModelPackage.PROJECT_SPACE__CHANGED_SHARED_PROPERTIES:
			return getChangedSharedProperties();
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			if (resolve)
				return getWorkspace();
			return basicGetWorkspace();
		case ModelPackage.PROJECT_SPACE__MERGED_VERSION:
			if (resolve)
				return getMergedVersion();
			return basicGetMergedVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID)
		{
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetWorkspace((Workspace) otherEnd, msgs);
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
		switch (featureID)
		{
		case ModelPackage.PROJECT_SPACE__PROJECT_ID:
			return basicSetProjectId(null, msgs);
		case ModelPackage.PROJECT_SPACE__BASE_VERSION:
			return basicSetBaseVersion(null, msgs);
		case ModelPackage.PROJECT_SPACE__WAITING_UPLOADS:
			return ((InternalEList<?>) getWaitingUploads()).basicRemove(otherEnd, msgs);
		case ModelPackage.PROJECT_SPACE__PROPERTIES:
			return ((InternalEList<?>) getProperties()).basicRemove(otherEnd, msgs);
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			return basicSetWorkspace(null, msgs);
		case ModelPackage.PROJECT_SPACE__MERGED_VERSION:
			return basicSetMergedVersion(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID)
		{
		case ModelPackage.PROJECT_SPACE__PROJECT_ID:
			return projectId != null;
		case ModelPackage.PROJECT_SPACE__PROJECT_NAME:
			return PROJECT_NAME_EDEFAULT == null ? projectName != null : !PROJECT_NAME_EDEFAULT.equals(projectName);
		case ModelPackage.PROJECT_SPACE__PROJECT_DESCRIPTION:
			return PROJECT_DESCRIPTION_EDEFAULT == null ? projectDescription != null : !PROJECT_DESCRIPTION_EDEFAULT
				.equals(projectDescription);
		case ModelPackage.PROJECT_SPACE__USERSESSION:
			return usersession != null;
		case ModelPackage.PROJECT_SPACE__LAST_UPDATED:
			return LAST_UPDATED_EDEFAULT == null ? lastUpdated != null : !LAST_UPDATED_EDEFAULT.equals(lastUpdated);
		case ModelPackage.PROJECT_SPACE__BASE_VERSION:
			return baseVersion != null;
		case ModelPackage.PROJECT_SPACE__RESOURCE_COUNT:
			return resourceCount != RESOURCE_COUNT_EDEFAULT;
		case ModelPackage.PROJECT_SPACE__DIRTY:
			return dirty != DIRTY_EDEFAULT;
		case ModelPackage.PROJECT_SPACE__OLD_LOG_MESSAGES:
			return oldLogMessages != null && !oldLogMessages.isEmpty();
		case ModelPackage.PROJECT_SPACE__WAITING_UPLOADS:
			return waitingUploads != null && !waitingUploads.isEmpty();
		case ModelPackage.PROJECT_SPACE__PROPERTIES:
			return properties != null && !properties.isEmpty();
		case ModelPackage.PROJECT_SPACE__CHANGED_SHARED_PROPERTIES:
			return changedSharedProperties != null && !changedSharedProperties.isEmpty();
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			return basicGetWorkspace() != null;
		case ModelPackage.PROJECT_SPACE__MERGED_VERSION:
			return mergedVersion != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID)
		{
		case ModelPackage.PROJECT_SPACE__PROJECT_ID:
			setProjectId((ProjectId) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__PROJECT_NAME:
			setProjectName((String) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__PROJECT_DESCRIPTION:
			setProjectDescription((String) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__USERSESSION:
			setUsersession((Usersession) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__LAST_UPDATED:
			setLastUpdated((Date) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__BASE_VERSION:
			setBaseVersion((PrimaryVersionSpec) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__RESOURCE_COUNT:
			setResourceCount((Integer) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__DIRTY:
			setDirty((Boolean) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__OLD_LOG_MESSAGES:
			getOldLogMessages().clear();
			getOldLogMessages().addAll((Collection<? extends String>) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__WAITING_UPLOADS:
			getWaitingUploads().clear();
			getWaitingUploads().addAll((Collection<? extends FileIdentifier>) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__PROPERTIES:
			getProperties().clear();
			getProperties().addAll((Collection<? extends EMFStoreProperty>) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__CHANGED_SHARED_PROPERTIES:
			getChangedSharedProperties().clear();
			getChangedSharedProperties().addAll((Collection<? extends EMFStoreProperty>) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			setWorkspace((Workspace) newValue);
			return;
		case ModelPackage.PROJECT_SPACE__MERGED_VERSION:
			setMergedVersion((PrimaryVersionSpec) newValue);
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
	protected EClass eStaticClass() {
		return ModelPackage.Literals.PROJECT_SPACE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID)
		{
		case ModelPackage.PROJECT_SPACE__PROJECT_ID:
			setProjectId((ProjectId) null);
			return;
		case ModelPackage.PROJECT_SPACE__PROJECT_NAME:
			setProjectName(PROJECT_NAME_EDEFAULT);
			return;
		case ModelPackage.PROJECT_SPACE__PROJECT_DESCRIPTION:
			setProjectDescription(PROJECT_DESCRIPTION_EDEFAULT);
			return;
		case ModelPackage.PROJECT_SPACE__USERSESSION:
			setUsersession((Usersession) null);
			return;
		case ModelPackage.PROJECT_SPACE__LAST_UPDATED:
			setLastUpdated(LAST_UPDATED_EDEFAULT);
			return;
		case ModelPackage.PROJECT_SPACE__BASE_VERSION:
			setBaseVersion((PrimaryVersionSpec) null);
			return;
		case ModelPackage.PROJECT_SPACE__RESOURCE_COUNT:
			setResourceCount(RESOURCE_COUNT_EDEFAULT);
			return;
		case ModelPackage.PROJECT_SPACE__DIRTY:
			setDirty(DIRTY_EDEFAULT);
			return;
		case ModelPackage.PROJECT_SPACE__OLD_LOG_MESSAGES:
			getOldLogMessages().clear();
			return;
		case ModelPackage.PROJECT_SPACE__WAITING_UPLOADS:
			getWaitingUploads().clear();
			return;
		case ModelPackage.PROJECT_SPACE__PROPERTIES:
			getProperties().clear();
			return;
		case ModelPackage.PROJECT_SPACE__CHANGED_SHARED_PROPERTIES:
			getChangedSharedProperties().clear();
			return;
		case ModelPackage.PROJECT_SPACE__WORKSPACE:
			setWorkspace((Workspace) null);
			return;
		case ModelPackage.PROJECT_SPACE__MERGED_VERSION:
			setMergedVersion((PrimaryVersionSpec) null);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PrimaryVersionSpec getBaseVersion() {
		if (baseVersion != null && baseVersion.eIsProxy())
		{
			InternalEObject oldBaseVersion = (InternalEObject) baseVersion;
			baseVersion = (PrimaryVersionSpec) eResolveProxy(oldBaseVersion);
			if (baseVersion != oldBaseVersion)
			{
				InternalEObject newBaseVersion = (InternalEObject) baseVersion;
				NotificationChain msgs = oldBaseVersion.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__BASE_VERSION, null, null);
				if (newBaseVersion.eInternalContainer() == null)
				{
					msgs = newBaseVersion.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ModelPackage.PROJECT_SPACE__BASE_VERSION, null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.PROJECT_SPACE__BASE_VERSION,
						oldBaseVersion, baseVersion));
			}
		}
		return baseVersion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<EMFStoreProperty> getChangedSharedProperties() {
		if (changedSharedProperties == null)
		{
			changedSharedProperties = new EObjectResolvingEList<EMFStoreProperty>(EMFStoreProperty.class, this,
				ModelPackage.PROJECT_SPACE__CHANGED_SHARED_PROPERTIES);
		}
		return changedSharedProperties;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<String> getOldLogMessages() {
		if (oldLogMessages == null)
		{
			oldLogMessages = new EDataTypeUniqueEList<String>(String.class, this,
				ModelPackage.PROJECT_SPACE__OLD_LOG_MESSAGES);
		}
		return oldLogMessages;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getProjectDescription() {
		return projectDescription;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ProjectId getProjectId() {
		if (projectId != null && projectId.eIsProxy())
		{
			InternalEObject oldProjectId = (InternalEObject) projectId;
			projectId = (ProjectId) eResolveProxy(oldProjectId);
			if (projectId != oldProjectId)
			{
				InternalEObject newProjectId = (InternalEObject) projectId;
				NotificationChain msgs = oldProjectId.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__PROJECT_ID, null, null);
				if (newProjectId.eInternalContainer() == null)
				{
					msgs = newProjectId.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ModelPackage.PROJECT_SPACE__PROJECT_ID, null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.PROJECT_SPACE__PROJECT_ID,
						oldProjectId, projectId));
			}
		}
		return projectId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<EMFStoreProperty> getProperties() {
		if (properties == null)
		{
			properties = new EObjectContainmentEList.Resolving<EMFStoreProperty>(EMFStoreProperty.class, this,
				ModelPackage.PROJECT_SPACE__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getResourceCount() {
		return resourceCount;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Usersession getUsersession() {
		if (usersession != null && usersession.eIsProxy())
		{
			InternalEObject oldUsersession = (InternalEObject) usersession;
			usersession = (Usersession) eResolveProxy(oldUsersession);
			if (usersession != oldUsersession)
			{
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelPackage.PROJECT_SPACE__USERSESSION,
						oldUsersession, usersession));
			}
		}
		return usersession;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<FileIdentifier> getWaitingUploads() {
		if (waitingUploads == null)
		{
			waitingUploads = new EObjectContainmentEList.Resolving<FileIdentifier>(FileIdentifier.class, this,
				ModelPackage.PROJECT_SPACE__WAITING_UPLOADS);
		}
		return waitingUploads;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Workspace getWorkspace() {
		if (eContainerFeatureID() != ModelPackage.PROJECT_SPACE__WORKSPACE)
			return null;
		return (Workspace) eContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setBaseVersion(PrimaryVersionSpec newBaseVersion) {
		if (newBaseVersion != baseVersion)
		{
			NotificationChain msgs = null;
			if (baseVersion != null)
				msgs = ((InternalEObject) baseVersion).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__BASE_VERSION, null, msgs);
			if (newBaseVersion != null)
				msgs = ((InternalEObject) newBaseVersion).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__BASE_VERSION, null, msgs);
			msgs = basicSetBaseVersion(newBaseVersion, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__BASE_VERSION,
				newBaseVersion, newBaseVersion));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDirty(boolean newDirty) {
		boolean oldDirty = dirty;
		dirty = newDirty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__DIRTY, oldDirty, dirty));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setLastUpdated(Date newLastUpdated) {
		Date oldLastUpdated = lastUpdated;
		lastUpdated = newLastUpdated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__LAST_UPDATED,
				oldLastUpdated, lastUpdated));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setProjectDescription(String newProjectDescription) {
		String oldProjectDescription = projectDescription;
		projectDescription = newProjectDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__PROJECT_DESCRIPTION,
				oldProjectDescription, projectDescription));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setProjectId(ProjectId newProjectId) {
		if (newProjectId != projectId)
		{
			NotificationChain msgs = null;
			if (projectId != null)
				msgs = ((InternalEObject) projectId).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__PROJECT_ID, null, msgs);
			if (newProjectId != null)
				msgs = ((InternalEObject) newProjectId).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__PROJECT_ID, null, msgs);
			msgs = basicSetProjectId(newProjectId, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__PROJECT_ID, newProjectId,
				newProjectId));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setProjectName(String newProjectName) {
		String oldProjectName = projectName;
		projectName = newProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__PROJECT_NAME,
				oldProjectName, projectName));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setResourceCount(int newResourceCount) {
		int oldResourceCount = resourceCount;
		resourceCount = newResourceCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__RESOURCE_COUNT,
				oldResourceCount, resourceCount));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setUsersession(Usersession newUsersession) {
		Usersession oldUsersession = usersession;
		usersession = newUsersession;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__USERSESSION,
				oldUsersession, usersession));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setWorkspace(Workspace newWorkspace) {
		if (newWorkspace != eInternalContainer()
			|| (eContainerFeatureID() != ModelPackage.PROJECT_SPACE__WORKSPACE && newWorkspace != null))
		{
			if (EcoreUtil.isAncestor(this, newWorkspace))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newWorkspace != null)
				msgs = ((InternalEObject) newWorkspace).eInverseAdd(this, ModelPackage.WORKSPACE__PROJECT_SPACES,
					Workspace.class, msgs);
			msgs = basicSetWorkspace(newWorkspace, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__WORKSPACE, newWorkspace,
				newWorkspace));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PrimaryVersionSpec getMergedVersion() {
		if (mergedVersion != null && mergedVersion.eIsProxy())
		{
			InternalEObject oldMergedVersion = (InternalEObject) mergedVersion;
			mergedVersion = (PrimaryVersionSpec) eResolveProxy(oldMergedVersion);
			if (mergedVersion != oldMergedVersion)
			{
				InternalEObject newMergedVersion = (InternalEObject) mergedVersion;
				NotificationChain msgs = oldMergedVersion.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__MERGED_VERSION, null, null);
				if (newMergedVersion.eInternalContainer() == null)
				{
					msgs = newMergedVersion.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
						- ModelPackage.PROJECT_SPACE__MERGED_VERSION, null, msgs);
				}
				if (msgs != null)
					msgs.dispatch();
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
						ModelPackage.PROJECT_SPACE__MERGED_VERSION, oldMergedVersion, mergedVersion));
			}
		}
		return mergedVersion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public PrimaryVersionSpec basicGetMergedVersion() {
		return mergedVersion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetMergedVersion(PrimaryVersionSpec newMergedVersion, NotificationChain msgs) {
		PrimaryVersionSpec oldMergedVersion = mergedVersion;
		mergedVersion = newMergedVersion;
		if (eNotificationRequired())
		{
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
				ModelPackage.PROJECT_SPACE__MERGED_VERSION, oldMergedVersion, newMergedVersion);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setMergedVersion(PrimaryVersionSpec newMergedVersion) {
		if (newMergedVersion != mergedVersion)
		{
			NotificationChain msgs = null;
			if (mergedVersion != null)
				msgs = ((InternalEObject) mergedVersion).eInverseRemove(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__MERGED_VERSION, null, msgs);
			if (newMergedVersion != null)
				msgs = ((InternalEObject) newMergedVersion).eInverseAdd(this, EOPPOSITE_FEATURE_BASE
					- ModelPackage.PROJECT_SPACE__MERGED_VERSION, null, msgs);
			msgs = basicSetMergedVersion(newMergedVersion, msgs);
			if (msgs != null)
				msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.PROJECT_SPACE__MERGED_VERSION,
				newMergedVersion, newMergedVersion));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (projectName: ");
		result.append(projectName);
		result.append(", projectDescription: ");
		result.append(projectDescription);
		result.append(", lastUpdated: ");
		result.append(lastUpdated);
		result.append(", resourceCount: ");
		result.append(resourceCount);
		result.append(", dirty: ");
		result.append(dirty);
		result.append(", oldLogMessages: ");
		result.append(oldLogMessages);
		result.append(')');
		return result.toString();
	}

	// custom code
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.model.ProjectSpace#getProject()
	 */
	public Project getProject() {
		Project project = projectReference.get();
		if (project == null) {
			URI projectURI = ClientURIUtil.createProjectURI(this);
			project = (Project) loadEObjectFromURI(projectURI);
			setProject(project);
			init();
		}

		return project;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.model.ProjectSpace#setProject(org.eclipse.emf.emfstore.internal.common.model.Project)
	 */
	public void setProject(Project newProject) {
		this.projectReference = new WeakReference<Project>(newProject);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.model.ProjectSpace#getLocalChangePackage()
	 */
	public ChangePackage getLocalChangePackage() {
		ChangePackage lcp = localChangePackageReference.get();
		if (lcp == null) {
			URI lcpURI = ClientURIUtil.createOperationsURI(this);
			lcp = (ChangePackage) loadEObjectFromURI(lcpURI);
			setLocalChangePackage(lcp);
		}

		return lcp;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.model.ProjectSpace#setLocalChangePackage(org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage)
	 */
	public void setLocalChangePackage(ChangePackage newLocalChangePackage) {
		this.localChangePackageReference = new WeakReference<ChangePackage>(newLocalChangePackage);
	}

	private EObject loadEObjectFromURI(URI uri) {
		ESExtensionPoint extensionPoint = new ESExtensionPoint("org.eclipse.emf.emfstore.client.resourceSetProvider",
			true);
		extensionPoint.setComparator(new ESPriorityComparator("priority", true));
		extensionPoint.reload();

		ESResourceSetProvider resourceSetProvider = extensionPoint.getElementWithHighestPriority().getClass("class",
			ESResourceSetProvider.class);

		ResourceSet resourceSet = resourceSetProvider.getResourceSet();
		if (resourceSet.getURIConverter().exists(uri, null)) {
			Resource resource = resourceSet.getResource(uri, true);
			return resource.getContents().get(0);
		} else {
			return null;
		}
	}
}