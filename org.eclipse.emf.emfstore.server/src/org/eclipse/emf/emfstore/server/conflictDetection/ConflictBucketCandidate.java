/*******************************************************************************
 * Copyright (c) 2012 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Maximilian Koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.conflictDetection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.emfstore.server.ServerConfiguration;
import org.eclipse.emf.emfstore.server.model.versioning.impl.ChangePackageImpl;
import org.eclipse.emf.emfstore.server.model.versioning.operations.AbstractOperation;

/**
 * Represents a bucket containing operations that potentially conflict but that do not neccessarily conflict. It also
 * includes the involved model element ids and the priority of each operation. The operation with the highest priority
 * is used to determine which of the operations is used to represent all my and all their operations in a conflict. The
 * operation with the highest priority is selected for representation.
 * 
 * @author koegel
 * 
 */
public class ConflictBucketCandidate {

	private static Integer maxBucketSize = initMaxBucketSize();
	private static final String MAX_BUCKET_SIZE = "-ConflictDetectionMaxBucketSize";
	private Set<AbstractOperation> myOperations;
	private Set<AbstractOperation> theirOperations;
	private Map<AbstractOperation, Integer> operationToPriorityMap;
	private ConflictBucketCandidate parentConflictBucketCandidate;

	/**
	 * Default constructor.
	 */
	public ConflictBucketCandidate() {
		myOperations = new LinkedHashSet<AbstractOperation>();
		theirOperations = new LinkedHashSet<AbstractOperation>();
		operationToPriorityMap = new LinkedHashMap<AbstractOperation, Integer>();
	}

