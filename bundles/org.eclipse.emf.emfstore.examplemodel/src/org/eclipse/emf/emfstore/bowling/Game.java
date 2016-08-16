/**
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 */
package org.eclipse.emf.emfstore.bowling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Game</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.emf.emfstore.bowling.Game#getMatchup <em>Matchup</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.bowling.Game#getPlayer <em>Player</em>}</li>
 * <li>{@link org.eclipse.emf.emfstore.bowling.Game#getFrames <em>Frames</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.emfstore.bowling.BowlingPackage#getGame()
 * @model
 * @generated
 */
public interface Game extends EObject {
	/**
	 * Returns the value of the '<em><b>Matchup</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.emf.emfstore.bowling.Matchup#getGames <em>Games</em>}
	 * '.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Matchup</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Matchup</em>' container reference.
	 * @see #setMatchup(Matchup)
	 * @see org.eclipse.emf.emfstore.bowling.BowlingPackage#getGame_Matchup()
	 * @see org.eclipse.emf.emfstore.bowling.Matchup#getGames
	 * @model opposite="games" required="true" transient="false"
	 * @generated
	 */
	Matchup getMatchup();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.emfstore.bowling.Game#getMatchup <em>Matchup</em>}' container
	 * reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value the new value of the '<em>Matchup</em>' container reference.
	 * @see #getMatchup()
	 * @generated
	 */
	void setMatchup(Matchup value);

	/**
	 * Returns the value of the '<em><b>Player</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Player</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Player</em>' reference.
	 * @see #setPlayer(Player)
	 * @see org.eclipse.emf.emfstore.bowling.BowlingPackage#getGame_Player()
	 * @model
	 * @generated
	 */
	Player getPlayer();

	/**
	 * Sets the value of the '{@link org.eclipse.emf.emfstore.bowling.Game#getPlayer <em>Player</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value the new value of the '<em>Player</em>' reference.
	 * @see #getPlayer()
	 * @generated
	 */
	void setPlayer(Player value);

	/**
	 * Returns the value of the '<em><b>Frames</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Frames</em>' attribute list isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Frames</em>' attribute list.
	 * @see org.eclipse.emf.emfstore.bowling.BowlingPackage#getGame_Frames()
	 * @model unique="false" upper="10"
	 * @generated
	 */
	EList<Integer> getFrames();

} // Game