package main;

public class EmotionDetector {
    private static EmotionDetector instance;

    private EmotionDetector() {

    }

    public static EmotionDetector getInstance() {
        if (instance == null) {
            synchronized (EmotionDetector.class) {
                if (null == instance) {
                    instance = new EmotionDetector();
                }
            }
        }
        return instance;
    }
}
