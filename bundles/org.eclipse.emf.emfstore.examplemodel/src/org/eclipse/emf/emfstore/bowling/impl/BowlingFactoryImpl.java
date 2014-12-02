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
package org.eclipse.emf.emfstore.bowling.impl;

import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.emfstore.bowling.Area;
import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.BowlingPackage;
import org.eclipse.emf.emfstore.bowling.Fan;
import org.eclipse.emf.emfstore.bowling.Game;
import org.eclipse.emf.emfstore.bowling.Gender;
import org.eclipse.emf.emfstore.bowling.League;
import org.eclipse.emf.emfstore.bowling.Matchup;
import org.eclipse.emf.emfstore.bowling.Merchandise;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.bowling.Referee;
import org.eclipse.emf.emfstore.bowling.Tournament;
import org.eclipse.emf.emfstore.bowling.TournamentType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class BowlingFactoryImpl extends EFactoryImpl implements BowlingFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public static BowlingFactory init() {
		try {
			final BowlingFactory theBowlingFactory = (BowlingFactory) EPackage.Registry.INSTANCE
				.getEFactory(BowlingPackage.eNS_URI);
			if (theBowlingFactory != null) {
				return theBowlingFactory;
			}
		} catch (final Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new BowlingFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BowlingFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case BowlingPackage.PLAYER:
			return createPlayer();
		case BowlingPackage.LEAGUE:
			return createLeague();
		case BowlingPackage.TOURNAMENT:
			return createTournament();
		case BowlingPackage.MATCHUP:
			return createMatchup();
		case BowlingPackage.GAME:
			return createGame();
		case BowlingPackage.PLAYER_TO_POINTS_MAP:
			return (EObject) createPlayerToPointsMap();
		case BowlingPackage.REFEREE:
			return createReferee();
		case BowlingPackage.REFEREE_TO_GAMES_MAP:
			return (EObject) createRefereeToGamesMap();
		case BowlingPackage.AREA:
			return createArea();
		case BowlingPackage.FAN:
			return createFan();
		case BowlingPackage.MERCHANDISE:
			return createMerchandise();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case BowlingPackage.TOURNAMENT_TYPE:
			return createTournamentTypeFromString(eDataType, initialValue);
		case BowlingPackage.GENDER:
			return createGenderFromString(eDataType, initialValue);
		case BowlingPackage.XML_DATE:
			return createXMLDateFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case BowlingPackage.TOURNAMENT_TYPE:
			return convertTournamentTypeToString(eDataType, instanceValue);
		case BowlingPackage.GENDER:
			return convertGenderToString(eDataType, instanceValue);
		case BowlingPackage.XML_DATE:
			return convertXMLDateToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Player createPlayer() {
		final PlayerImpl player = new PlayerImpl();
		return player;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public League createLeague() {
		final LeagueImpl league = new LeagueImpl();
		return league;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Tournament createTournament() {
		final TournamentImpl tournament = new TournamentImpl();
		return tournament;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Matchup createMatchup() {
		final MatchupImpl matchup = new MatchupImpl();
		return matchup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Game createGame() {
		final GameImpl game = new GameImpl();
		return game;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Map.Entry<Player, Integer> createPlayerToPointsMap() {
		final PlayerToPointsMapImpl playerToPointsMap = new PlayerToPointsMapImpl();
		return playerToPointsMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Referee createReferee() {
		final RefereeImpl referee = new RefereeImpl();
		return referee;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Map.Entry<Referee, Game> createRefereeToGamesMap() {
		final RefereeToGamesMapImpl refereeToGamesMap = new RefereeToGamesMapImpl();
		return refereeToGamesMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Area createArea() {
		final AreaImpl area = new AreaImpl();
		return area;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Fan createFan() {
		final FanImpl fan = new FanImpl();
		return fan;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Merchandise createMerchandise() {
		final MerchandiseImpl merchandise = new MerchandiseImpl();
		return merchandise;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public TournamentType createTournamentTypeFromString(EDataType eDataType, String initialValue) {
		final TournamentType result = TournamentType.get(initialValue);
		if (result == null)
		{
			throw new IllegalArgumentException(
				"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertTournamentTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Gender createGenderFromString(EDataType eDataType, String initialValue) {
		final Gender result = Gender.get(initialValue);
		if (result == null)
		{
			throw new IllegalArgumentException(
				"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertGenderToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public XMLGregorianCalendar createXMLDateFromString(EDataType eDataType, String initialValue) {
		return (XMLGregorianCalendar) XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.DATE,
			initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertXMLDateToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.DATE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public BowlingPackage getBowlingPackage() {
		return (BowlingPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static BowlingPackage getPackage() {
		return BowlingPackage.eINSTANCE;
	}

} // BowlingFactoryImpl