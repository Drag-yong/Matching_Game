import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.util.Random;

public class MatchingGame {
    // Congratulations message
    private final static String CONGRA_MSG = "CONGRATULATIONS! YOU WON!";
    // Cards not matched message
    private final static String NOT_MATCHED = "CARDS NOT MATCHED. Try again!";
    // Cards matched message
    private final static String MATCHED = "CARDS MATCHED! Good Job!";
    // 2D-array which stores cards coordinates on the window display
    private final static float[][] CARDS_COORDINATES =
            new float[][]{{170, 170}, {324, 170}, {478, 170}, {632, 170},
                    {170, 324}, {324, 324}, {478, 324}, {632, 324},
                    {170, 478}, {324, 478}, {478, 478}, {632, 478}};
    // Array that stores the card images filenames
    private final static String[] CARD_IMAGES_NAMES = new String[]{"apple.png",
            "ball.png", "peach.png", "redFlower.png", "shark.png", "yellowFlower.png"};

    private static PApplet processing; // PApplet object that represents
    // the graphic display window
    private static Card[] cards; // one dimensional array of cards
    private static PImage[] images; // array of images of the different cards
    private static Random randGen; // generator of random numbers
    private static Card selectedCard1; // First selected card
    private static Card selectedCard2; // Second selected card
    private static boolean winner; // boolean evaluated true if the game is won,
    // and false otherwise
    private static int matchedCardsCount; // number of cards matched so far
    // in one session of the game
    private static String message; // Displayed message to the display window
    private static int cardNumber = 0;

    private static int[] numList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}; // I will shuffle this with randGen

    /**
     * Defines the initial environment properties of this game as the program starts
     */
    public static void setup(PApplet processingInput) {
        processing = processingInput;

//        // Set the color used for the background of the Processing window
//        processing.background(245, 255, 250); // Mint cream color

        images = new PImage[CARD_IMAGES_NAMES.length];
        //load apple.png image file as PImage object and store its reference into images[0] ~ image[5]
        for (int i = 0; i < CARD_IMAGES_NAMES.length; i++) {
            images[i] = processing.loadImage("images" + File.separator + CARD_IMAGES_NAMES[i]);
        }

        initGame();
    }

    /**
     * Initializes the Game
     */
    public static void initGame() {
        randGen = new Random(Utility.getSeed());
        selectedCard1 = null;
        selectedCard2 = null;
        matchedCardsCount = 0;
        winner = false;
        message = "";
        cards = new Card[CARD_IMAGES_NAMES.length * 2];

        shuffleCards();
    }

    /**
     * Callback method called each time the user presses a key
     */
    public static void keyPressed() {
        if (processing.key == 'n' || processing.key == 'N') {
            initGame();
        }
    }

    /**
     * Callback method draws continuously this application window display
     */
    public static void draw() {
//        System.out.println("Draw method is working");
        // Set the color used for the background of the Processing window
        processing.background(245, 255, 250); // Mint cream color

        displayMessage(message);

        for (int i = 0; i < cards.length; i++) {
            cards[i].draw();
        }
    }

    /**
     * Displays a given message to the display window
     *
     * @param message to be displayed to the display window
     */
    public static void displayMessage(String message) {
        processing.fill(0);
        processing.textSize(20);
        processing.text(message, processing.width / 2, 50);
        processing.textSize(12);
    }

    /**
     * Checks whether the mouse is over a given Card
     *
     * @return true if the mouse is over the storage list, false otherwise
     */
    public static boolean isMouseOver(Card card) {
        int halfWidth = card.getImage().width / 2;
        int halfHeight = card.getImage().height / 2;
        int mouseX = processing.mouseX;
        int mouseY = processing.mouseY;

//        System.out.println("-----------------------");
//        System.out.println(card.getX());
//        System.out.println(card.getY());
//        System.out.println(mouseX);
//        System.out.println(mouseY);

        return card.getX() - halfWidth <= mouseX && mouseX <= card.getX() + halfWidth &&
                card.getY() - halfHeight <= mouseY && mouseY <= card.getY() + halfHeight;
    }

    /**
     * Callback method called each time the user presses the mouse
     */
    public static void mousePressed() {
        if (message.equals(NOT_MATCHED)) {
            for (int i = 0; i < cards.length; i++) {
                if (cards[i].equals(selectedCard1) || cards[i].equals(selectedCard2)) {
                    cards[i].deselect();
                    cards[i].setVisible(false);
                }
            }
            message = "";
            selectedCard1 = null;
            selectedCard2 = null;
        }

        for (int i = 0; i < cards.length; i++) {
            if (isMouseOver(cards[i]) && !cards[i].isVisible() && !winner) {
                if (cardNumber == 0) { // first card
                    selectedCard1 = cards[i];
                    cardNumber++;
                } else if (cardNumber == 1) { // second card
                    selectedCard2 = cards[i];
                    cardNumber = 0;
                }
//                System.out.println("mousePressed!!");
                cards[i].setVisible(true);
                cards[i].select();
            }
        }

        if (selectedCard1 != null && selectedCard2 != null) {
            if (matchingCards(selectedCard1, selectedCard2)) {
                message = MATCHED;
                matchedCardsCount++;
                selectedCard1 = null;
                selectedCard2 = null;
            } else {
                message = NOT_MATCHED;
            }
        }

        if (matchedCardsCount >= 6) {
            message = CONGRA_MSG;
            winner = true;
            selectedCard1 = null;
            selectedCard2 = null;
        }
    }

    /**
     * Checks whether two cards match or not
     *
     * @param card1 reference to the first card
     * @param card2 reference to the second card
     * @return true if card1 and card2 image references are the same, false otherwise
     */
    public static boolean matchingCards(Card card1, Card card2) {
        return card1.getImage().equals(card2.getImage());
    }

    //////////////////
    //private method//
    //////////////////

    /**
     * Shuffle the card deck
     */
    private static void shuffleCards() { // This private method will shuffle the cards in order to the randGen
        for (int i = 0; i < numList.length; i++) { // shuffle the list
            int ranNum = randGen.nextInt(12);
            int temp = numList[ranNum];
            numList[ranNum] = numList[i];
            numList[i] = temp;
        }

        for (int i = 0; i < CARDS_COORDINATES.length; i++) { // deploy the cards according to the list
            cards[i] = new Card(images[i / 2], CARDS_COORDINATES[numList[i]][0], CARDS_COORDINATES[numList[i]][1]);
//            cards[i].setVisible(false);
//            cards[i].draw();
        }
    }

    public static void main(String[] args) {
        Utility.runApplication();
    }
}
