package main;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;

public class TrackingConfigure {

    private static Gson gson = new Gson();

    private int fontCamId   = 0;
    private int behindCamId = 0;

    private boolean isFocused = false;
    private int focusWidth = 500;
    private int focusHeight = 300;
    private int focusX = 0;
    private int focusY = 0;

    public int getFontCamId() { return fontCamId; }
    public void setFontCamId(int fontCamId) { this.fontCamId = fontCamId; }

    public int getBehindCamId() { return behindCamId; }
    public void setBehindCamId(int behindCamId) { this.behindCamId = behindCamId; }

    public boolean isFocused() { return isFocused; }
    public void setFocused(boolean focused) { isFocused = focused; }

    public int getFocusWidth() { return focusWidth; }
    public void setFocusWidth(int focusWidth) { this.focusWidth = focusWidth; }

    public int getFocusHeight() { return focusHeight; }
    public void setFocusHeight(int focusHeight) { this.focusHeight = focusHeight; }

    public int getFocusX() { return focusX; }
    public void setFocusX(int focusX) { this.focusX = focusX; }

    public int getFocusY() { return focusY; }
    public void setFocusY(int focusY) { this.focusY = focusY; }

    public TrackingConfigure() {}

    public TrackingConfigure(String configFileName) {
        try {
            JsonReader reader = new JsonReader(new FileReader(configFileName));
            TrackingConfigure loadedConfig = gson.fromJson(reader, getClass());

            this.fontCamId = loadedConfig.fontCamId;
            this.behindCamId = loadedConfig.behindCamId;
            this.focusHeight = loadedConfig.focusHeight;
            this.focusWidth = loadedConfig.focusWidth;
            this.focusX = loadedConfig.focusX;
            this.focusY = loadedConfig.focusY;
            this.isFocused = loadedConfig.isFocused;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save(String configFileName) {
        try {
            Writer writer = new FileWriter(configFileName);
            System.out.println(gson.toJson(this));
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}
