package model;
import repository.WordRepository;
import java.util.*;
import java.util.stream.Collectors;


public class GameModel {
    private final List<Card> russianCards;
    private final List<Card> foreignCards;

    public GameModel(WordRepository repository, int pairCount) {
        List<WordPair> pairs = repository.loadPairs();
        Collections.shuffle(pairs);
        int n = Math.min(pairCount, pairs.size());
        List<WordPair> selected = pairs.subList(0, n);

        this.russianCards = selected.stream()
                .map(p -> new Card(p.id(), p.russian()))
                .collect(Collectors.toCollection(ArrayList::new));
        this.foreignCards = selected.stream()
                .map(p -> new Card(p.id(), p.foreign()))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(this.russianCards);
        Collections.shuffle(this.foreignCards);
    }

    public List<Card> getRussianCards() { return russianCards; }
    public List<Card> getForeignCards() { return foreignCards; }
    public boolean check(Card c1, Card c2) { return c1.getId() == c2.getId(); }
}