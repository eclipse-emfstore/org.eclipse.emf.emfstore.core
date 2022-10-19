/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import java.util.Set;

/**
 * <p>
 * Mapping from model elements to their features.
 * </p>
 *
 * @author mkoegel
 * @author emueller
 *
 */
public class ReservationSet {

	private final ModelElementIdReservationMap modelElementIdReservationMap;

	/**
	 * Default constructor.
	 */
	public ReservationSet() {
		modelElementIdReservationMap = new ModelElementIdReservationMap();
	}

	/**
	 * Adds an full reservation.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 */
	public void addFullReservation(String modelElementId) {
		addFullReservation(modelElementId, null);
	}

	/**
	 * Adds an existence reservation.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 */
	public void addExistenceReservation(String modelElementId) {
		addExistenceReservation(modelElementId, null);
	}

	/**
	 * Adds an full reservation.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 */
	public void addContainerReservation(String modelElementId) {
		addFeatureReservation(modelElementId, FeatureNameReservationMap.CONTAINER_FEATURE);
	}

	/**
	 * Adds a feature reservation for the given model element an one of its feature.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a feature
	 */
	public void addFeatureReservation(String modelElementId, String featureName) {
		addFeatureReservation(modelElementId, featureName, null);
	}

	/**
	 * Adds a multi-reference feature reservation together with an opposite model element.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a feature
	 * @param oppositeModelElementId
	 *            the ID of a model element
	 */
	public void addMultiReferenceWithOppositeReservation(String modelElementId, String featureName,
		String oppositeModelElementId) {
		addMultiReferenceWithOppositeReservation(modelElementId, featureName, oppositeModelElementId, null);
	}

	/**
	 * Adds a reservation for a map entry that is contained in a map feature.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a feature
	 * @param key
	 *            the key of the map entry
	 */
	public void addMapKeyReservation(String modelElementId, String featureName,
		String key) {
		// use map entry key as opposite ID to make a reservation on both - feature & key and feature
		addMultiReferenceWithOppositeReservation(modelElementId, featureName, key);
	}

	/**
	 * Adds a full reservation for the given model element.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param conflictBucketCandidate
	 *            the conflict bucket into which the reservation is put
	 */
	public void addFullReservation(String modelElementId, ConflictBucketCandidate conflictBucketCandidate) {
		final FeatureNameReservationMap featureNameReservationMap = new FeatureNameReservationMap(true);
		featureNameReservationMap.setConflictBucketCandidate(conflictBucketCandidate);
		modelElementIdReservationMap.put(modelElementId, featureNameReservationMap);
	}

