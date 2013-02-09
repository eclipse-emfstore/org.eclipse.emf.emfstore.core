package org.eclipse.emf.emfstore.client.test.api;

import org.eclipse.emf.emfstore.bowling.BowlingFactory;
import org.eclipse.emf.emfstore.bowling.Game;
import org.eclipse.emf.emfstore.bowling.League;
import org.eclipse.emf.emfstore.bowling.Matchup;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.bowling.Tournament;
import org.eclipse.emf.emfstore.bowling.TournamentType;
import org.eclipse.emf.emfstore.client.ILocalProject;
import org.eclipse.emf.emfstore.client.IWorkspaceProvider;

public final class ProjectChangeUtil {

	private ProjectChangeUtil() {
	}

	public static Player addPlayerToProject(ILocalProject localProject) {
		Player player = createPlayer("player");
		localProject.getModelElements().add(player);
		return player;
	}

	public static League createLeague(String name) {
		League league = BowlingFactory.eINSTANCE.createLeague();
		league.setName(name);
		return league;
	}

	public static Game createGame(Player player) {
		Game game = BowlingFactory.eINSTANCE.createGame();
		game.setPlayer(player);
		return game;
	}

	public static Player createPlayer(String name) {
		Player player = BowlingFactory.eINSTANCE.createPlayer();
		player.setName(name);
		return player;
	}

	public static Tournament createTournament(boolean professional) {
		Tournament tournament = BowlingFactory.eINSTANCE.createTournament();
		if (professional) {
			tournament.setType(TournamentType.PRO);
		} else {
			tournament.setType(TournamentType.AMATEUR);
		}
		return tournament;
	}

	public static Matchup createMatchup(Game firstGame, Game secondGame) {
		Matchup matchup = BowlingFactory.eINSTANCE.createMatchup();

		if (firstGame != null) {
			matchup.getGames().add(firstGame);
		}

		if (secondGame != null) {
			matchup.getGames().add(firstGame);
		}
		return matchup;
	}

	public static ILocalProject createBasicBowlingProject() {
		ILocalProject localProject = IWorkspaceProvider.INSTANCE.getWorkspace().createLocalProject(
			"BasicBowlingProject", "");

		League leagueA = ProjectChangeUtil.createLeague("America");
		League leagueB = ProjectChangeUtil.createLeague("Europe");
		Player playerA = ProjectChangeUtil.createPlayer("Hans");
		Player playerB = ProjectChangeUtil.createPlayer("Anton");
		Player playerC = ProjectChangeUtil.createPlayer("Paul");
		Player playerD = ProjectChangeUtil.createPlayer("Klaus");
		Tournament tournamentA = ProjectChangeUtil.createTournament(false);
		Game gameA = ProjectChangeUtil.createGame(playerA);
		Game gameB = ProjectChangeUtil.createGame(playerB);
		Game gameC = ProjectChangeUtil.createGame(playerC);
		Game gameD = ProjectChangeUtil.createGame(playerD);
		Matchup matchupA = ProjectChangeUtil.createMatchup(gameA, gameB);
		Matchup matchupB = ProjectChangeUtil.createMatchup(gameC, gameD);

		leagueA.getPlayers().add(playerA);
		leagueA.getPlayers().add(playerB);
		leagueA.getPlayers().add(playerC);
		leagueA.getPlayers().add(playerD);

		tournamentA.getMatchups().add(matchupA);
		tournamentA.getMatchups().add(matchupB);

		localProject.getModelElements().add(leagueA);
		localProject.getModelElements().add(leagueB);
		localProject.getModelElements().add(tournamentA);

		return localProject;
	}

}
