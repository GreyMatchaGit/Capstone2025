package edu.citu.procrammers.eva.data;

public class LessonContent {
    private final int id;
    private final String topicTitle;
    private final String lessonText;
    private final String codeSnippet;
    private final String visualizerPath;

    public LessonContent(int id, String topicTitle, String lessonText, String codeSnippet, String visualizerPath) {
        this.id = id;
        this.topicTitle = topicTitle;
        this.lessonText = lessonText;
        this.codeSnippet = codeSnippet;
        this.visualizerPath = visualizerPath;
    }

    public int getId() {
        return id;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public String getLessonText() {
        return lessonText;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public String getVisualizerPath() {
        return visualizerPath;
    }
}
