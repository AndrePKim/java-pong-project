import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable{
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH*(0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 15;
    static final int PADDLE_HEIGHT = 150;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    PaddleBot paddle2;
    Ball ball;
    Score score;

    GamePanel() {
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH,GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        gameThread = new Thread(this);
        gameThread.start();
    }
    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
    }
    public void newPaddles() {
        paddle1 = new Paddle(10,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT);
        paddle2 = new PaddleBot(GAME_WIDTH-PADDLE_WIDTH-10,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT, ball);
    }
    public void paint(Graphics g) {
        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }
    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }
    public void move() {
        paddle1.move();
        paddle2.move();
        ball.move();
    }
    public void checkCollision() {
        random = new Random();
        //bounce ball off top & bottom window edges
        if (ball.y<=0) {
            ball.setYDirection(-ball.yVelocity);
        }
        if (ball.y>=GAME_HEIGHT-BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }
        //bounce ball off paddles
        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity += random.nextInt(2);
            if (ball.yVelocity>0) {
                ball.yVelocity += random.nextInt(3);
            } else {
                ball.yVelocity -= random.nextInt(3);
            }
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if (ball.intersects(paddle2)) {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity += random.nextInt(3);
            if (ball.yVelocity>0) {
                ball.yVelocity += random.nextInt(3);
            } else {
                ball.yVelocity -= random.nextInt(3);
            }
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        //stop paddles at window edges
        if (paddle1.y<=0) {
            paddle1.y = 0;
        }
        if (paddle1.y>=(GAME_HEIGHT-PADDLE_HEIGHT)) {
            paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
        }
        if (paddle2.y<=0) {
            paddle2.y = 0;
        }
        if (paddle2.y>=(GAME_HEIGHT-PADDLE_HEIGHT)) {
            paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
        }
        //give players a point and create new paddles and ball
        if (ball.x<=0) {
            score.player2++;
            newPaddles();
            newBall();
        }
        if (ball.x>=GAME_WIDTH-BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
        }
    }
    public void botYMovement() {
        if ((paddle2.y+PADDLE_HEIGHT/2)>ball.y+BALL_DIAMETER/2) {
            paddle2.setYDirection(-6);
            paddle2.move();
        } else if ((paddle2.y+PADDLE_HEIGHT/2)<ball.y+BALL_DIAMETER/2) {
            paddle2.setYDirection(6);
            paddle2.move();
        }
    }
    public void run() {
        //game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000/amountOfTicks;
        double delta = 0;
        while(true) {
            long now = System.nanoTime();
            delta += (now-lastTime)/ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                botYMovement();
                delta--;
            }
        }
    }
    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
        }
        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
        }
    }
}