	/**
	 * Adds an existence reservation.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param conflictBucketCandidate
	 *            the conflict bucket candidate into which the reservation is put
	 */
	public void addExistenceReservation(String modelElementId, ConflictBucketCandidate conflictBucketCandidate) {
		FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementId);
		if (featureNameReservationMap == null) {
			featureNameReservationMap = new FeatureNameReservationMap();
			modelElementIdReservationMap.put(modelElementId, featureNameReservationMap);
		}
		ExistenceOppositeReservationMap existenceOppositeReservationMap;
		if (!featureNameReservationMap.hasExistenceFeature()) {
			existenceOppositeReservationMap = new ExistenceOppositeReservationMap();
			featureNameReservationMap.put(FeatureNameReservationMap.EXISTENCE_FEATURE, existenceOppositeReservationMap);
		} else {
			existenceOppositeReservationMap = (ExistenceOppositeReservationMap) featureNameReservationMap
				.get(FeatureNameReservationMap.EXISTENCE_FEATURE);
		}
		existenceOppositeReservationMap.addConflictBucketCandidate(conflictBucketCandidate);
	}

	/**
	 * Adds a feature reservation for the given feature.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a feature
	 * @param conflictBucketCandidate
	 *            the conflict bucket candidate into which the reservation is put
	 */
	public void addFeatureReservation(String modelElementId, String featureName,
		ConflictBucketCandidate conflictBucketCandidate) {
		if (FeatureNameReservationMap.EXISTENCE_FEATURE.equals(featureName)) {
			addExistenceReservation(modelElementId);
			return;
		}
		FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementId);
		if (featureNameReservationMap == null) {
			featureNameReservationMap = new FeatureNameReservationMap();
			modelElementIdReservationMap.put(modelElementId, featureNameReservationMap);
		}
		OppositeReservationMap oppositeReservationMap;
		if (!featureNameReservationMap.containsKey(featureName)) {
			oppositeReservationMap = new OppositeReservationMap(false);
			featureNameReservationMap.put(featureName, oppositeReservationMap);
		} else {
			oppositeReservationMap = featureNameReservationMap.get(featureName);
		}
		oppositeReservationMap.setConflictBucketCandidate(conflictBucketCandidate);
		if (oppositeReservationMap.hasOpposites()) {
			throw new IllegalStateException(Messages.ReservationSet_IllegalReservation_SameFeatureAmbigousOpposite);
		}
	}

	/**
	 * Performs an opposite reservation for multi-reference features.
	 *
	 * @param modelElementID
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a multi-reference feature
	 * @param oppositeModelElementId
	 *            the opposite model element ID
	 * @param conflictBucketCandidate
	 *            the conflict bucket candidate into which the reservation is put
	 */
	public void addMultiReferenceWithOppositeReservation(String modelElementID, String featureName,
		String oppositeModelElementId, ConflictBucketCandidate conflictBucketCandidate) {
		FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementID);
		if (featureNameReservationMap == null) {
			featureNameReservationMap = new FeatureNameReservationMap();
			modelElementIdReservationMap.put(modelElementID, featureNameReservationMap);
		}
		OppositeReservationMap oppositeReservationMap;
		if (!featureNameReservationMap.containsKey(featureName)) {
			oppositeReservationMap = new OppositeReservationMap(true);
			featureNameReservationMap.put(featureName, oppositeReservationMap);
		} else {
			oppositeReservationMap = featureNameReservationMap.get(featureName);
		}

		// oppositeReservationMap.put(oppositeModelElementId, null);
		// oppositeReservationMap.setConflictBucketCandidate(conflictBucketCandidate);

		if (oppositeReservationMap.hasOpposites()) {
			// oppositeReservationMap.setConflictBucketCandidate(conflictBucketCandidate);
			oppositeReservationMap.put(oppositeModelElementId, conflictBucketCandidate);
		} else {
			throw new IllegalStateException(Messages.ReservationSet_IllegalReservation_SameFeatureAmbigousOpposite);
		}
	}

	/**
	 * Returns all model elements in the reservation set.
	 *
	 * @return a set of all model elements
	 */
	public Set<String> getAllModelElements() {
		return modelElementIdReservationMap.keySet();
	}

	/**
	 * Whether the given model element has an existence reservation.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @return {@code true}, if there is an existence reservation for the given model element, {@code false} otherwise
	 */
	public boolean hasExistenceReservation(String modelElementId) {
		final FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementId);
		return featureNameReservationMap != null && featureNameReservationMap.hasExistenceFeature();
	}

	/**
	 * Whether the given model element has a full reservation.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @return {@code true}, if there is a full reservation for the given model element, {@code false} otherwise
	 */
	public boolean hasFullReservation(String modelElementId) {
		final FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementId);
		return featureNameReservationMap != null && featureNameReservationMap.isAllFeatures();
	}

	/**
	 * Returns all reserved features for the given model element.
	 *
	 * @param modelElementId
	 *            the ID of a mode element
	 * @return a set of all feature names that are reserved for the given model element
	 */
	public Set<String> getFeatureNames(String modelElementId) {
		return modelElementIdReservationMap.get(modelElementId).keySet();
	}

	/**
	 * Whether the given model element has a reservation for the given feature.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            a feature name
	 * @return {@code true}, if the model element has a feature reservation for the given feature, {@code false}
	 *         otherwise
	 */
	public boolean hasFeatureReservation(String modelElementId, String featureName) {
		final FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementId);
		if (featureNameReservationMap == null) {
			return false;
		}
		final OppositeReservationMap oppositeReservationMap = featureNameReservationMap.get(featureName);
		return oppositeReservationMap != null && !oppositeReservationMap.hasOpposites();
	}

	/**
	 * Whether the given model element has a opposite reservation for the given feature.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a feature
	 * @return {@code true}, if the model element has a opposite reservation for the given feature, {@code false}
	 *         otherwise
	 */
	public boolean hasOppositeReservations(String modelElementId, String featureName) {
		final FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementId);
		if (featureNameReservationMap == null) {
			return false;
		}
		final OppositeReservationMap oppositeReservationMap = featureNameReservationMap.get(featureName);
		return oppositeReservationMap != null && oppositeReservationMap.hasOpposites();
	}

	/**
	 * Returns all opposites for the given model element ID and a feature.
	 *
	 * @param modelElementID
	 *            the ID of a model element
	 * @param featureName
	 *            the name of a feature
	 * @return a set of opposite model element IDs
	 */
	public Set<String> getOpposites(String modelElementID, String featureName) {
		return modelElementIdReservationMap.get(modelElementID).get(featureName).keySet();
	}

	/**
	 * Returns all conflict bucket candidates for the given model element.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @return a set of {@link ConflictBucketCandidate}s
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String modelElementId) {
		return modelElementIdReservationMap.getConflictBucketCandidates(modelElementId);
	}

	/**
	 * Returns all conflict bucket candidates for the given model element and a feature.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the feature name
	 * @return a set of {@link ConflictBucketCandidate}s
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String modelElementId, String featureName) {
		return modelElementIdReservationMap.getConflictBucketCandidates(modelElementId, featureName);
	}

	/**
	 * Returns all conflict bucket candidates for the given model element and its feature, together with
	 * an opposite model element.
	 *
	 * @param modelElementId
	 *            the ID of a model element
	 * @param featureName
	 *            the feature name
	 * @param oppositeModelElement
	 *            the ID of the opposite model element
	 * @return a set of {@link ConflictBucketCandidate}s
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String modelElementId, String featureName,
		String oppositeModelElement) {
		return modelElementIdReservationMap.getConflictBucketCandidates(modelElementId, featureName,
			oppositeModelElement);
	}

	/**
	 * Whether the given model element has a feature reservation with the given opposite model element.
	 *
	 * @param modelElementID
	 *            the ID of the model element
	 * @param featureName
	 *            a feature of the model element
	 * @param oppositeModelElementID
	 *            the ID of the opposite model element
	 * @return {@code true}, if there exists a opposite reservation, {@code false} otherwise
	 */
	public boolean hasOppositeReservation(String modelElementID, String featureName, String oppositeModelElementID) {
		final FeatureNameReservationMap featureNameReservationMap = modelElementIdReservationMap.get(modelElementID);
		if (featureNameReservationMap == null) {
			return false;
		}
		final OppositeReservationMap oppositeReservationMap = featureNameReservationMap.get(featureName);
		if (oppositeReservationMap == null || !oppositeReservationMap.hasOpposites()) {
			return false;
		}
		return oppositeReservationMap.containsKey(oppositeModelElementID);
	}

}