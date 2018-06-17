package kr.ac.cnu.web.games.blackjack;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rokim on 2018. 5. 26..
 */
public class Hand {
    private Deck deck;
    @Getter
    private List<Card> cardList = new ArrayList<>();

    public Hand(Deck deck) {
        this.deck = deck;
    }

    public Card drawCard() {
        Card card = deck.drawCard();
        cardList.add(card);
        return card;
    }

    public int getCardSum() {
        final int[] CardSum = {0};

        cardList.stream()
                .forEach(
                        Card -> {
                            // J, Q, K 는 10으로 계산
                            if(Card.getRank() == 11 || Card.getRank() == 12 || Card.getRank() == 13){
                                CardSum[0] += 10;
                            }
                            // 카드가 ACE 일 경우
                            if(Card.getRank() == 1){
                                // ACE를 11로 했을때 21이 넘어버림 -> ACE = 1
                                if (CardSum[0] > 11){
                                    CardSum[0] += 1;
                                }
                                else{
                                    CardSum[0] += 11;
                                }
                            }
                            else{
                                CardSum[0] += Card.getRank();
                            }
                        }
                );

        return CardSum[0];
    }

    public void reset() {
        cardList.clear();
    }
}
