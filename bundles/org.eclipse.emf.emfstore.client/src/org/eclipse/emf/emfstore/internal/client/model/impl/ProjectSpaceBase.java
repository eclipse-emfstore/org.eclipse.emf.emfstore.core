/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * wesendon
 * emueller
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil.UsageCrossReferencer;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.callbacks.ESCommitCallback;
import org.eclipse.emf.emfstore.client.callbacks.ESUpdateCallback;
import org.eclipse.emf.emfstore.client.handler.ESRunnableContext;
import org.eclipse.emf.emfstore.client.observer.ESLoginObserver;
import org.eclipse.emf.emfstore.client.observer.ESMergeObserver;
import org.eclipse.emf.emfstore.common.ESDisposable;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ExtensionRegistry;
import org.eclipse.emf.emfstore.internal.client.importexport.impl.ExportChangesController;
import org.eclipse.emf.emfstore.internal.client.importexport.impl.ExportProjectController;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.commands.EMFStoreCommandStack;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.ConflictResolver;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.recording.NotificationRecorder;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ServerCall;
import org.eclipse.emf.emfstore.internal.client.model.controller.ChangeConflict;
import org.eclipse.emf.emfstore.internal.client.model.controller.CommitController;
import org.eclipse.emf.emfstore.internal.client.model.controller.ShareController;
import org.eclipse.emf.emfstore.internal.client.model.controller.UpdateController;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.IllegalProjectSpaceStateException;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.MEUrlResolutionException;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.PropertyNotFoundException;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileDownloadStatus;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileInformation;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileTransferManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.client.observers.DeleteProjectSpaceObserver;
import org.eclipse.emf.emfstore.internal.client.properties.PropertyManager;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.impl.IdentifiableElementImpl;
import org.eclipse.emf.emfstore.internal.common.model.impl.ProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucketCandidate;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictDetector;
import org.eclipse.emf.emfstore.internal.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidVersionSpecException;
import org.eclipse.emf.emfstore.internal.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.internal.server.model.url.ModelElementUrlFragment;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Versions;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

/**
 * Project space base class that contains custom user methods.
 * 
 * @author koegel
 * @author wesendon
 * @author emueller
 * 
 */
