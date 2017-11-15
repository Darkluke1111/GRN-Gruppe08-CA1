package de.uulm.in.vs.grn.a2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGuessingGameRequestHandler implements Runnable {

  private final int MAX_GUESSES = 6;
  private Socket socket;
  private int guesses;
  private int number;
  private boolean won;

  public NumberGuessingGameRequestHandler(Socket socket) {
    guesses = 0;
    won = false;
    number = ThreadLocalRandom.current().nextInt(50);
    this.socket = socket;
  }

  @Override
  public void run() {
    runGame();
  }

  private void runGame() {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
         PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")), true)) {

      pw.write("Welcome to the Number Guessing Game!\nGuess the secret number between 0 and 50.\nYou have " + MAX_GUESSES + " Tries left!\n");


      //Gamelogic
      while (guesses < MAX_GUESSES && !won) {
        int guessed = readInt(br, pw);

        if (guessed != -1) {
          guesses++;
          won = testGuess(guessed, number, br, pw);
        }
      }

      if (!won) {
        pw.write("You lost!\n");

      }
      //end Gamelogic

    } catch (IOException e) {
      System.err.println("Socket Streams wurden geschlossen. Der Client hat vermutlich die Verbindung verloren/beendet.");
    }
  }

  private int readInt(BufferedReader br, Writer bw) throws IOException {
    String input = br.readLine();


    int number;
    try {
      number = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      bw.write("Please enter a valid number between 0 and 50.\n");

      return -1;
    }


    if (number > 50 || number < 0) {
      bw.write("Please enter a valid number between 0 and 50.\n");

      return -1;
    }
    return number;
  }

  private boolean testGuess(int guess, int number, BufferedReader br, Writer bw) throws IOException {
    if (number == guess) {
      bw.write("You won!\n");

      return true;
    } else if (number < guess) {
      bw.write("Your guess is too high. Guesses left: " + (MAX_GUESSES - guesses) + "\n");

      return false;
    } else {
      bw.write("Your guess is too low. Guesses left: " + (MAX_GUESSES - guesses) + "\n");

      return false;
    }

  }
}
