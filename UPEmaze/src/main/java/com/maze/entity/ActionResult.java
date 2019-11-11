package com.maze.entity;

public class ActionResult {
    public static final String SUCCESS = "SUCCESS";
    public static final String WALL = "WALL";
    public static final String END = "END";
    public static final String OUT_OF_BOUNDS = "OUT_OF_BOUNDS";

    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
