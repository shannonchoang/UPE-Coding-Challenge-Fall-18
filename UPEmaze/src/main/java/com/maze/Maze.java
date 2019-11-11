package com.maze;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.maze.entity.Action;
import com.maze.entity.ActionResult;
import com.maze.entity.Board;
import com.maze.entity.StudentID;
import com.maze.entity.Token;

public class Maze {
    private static final String UID = "504768919";
    private static final String HOST_URL = "http://ec2-34-216-8-43.us-west-2.compute.amazonaws.com";
    private static final String TOKE_PATH = "/session";
    private static final String MAZE_PATH = "/game?token=%s";

    private HttpClient httpClient;
    private Gson gson;

    private String token;

    public Maze() {
        HttpClientBuilder create = HttpClientBuilder.create();
        httpClient = create.build();
        gson = new Gson();
    }

    private void retriveSession() throws IOException {
        HttpPost post = new HttpPost(HOST_URL + TOKE_PATH);
        post.setHeader("Content-Type", "application/json");

        StudentID uid = new StudentID();
        uid.setUid(UID);
        HttpEntity input = new StringEntity(gson.toJson(uid));
        post.setEntity(input);

        HttpResponse response = httpClient.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            Token tokenObj = gson.fromJson(EntityUtils.toString(response.getEntity(), "UTF-8"), Token.class);
            token = tokenObj.getToken();
            System.out.println("Got token: " + token);
        } else {
            System.err.println("Failed to get token: response code: " + response.getStatusLine().getStatusCode() + " Reason: " + response.getStatusLine().getReasonPhrase());
            System.exit(1);
        }
    }

    private Board getGame() throws IOException {
        HttpGet httpGet = new HttpGet(HOST_URL + String.format(MAZE_PATH, token));
        httpGet.setHeader("Accept", "application/json");

        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return gson.fromJson(EntityUtils.toString(response.getEntity(), "UTF-8"), Board.class);
        } else {
            System.err.println("Failed get the maze: response code: " + response.getStatusLine().getStatusCode());
            return null;
        }
    }

    private ActionResult move(String direction) throws IOException {
        HttpPost post = new HttpPost(HOST_URL + String.format(MAZE_PATH, token));
        post.setHeader("Content-Type", "application/json");

        Action action = new Action();
        action.setAction(direction);
        StringEntity input = new StringEntity(gson.toJson(action));
        post.setEntity(input);

        HttpResponse response = httpClient.execute(post);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return gson.fromJson(EntityUtils.toString(response.getEntity(), "UTF-8"), ActionResult.class);
        } else {
            System.err.println("Failed to make a move to: " + direction + " response code: " + response.getStatusLine().getStatusCode());
            return null;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Let's play!");
        Maze maze = new Maze();

        System.out.println("Getting a new session!");
        try {
            maze.retriveSession();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed get token");
            System.exit(1);
        }

        System.out.println("Creating the maze!");
        Board board = null;
	    do {
	    	try {
	            board = maze.getGame();
	            if (board == null) {
	                System.exit(1);
	            }
	            else if (board.getStatus() == "FINISHED") {
	            	System.out.println("Finished all levels! Congrats!");
	            	System.exit(1);
	            }
	            board.createBoard();
	            board.printBoard();
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.err.println("Failed get the game");
	            System.exit(1);
	        }
	
	        System.out.println("Starting the game!");
	        //loop until the game is done (break)
	        while (true) {
	            String direction = board.getNextMove();
	            if (direction == null) {
	                System.err.println("No place to go, game over...");
	               break;
	            }
	
	            try {
	                ActionResult result = maze.move(direction);
	                if (result == null || result.getResult().equals("EXPIRED")) {
	                    System.err.println("Failed to make a move, you finished " + board.getLevels_completed() + " levels. Game status: " + board.getStatus() + " exiting the game...");
	                    System.exit(1);
	                }
	
	                int[] position = board.getNextPosition(direction);
	                System.out.println("Moving to : " + direction + " was: " + result.getResult() +
	                        " Position: " + position[0] + " " + position[1]);
	
	                if (result.getResult().equals(ActionResult.WALL)) {
	                    board.setValue(position, Board.WALL);
	                } else if (result.getResult().equals(ActionResult.END)) {
	                    board.setCurrent_location(position);
	                    board.setValue(position, Board.END);
	                    break;
	                } else if (result.getResult().equals(ActionResult.SUCCESS)) {
	                    board.setCurrent_location(position);
	                    board.setPass(position);
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	                System.err.println("Unexpecting error happened, exiting the game...");
	                System.exit(1);
	            }
	//            board.printBoard();
	        }
	
	        board.printBoard();
	        board.printBoardWithPassCount();
	        System.out.println("Done!");
	        System.out.println("Just finished level " + (board.getLevels_completed() + 1) + " Total levels " + board.getTotal_levels());
	        System.out.println("Game status: " + board.getStatus());
        } while(board.getStatus() != "FINISHED" || (board.getLevels_completed() + 1 >= board.getTotal_levels()));
    }
}
