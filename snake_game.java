import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class snake_game extends JPanel implements ActionListener, KeyListener {

    // a class for holding the coordinates of each cell in the game
    public class cell{
        int x,y,size;

        public cell(int x, int y , int size){
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }

    // creating some important components of the game
    int x_velocity ;
    int y_velocity = 0;
    int panel_size;
    int cell_size = 25;
    boolean start_game = false;
    boolean game_over = false;
    Random random = new Random();
    Timer game_loop;
    JButton start_button,restart_button,exit_button;


    int score = 0 ;
    int highscore ;
    JLabel score_label;
    JLabel highscore_label;
    BufferedReader reader;

    cell food;
    cell snake_head;
    ArrayList<cell> snake_body = new ArrayList<>();



// *****************************************Creating constructor******************************************************
    public snake_game(int size)  {


        // setting panel
        this.panel_size = size;
        setBackground(Color.black);
        setPreferredSize(new Dimension(this.panel_size, this.panel_size));
        setFocusable(true);


        // creating score and high score labels
        this.score_label = new JLabel();
        this.score_label.setForeground(Color.white);
        this.score_label.setFont(new Font("Arial", Font.BOLD , 20));


        // creating highscore label
        this.highscore_label = new JLabel();
        this.highscore_label.setForeground(Color.white);
        this.highscore_label.setFont(new Font("Arial", Font.BOLD,20));


        // creating a reader object and reading data from highscore file through it
        try {
            this.reader = new BufferedReader(new FileReader("highscore"));
            this.highscore = Integer.parseInt(this.reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // creating head and a few initial body parts of the snake
        this.snake_head = new cell(random.nextInt(1,20)*(25),random.nextInt(1,20)*25,this.cell_size);



        //creating food
        this.food = new cell(random.nextInt(1,20)*(25),random.nextInt(1,20)*25,this.cell_size);



        // ensuring if the starting position of snake is more towards left in the panel then it will start moving  toward right on clicking the start button and vice versa to
        if (this.snake_head.x > (this.panel_size/2)){
            this.x_velocity = -25;
        }else{
            this.x_velocity = 25;
        }


        // creating reset button
        this.restart_button = new JButton(new ImageIcon("replay.png"));
        this.restart_button.addActionListener(this);


        // creating exit button
        this.exit_button = new JButton(new ImageIcon("exit.png"));
        this.exit_button.addActionListener(this);


        // creating start button for starting the game
        this.start_button = new JButton(new ImageIcon("start_button.jpg"));
        this.start_button.setBounds((int)(panel_size /2.5),(int)(panel_size /1.4),75,35);
        this.start_button.setBackground(Color.black);
        this.start_button.addActionListener(this);


        // enabling the panel to respond to key presses
        addKeyListener(this);


        // Initializing and starting a timer object  that will automatically call actionPerformed method every 100 milliseconds
        this.game_loop = new Timer(100,this);
        this.game_loop.start();
    }

//*********************************************Finished***************************************************************



    @Override
    // method that will be called by the Timer object every 100 milliseconds and execute all the code written within whenever it is called
    public void actionPerformed(ActionEvent e) {


        // if start button is clicked it will set the start_game variable to true
        if (e.getSource() == this.start_button) {
            this.start_game = true;
        } else if (e.getSource() == this.exit_button) {  // if the exit button is clicked it will close the game
            System.exit(0);
        } else if (e.getSource() == this.restart_button) { // if the restart button is clicked it will call the restart method
            restart();
        }


        // if the start_game variable is true it will call move_snake method and repaint method
        if (this.start_game) {
            repaint(); // repaint() will call the paintComponent method which will call the draw method
            move_snake();
        }
    }




    // method that will control the size and movement of the snake on the panel
    public void move_snake(){

        // if the snake hits any border of the panel it will set the game_over variable to true
        if(this.snake_head.x < 0 || this.snake_head.y < 0 || this.snake_head.x > this.panel_size  || this.snake_head.y > this.panel_size){
            this.game_over = true;
        }

        // if the snake hits the food it will perform the following actions
        if (detect_collision(snake_head,food)){
            update_score_and_highscore(); // updating score and highscore
            this.snake_body.add(new cell(this.food.x,this.food.y,this.cell_size)); // adding a new cell to a snake array
            reposition(this.food); //reposition the food
        }

        // as long as the game_over variable is false it will keep moving the snake
        if (!(this.game_over)) {
            for (int i = this.snake_body.size()-1; i >= 0 ; i--) {
                if(i==0){
                    snake_body.get(i).x = this.snake_head.x;
                    snake_body.get(i).y = this.snake_head.y;
                }else{
                    snake_body.get(i).x = snake_body.get(i-1).x;
                    snake_body.get(i).y = snake_body.get(i-1).y;
                }

            }
            this.snake_head.x += this.x_velocity;
            this.snake_head.y += this.y_velocity;
            for(cell body_part : this.snake_body){
                if(detect_collision(snake_head,body_part)){
                    this.game_over = true;
                }
            }
        }


    }


    // method for detecting collision between two cells
    public boolean detect_collision(cell c1, cell c2){
        return c1.x == c2.x && c1.y == c2.y;
    }


    // methods that will reposition the snake and food on the panel
    public void reposition(cell c ){
        c.x = random.nextInt(1,20)*25;
        c.y = random.nextInt(1,20)*25;
    }

    // method that will update score and highscore
    public void update_score_and_highscore() {
        this.score++;
        if (this.score > this.highscore) {
            this.highscore = this.score;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore"))) {
                writer.write(Integer.toString(this.highscore));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    // method that will be called by the repaint method automatically every 100 milliseconds
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
            draw(g);
    }




    // a method for drawing everything on game panel it will be called inside paintComponent method
    // @Override
    public void draw(Graphics g){

        // drawing reset button and exit button whenever game_over is true
        if(this.game_over){
            this.restart_button.setBounds((panel_size /3)-50, panel_size /2,this.cell_size*2,this.cell_size*2);
            add(restart_button);
            this.exit_button.setBounds((panel_size /2)+50, panel_size /2,this.cell_size*2,this.cell_size*2);
            add(this.exit_button);

        }

        // drawing start button and removing reset and exit button whenever game_over variable is false
        if (!(this.game_over)) {
            this.start_button.setBounds((int)(panel_size /2.5),(int)(panel_size /1.4),75,35);
            remove(this.exit_button);
            remove(this.restart_button);
            add(this.start_button);
        }




        // whenever start_game variable becomes true it will remove the start button from the panel
        if (this.start_game) {
            remove(this.start_button);
        }


        // drawing score label on the panel
        this.score_label.setBounds(0,0,this.cell_size*5,this.cell_size*2);
        add(this.score_label);
        this.score_label.setText("Score: " + this.score);
        this.highscore_label.setBounds(this.cell_size*16,0,this.cell_size*10,this.cell_size*2);
        this.highscore_label.setText("High Score: " + this.highscore);
        add(this.highscore_label);

        // uncomment the following lines of code if you want to draw grid lines
    //    g.setColor(Color.darkGray);
    //    for (int i = 0; i <= this.panel_size / this.cell_size; i++) {
    //        g.drawLine(0, i * this.cell_size, this.panel_size, i * this.cell_size);
    //        g.drawLine(i * this.cell_size,0, i * this.cell_size,this.panel_size);
    //    }
        // drawing food
        g.setColor(Color.red);
        g.fillOval(this.food.x, this.food.y, this.food.size, this.food.size);


        // drawing snake head and body
        g.setColor(Color.green);
        g.fillRect(this.snake_head.x, this.snake_head.y, this.snake_head.size, this.snake_head.size);

        for (int i = 0; i < snake_body.size(); i++) {
            g.fillRect(this.snake_body.get(i).x, this.snake_body.get(i).y, this.snake_body.get(i).size, this.snake_body.get(i).size);

        }

    }

    // this method will be called whenever game_over variable is true and reset button is clicked
    public void restart(){
        this.game_over = false;
        this.start_game = false;
        this.score = 0;
        this.snake_body.clear();
        reposition(this.food);
        reposition(this.snake_head);
        repaint();
    }



    @Override
    public void keyTyped(KeyEvent e) {

    }

    // method for changing the directions of snake
    @Override
    public void keyPressed(KeyEvent e) {
        if (this.start_game){
            if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && this.y_velocity != this.cell_size){
                this.y_velocity = -this.cell_size;
                this.x_velocity = 0;
            } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S ) && this.y_velocity != -this.cell_size){
                this.y_velocity = this.cell_size;
                this.x_velocity = 0;
            } else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A ) && this.x_velocity != this.cell_size){
                this.x_velocity = - this.cell_size;
                this.y_velocity = 0;
            } else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D ) && this.x_velocity != -this.cell_size){
                this.x_velocity = this.cell_size;
                this.y_velocity = 0;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    }