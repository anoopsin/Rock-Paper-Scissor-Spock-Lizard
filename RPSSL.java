import java.util.*;

public class RPSSL {
    private enum GameCharacter {ROCK, PAPER, SCISSORS, SPOCK, LIZARD};

    private static class Rule {
        private GameCharacter gameCharacter;
        private Set<GameCharacter> beatsGameCharacters;

        Rule(final GameCharacter gameCharacter, final Set<GameCharacter> beatsGameCharacters) {
            this.gameCharacter = gameCharacter;
            this.beatsGameCharacters = beatsGameCharacters;
        }
    }

    private static Map<GameCharacter, Rule> gameCharacterToRule = new HashMap<>(GameCharacter.values().length);

    private Map<GameCharacter, Integer> gameCharacterToWinCount = new HashMap<>(GameCharacter.values().length);
    private Map<GameCharacter, Integer> gameCharacterToLossCount = new HashMap<>(GameCharacter.values().length);
    private int numPlayers, numGames;

    static {
        final Rule rockRule = new Rule(GameCharacter.ROCK,
                new HashSet<>(Arrays.asList(GameCharacter.LIZARD, GameCharacter.SCISSORS)));

        final Rule paperRule = new Rule(GameCharacter.PAPER,
                new HashSet<>(Arrays.asList(GameCharacter.ROCK, GameCharacter.SPOCK)));

        final Rule scissorsRule = new Rule(GameCharacter.SCISSORS,
                new HashSet<>(Arrays.asList(GameCharacter.LIZARD, GameCharacter.PAPER)));

        final Rule spockRule = new Rule(GameCharacter.SPOCK,
                new HashSet<>(Arrays.asList(GameCharacter.ROCK, GameCharacter.SCISSORS)));

        final Rule lizardRule = new Rule(GameCharacter.LIZARD,
                new HashSet<>(Arrays.asList(GameCharacter.PAPER, GameCharacter.SPOCK)));


        gameCharacterToRule.put(GameCharacter.ROCK, rockRule);
        gameCharacterToRule.put(GameCharacter.PAPER, paperRule);
        gameCharacterToRule.put(GameCharacter.SCISSORS, scissorsRule);
        gameCharacterToRule.put(GameCharacter.SPOCK, spockRule);
        gameCharacterToRule.put(GameCharacter.LIZARD, lizardRule);
    }

    RPSSL(final int numPlayers, final int numGames) {
        this.numPlayers = numPlayers;
        this.numGames = numGames;
    }

    public void play() {
        for (int i = 0; i < numGames; i++) {
            final Set<GameCharacter> moves = generateMoves(numPlayers);

            // Tie check
            if (moves == null) continue;

            final GameCharacter winner = findWinner(new HashSet<>(moves));

            doBookkeeping(winner, moves);
        }
    }

    public void printResults() {
        for (final GameCharacter gameCharacter : GameCharacter.values()) {
            final int winCount = gameCharacterToWinCount.containsKey(gameCharacter) ?
                    gameCharacterToWinCount.get(gameCharacter) : 0;

            final int lossCount = gameCharacterToLossCount.containsKey(gameCharacter) ?
                    gameCharacterToLossCount.get(gameCharacter) : 0;

            final int numPicks = winCount + lossCount;

            final Integer winPercentage = (numPicks == 0) ? null : (winCount * 100) / (winCount + lossCount);

            System.out.println(gameCharacter + " won " + winCount + " times, lost " + lossCount +
                    " times and win percentage is " + ((winPercentage == null) ? "NA" : winPercentage + "%"));
        }
    }

    private static Set<GameCharacter> generateMoves(final int numMoves) {
        final Set<GameCharacter> moves = new HashSet<>(numMoves);

        for (int i = 0; i < numMoves; i++) {
            final GameCharacter move = pickRandomGameCharacter();

            // Tie check
            if (moves.contains(move)) return null;

            moves.add(move);
        }

        return moves;
    }

    private static GameCharacter findWinner(final Set<GameCharacter> gameCharacters) {
        if (gameCharacters.isEmpty()) return null;

        final Iterator<GameCharacter> iterator = gameCharacters.iterator();
        final Set<GameCharacter> losers = new HashSet<>();

        for (final GameCharacter gameCharacter : gameCharacters) {
            losers.addAll(gameCharacterToRule.get(gameCharacter).beatsGameCharacters);
        }

        gameCharacters.removeAll(losers);

        return gameCharacters.isEmpty() ? null : gameCharacters.iterator().next();
    }

    private static GameCharacter pickRandomGameCharacter() {
        final GameCharacter[] gameCharacters = GameCharacter.values();

        final int randomGameCharacterValue = new Random().nextInt(gameCharacters.length);

        return gameCharacters[randomGameCharacterValue];
    }

    private void doBookkeeping(final GameCharacter winner, final Set<GameCharacter> gameCharacters) {
        // Tie check
        if (winner == null) return;

        if (!gameCharacterToWinCount.containsKey(winner)) gameCharacterToWinCount.put(winner, 0);

        final int winnerCount = gameCharacterToWinCount.get(winner);
        gameCharacterToWinCount.put(winner, winnerCount + 1);

        gameCharacters.remove(winner);

        for (final GameCharacter loser : gameCharacters) {
            if (!gameCharacterToLossCount.containsKey(loser)) gameCharacterToLossCount.put(loser, 0);

            final int loserCount = gameCharacterToLossCount.get(loser);
            gameCharacterToLossCount.put(loser, loserCount + 1);
        }
    }

    public static void main(final String[] args) {
        final int numPlayers = 2;
        final int numGames = 10000;

        final RPSSL rpssl = new RPSSL(numPlayers, numGames);

        rpssl.play();
        rpssl.printResults();
    }
}
