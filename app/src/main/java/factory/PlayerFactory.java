package factory;

import config.GameConfiguration;
import model.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerFactory {
    public static List<Player> generate(GameConfiguration gameConfiguration){
        int amount = gameConfiguration.getPlayers_quantity();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int temp = i +1;
            players.add(new Player("player-" + i, gameConfiguration.getAmountOfLives(), "starship-" + temp));
        }
        return players;
    }
}
