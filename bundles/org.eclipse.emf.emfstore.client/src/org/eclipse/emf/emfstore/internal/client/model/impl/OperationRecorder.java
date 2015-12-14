/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver;
import org.eclipse.emf.emfstore.client.observer.ESCommitObserver;
import org.eclipse.emf.emfstore.client.observer.ESPostCreationObserver;
import org.eclipse.emf.emfstore.client.observer.ESShareObserver;
import org.eclipse.emf.emfstore.client.observer.ESUpdateObserver;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.NotificationToOperationConverter;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter.FilterStack;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.recording.NotificationRecorder;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.MissingCommandException;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.impl.IdEObjectCollectionImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.NotificationInfo;
import org.eclipse.emf.emfstore.internal.common.model.util.SettingWithReferencedElement;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.ReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl.CreateDeleteOperationImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.SemanticCompositeOperation;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;

/**
 * Tracks changes on any given {@link IdEObjectCollection}.
 *
 * @author koegel
 * @author emueller
 */
public class OperationRecorder implements ESCommandObserver, ESCommitObserver, ESUpdateObserver, ESShareObserver,
	IdEObjectCollectionChangeObserver {

	/**
	 * Name of unknown creator.
	 */
	public static final String UNKOWN_CREATOR = Messages.OperationRecorder_Unknown;

	private int currentOperationListSize;

	private List<AbstractOperation> operations;
	private final List<OperationRecorderListener> observers;
	private final RemovedElementsCache removedElementsCache;

	private final NotificationToOperationConverter converter;
	private NotificationRecorder notificationRecorder;
	private CompositeOperation compositeOperation;

	private final ProjectSpaceBase projectSpace;
	private final IdEObjectCollectionImpl collection;

	private boolean isRecording;
	private boolean commandIsRunning;

	private final OperationRecorderConfig config;

	/**
	 * Constructor.
	 *
	 * @param projectSpace
	 *            the {@link ProjectSpaceBase} the recorder should be attached to
	 */
	// TODO: provide ext. point for rollBackInCaseOfCommandFailure
	public OperationRecorder(ProjectSpaceBase projectSpace) {
		this.projectSpace = projectSpace;
		collection = (IdEObjectCollectionImpl) projectSpace.getProject();

		operations = new ArrayList<AbstractOperation>();
		observers = new ArrayList<OperationRecorderListener>();
		removedElementsCache = new RemovedElementsCache(collection);

		config = new OperationRecorderConfig();
		converter = new NotificationToOperationConverter(collection);
	}

	/**
	 * Clears the operations list.
	 *
	 * @return the list of cleared operations
	 */
	public List<AbstractOperation> clearOperations() {
		final List<AbstractOperation> ops = new ArrayList<AbstractOperation>(operations);
		operations.clear();
		return ops;
	}

	/**
	 * Returns the configuration options for the operation recorder.
	 *
	 * @return the operation recorder configuration options
	 */
	public OperationRecorderConfig getConfig() {
		return config;
	}

	/**
	 * Returns the collection the operation recorder is operation on.
	 *
	 * @return the collection the operation recorder is operation on
	 */
	public IdEObjectCollection getCollection() {
		return collection;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#modelElementAdded(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void modelElementAdded(IdEObjectCollection project, EObject modelElement) {
		// if element was just pasted from clipboard then do nothing
		// if (this.getModelElementsFromClipboard().contains(modelElement)) {
		// return;
		// }
		if (!isRecording) {
			return;
		}

		checkCommandConstraints(modelElement);

		// notify Post Creation Listeners with change tracking switched off since only attribute changes are allowd
		stopChangeRecording();
		ESWorkspaceProviderImpl.getObserverBus().notify(ESPostCreationObserver.class).onCreation(modelElement);
		startChangeRecording();

		final Set<EObject> allModelElements = new LinkedHashSet<EObject>();
		allModelElements.add(modelElement);
		allModelElements.addAll(ModelUtil.getAllContainedModelElements(modelElement, false));

		// collect out-going cross-reference for containment tree of modelElement
		final List<SettingWithReferencedElement> crossReferences = ModelUtil.collectOutgoingCrossReferences(collection,
			allModelElements);

		// collect in-going cross-reference for containment tree of modelElement
		final List<SettingWithReferencedElement> ingoingCrossReferences = collectIngoingCrossReferences(collection,
			allModelElements);
		crossReferences.addAll(ingoingCrossReferences);

		// clean up already existing references when collection already contained the object
		final List<SettingWithReferencedElement> savedSettings = removedElementsCache
			.getRemovedRootElementToReferenceSetting(modelElement);
		if (savedSettings != null) {
			final List<SettingWithReferencedElement> toRemove = new ArrayList<SettingWithReferencedElement>();
			for (final SettingWithReferencedElement setting : savedSettings) {
				for (final SettingWithReferencedElement newSetting : crossReferences) {
					if (setting.getSetting().getEStructuralFeature()
						.equals(newSetting.getSetting().getEStructuralFeature())
						&& setting.getReferencedElement().equals(newSetting.getReferencedElement())) {
						toRemove.add(newSetting);
					}
				}
			}

			crossReferences.removeAll(toRemove);
		}

		// collect recorded operations and add to create operation
		final List<ReferenceOperation> recordedOperations = generateCrossReferenceOperations(crossReferences);

		List<AbstractOperation> resultingOperations;

		// check if create element has been deleted during running command, if so do not record a create operation
		if (commandIsRunning && removedElementsCache.contains(modelElement)) {
			resultingOperations = new ArrayList<AbstractOperation>(recordedOperations);
		} else {
			final CreateDeleteOperation createDeleteOperation = createCreateDeleteOperation(modelElement, false);
			createDeleteOperation.getSubOperations().addAll(recordedOperations);
			resultingOperations = new ArrayList<AbstractOperation>();
			resultingOperations.add(createDeleteOperation);
		}

		if (compositeOperation != null) {
			compositeOperation.getSubOperations().addAll(resultingOperations);
		} else {
			bufferOrRecordOperations(resultingOperations);
		}
	}

	private void checkCommandConstraints(EObject modelElement) {
		if (!commandIsRunning && config.isForceCommands()) {
			WorkspaceUtil.handleException(Messages.OperationRecorder_ElementChangedWithoutCommand_0,
				new MissingCommandException(
					MessageFormat.format(Messages.OperationRecorder_ElementChangedWithoutCommand_1, modelElement)));
		}
	}

	private List<SettingWithReferencedElement> collectIngoingCrossReferences(IdEObjectCollection collection,
		Set<EObject> allModelElements) {
		final List<SettingWithReferencedElement> settings = new ArrayList<SettingWithReferencedElement>();
		for (final EObject modelElement : allModelElements) {
			final Collection<Setting> inverseReferences = projectSpace.findInverseCrossReferences(modelElement);

			for (final Setting setting : inverseReferences) {
				if (!ModelUtil.shouldBeCollected(collection, allModelElements, setting.getEObject())) {
					continue;
				}
				final EReference reference = (EReference) setting.getEStructuralFeature();
				final EClassifier eType = reference.getEType();

				if (reference.isContainer() || reference.isContainment() || !reference.isChangeable()
					|| !(eType instanceof EClass)) {
					continue;
				}

				final SettingWithReferencedElement settingWithReferencedElement = new SettingWithReferencedElement(
					setting,
					modelElement);
				settings.add(settingWithReferencedElement);
			}
		}

		return settings;
	}

	private List<ReferenceOperation> generateCrossReferenceOperations(
		Collection<SettingWithReferencedElement> crossReferences) {
		final List<ReferenceOperation> result = new ArrayList<ReferenceOperation>();

		for (final SettingWithReferencedElement setting : crossReferences) {
			final EObject referencedElement = setting.getReferencedElement();

			// fetch ID of referenced element
			ModelElementId newModelElementId = collection.getModelElementId(referencedElement);
			if (newModelElementId == null) {
				newModelElementId = collection.getDeletedModelElementId(referencedElement);
			}

			final EObject eObject = setting.getSetting().getEObject();
			final EReference reference = (EReference) setting.getSetting().getEStructuralFeature();

			if (setting.getSetting().getEStructuralFeature().isMany()) {
				final int position = ((List<?>) eObject.eGet(reference)).indexOf(referencedElement);
				final MultiReferenceOperation multiRefOp = NotificationToOperationConverter
					.createMultiReferenceOperation(
						collection, eObject, reference, Arrays.asList(referencedElement), true, position);
				result.add(multiRefOp);
			} else {
				final SingleReferenceOperation singleRefOp = NotificationToOperationConverter
					.createSingleReferenceOperation(
						collection, null, newModelElementId, reference, eObject);
				result.add(singleRefOp);
			}
		}

		return result;
	}

	private void operationsRecorded(List<AbstractOperation> operations) {

		if (operations.size() == 0) {
			return;
		}

		for (final OperationRecorderListener observer : observers) {
			observer.operationsRecorded(operations);
		}
	}

	/**
	 * Adds an operation recorder observer.
	 *
	 * @param observer
	 *            the observer to be added
	 */
	public void addOperationRecorderListener(OperationRecorderListener observer) {
		observers.add(observer);
	}

	/**
	 * Removes an operation recorder observer.
	 *
	 * @param observer
	 *            the observer to be removed
	 */
	public void removeOperationRecorderListener(OperationRecorderListener observer) {
		observers.remove(observer);
	}

	/**
	 * Starts change recording on this workspace, resumes previous recordings if
	 * there are any.
	 *
	 */
	public void startChangeRecording() {
		if (notificationRecorder == null) {
			notificationRecorder = new NotificationRecorder();
		}
		isRecording = true;
	}

	/**
	 * Stops current recording of changes and adds recorded changes to this
	 * project spaces changes.
	 *
	 */
	public void stopChangeRecording() {
		isRecording = false;
	}

	private List<AbstractOperation> recordingFinished() {

		// create operations from "valid" notifications, log invalid ones,
		// accumulate the ops
		final List<AbstractOperation> ops = new LinkedList<AbstractOperation>();
		final List<NotificationInfo> rec = notificationRecorder.getRecording().asMutableList();
		for (final NotificationInfo n : rec) {
			if (!n.isValid()) {
				WorkspaceUtil.log(Messages.OperationRecorder_InvalidNotificationMessage + n.getValidationMessage(),
					null, 0);
				continue;
			}
			final AbstractOperation op = converter.convert(n);
			if (op != null) {
				ops.add(op);
			} else {
				// we should never get here, this would indicate a
				// consistency error,
				// n.isValid() should have been false
				WorkspaceUtil.log(Messages.OperationRecorder_InvalidNotificationClassification_0
					+ Messages.OperationRecorder_InvalidNotificationClassification_1 + n.toString(), null, 0);
				continue;
			}
		}

		return ops;
	}

	/**
	 * Returns the notification recorder of the project space.
	 *
	 * @return the notification recorder
	 */
	public NotificationRecorder getNotificationRecorder() {
		return notificationRecorder;
	}

	/**
	 * Create a CreateDeleteOperation.
	 *
	 * @param modelElement
	 *            the model element to delete or create
	 * @param delete
	 *            whether the element is deleted or created
	 * @return the operation
	 */
	private CreateDeleteOperation createCreateDeleteOperation(EObject modelElement, boolean delete) {
		final CreateDeleteOperation createDeleteOperation = OperationsFactory.eINSTANCE.createCreateDeleteOperation();
		createDeleteOperation.setDelete(delete);
		final EObject element = modelElement;

		final List<EObject> allContainedModelElements = ModelUtil.getAllContainedModelElementsAsList(element, false);
		allContainedModelElements.add(element);

		final Copier copier = new Copier(true, false);
		final EObject copiedElement = copier.copy(element);
		copier.copyReferences();

		final List<EObject> copiedAllContainedModelElements = ModelUtil.getAllContainedModelElementsAsList(
			copiedElement,
			false);
		copiedAllContainedModelElements.add(copiedElement);

		for (int i = 0; i < allContainedModelElements.size(); i++) {
			final EObject child = allContainedModelElements.get(i);

			if (ModelUtil.isIgnoredDatatype(child)) {
				continue;
			}

			final EObject copiedChild = copiedAllContainedModelElements.get(i);
			final ModelElementId childId = collection.getModelElementId(child);

			((CreateDeleteOperationImpl) createDeleteOperation).getEObjectToIdMap().put(copiedChild, childId);
		}

		createDeleteOperation.setModelElement(copiedElement);
		createDeleteOperation.setModelElementId(collection.getModelElementId(modelElement));

		createDeleteOperation.setClientDate(new Date());
		return createDeleteOperation;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#modelElementRemoved(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void modelElementRemoved(IdEObjectCollection project, EObject modelElement) {
		if (isRecording) {
			if (!commandIsRunning) {
				handleElementDelete(modelElement);
				collection.clearAllocatedCaches();
			} else {
				final Set<EObject> allModelElements = new LinkedHashSet<EObject>();
				allModelElements.add(modelElement);
				allModelElements.addAll(
					ModelUtil.getAllContainedModelElements(
						modelElement,
						/* includeTransientContainments= */false));
				final List<SettingWithReferencedElement> crossReferences = ModelUtil.collectOutgoingCrossReferences(
					collection, allModelElements);
				final List<SettingWithReferencedElement> ingoingCrossReferences = collectIngoingCrossReferences(
					collection,
					allModelElements);
				crossReferences.addAll(ingoingCrossReferences);

				removedElementsCache.addRemovedElement(modelElement, allModelElements, crossReferences);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandCompleted(org.eclipse.emf.common.command.Command)
	 */
	public void commandCompleted(Command command) {
		commandCompleted(command, false);
	}

	/**
	 * Called to notify listener about the successful completion of the given command.
	 *
	 * @param command
	 *            the completed command
	 * @param isNestedCommand
	 *            whether the completed command is a command inside another one
	 */
	public void commandCompleted(Command command, boolean isNestedCommand) {

		// means that we have not seen a command start yet
		// if (currentClipboard == null) {
		// return;
		// }

		final List<EObject> deletedElements = new ArrayList<EObject>();
		for (int i = removedElementsCache.getRemovedRootElements().size() - 1; i >= 0; i--) {
			final EObject removedElement = removedElementsCache.getRemovedRootElements().get(i);
			if (!collection.contains(removedElement)) {
				if (!deletedElements.contains(removedElement)) {
					deletedElements.add(0, removedElement);
				}
			}
		}

		for (final EObject deletedElement : deletedElements) {
			// element was deleted
			projectSpace.executeRunnable(new Runnable() {
				public void run() {
					handleElementDelete(deletedElement);
				}
			});
		}

		// add all cut elements to modelElements to guarantee a consistent state if it is allowed
		final Project project = projectSpace.getProject();
		final EList<EObject> cutElements = project.getCutElements();
		if (config.isDenyAddCutElementsToModelElements() && cutElements.size() != 0) {
			throw new IllegalStateException(
				Messages.OperationRecorder_CutElementsPresent_0
					+ Messages.OperationRecorder_CutElementsPresent_1);
		}

		for (final EObject eObject : new ArrayList<EObject>(cutElements)) {
			project.addModelElement(eObject);
		}

		operations = modifyOperations(operations, command);

		operationsRecorded(operations);
		removedElementsCache.clear();
		operations.clear();
		collection.clearAllocatedCaches();

		commandIsRunning = isNestedCommand;
	}

	private List<AbstractOperation> modifyOperations(List<AbstractOperation> operations, Command command) {
		if (operations.isEmpty() || config.getOperationModifier() == null) {
			return operations;
		}
		return config.getOperationModifier().modify(operations, command);
	}

	private void deleteOutgoingCrossReferencesOfContainmentTree(Set<EObject> allEObjects) {

		final List<SettingWithElementsToRemove> settingsToUnset = new ArrayList<SettingWithElementsToRemove>();

		// delete all non containment cross references to other elements
		for (final EObject modelElement : allEObjects) {
			for (final EReference reference : modelElement.eClass().getEAllReferences()) {

				if (!EClass.class.isInstance(reference.getEType())) {
					continue;
				}
				final EClass eClass = (EClass) reference.getEType();

				if (Map.Entry.class.isAssignableFrom(eClass.getInstanceClass()) && reference.isContainment()
					&& reference.isChangeable()) {

					handleMapEntryDeletion(modelElement, eClass, reference, allEObjects);
					continue;
				}

				if (reference.isContainer() || reference.isContainment() || !reference.isChangeable()) {
					continue;
				}

				// remove all (outgoing) references to elements outside of the containment tree of the element to be
				// deleted
				if (reference.isMany()) {
					@SuppressWarnings("unchecked")
					final Set<EObject> referencesToRemove = filterAllNonContained(
						(List<EObject>) modelElement.eGet(reference),
						allEObjects);
					if (referencesToRemove.size() > 0) {
						settingsToUnset.add(
							new SettingWithElementsToRemove(
								InternalEObject.class.cast(modelElement).eSetting(reference),
								referencesToRemove));
					}
				} else {
					final EObject referencedElement = (EObject) modelElement.eGet(reference);
					if (referencedElement != null && !allEObjects.contains(referencedElement)) {
						settingsToUnset.add(new SettingWithElementsToRemove(
							InternalEObject.class.cast(modelElement).eSetting(reference)));
					}
				}
			}
		}

		unsetAll(settingsToUnset);
	}

	private void unsetAll(final List<SettingWithElementsToRemove> settingsToUnset) {
		for (final SettingWithElementsToRemove settingWithElementsToRemove : settingsToUnset) {
			final Setting setting = settingWithElementsToRemove.setting;
			final EStructuralFeature feature = setting.getEStructuralFeature();
			final Set<EObject> referencesToRemove = settingWithElementsToRemove.elementsToRemove;

			if (feature.isMany()) {
				@SuppressWarnings("unchecked")
				final List<EObject> referencedElements = (List<EObject>) setting.getEObject().eGet(feature);
				referencedElements.removeAll(referencesToRemove);
			} else {
				setting.getEObject().eSet(feature, null);
			}
		}
	}

	/**
	 * Returns all elements that are not contained in {@code allElements}.
	 *
	 * @param elements
	 *            the set of elements whose containment in {@code allElements} should be checked
	 * @param allElements
	 *            the set of all elements
	 * @return all elements that are not contained in {@code allElements}
	 */
	private Set<EObject> filterAllNonContained(final List<EObject> elements, Set<EObject> allElements) {
		final Set<EObject> referencedElementsCopy = new LinkedHashSet<EObject>(elements);
		referencedElementsCopy.removeAll(allElements);
		return referencedElementsCopy;
	}

	private void handleMapEntryDeletion(EObject modelElement, EClass mapEntryEClass, EReference reference,
		Set<EObject> allEObjects) {

		@SuppressWarnings("unchecked")
		final List<EObject> mapEntriesEList = (List<EObject>) modelElement.eGet(reference);
		final Set<EObject> mapEntriesToRemove = new LinkedHashSet<EObject>();

		final EReference nonContainmentKeyReference = getNonContainmentKeyReference(mapEntryEClass);
		// key references seems to be containment, skip loop
		if (nonContainmentKeyReference == null) {
			return;
		}

		// find all map entries which reference an object outside of the containment tree
		// with their non-containment key
		for (final EObject mapEntry : mapEntriesEList) {

			final Object targetElement = mapEntry.eGet(nonContainmentKeyReference);

			if (!allEObjects.contains(targetElement)) {
				mapEntriesToRemove.add(mapEntry);
			}
		}

		if (mapEntriesToRemove.isEmpty()) {
			return;
		}

		// the reference is a containment map feature and its referenced entries do have at least one
		// non-containment key cross-reference that goes to an element outside of
		// the containment tree, therefore we delete respective map entries
		// instead of waiting for the referenced key element to be cut off from the map entry
		// in the children recursion
		// since cutting off a key reference will render the map into an invalid state on de-serialization
		// which can
		// result in unresolved proxies
		EcoreUtil.resolveAll(modelElement);

		for (final EObject mapEntryToRemove : mapEntriesToRemove) {
			mapEntriesEList.remove(mapEntryToRemove);
			handleElementDelete(mapEntryToRemove);
		}
	}

	private EReference getNonContainmentKeyReference(EClass eClass) {
		for (final EReference eRef : eClass.getEReferences()) {
			if (eRef.getName().equals("key") && !eRef.isContainment()) { //$NON-NLS-1$
				return eRef;
			} else if (eRef.getName().equals("key") && eRef.isContainment()) { //$NON-NLS-1$
				return null;
			}
		}

		// no key reference found
		return null;
	}

	private void handleElementDelete(EObject deletedElement) {

		final Set<EObject> allDeletedModelElements = ModelUtil.getAllContainedModelElements(deletedElement,
			false /* includeTransientContainments is false */);
		allDeletedModelElements.add(deletedElement);
		deleteOutgoingCrossReferencesOfContainmentTree(allDeletedModelElements);

		if (config.isCutOffIncomingCrossReferences()) {
			for (final EObject element : allDeletedModelElements) {
				// delete incoming cross references
				final Collection<Setting> inverseReferences = projectSpace.findInverseCrossReferences(element);
				ModelUtil.deleteIncomingCrossReferencesToElement(element,
					inverseReferences,
					allDeletedModelElements);

			}
		}

		if (!isRecording) {
			return;
		}

		final List<EObject> allContainedModelElements = ModelUtil.getAllContainedModelElementsAsList(deletedElement,
			false);
		allContainedModelElements.add(deletedElement);
		final EObject copiedElement = ModelUtil.clone(deletedElement);
		final List<EObject> copiedAllContainedModelElements = ModelUtil.getAllContainedModelElementsAsList(
			copiedElement,
			false);
		copiedAllContainedModelElements.add(copiedElement);

		final CreateDeleteOperation deleteOperation = OperationsFactory.eINSTANCE.createCreateDeleteOperation();
		deleteOperation.setClientDate(new Date());
		deleteOperation.setModelElement(copiedElement);
		deleteOperation.setModelElementId(getDeletedModelElementId(deletedElement));

		// sync IDs into Map
		for (int i = 0; i < allContainedModelElements.size(); i++) {
			final EObject child = allContainedModelElements.get(i);
			final EObject copiedChild = copiedAllContainedModelElements.get(i);
			final ModelElementId childId = getDeletedModelElementId(child); // collection.getDeletedModelElementId(child);
			((CreateDeleteOperationImpl) deleteOperation).getEObjectToIdMap().put(copiedChild, childId);
		}

		deleteOperation.setDelete(true);

		// extract all reference ops that belong to the delete
		final List<CompositeOperation> compositeOperationsToDelete = new ArrayList<CompositeOperation>();
		final List<ReferenceOperation> extractReferenceOperationsForDelete = extractReferenceOperationsForDelete(
			deletedElement, compositeOperationsToDelete);
		deleteOperation.getSubOperations().addAll(extractReferenceOperationsForDelete);
		operations.removeAll(compositeOperationsToDelete);

		if (compositeOperation != null) {
			compositeOperation.getSubOperations().add(deleteOperation);
		} else {
			bufferOrRecordOperation(deleteOperation);
		}
	}

	private ModelElementId getDeletedModelElementId(EObject deletedElement) {
		if (!commandIsRunning) {
			return collection.getDeletedModelElementId(deletedElement);
		}
		return ModelUtil.clone(removedElementsCache.getRemovedElementId(deletedElement));
	}

	@SuppressWarnings("unchecked")
	private List<ReferenceOperation> extractReferenceOperationsForDelete(EObject deletedElement,
		List<CompositeOperation> compositeOperationsToDelete) {
		final Set<ModelElementId> allDeletedElementsIds = new LinkedHashSet<ModelElementId>();
		for (final EObject child : ModelUtil.getAllContainedModelElements(deletedElement, false)) {
			final ModelElementId childId = collection.getDeletedModelElementId(child);
			allDeletedElementsIds.add(childId);
		}
		allDeletedElementsIds.add(collection.getDeletedModelElementId(deletedElement));

		final List<ReferenceOperation> referenceOperationsForDelete = new ArrayList<ReferenceOperation>();
		final List<AbstractOperation> newOperations = operations.subList(0, operations.size());
		final List<AbstractOperation> l = new ArrayList<AbstractOperation>();

		for (int i = newOperations.size() - 1; i >= 0; i--) {
			final AbstractOperation operation = newOperations.get(i);
			if (belongsToDelete(operation, allDeletedElementsIds)) {
				referenceOperationsForDelete.add(0, (ReferenceOperation) operation);
				l.add(operation);
				continue;
			}
			if (operation instanceof CompositeOperation
				&& ((CompositeOperation) operation).getMainOperation() != null) {
				final CompositeOperation compositeOperation = (CompositeOperation) operation;
				boolean doesNotBelongToDelete = false;
				for (final AbstractOperation subOperation : compositeOperation.getSubOperations()) {
					if (!belongsToDelete(subOperation, allDeletedElementsIds)) {
						doesNotBelongToDelete = true;
						break;
					}
				}
				if (!doesNotBelongToDelete) {
					referenceOperationsForDelete.addAll(0,
						(Collection<? extends ReferenceOperation>) compositeOperation.getSubOperations());
					compositeOperationsToDelete.add(compositeOperation);
				}
				continue;
			}
			break;
		}

		operations.removeAll(l);

		return referenceOperationsForDelete;
	}

	private boolean belongsToDelete(AbstractOperation operation, Set<ModelElementId> allDeletedElementsIds) {
		if (operation instanceof ReferenceOperation) {
			final ReferenceOperation referenceOperation = (ReferenceOperation) operation;
			final Set<ModelElementId> allInvolvedModelElements = referenceOperation.getAllInvolvedModelElements();
			if (allInvolvedModelElements.removeAll(allDeletedElementsIds)) {
				return isDestructorReferenceOperation(referenceOperation);
			}
		}
		return false;
	}

	private boolean isDestructorReferenceOperation(ReferenceOperation referenceOperation) {
		if (referenceOperation instanceof MultiReferenceOperation) {
			final MultiReferenceOperation multiReferenceOperation = (MultiReferenceOperation) referenceOperation;
			return !multiReferenceOperation.isAdd();
		} else if (referenceOperation instanceof SingleReferenceOperation) {
			final SingleReferenceOperation singleReferenceOperation = (SingleReferenceOperation) referenceOperation;
			return singleReferenceOperation.getOldValue() != null && singleReferenceOperation.getNewValue() == null;
		}
		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandFailed(org.eclipse.emf.common.command.Command,
	 *      java.lang.Exception)
	 */
	public void commandFailed(Command command, Exception exception) {

		// this is a backup in order to remove obsolete operations. In most
		// (all?) cases though, the rollback of the
		// transaction does this.

		if (compositeOperation != null) {
			for (int i = compositeOperation.getSubOperations().size() - 1; i >= currentOperationListSize; i--) {
				compositeOperation.getSubOperations().remove(i);
			}
		}

		if (config.isRollbackAtCommandFailure()) {
			for (int i = operations.size() - 1; i >= 0; i--) {
				operations.get(i).reverse().apply(collection);
			}
		} else {
			commandCompleted(command);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandStarted(org.eclipse.emf.common.command.Command)
	 */
	public void commandStarted(Command command) {
		currentOperationListSize = 0;
		commandIsRunning = true;
	}

	/**
	 * Returns the composite operation.
	 *
	 * @return the composite operation
	 */
	public CompositeOperation getCompositeOperation() {
		return compositeOperation;
	}

	/**
	 * Begins a composite operation.
	 *
	 * @return the handle to the newly created composite operation
	 */
	public CompositeOperationHandle beginCompositeOperation() {

		if (compositeOperation != null) {
			throw new IllegalStateException(Messages.OperationRecorder_OnlyOneCompositeAllowed);
		}

		compositeOperation = OperationsFactory.eINSTANCE.createCompositeOperation();
		final CompositeOperationHandle handle = new CompositeOperationHandle(this, compositeOperation);
		notificationRecorder.newRecording();

		return handle;
	}

	/**
	 * Replace and complete the current composite operation.
	 *
	 * @param semanticCompositeOperation
	 *            the semantic operation that replaces the composite operation
	 */
	public void endCompositeOperation(SemanticCompositeOperation semanticCompositeOperation) {
		compositeOperation = semanticCompositeOperation;
		endCompositeOperation();
	}

	/**
	 * Complete the current composite operation.
	 */
	public void endCompositeOperation() {
		bufferOrRecordOperation(compositeOperation);
		compositeOperation = null;
		notificationRecorder.stopRecording();
	}

	/**
	 * Aborts the current composite operation.
	 */
	public void abortCompositeOperation() {
		final AbstractOperation reversedCompositeOperation = compositeOperation.reverse();
		projectSpace.applyOperations(
			Collections.singletonList(reversedCompositeOperation), false);

		removedElementsCache.clear();
		notificationRecorder.stopRecording();

		compositeOperation = null;
		currentOperationListSize = operations.size();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#notify(org.eclipse.emf.common.notify.Notification,
	 *      org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection, org.eclipse.emf.ecore.EObject)
	 */
	public void notify(Notification notification, IdEObjectCollection collection, EObject modelElement) {

		if (!isRecording) {
			return;
		}

		// filter unwanted notifications
		if (FilterStack.DEFAULT.check(new NotificationInfo(notification).toAPI(), collection)) {
			return;
		}

		checkCommandConstraints(modelElement);

		notificationRecorder.record(notification);

		if (notificationRecorder.isRecordingComplete()) {

			final List<AbstractOperation> ops = recordingFinished();

			// add resulting operations as sub-operations to composite or top-level operations
			if (compositeOperation != null) {
				compositeOperation.getSubOperations().addAll(ops);
				return;
			}

			if (ops.size() > 1) {
				bufferOrRecordOperation(createCompositeOperation(ops));
			} else if (ops.size() == 1) {
				bufferOrRecordOperation(ops.get(0));
			}
		}
	}

	private CompositeOperation createCompositeOperation(List<AbstractOperation> ops) {
		final CompositeOperation op = OperationsFactory.eINSTANCE.createCompositeOperation();
		op.getSubOperations().addAll(ops);
		// set the last operation as the main one for natural composites
		op.setMainOperation(ops.get(ops.size() - 1));
		op.setModelElementId(ModelUtil.clone(op.getMainOperation().getModelElementId()));
		return op;
	}

	private void bufferOrRecordOperations(List<AbstractOperation> operations) {
		if (commandIsRunning && config.isEmitOperationsUponCommandCompletion()) {
			this.operations.addAll(operations);
		} else {
			operationsRecorded(operations);
		}
	}

	private void bufferOrRecordOperation(AbstractOperation operation) {
		bufferOrRecordOperations(Arrays.asList(operation));
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#collectionDeleted(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection)
	 */
	public void collectionDeleted(IdEObjectCollection collection) {

	}

	/**
	 * Returns the project space this operation recorder is attached to.
	 *
	 * @return the project space this operation recorder is attached to
	 */
	public ProjectSpace getProjectSpace() {
		return projectSpace;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESUpdateObserver#inspectChanges(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      java.util.List, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changePackages,
		IProgressMonitor monitor) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESCommitObserver#inspectChanges(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.emf.emfstore.server.model.ESChangePackage, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean inspectChanges(ESLocalProject project, ESChangePackage changePackage, IProgressMonitor monitor) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESUpdateObserver#updateCompleted(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void updateCompleted(ESLocalProject project, IProgressMonitor monitor) {
		clearAllocatedCaches(project);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESShareObserver#shareDone(org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public void shareDone(ESLocalProject localProject) {
		clearAllocatedCaches(localProject);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESCommitObserver#commitCompleted(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void commitCompleted(ESLocalProject project, ESPrimaryVersionSpec newRevision, IProgressMonitor monitor) {
		clearAllocatedCaches(project);
	}

	private void clearAllocatedCaches(ESLocalProject project) {
		if (((ESLocalProjectImpl) project).toInternalAPI().getProject().equals(collection) && !commandIsRunning) {
			collection.clearAllocatedCaches();
		}
	}

	/**
	 * Whether the operation recorder considers a command is being run.
	 *
	 * @return <code>true</code> if a command is being run, <code>false</code> otherwise
	 */
	public boolean isCommandRunning() {
		return commandIsRunning;
	}

	/**
	 * Helper class to capture a setting a all elements that need to be
	 * removed from the feature in case the feature represents a many reference.
	 * Otherwise the collection of elements to be removed remains empty.
	 *
	 */
	class SettingWithElementsToRemove {

		/**
		 * The {@link Setting}.
		 */
		private final Setting setting;

		/**
		 * The elements to be removed in case the feature within in the setting is a many reference.
		 */
		private final Set<EObject> elementsToRemove = new LinkedHashSet<EObject>();

		/**
		 * Constructor for non-many references.
		 *
		 * @param setting
		 *            a setting consisting of an {@link EObject} and a non-many reference
		 */
		public SettingWithElementsToRemove(Setting setting) {
			this.setting = setting;
		}

		/**
		 * Constructor for many references.
		 *
		 * @param setting
		 *            a setting consisting of an {@link EObject} and a many reference
		 * @param elementsToRemove
		 *            the elemets to be removed from the many reference
		 */
		public SettingWithElementsToRemove(Setting setting, Set<EObject> elementsToRemove) {
			this.setting = setting;
			this.elementsToRemove.addAll(elementsToRemove);
		}

		/**
		 * Returns the {@link Setting}.
		 *
		 * @return the setting
		 */
		public Setting getSetting() {
			return setting;
		}

		/**
		 * Returns the elements to be removed in case the feature within in the setting is a many reference.
		 *
		 * @return the elements to be removed. In case the feature of the setting is non-many, the set
		 *         will be empty
		 */
		public Set<EObject> getElementsToRemove() {
			return elementsToRemove;
		}
	}
}
