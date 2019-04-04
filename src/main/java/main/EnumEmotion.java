package main;

public enum EnumEmotion {
    JOY, SORROW, ANGER, NEURAL,UNKNOWN, ERROR;

    public String getColorRelate(){
        switch (this){
            case JOY:
                return "#00c853"; //Green
            case ANGER:
                return "#b71c1c"; //Red
            case SORROW:
                return "#304ffe"; //Blue
            case NEURAL:
            case UNKNOWN:
            default:
                return "#212121";
        }
    }

    @Override
    public String toString(){
        switch (this){
            case ANGER:
                return "ANGER";
            case JOY:
                return "JOY";
            case SORROW:
                return "SORROW";
            case UNKNOWN:
                return "UNKNOWN";
            case NEURAL:
                return "NEURAL";
            case ERROR:
            default:
                return "ERROR";
        }
    }
}
