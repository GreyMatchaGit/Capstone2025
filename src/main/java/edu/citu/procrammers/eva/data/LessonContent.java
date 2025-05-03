package edu.citu.procrammers.eva.data;

public class LessonContent {
    private final String topicTitle;
    private final String lessonText;
    private final String codeSnippet;
    private final String visualizerPath;

    public LessonContent(String topicTitle, String lessonText, String codeSnippet, String visualizerPath) {
        this.topicTitle = topicTitle;
        this.lessonText = lessonText;
        this.codeSnippet = codeSnippet;
        this.visualizerPath = visualizerPath;
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
