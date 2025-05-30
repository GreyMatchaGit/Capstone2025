package edu.citu.procrammers.eva.utils;

import javafx.scene.paint.Color;

public class Constant {

    public static final String EMPTY_STRING = "";

    public static final String[] ORACLE_MESSAGES = {
            "The Oracle is pondering your words...",
            "Consulting ancient scrolls...",
            "The Oracle is formulating wisdom...",
            "Seeking insight...",
            "The Oracle is meditating on your query...",
            "Gazing into the data beyond...",
            "Whispers of knowledge are forming...",
            "Divining the best response...",
            "Channeling the truth...",
            "Interpreting the signs..."
    };

    public static class Page {
        public static final String Loading = "loading-view.fxml";
        public static final String ErrorScreen = "error-view.fxml";

        public static final String Splash = "splash-view.fxml";
        public static final String Main = "main-view.fxml";

        public static final String Selection = "selection-view.fxml";
        public static final String Settings = "settings-view.fxml";
        public static final String MainMenu = "main-menu-view.fxml";

        public static final String Academy = "academy-view.fxml";
        public static final String Conquest = "conquest-view.fxml";
        public static final String ArrayListConquest = "arraylist-conquest-view.fxml";
        public static final String StackConquest = "stack-conquest-view.fxml";
        public static final String QueueConquest = "queue-conquest-view.fxml";
        public static final String DequeConquest = "deque-conquest-view.fxml";

        public static final String Chatbot = "chatbot-view.fxml";
        public static final String GeneralChatbot = "general-chatbot-view.fxml";

        public static final String PROMPT_PATH = "prompt.json";
        public static final String DATA_PATH = "data.json";
        public static final String Arrays = "array-visualizer-view.fxml";
        public static final String Stack = "stack-visualizer-view.fxml";
        public static final String Queue = "queue-visualizer-view.fxml";
        public static final String Deque = "deque-visualizer-view.fxml";
        public static final String BST = "bst-visualizer-view.fxml";
        public static final String LinkedList = "linkedlist-visualizer-view.fxml";
        public static final String Hashtable = "hashtable-visualizer-view.fxml";
    }

    public static class Value{
        public static final double BOX_SIZE = 50.0;
        public static final double OFFSET = 55.0;
        public static final double FRONT_BOUNDARY_X = 100.0;
    }

    public static class Sound{
        public static final String SFX_BUTTON_CLICK = "sfx/btn_click.MP3";
        public static final String SFX_LOGO_DING = "sfx/logo_ding.MP3";
        public static final String SFX_ACADEMY_HOVER = "sfx/academy_hover.mp3";
        public static final String SFX_CONQUEST_HOVER = "sfx/conquest_hover.mp3";
        public static final String MUSIC_ACADEMY = "music/academy_music.MP3";
        public static final String MUSIC_CONQUEST = "music/conquest_music.MP3";
        public static final String MUSIC_MAINMENU = "music/main_menu_music.m4a";
    }

    public static class Color {

        public static final javafx.scene.paint.Color POSITIVE = javafx.scene.paint.Color.ORANGE;
        public static final javafx.scene.paint.Color NEGATIVE = javafx.scene.paint.Color.RED;
        public static final javafx.scene.paint.Color SEARCH = javafx.scene.paint.Color.GREENYELLOW;
        public static final javafx.scene.paint.Color DEFAULT = javafx.scene.paint.Color.BLACK;
        public static final javafx.scene.paint.Color DEFAULTR = javafx.scene.paint.Color.WHITE;
        public static final javafx.scene.paint.Color PASTEL_ORANGE = javafx.scene.paint.Color.valueOf("#FFBD44");
        public static final javafx.scene.paint.Color SUNSET_ORANGE = javafx.scene.paint.Color.valueOf("#FF605C");
        public static final javafx.scene.paint.Color MALACHITE = javafx.scene.paint.Color.valueOf("#00CA4E");
    }

    public static class HashTable {
        public static final int EMPTY = Integer.MIN_VALUE;
        public static final int FULL  = EMPTY + 1;
        public static final int SENTINEL = FULL + 1;
        public static final int FINISHED = SENTINEL + 1;
        public static final int ERROR = FINISHED + 1;
        public static final int INVALID_COLLISION_METHOD = FINISHED + 1;
    }
}
