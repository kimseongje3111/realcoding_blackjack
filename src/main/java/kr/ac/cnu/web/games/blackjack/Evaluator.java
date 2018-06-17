package kr.ac.cnu.web.games.blackjack;

import java.util.Map;

/**
 * Created by rokim on 2018. 5. 27..
 */
public class Evaluator {
    private Map<String, Player> playerMap;
    private Dealer dealer;

    public Evaluator(Map<String, Player> playerMap, Dealer dealer) {
        this.playerMap = playerMap;
        this.dealer = dealer;
    }

    public boolean evaluate() {
        if (playerMap.values().stream().anyMatch(player -> player.isPlaying())) {
            return false;
        }

        int dealerResult = dealer.getHand().getCardSum();

        if (dealerResult > 21) {//딜러의 카드 합이 21이 넘을때 무조건 플레이어가 이기도록 구현되어 있던 부분을 바꾸었다.
            playerMap.forEach((s, player) -> {
                        if( player.getHand().getCardSum() > 21){
                            player.lost();
                        }
                        else{
                            player.win();
                        }
                    }
            );
            return true;
        }

        playerMap.forEach((s, player) -> {
            int playerResult = player.getHand().getCardSum();
            if (playerResult > 21) {
                player.lost();
            } else if (playerResult > dealerResult) {
                player.win();
            } else if (playerResult == dealerResult) {
                player.tie();
            } else {
                player.lost();
            }
        });

        return true;
    }


}