	private static Integer initMaxBucketSize() {
		int result = 1000;
		String startArgument = ServerConfiguration.getStartArgument(MAX_BUCKET_SIZE);
		if (startArgument != null) {
			try {
				result = Integer.parseInt(startArgument);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return result;
	}

	/**
	 * Add an operation for a model element id and its feature to the bucket.
	 * 
	 * @param operation the operation
	 * @param isMyOperation a boolean to determine if the operation is to be added to mz or their operations
	 * @param priority the global priority of the operation
	 */
	public void addOperation(AbstractOperation operation, boolean isMyOperation, int priority) {
		if (operation == null) {
			return;
		}
		operationToPriorityMap.put(operation, priority);
		if (isMyOperation) {
			myOperations.add(operation);
		} else {
			theirOperations.add(operation);
		}
	}

	/**
	 * Add another another conflict candidate bucket to this bucket including all their collected operations and
	 * invoveld ids.
	 * 
	 * @param otherBucket the other bucket
	 */
	public void addConflictBucketCandidate(ConflictBucketCandidate otherBucket) {
		if (otherBucket == null) {
			return;
		}

		myOperations.addAll(otherBucket.getMyOperations());
		theirOperations.addAll(otherBucket.getTheirOperations());
		operationToPriorityMap.putAll(otherBucket.operationToPriorityMap);
	}

	/**
	 * Returns the root conflict bucket this bucket belongs to.
	 * 
	 * @return the root conflict bucket
	 */
	public ConflictBucketCandidate getRootConflictBucketCandidate() {
		if (parentConflictBucketCandidate == null) {
			return this;
		}
		return getParentConflictBucketCandidate(new ArrayList<ConflictBucketCandidate>());
	}

	private ConflictBucketCandidate getParentConflictBucketCandidate(List<ConflictBucketCandidate> pathToRoot) {
		if (parentConflictBucketCandidate == null) {
			// this is root, compress path
			for (ConflictBucketCandidate conflictBucketCandidate : pathToRoot) {
				conflictBucketCandidate.setParentConflictBucketCandidate(this);
			}
			return this;
		}
		// root not yet found
		pathToRoot.add(this);
		return parentConflictBucketCandidate.getParentConflictBucketCandidate(pathToRoot);
	}

	/**
	 * Sets the parent conflict bucket of this bucket.
	 * 
	 * @param parentConflictBucketCandidate
	 *            the parent bucket of this bucket
	 */
	public void setParentConflictBucketCandidate(ConflictBucketCandidate parentConflictBucketCandidate) {
		// disallow loops
		if (this == parentConflictBucketCandidate) {
			return;
		}
		this.parentConflictBucketCandidate = parentConflictBucketCandidate;
	}

	/**
	 * @return the size of the bucket in the total number of involved operations
	 */
	public int size() {
		return theirOperations.size() + myOperations.size();
	}

	/**
	 * @return true, if the set is conflicting, that is my and their operations are not empty
	 */
	public boolean isConflicting() {
		return theirOperations.size() > 0 && myOperations.size() > 0;
	}

	/**
	 * @return my operations
	 */
	public Set<AbstractOperation> getMyOperations() {
		return myOperations;
	}

	/**
	 * @return their operations
	 */
	public Set<AbstractOperation> getTheirOperations() {
		return theirOperations;
	}

	/**
	 * Calculate a set of conflict buckets from this candidate bucket. The result set may be empty if no conflicts are
	 * found within
	 * the candidate bucket.
	 * 
	 * @param detector the conflict detector
	 * @param myOperationsNonConflictingOperations a transient set where all non conflicting my operations are added
	 *            to
	 *            during this operation
	 * @return a set of conflict buckets
	 */
	public Set<ConflictBucket> calculateConflictBuckets(ConflictDetector detector,
		Set<AbstractOperation> myOperationsNonConflictingOperations) {
		Set<ConflictBucket> conflictBucketsSet = new LinkedHashSet<ConflictBucket>();

		// if the bucket is not conflicting (empty my or their) just add all my operations to non conflicting set
		if (!isConflicting()) {
			myOperationsNonConflictingOperations.addAll(myOperations);
			return conflictBucketsSet;
		}

		// if bucket is too large, it will not be checked manually
		if (bucketIsTooLarge()) {
			ConflictBucket newConflictBucket = new ConflictBucket(getMyOperations(), getTheirOperations());
			conflictBucketsSet.add(newConflictBucket);
			return selectMyandTheirOperation(conflictBucketsSet);
		}

		Map<AbstractOperation, ConflictBucket> operationToConflictBucketMap = new LinkedHashMap<AbstractOperation, ConflictBucket>();

		for (AbstractOperation myOperation : getMyOperations()) {

			boolean involved = false;

			for (AbstractOperation theirOperation : getTheirOperations()) {

				if (detector.doConflict(myOperation, theirOperation)) {
					involved = true;
					ConflictBucket myConflictBucket = operationToConflictBucketMap.get(myOperation);
					ConflictBucket theirConflictBucket = operationToConflictBucketMap.get(theirOperation);

					if (myConflictBucket == null && theirConflictBucket == null) {
						ConflictBucket newConflictBucket = new ConflictBucket(myOperation, theirOperation);
						operationToConflictBucketMap.put(myOperation, newConflictBucket);
						operationToConflictBucketMap.put(theirOperation, newConflictBucket);
						conflictBucketsSet.add(newConflictBucket);
					} else if (myConflictBucket != null && theirConflictBucket == null) {
						myConflictBucket.getTheirOperations().add(theirOperation);
						operationToConflictBucketMap.put(theirOperation, myConflictBucket);
					} else if (myConflictBucket == null && theirConflictBucket != null) {
						theirConflictBucket.getMyOperations().add(myOperation);
						operationToConflictBucketMap.put(myOperation, theirConflictBucket);
					} else {
						myConflictBucket.getMyOperations().addAll(theirConflictBucket.getMyOperations());
						for (AbstractOperation op : theirConflictBucket.getMyOperations()) {
							operationToConflictBucketMap.put(op, myConflictBucket);
						}
						myConflictBucket.getTheirOperations().addAll(theirConflictBucket.getTheirOperations());
						for (AbstractOperation op : theirConflictBucket.getTheirOperations()) {
							operationToConflictBucketMap.put(op, myConflictBucket);
						}

						conflictBucketsSet.remove(theirConflictBucket);
					}
				}
			}
			if (!involved) {
				// only not involved my operations have to be recorded
				myOperationsNonConflictingOperations.add(myOperation);
			}
		}
		return selectMyandTheirOperation(conflictBucketsSet);
	}

	private boolean bucketIsTooLarge() {
		int myOperationsSize = ChangePackageImpl.countLeafOperations(myOperations);
		int theirOperationSize = ChangePackageImpl.countLeafOperations(theirOperations);
		if (myOperationsSize > maxBucketSize || theirOperationSize > maxBucketSize) {
			return true;
		}
		return (myOperationsSize * theirOperationSize > maxBucketSize);
	}

	private Set<ConflictBucket> selectMyandTheirOperation(Set<ConflictBucket> conflictBucketsSet) {

		for (ConflictBucket conflictBucket : conflictBucketsSet) {
			Integer maxPriority = -1;
			AbstractOperation maxOperation = null;
			for (AbstractOperation myOperation : conflictBucket.getMyOperations()) {
				Integer currentPrio = operationToPriorityMap.get(myOperation);
				if (currentPrio > maxPriority) {
					maxPriority = currentPrio;
					maxOperation = myOperation;
				}
			}
			conflictBucket.setMyOperation(maxOperation);
		}

		for (ConflictBucket conflictBucket : conflictBucketsSet) {
			Integer maxPriority = -1;
			AbstractOperation maxOperation = null;
			for (AbstractOperation theirOperation : conflictBucket.getTheirOperations()) {
				Integer currentPrio = operationToPriorityMap.get(theirOperation);
				if (currentPrio > maxPriority) {
					maxPriority = currentPrio;
					maxOperation = theirOperation;
				}
			}
			conflictBucket.setTheirOperation(maxOperation);
		}

		return conflictBucketsSet;
	}
}