import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BrickBreaker {
	JFrame frame;
	static int[] paddleMove = { 0 };

	public BrickBreaker(boolean execute) {
		if (execute) {
			frame = new JFrame("Brick Breaker!");
			frame.setVisible(true);
			frame.setSize(1050, 800);
			BrickMovement b = new BrickMovement();
			frame.add(b);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
						paddleMove[0] = 1;
					}
					if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
						paddleMove[0] = -1;
					}
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					if (arg0.getKeyCode() == KeyEvent.VK_RIGHT && paddleMove[0] == 1) {
						paddleMove[0] = 0;
					}
					if (arg0.getKeyCode() == KeyEvent.VK_LEFT && paddleMove[0] == -1) {
						paddleMove[0] = 0;
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	public int[] getPaddleMove() {
		return paddleMove;
	}

	public static void main(String[] args) {
		new BrickBreaker(true);
	}
}

class BrickMovement extends JComponent {
	int padX, padY, ballX, ballY, ballXmomen, ballYmomen, playerwid=200;
	Rectangle player;
	long time = System.currentTimeMillis();
	long currentTime = 0;

	Color paddle = new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
	Color ball = new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
	Color brick = new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));

	CopyOnWriteArrayList<Rectangle> wall = new CopyOnWriteArrayList<Rectangle>();
	CopyOnWriteArrayList<Rectangle> powers = new CopyOnWriteArrayList<Rectangle>();

	int ballWid, ballLen;
	long ballTime = 0;
	
	boolean bigBall = false, laserPad = false;
	
	public BrickMovement() {
		padX = 350;
		padY = 600;
		ballX = 450;
		ballY = 500;
		ballXmomen = 5;
		ballYmomen = -5;
		player = new Rectangle(padX, padY, 200, 25);
		ballWid = 20;
		ballLen = 20;

		int rows = 0;

		for (int y = 0; y <= 420; y += 30) {
			for (int x = 0; x <= 900; x += 100) {
				if (rows % 2 == 0) {
					wall.add(new Rectangle(x, y, 90, 20));
				} else {
					wall.add(new Rectangle(x + 30, y, 90, 20));
				}
			}
			rows++;
		}

		BrickBreaker b = new BrickBreaker(false);
		Thread animate = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {

					currentTime = System.currentTimeMillis() - time;

					if (currentTime >= 10000) {
						if (ballXmomen > 0)
							ballXmomen += 1;
						else
							ballXmomen -= 1;
						if (ballYmomen > 0)
							ballYmomen += 1;
						else
							ballYmomen -= 1;
						currentTime = 0;
						time = System.currentTimeMillis();
					}
					int[] current = b.getPaddleMove();
					repaint();
					Random x = new Random();
					for(Rectangle r : powers) {
						r.setLocation((int)r.getX(), (int)r.getY()+3);
						if(player.contains(r.getX(), r.getY())) {
							int numb = x.nextInt(3);
							powers.remove(r);
							if(numb == 0)
								playerwid+=20;
							if(numb == 1) {
								ballXmomen = 5;
								ballYmomen = 5;
							}
							if(numb == 2) {
								ballWid = 40;
								ballLen = 40;
								bigBall = true;
								ballTime = System.currentTimeMillis();
							}
								
						}
					}
					
					if(bigBall) {
						if(System.currentTimeMillis() - ballTime >= 4000) {
							bigBall = false;
							ballTime = 0;
							ballWid = 20;
							ballLen = 20;
						}
					}
					
					if (current[0] == 1 && padX <= 850) {
						padX += 10;
					} else if (current[0] == -1 && padX >= 0) {
						padX -= 10;
					}
					ballX += ballXmomen;
					ballY += ballYmomen;
					if (ballX >= 1020 || ballX <= 0) {
						ballXmomen *= -1;
					}
					if (ballY <= 0) {
						ballYmomen *= -1;
					}
					if (ballY >= 800) {
						JOptionPane.showMessageDialog(null, "You Lose!");
						System.exit(0);
					}
					if (player.contains(ballX, ballY + 10) || player.contains(ballX, ballY) || 
							player.contains(ballX-10, ballY) || player.contains(ballX+10, ballY)) {
						ballYmomen *= -1;
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		animate.start();
	}

	public void paintComponent(Graphics g) {
		Graphics2D draw = (Graphics2D) g;
		player = new Rectangle(padX, padY, playerwid, 25);

		draw.setColor(paddle);
		draw.fillRect(padX, padY, playerwid, 25);

		draw.setColor(ball);
		draw.fillRect(ballX, ballY, ballWid, ballLen);

		draw.setColor(brick);

		for (Rectangle r : wall) {
			draw.fill(r);
			if (r.contains(ballX, ballY)) {
				if (new Random().nextInt(5) == 4) {
					powers.add(new Rectangle((int)r.getX(),(int)r.getY(),40,20));
				}
				wall.remove(r);
				ballYmomen *= -1;
			}
		}
		
		for (Rectangle r : powers) {
			draw.fill(r);
		}
	}
}
