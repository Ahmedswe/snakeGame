import javax.swing.*;

public class window {

    public static void main(String[] args) {


        int board_size = 550;
        JFrame frame = new JFrame("snake");
        frame.setSize(board_size,board_size);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel game = new snake_game(board_size);
        frame.add(game);
        frame.pack();
    }
}