public abstract class ProjectSpaceBase extends IdentifiableElementImpl implements ProjectSpace, ESLoginObserver,
	ESDisposable {

	private ESLocalProjectImpl esLocalProjectImpl;

	private boolean initCompleted;
	private boolean isTransient;
	private boolean disposed;

	private FileTransferManager fileTransferManager;
	private OperationManager operationManager;

	private PropertyManager propertyManager;
	private Map<String, OrgUnitProperty> propertyMap;

	private ResourceSet resourceSet;
	private ResourcePersister resourcePersister;

	private ECrossReferenceAdapter crossReferenceAdapter;
	private ESRunnableContext runnableContext;

	/**
	 * Constructor.
	 */
	public ProjectSpaceBase() {
		propertyMap = new LinkedHashMap<String, OrgUnitProperty>();
		initRunnableContext();
	}

	private void initRunnableContext() {
		setRunnableContext(ExtensionRegistry.INSTANCE.get(
			RUNNABLE_CONTEXT_ID,
			ESRunnableContext.class,
			new DefaultRunnableContext(), true));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#addFile(java.io.File)
	 */
	public FileIdentifier addFile(File file) throws FileTransferException {
		return fileTransferManager.addFile(file);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#addOperations(java.util.List)
	 */
	public void addOperations(List<? extends AbstractOperation> operations) {
		getOperations().addAll(operations);
		updateDirtyState();

		for (AbstractOperation op : operations) {
			operationManager.notifyOperationExecuted(op);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#addTag(org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec,
	 *      org.eclipse.emf.emfstore.server.model.versioning.TagVersionSpec)
	 */
	public void addTag(PrimaryVersionSpec versionSpec, TagVersionSpec tag) throws ESException {
		final ConnectionManager cm = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		cm.addTag(getUsersession().getSessionId(), getProjectId(), versionSpec, tag);
	}

	/**
	 * Helper method which applies merged changes on the ProjectSpace. This
	 * method is used by merge mechanisms in update as well as branch merging.
	 * 
	 * @param baseSpec
	 *            new base version
	 * @param incoming
	 *            changes from the current branch
	 * @param myChanges
	 *            merged changes
	 * 
	 * @throws ESException in case the checksum comparison failed and the activated IChecksumErrorHandler
	 *             also failed
	 */
	public void applyChanges(PrimaryVersionSpec baseSpec, List<ChangePackage> incoming, ChangePackage myChanges)
		throws ESException {
		applyChanges(baseSpec, incoming, myChanges, ESUpdateCallback.NOCALLBACK, new NullProgressMonitor());
	}

	/**
	 * Helper method which applies merged changes on the ProjectSpace. This
	 * method is used by merge mechanisms in update as well as branch merging.
	 * 
	 * @param baseSpec
	 *            new base version
	 * @param incoming
	 *            changes from the current branch
	 * @param myChanges
	 *            merged changes
	 * @param callback
	 *            a {@link ESUpdateCallback} that is used to handle a possibly occurring checksum error
	 * @param progressMonitor
	 *            an {@link IProgressMonitor} to inform about the progress of the UpdateCallback in case it is called
	 * 
	 * @throws ESException in case the checksum comparison failed and the activated IChecksumErrorHandler
	 *             also failed
	 */
	public void applyChanges(PrimaryVersionSpec baseSpec, List<ChangePackage> incoming, ChangePackage myChanges,
		ESUpdateCallback callback, IProgressMonitor progressMonitor) throws ESException {

		// revert local changes
		notifyPreRevertMyChanges(getLocalChangePackage());
		revert();
		notifyPostRevertMyChanges();

		// apply changes from repo. incoming (aka theirs)
		applyChangePackages(incoming, false);
		notifyPostApplyTheirChanges(incoming);

		progressMonitor.subTask("Computing checksum");
		if (!performChecksumCheck(baseSpec, getProject())) {
			progressMonitor.subTask("Invalid checksum.  Activating checksum error handler.");
			boolean errorHandled = callback.checksumCheckFailed(this.toAPI(), baseSpec.toAPI(),
				progressMonitor);
			if (!errorHandled) {
				// rollback
				for (int i = incoming.size() - 1; i >= 0; i--) {
					applyChangePackage(incoming.get(i).reverse(), false);
				}
				applyChangePackage(getLocalChangePackage(), true);

				throw new ESException("Update cancelled by checksum error handler due to invalid checksum.");
			}
		}

		// reapply local changes
		applyOperations(myChanges.getOperations(), true);
		notifyPostApplyMergedChanges(myChanges);

		setBaseVersion(baseSpec);
		saveProjectSpaceOnly();
	}

	private void applyChangePackage(ChangePackage changePackage, boolean addOperations) {
		applyOperations(changePackage.getOperations(), addOperations);
	}

	private void applyChangePackages(List<ChangePackage> changePackages, boolean addOperations) {
		for (ChangePackage changePackage : changePackages) {
			applyChangePackage(changePackage, addOperations);
		}
	}

	private boolean performChecksumCheck(PrimaryVersionSpec baseVersion, Project project) {

		if (Configuration.getClientBehavior().isChecksumCheckActive()) {
			long expectedChecksum = baseVersion.getProjectStateChecksum();
			try {
				long computedChecksum = ModelUtil.computeChecksum(project);
				return expectedChecksum == computedChecksum;
			} catch (SerializationException e) {
				WorkspaceUtil.logWarning("Could not compute checksum while applying changes.", e);
			}
		}

		return true;
	}

	/**
	 * Applies a list of operations to the project. The change tracking will be
	 * stopped meanwhile.
	 * 
	 * 
	 * @param operations
	 *            the list of operations to be applied upon the project space
	 * @param addOperations
	 *            whether the operations should be saved in project space
	 * 
	 * @see #applyOperationsWithRecording(List, boolean)
	 */
	public void applyOperations(List<AbstractOperation> operations, boolean addOperations) {
		executeRunnable(new ApplyOperationsRunnable(this, operations, addOperations));
	}

	/**
	 * Executes a given {@link Runnable} in the context of this {@link ProjectSpace}.<br>
	 * The {@link Runnable} usually modifies the Project contained in the {@link ProjectSpace}.
	 * 
	 * @param runnable
	 *            the {@link Runnable} to be executed in the context of this {@link ProjectSpace}
	 */
	public void executeRunnable(Runnable runnable) {
		getRunnableContext().executeRunnable(runnable);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#beginCompositeOperation()
	 */
	public CompositeOperationHandle beginCompositeOperation() {
		return this.operationManager.beginCompositeOperation();
	}

	/**
	 * Removes the elements that are marked as cutted from the project.
	 */
	public void cleanCutElements() {
		List<EObject> cutElements = new ArrayList<EObject>(getProject().getCutElements());
		for (EObject cutElement : cutElements) {
			getProject().deleteModelElement(cutElement);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#commit()
	 */
	public PrimaryVersionSpec commit(IProgressMonitor monitor) throws ESException {
		return new CommitController(this, null, null, monitor).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#commit(org.eclipse.emf.emfstore.server.model.versioning.LogMessage,
	 *      org.eclipse.emf.emfstore.client.model.controller.callbacks.CommitCallback,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public PrimaryVersionSpec commit(LogMessage logMessage, ESCommitCallback callback, IProgressMonitor monitor)
		throws ESException {
		return new CommitController(this, logMessage, callback, monitor).execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public PrimaryVersionSpec commitToBranch(BranchVersionSpec branch, LogMessage logMessage,
		ESCommitCallback callback,
		IProgressMonitor monitor) throws ESException {
		return new CommitController(this, branch, logMessage, callback, monitor).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#exportLocalChanges(java.io.File,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void exportLocalChanges(File file, IProgressMonitor progressMonitor) throws IOException {
		new ExportChangesController(this).execute(file, progressMonitor);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#exportLocalChanges(java.io.File)
	 */
	public void exportLocalChanges(File file) throws IOException {
		new ExportChangesController(this).execute(file, new NullProgressMonitor());
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#exportProject(java.io.File,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void exportProject(File file, IProgressMonitor progressMonitor) throws IOException {
		new ExportProjectController(this).execute(file, progressMonitor);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#exportProject(java.io.File)
	 */
	public void exportProject(File file) throws IOException {
		new ExportProjectController(this).execute(file, new NullProgressMonitor());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @generated NOT
	 */
	public List<ChangePackage> getChanges(VersionSpec sourceVersion, VersionSpec targetVersion)
		throws InvalidVersionSpecException, ESException {
		// TODO: is this a server call?
		final ConnectionManager connectionManager = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		List<ChangePackage> changes = connectionManager.getChanges(getUsersession().getSessionId(), getProjectId(),
			sourceVersion, targetVersion);
		return changes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getFile(org.eclipse.emf.emfstore.server.model.FileIdentifier,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public FileDownloadStatus getFile(FileIdentifier fileIdentifier) throws FileTransferException {
		return fileTransferManager.getFile(fileIdentifier);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getFileInfo(org.eclipse.emf.emfstore.server.model.FileIdentifier)
	 */
	public FileInformation getFileInfo(FileIdentifier fileIdentifier) {
		return fileTransferManager.getFileInfo(fileIdentifier);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getLocalChangePackage()
	 */
	public ChangePackage getLocalChangePackage(boolean canonize) {
		ChangePackage changePackage = VersioningFactory.eINSTANCE.createChangePackage();
		// copy operations from ProjectSpace
		for (AbstractOperation abstractOperation : getOperations()) {
			AbstractOperation copy = ModelUtil.clone(abstractOperation);
			changePackage.getOperations().add(copy);
		}

		return changePackage;
	}

	/**
	 * Get the current notification recorder.
	 * 
	 * @return the recorder
	 */
	public NotificationRecorder getNotificationRecorder() {
		return this.operationManager.getNotificationRecorder();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getOperationManager()
	 */
	public OperationManager getOperationManager() {
		return operationManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getOperations()
	 */
	public List<AbstractOperation> getOperations() {
		ChangePackage localChangePackage = getLocalChangePackage();
		if (localChangePackage == null) {
			this.setLocalChangePackage(VersioningFactory.eINSTANCE.createChangePackage());
			localChangePackage = getLocalChangePackage();
		}

		if (getLocalOperations() != null) {
			migrateOperations(localChangePackage);
		}

		return localChangePackage.getOperations();
	}

	private void migrateOperations(ChangePackage localChangePackage) {

		if (getLocalOperations() == null || getLocalOperations().getOperations().size() == 0 || isTransient()) {
			return;
		}

		localChangePackage.getOperations().addAll(getLocalOperations().getOperations());

		Resource eResource = getLocalOperations().eResource();
		// if for some reason the resource of project space and operations
		// are not different, then reinitialize operations URI
		// TODO: first case kills change package
		if (this.eResource() == eResource) {
			String localChangePackageFileName = Configuration.getFileInfo().getWorkspaceDirectory()
				+ Configuration.getFileInfo().getProjectSpaceDirectoryPrefix() + getIdentifier() + File.separatorChar
				+ Configuration.getFileInfo().getLocalChangePackageFileName()
				+ Configuration.getFileInfo().getLocalChangePackageFileExtension();
			eResource = resourceSet.createResource(URI.createFileURI(localChangePackageFileName));
		} else {
			eResource.getContents().remove(0);
		}
		setLocalOperations(null);
		eResource.getContents().add(localChangePackage);
		saveResource(eResource);
		save();

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getProjectInfo()
	 * @generated NOT
	 */
	public ProjectInfo getProjectInfo() {
		ProjectInfo projectInfo = org.eclipse.emf.emfstore.internal.server.model.ModelFactory.eINSTANCE
			.createProjectInfo();
		projectInfo.setProjectId(ModelUtil.clone(getProjectId()));
		projectInfo.setName(getProjectName());
		projectInfo.setDescription(getProjectDescription());
		projectInfo.setVersion(ModelUtil.clone(getBaseVersion()));
		return projectInfo;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getPropertyManager()
	 */
	public PropertyManager getPropertyManager() {
		if (this.propertyManager == null) {
			this.propertyManager = new PropertyManager(this);
		}

		return this.propertyManager;
	}

	/**
	 * getter for a string argument - see {@link #setProperty(OrgUnitProperty)}.
	 */
	private OrgUnitProperty getProperty(String name) throws PropertyNotFoundException {
		// sanity checks
		if (getUsersession() != null && getUsersession().getACUser() != null) {
			OrgUnitProperty orgUnitProperty = propertyMap.get(name);
			if (orgUnitProperty != null) {
				return orgUnitProperty;
			}
		}
		throw new PropertyNotFoundException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#importLocalChanges(java.lang.String)
	 */
	public void importLocalChanges(String fileName) throws IOException {

		ResourceSetImpl resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createFileURI(fileName), true);
		EList<EObject> directContents = resource.getContents();
		// sanity check

		if (directContents.size() != 1 && (!(directContents.get(0) instanceof ChangePackage))) {
			throw new IOException("File is corrupt, does not contain Changes.");
		}

		ChangePackage changePackage = (ChangePackage) directContents.get(0);

		if (!initCompleted) {
			init();
		}

		applyOperations(changePackage.getOperations(), true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#init()
	 * @generated NOT
	 */
	public void init() {
		initCrossReferenceAdapter();

		EMFStoreCommandStack commandStack = (EMFStoreCommandStack)
			ESWorkspaceProviderImpl.getInstance().getEditingDomain().getCommandStack();

		fileTransferManager = new FileTransferManager(this);
		operationManager = new OperationManager(this);

		initResourcePersister();

		commandStack.addCommandStackObserver(operationManager);
		commandStack.addCommandStackObserver(resourcePersister);

		// initialization order is important!
		getProject().addIdEObjectCollectionChangeObserver(operationManager);
		getProject().addIdEObjectCollectionChangeObserver(resourcePersister);

		if (getProject() instanceof ProjectImpl) {
			((ProjectImpl) this.getProject()).setUndetachable(operationManager);
			((ProjectImpl) this.getProject()).setUndetachable(resourcePersister);
		}

		initPropertyMap();

		startChangeRecording();
		cleanCutElements();

		initCompleted = true;
	}

	@SuppressWarnings("unchecked")
	private void initPropertyMap() {
		// TODO: deprecated, OrgUnitPropertiy will be removed soon
		if (getUsersession() != null) {
			ESWorkspaceProviderImpl.getObserverBus().register(this, ESLoginObserver.class);
			ACUser acUser = getUsersession().getACUser();
			if (acUser != null) {
				for (OrgUnitProperty p : acUser.getProperties()) {
					if (p.getProject() != null && p.getProject().equals(getProjectId())) {
						propertyMap.put(p.getName(), p);
					}
				}
			}
		}
	}

	private void initCrossReferenceAdapter() {

		// default
		boolean useCrossReferenceAdapter = true;

		for (ESExtensionElement element : new ESExtensionPoint(
			"org.eclipse.emf.emfstore.client.inverseCrossReferenceCache")
			.getExtensionElements()) {
			useCrossReferenceAdapter &= element.getBoolean("activated");
		}

		if (useCrossReferenceAdapter) {
			crossReferenceAdapter = new ECrossReferenceAdapter();
			getProject().eAdapters().add(crossReferenceAdapter);
		}
	}

	private void initResourcePersister() {

		resourcePersister = new ResourcePersister(getProject());

		if (!isTransient) {
			resourcePersister.addResource(this.eResource());
			resourcePersister.addResource(getLocalChangePackage().eResource());
			resourcePersister.addResource(getProject().eResource());
			resourcePersister.addDirtyStateChangeLister(new ProjectSpaceSaveStateNotifier(this));
			ESWorkspaceProviderImpl.getObserverBus().register(resourcePersister);
		}
	}

	/**
	 * Returns the file transfer manager.
	 * 
	 * @return the file transfer manager
	 */
	public FileTransferManager getFileTransferManager() {
		return fileTransferManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#initResources(org.eclipse.emf.ecore.resource.ResourceSet)
	 * @generated NOT
	 */
	public void initResources(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
		initCompleted = true;
		String projectSpaceFileNamePrefix = Configuration.getFileInfo().getWorkspaceDirectory()
			+ Configuration.getFileInfo().getProjectSpaceDirectoryPrefix() + getIdentifier() + File.separatorChar;
		String projectSpaceFileName = projectSpaceFileNamePrefix
			+ Configuration.getFileInfo().getProjectSpaceFileName()
			+ Configuration.getFileInfo().getProjectSpaceFileExtension();
		String localChangePackageFileName = projectSpaceFileNamePrefix
			+ Configuration.getFileInfo().getLocalChangePackageFileName()
			+ Configuration.getFileInfo().getLocalChangePackageFileExtension();
		URI projectSpaceURI = URI.createFileURI(projectSpaceFileName);
		URI localChangePackageURI = URI.createFileURI(localChangePackageFileName);

		setResourceCount(0);
		String fileName = projectSpaceFileNamePrefix + Configuration.getFileInfo().getProjectFragmentFileName()
			+ Configuration.getFileInfo().getProjectFragmentFileExtension();
		URI fileURI = URI.createFileURI(fileName);

		List<Resource> resources = new ArrayList<Resource>();
		Resource resource = resourceSet.createResource(fileURI);
		// if resource splitting fails, we need a reference to the old resource
		resource.getContents().add(this.getProject());
		resources.add(resource);
		setResourceCount(getResourceCount() + 1);

		for (EObject modelElement : getProject().getAllModelElements()) {
			((XMIResource) resource).setID(modelElement, getProject().getModelElementId(modelElement).getId());
		}

		Resource localChangePackageResource = resourceSet.createResource(localChangePackageURI);
		if (this.getLocalChangePackage() == null) {
			this.setLocalChangePackage(VersioningFactory.eINSTANCE.createChangePackage());
		}
		localChangePackageResource.getContents().add(this.getLocalChangePackage());
		resources.add(localChangePackageResource);

		Resource projectSpaceResource = resourceSet.createResource(projectSpaceURI);
		projectSpaceResource.getContents().add(this);
		resources.add(projectSpaceResource);

		// save all resources that have been created
		for (Resource currentResource : resources) {
			try {
				ModelUtil.saveResource(currentResource, WorkspaceUtil.getResourceLogger());
			} catch (IOException e) {
				WorkspaceUtil.logException("Project Space resource init failed!", e);
			}
		}

		init();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.model.ProjectSpace#delete()
	 * @generated NOT
	 */
	public void delete(IProgressMonitor monitor) throws IOException {

		ESWorkspaceProviderImpl.getObserverBus().notify(DeleteProjectSpaceObserver.class).projectSpaceDeleted(this);

		// delete project to notify listeners
		getProject().delete();

		String pathToProject = Configuration.getFileInfo().getWorkspaceDirectory()
			+ Configuration.getFileInfo().ProjectSpaceDirectoryPrefix + getIdentifier();

		resourceSet.getResources().remove(getProject().eResource());
		resourceSet.getResources().remove(eResource());
		resourceSet.getResources().remove(getLocalChangePackage().eResource());

		// TODO: remove project space from workspace, this is not the case if delete
		// is performed via Workspace#deleteProjectSpace
		ESWorkspaceProviderImpl.getInstance().getInternalWorkspace().getProjectSpaces().remove(this);

		dispose();

		deleteResource(getProject().eResource());
		deleteResource(eResource());
		deleteResource(getLocalChangePackage().eResource());

		// delete folder of project space
		FileUtil.deleteDirectory(new File(pathToProject), true);
	}

	private void deleteResource(Resource resource) throws IOException {
		if (resource != null) {
			resource.delete(null);
		}
	}

	/**
	 * Returns the {@link ECrossReferenceAdapter}, if available.
	 * 
	 * @param modelElement
	 *            the model element for which to find inverse cross references
	 * 
	 * @return the {@link ECrossReferenceAdapter}
	 */
	public Collection<Setting> findInverseCrossReferences(EObject modelElement) {
		if (crossReferenceAdapter != null) {
			return crossReferenceAdapter.getInverseReferences(modelElement);
		}

		return UsageCrossReferencer.find(modelElement, resourceSet);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#getResourceSet()
	 */
	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#setResourceSet(org.eclipse.emf.ecore.resource.ResourceSet)
	 */
	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#isTransient()
	 */
	public boolean isTransient() {
		return isTransient;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#isUpdated()
	 */
	public boolean isUpdated() throws ESException {
		PrimaryVersionSpec headVersion = resolveVersionSpec(Versions.createHEAD(getBaseVersion()),
			new NullProgressMonitor());
		return getBaseVersion().equals(headVersion);
	}

	/**
	 * {@inheritDoc}
	 */
	public void loginCompleted(ESUsersession session) {
		// TODO Implement possibility in observerbus to register only for
		// certain notifier
		if (getUsersession() == null || !getUsersession().toAPI().equals(session)) {
			return;
		}
		try {
			transmitProperties();
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (RuntimeException e) {
			// END SUPRESS CATCH EXCEPTION
			WorkspaceUtil.logException("Resuming file transfers or transmitting properties failed!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#makeTransient()
	 */
	public void makeTransient() {
		if (initCompleted) {
			throw new IllegalAccessError("Project Space cannot be set to transient after init.");
		}
		isTransient = true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public boolean merge(PrimaryVersionSpec target, ChangeConflict conflict,
		ConflictResolver conflictResolver, ESUpdateCallback callback, IProgressMonitor progressMonitor)
		throws ESException {
		// merge the conflicts
		if (conflictResolver.resolveConflicts(getProject(), conflict,
			getBaseVersion(), target)) {
			progressMonitor.subTask("Conflicts resolved, calculating result");
			ChangePackage mergedResult = conflictResolver.getMergedResult();
			applyChanges(target, conflict.getNewPackages(), mergedResult,
				callback, progressMonitor);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void mergeBranch(final PrimaryVersionSpec branchSpec, final ConflictResolver conflictResolver,
		final IProgressMonitor monitor)
		throws ESException {
		new ServerCall<Void>(this) {
			@Override
			protected Void run() throws ESException {
				if (branchSpec == null || conflictResolver == null) {
					throw new IllegalArgumentException("Arguments must not be null.");
				}
				if (Versions.isSameBranch(getBaseVersion(), branchSpec)) {
					throw new InvalidVersionSpecException("Can't merge branch with itself.");
				}
				PrimaryVersionSpec commonAncestor = resolveVersionSpec(Versions.createANCESTOR(getBaseVersion(),
					branchSpec), monitor);
				List<ChangePackage> baseChanges = getChanges(commonAncestor, getBaseVersion());
				List<ChangePackage> branchChanges = getChanges(commonAncestor, branchSpec);

				Set<ConflictBucketCandidate> calculateConflictCandidateBuckets = new ConflictDetector()
					.calculateConflictCandidateBuckets(branchChanges, baseChanges, getProject());

				ChangeConflict conflictException = new ChangeConflict(ProjectSpaceBase.this,
					branchChanges, baseChanges, calculateConflictCandidateBuckets, ProjectSpaceBase.this.getProject());

				if (conflictResolver.resolveConflicts(getProject(), conflictException, getBaseVersion(), null)) {
					// TODO: do we need to care about checksum errors here?
					applyChanges(getBaseVersion(), baseChanges, conflictResolver.getMergedResult());
					setMergedVersion(ModelUtil.clone(branchSpec));
				}

				return null;
			}
		}.execute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @generated NOT
	 */
	public List<BranchInfo> getBranches() throws ESException {
		return new ServerCall<List<BranchInfo>>(this) {
			@Override
			protected List<BranchInfo> run() throws ESException {
				final ConnectionManager cm = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
				return cm.getBranches(getSessionId(), getProjectId());
			};
		}.execute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @generated NOT
	 */
	public void removeTag(PrimaryVersionSpec versionSpec, TagVersionSpec tag) throws ESException {
		final ConnectionManager cm = ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		cm.removeTag(getUsersession().getSessionId(), getProjectId(), versionSpec, tag);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#resolve(org.eclipse.emf.emfstore.server.model.url.ModelElementUrlFragment)
	 */
	public EObject resolve(ModelElementUrlFragment modelElementUrlFragment) throws MEUrlResolutionException {
		ModelElementId modelElementId = modelElementUrlFragment.getModelElementId();
		EObject modelElement = getProject().getModelElement(modelElementId);
		if (modelElement == null) {
			throw new MEUrlResolutionException();
		}
		return modelElement;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#resolveVersionSpec(org.eclipse.emf.emfstore.server.model.versioning.VersionSpec)
	 * @throws ESException
	 * @generated NOT
	 */
	public PrimaryVersionSpec resolveVersionSpec(final VersionSpec versionSpec, IProgressMonitor monitor)
		throws InvalidVersionSpecException, ESException {
		return new ServerCall<PrimaryVersionSpec>(this, monitor) {
			@Override
			protected PrimaryVersionSpec run() throws ESException {
				return getConnectionManager().resolveVersionSpec(
					getSessionId(),
					getProjectId(),
					versionSpec);
			}
		}.execute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @generated NOT
	 */
	public void revert() {
		while (!getOperations().isEmpty()) {
			undoLastOperation();
		}
		updateDirtyState();
	}

	/**
	 * Saves the project space itself only, no containment children.
	 */
	public void saveProjectSpaceOnly() {
		saveResource(this.eResource());
	}

	/**
	 * Saves the project space.
	 */
	public void save() {
		saveProjectSpaceOnly();
		saveChangePackage();
		resourcePersister.saveDirtyResources(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#hasUnsavedChanges()
	 */
	public boolean hasUnsavedChanges() {

		if (resourcePersister != null) {
			return resourcePersister.isDirty();
		}

		// in case the project space has not been initialized yet
		return false;
	}

	private void saveChangePackage() {
		ChangePackage localChangePackage = getLocalChangePackage();
		if (localChangePackage.eResource() != null) {
			saveResource(localChangePackage.eResource());
		}
	}

	/**
	 * Save the given resource that is part of the project space resource set.
	 * 
	 * @param resource
	 *            the resource
	 */
	public void saveResource(Resource resource) {
		try {
			if (resource == null) {
				if (!isTransient) {
					WorkspaceUtil.logException("Resources of project space are not properly initialized!",
						new IllegalProjectSpaceStateException("Resource to save is null"));
				}
				return;
			}
			ModelUtil.saveResource(resource, WorkspaceUtil.getResourceLogger());
		} catch (IOException e) {
			WorkspaceUtil.logException("An error in the data was detected during save!"
				+ " The safest way to deal with this problem is to delete this project and checkout again.", e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#setProperty(org.eclipse.emf.emfstore.server.model.accesscontrol.OrgUnitProperty)
	 */
	public void setProperty(OrgUnitProperty property) {
		// sanity checks
		if (getUsersession() != null && getUsersession().getACUser() != null) {
			try {
				if (property.getProject() == null) {
					property.setProject(ModelUtil.clone(getProjectId()));
				} else if (!property.getProject().equals(getProjectId())) {
					return;
				}
				OrgUnitProperty prop = getProperty(property.getName());
				prop.setValue(property.getValue());
			} catch (PropertyNotFoundException e) {
				getUsersession().getACUser().getProperties().add(property);
				propertyMap.put(property.getName(), property);
			}
			// the properties that have been altered are retained in a separate
			// list
			for (OrgUnitProperty changedProperty : getUsersession().getChangedProperties()) {
				if (changedProperty.getName().equals(property.getName())
					&& changedProperty.getProject().equals(getProjectId())) {
					changedProperty.setValue(property.getValue());
					ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI().save();
					return;
				}
			}
			getUsersession().getChangedProperties().add(property);
			ESWorkspaceProviderImpl.getInstance().getWorkspace().toInternalAPI().save();
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#shareProject()
	 */
	public void shareProject(IProgressMonitor monitor) throws ESException {
		shareProject(null, monitor);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#shareProject(org.eclipse.emf.emfstore.client.model.Usersession,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void shareProject(Usersession session, IProgressMonitor monitor) throws ESException {
		new ShareController(this, session, monitor).execute();
	}

	/**
	 * Starts change recording on this workspace, resumes previous recordings if
	 * there are any.
	 * 
	 * @generated NOT
	 */
	public void startChangeRecording() {
		operationManager.startChangeRecording();
		updateDirtyState();
	}

	/**
	 * Stops current recording of changes and adds recorded changes to this
	 * project spaces changes.
	 * 
	 * @generated NOT
	 */
	public void stopChangeRecording() {
		if (operationManager != null) {
			operationManager.stopChangeRecording();
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#transmitProperties()
	 */
	public void transmitProperties() {
		List<OrgUnitProperty> temp = new ArrayList<OrgUnitProperty>();
		for (OrgUnitProperty changedProperty : getUsersession().getChangedProperties()) {
			if (changedProperty.getProject() != null && changedProperty.getProject().equals(getProjectId())) {
				temp.add(changedProperty);
			}
		}
		ListIterator<OrgUnitProperty> iterator = temp.listIterator();
		while (iterator.hasNext()) {
			try {
				ESWorkspaceProviderImpl
					.getInstance()
					.getConnectionManager()
					.transmitProperty(getUsersession().getSessionId(), iterator.next(), getUsersession().getACUser(),
						getProjectId());
				iterator.remove();
			} catch (ESException e) {
				WorkspaceUtil.logException("Transmission of properties failed with exception", e);
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#undoLastOperation()
	 */
	public void undoLastOperation() {
		undoLastOperations(1);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#undoLastOperation()
	 */
	public void undoLastOperations(int numberOfOperations) {

		if (numberOfOperations <= 0) {
			return;
		}

		if (!this.getOperations().isEmpty()) {
			List<AbstractOperation> operations = this.getOperations();
			AbstractOperation lastOperation = operations.get(operations.size() - 1);

			applyOperations(Collections.singletonList(lastOperation.reverse()), false);
			operationManager.notifyOperationUndone(lastOperation);

			operations.remove(lastOperation);
			undoLastOperations(--numberOfOperations);
		}
		updateDirtyState();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#update()
	 */
	public PrimaryVersionSpec update(IProgressMonitor monitor) throws ESException {
		return update(Versions.createHEAD(getBaseVersion()), null, monitor);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#update(org.eclipse.emf.emfstore.server.model.versioning.VersionSpec)
	 */
	public PrimaryVersionSpec update(final VersionSpec version) throws ESException {
		return update(version, null, null);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#update(org.eclipse.emf.emfstore.server.model.versioning.VersionSpec,
	 *      org.eclipse.emf.emfstore.client.model.controller.callbacks.UpdateCallback,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public PrimaryVersionSpec update(VersionSpec version, ESUpdateCallback callback, IProgressMonitor progress)
		throws ESException {
		return new UpdateController(this, version, callback, progress).execute();
	}

	/**
	 * Updates the dirty state of the project space.
	 */
	public void updateDirtyState() {
		setDirty(!getOperations().isEmpty());
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.common.ESDisposable#dispose()
	 */
	@SuppressWarnings("unchecked")
	public void dispose() {

		if (disposed) {
			return;
		}

		stopChangeRecording();

		if (crossReferenceAdapter != null) {
			getProject().eAdapters().remove(crossReferenceAdapter);
		}

		EMFStoreCommandStack commandStack = (EMFStoreCommandStack)
			ESWorkspaceProviderImpl.getInstance().getEditingDomain().getCommandStack();
		commandStack.removeCommandStackObserver(operationManager);
		commandStack.removeCommandStackObserver(resourcePersister);

		getProject().removeIdEObjectCollectionChangeObserver(operationManager);
		getProject().removeIdEObjectCollectionChangeObserver(resourcePersister);

		ESWorkspaceProviderImpl.getObserverBus().unregister(resourcePersister);
		ESWorkspaceProviderImpl.getObserverBus().unregister(this, ESLoginObserver.class);
		ESWorkspaceProviderImpl.getObserverBus().unregister(this);

		operationManager.dispose();
		resourcePersister.dispose();
		disposed = true;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.model.ProjectSpace#isShared()
	 */
	public boolean isShared() {
		return getUsersession() != null;
	}

	private void notifyPreRevertMyChanges(final ChangePackage changePackage) {
		ESWorkspaceProviderImpl.getObserverBus().notify(ESMergeObserver.class)
			.preRevertMyChanges(this.toAPI(), changePackage.toAPI());
	}

	private void notifyPostRevertMyChanges() {
		ESWorkspaceProviderImpl.getObserverBus().notify(ESMergeObserver.class).postRevertMyChanges(this.toAPI());
	}

	private void notifyPostApplyTheirChanges(List<ChangePackage> theirChangePackages) {
		// TODO ASYNC review this cancel
		ESWorkspaceProviderImpl.getObserverBus().notify(ESMergeObserver.class)
			.postApplyTheirChanges(this.toAPI(), APIUtil.mapToAPI(ESChangePackage.class, theirChangePackages));
	}

	private void notifyPostApplyMergedChanges(ChangePackage changePackage) {
		ESWorkspaceProviderImpl.getObserverBus().notify(ESMergeObserver.class)
			.postApplyMergedChanges(
				this.toAPI(), changePackage.toAPI());
	}

	public ESLocalProjectImpl toAPI() {
		if (esLocalProjectImpl == null) {
			esLocalProjectImpl = createAPI();
		}
		return esLocalProjectImpl;
	}

	public ESLocalProjectImpl createAPI() {
		return new ESLocalProjectImpl(this);
	}

	public ESRunnableContext getRunnableContext() {
		return runnableContext;
	}

	public void setRunnableContext(ESRunnableContext runnableContext) {
		this.runnableContext = runnableContext;
	}
}
