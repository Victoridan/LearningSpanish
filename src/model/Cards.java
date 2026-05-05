package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cards {
    private static final String[][] words = {
            // Приветствия и вежливые слова
            {"привет", "hola"},
            {"доброе утро", "buenos días"},
            {"добрый вечер", "buenas tardes"},
            {"доброй ночи", "buenas noches"},
            {"до свидания", "adiós"},
            {"пока", "hasta luego"},
            {"пожалуйста", "por favor"},
            {"спасибо", "gracias"},
            {"не за что", "de nada"},
            {"извините", "perdón"},
            {"да", "sí"},
            {"нет", "no"},

            // Местоимения
            {"я", "yo"},
            {"ты", "tú"},
            {"он", "él"},
            {"она", "ella"},
            {"вы", "usted"},
            {"мы", "nosotros"},

            //  глаголы

            {"иметь", "tener"},
            {"делать", "hacer"},
            {"говорить", "hablar"},
            {"есть", "comer"},
            {"пить", "beber"},
            {"жить", "vivir"},
            {"работать", "trabajar"},
            {"учиться", "estudiar"},
            {"читать", "leer"},
            {"писать", "escribir"},
            {"идти", "ir"},
            {"приходить", "venir"},
            {"хотеть", "querer"},
            {"мочь", "poder"},
            {"знать", "saber"},
            {"нравиться", "gustar"},

            // Семья
            {"мама", "madre"},
            {"папа", "padre"},
            {"сын", "hijo"},
            {"дочь", "hija"},
            {"брат", "hermano"},
            {"сестра", "hermana"},
            {"муж", "esposo"},
            {"жена", "esposa"},
            {"друг", "amigo"},
            {"подруга", "amiga"},

            // Дом
            {"дом", "casa"},
            {"квартира", "piso"},
            {"комната", "habitación"},
            {"кухня", "cocina"},
            {"ванная", "baño"},
            {"стол", "mesa"},
            {"стул", "silla"},
            {"кровать", "cama"},
            {"дверь", "puerta"},
            {"окно", "ventana"},

            // Еда и напитки
            {"вода", "agua"},
            {"хлеб", "pan"},
            {"молоко", "leche"},
            {"кофе", "café"},
            {"чай", "té"},
            {"вино", "vino"},
            {"пиво", "cerveza"},
            {"мясо", "carne"},
            {"рыба", "pescado"},
            {"фрукты", "fruta"},
            {"рис", "arroz"},
            {"сахар", "azúcar"},
            {"соль", "sal"},

            // Город и транспорт
            {"улица", "calle"},
            {"площадь", "plaza"},
            {"магазин", "tienda"},
            {"ресторан", "restaurante"},
            {"больница", "hospital"},
            {"аэропорт", "aeropuerto"},
            {"машина", "coche"},
            {"автобус", "autobús"},
            {"поезд", "tren"},
            {"такси", "taxi"},

            // Цвета
            {"красный", "rojo"},
            {"синий", "azul"},
            {"зелёный", "verde"},
            {"жёлтый", "amarillo"},
            {"чёрный", "negro"},
            {"белый", "blanco"},

            // Числа
            {"один", "uno"},
            {"два", "dos"},
            {"три", "tres"},
            {"четыре", "cuatro"},
            {"пять", "cinco"},
            {"шесть", "seis"},
            {"семь", "siete"},
            {"восемь", "ocho"},
            {"девять", "nueve"},
            {"десять", "diez"},

            // Время
            {"сегодня", "hoy"},
            {"завтра", "mañana"},
            {"вчера", "ayer"},
            {"сейчас", "ahora"},
            {"всегда", "siempre"},
            {"никогда", "nunca"},
            {"день", "día"},
            {"ночь", "noche"},
            {"неделя", "semana"},
            {"месяц", "mes"},
            {"год", "año"},

            // Прилагательные
            {"большой", "grande"},
            {"маленький", "pequeño"},
            {"хороший", "bueno"},
            {"плохой", "malo"},
            {"красивый", "bonito"},
            {"новый", "nuevo"},
            {"старый", "viejo"},
            {"дорогой", "caro"},
            {"дешёвый", "barato"},
            {"холодный", "frío"},
            {"горячий", "caliente"},

            // Вопросы
            {"что?", "¿qué?"},
            {"кто?", "¿quién?"},
            {"где?", "¿dónde?"},
            {"когда?", "¿cuándo?"},
            {"почему?", "¿por qué?"},
            {"как?", "¿cómo?"},
            {"сколько?", "¿cuánto?"}
    };

    /**
     * Класс карточки.
     * id - индекс пары, по нему проверяем правильность ответа (у карточек, которые друг другу
     * соответствуют, он одинаковый)
     * text - само слово
     * state - состояние карточки в процессе игры
     */
    public static class Card {
        private final int id;
        private final String text;
        private State state;

        public enum State { NORMAL, SELECTED, CORRECT, WRONG, MATCHED }

        public Card(int pairId, String text) {
            this.id = pairId;
            this.text = text;
            this.state = State.NORMAL;
        }

        public int getId() { return id; }
        public String getText() { return text; }
        public State getState() { return state; }
        public void setState(State state) { this.state = state; }
    }

    /**
     * Функция принимает на вход количество пар и выбирает их случайным образом.
     * Если просят больше, чем есть, возвращает все, что есть.
     * Составляем список всех индексов, перемешиваем его с помощью функции shuffle,
     * берем первые n индексов, оборачиваем эти пары в оболочку класса Card и складываем
     * в два массива, русский и испанские, перемешиваем их отдельно и затем объединяем в один массив,
     * таким образом первая половина результирующего массива на русском,
     * вторая на испанском
     */
    public static List<Card> getRandomPairs(int n) {
        if (n > words.length) {
            n = words.length;
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        List<Card> ruCards = new ArrayList<>();
        List<Card> esCards = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int pairId = indices.get(i);
            ruCards.add(new Card(pairId, words[pairId][0]));
            esCards.add(new Card(pairId, words[pairId][1]));
        }

        Collections.shuffle(ruCards);
        Collections.shuffle(esCards);

        List<Card> cards = new ArrayList<>();
        cards.addAll(ruCards);
        cards.addAll(esCards);

        return cards;
    }
}