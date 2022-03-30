import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class s_GCTester extends GameCore {

	// Physics
	final int WALK_SPEED = 10;
	final int RUN_SPEED = 15;
	final int WEIGHT = 3;

	// Heights of platforms and other measurements
	final int SEVEN_BLOCKS 	= 192;
	final int SIX_BLOCKS 	= 256; // e.g. chandeliers
	final int FIVE_BLOCKS 	= 320;
	final int FOUR_BLOCKS   = 384;
	final int THREE_BLOCKS 	= 448; // e.g. piano
	final int TWO_BLOCKS 	= 512; // e.g. people
	final int ZERO_BLOCKS 	= 640; // e.g. floor

	final int BLOCK = 64; // width, not height from ground
	int floor = ZERO_BLOCKS;
	
	int elapsed = 0;
	int yPos = ZERO_BLOCKS;
	int indent = THREE_BLOCKS;
	int c = (indent + 64) * -1;  // collision factor.  Sprites are drawn from the left side of the screen, not the player; this compensates for the difference.
	int xPos = 0;
	int jump_dx = 0; // when jumping, the player rises by this height every time.
	
	
	int piano_left = (BLOCK * 14) + 32;
	int chand1_left = BLOCK * 10;
	int chand2_left = BLOCK * 19;
	int chand3_left = BLOCK * 26;
	
	boolean mayGoLeft = true;
	boolean mayGoRight = true;
	boolean mayAscend = true;
	boolean mayDescend = false;

	// Controls
	boolean goLeft = false;		// a
	boolean goRight = false;	// d
	boolean run = false;		// shift (+ a or d)
	boolean jump = false;		// space
	boolean attackl = false;	// h
	boolean attackr = false;	// l

	Image bg;
	
	Animation legs_anim;
	Animation chand_anim;
	Animation chandTop_anim;
	Animation arch_anim;

	// Player
	Sprite torso_spr;
	Sprite larm_spr;
	Sprite rarm_spr;
	Sprite legs_spr;

	// NPCs
	Enemy chas_spr;
	
	// Objects
	Sprite piano_spr;
	Sprite chand1_spr;
	Sprite chand2_spr;
	Sprite chand3_spr;
	Sprite chandTop1_spr;
	Sprite chandTop2_spr;
	Sprite chandTop3_spr;
	Sprite arch_spr;

	TileMap rokumeikan = new TileMap();



	public static void main(String[] args) {
		s_GCTester gct = new s_GCTester();
		gct.init();
		gct.level1();
		gct.run(false,1024,768);
		//gct.gameLoop();
	}
	


	public void init() {
		// Enemies
		Image chas_img = loadImage("images/rifleman.png");
		Image chascrouch_img = loadImage("images/rman_crouch.png");
		Animation chas_anim = new Animation();
		chas_anim.addFrame(chas_img, 200);
		chas_spr = new Enemy (chas_anim);
		


		// Player
		Image torso_img = loadImage("images/torso.png");
		Animation torso_anim = new Animation();
		torso_anim.addFrame(torso_img, 200);
		torso_spr = new Sprite(torso_anim);
		
		Image larm_img = loadImage("images/larm.png");
		Animation larm_anim = new Animation();
		larm_anim.addFrame(larm_img, 200);
		larm_spr = new Sprite(larm_anim);
		
		Image rarm_img = loadImage("images/rarm.png");
		Animation rarm_anim = new Animation();
		rarm_anim.addFrame(rarm_img, 200);
		rarm_spr = new Sprite(rarm_anim);
		
		Image run1_img = loadImage("images/run1.png");
		Image run2_img = loadImage("images/run2.png");
		Image run3_img = loadImage("images/run3.png");
		Image run4_img = loadImage("images/run4.png");
		Animation legs_anim = new Animation();
		legs_anim.setLoop(true);
		legs_anim.addFrame(run1_img, 200);
		legs_anim.addFrame(run2_img, 200);
		legs_anim.addFrame(run3_img, 200);
		legs_anim.addFrame(run4_img, 200);
		//legs_anim.play();
		legs_spr = new Sprite(legs_anim);
	//	legs_anim.start();
    	}



	public void level1() {
		// Loading images and maps.
		bg = new ImageIcon("images/shinhanga.jpg").getImage();	
		rokumeikan.loadMap("maps", "rokumeikan.txt");



		// Collision objects
		Image piano_img = loadImage("images/piano.png");
		Animation piano_anim = new Animation();
		piano_anim.addFrame(piano_img, 200);
		piano_spr = new Sprite(piano_anim);
		piano_spr.setX(piano_left);
		piano_spr.setY(THREE_BLOCKS);
		
		Image chand_img = loadImage("images/chand.png");
		Animation chand_anim = new Animation();
		chand_anim.addFrame(chand_img, 200);
		chand1_spr = new Sprite(chand_anim);	chand1_spr.setX(chand1_left);	chand1_spr.setY(SIX_BLOCKS);
		chand2_spr = new Sprite(chand_anim);	chand2_spr.setX(chand2_left);	chand2_spr.setY(SIX_BLOCKS);
		chand3_spr = new Sprite(chand_anim);	chand3_spr.setX(chand3_left);	chand3_spr.setY(SIX_BLOCKS);



		// Interactable objects
		Image chandTop_img = loadImage("images/chandTop.png");
		Animation chandTop_anim = new Animation();
		chandTop_anim.addFrame(chandTop_img, 200);
		chandTop1_spr = new Sprite(chandTop_anim);	chandTop1_spr.setX(chand1_left + BLOCK);	chandTop1_spr.setY(0);
		chandTop2_spr = new Sprite(chandTop_anim);	chandTop2_spr.setX(chand2_left + BLOCK);	chandTop2_spr.setY(0);
		chandTop3_spr = new Sprite(chandTop_anim);	chandTop3_spr.setX(chand3_left + BLOCK);	chandTop3_spr.setY(0);	
		
		
		
		// Misc objects
		Image arch_img = loadImage("images/arch.png");
		Animation arch_anim = new Animation();
		arch_anim.addFrame(arch_img, 200);
		arch_spr = new Sprite(arch_anim);	arch_spr.setX(BLOCK * 52);	
	}



	public void draw(Graphics2D g) {
		Color black = new Color(0,0,0);
		g.setColor(black);
		g.fillRect(0,0,getWidth(),getHeight());
		g.drawImage(bg,(-xPos / 32),0,null);
		
		rokumeikan.draw(g,-xPos,0);
		g.fillRect((-xPos - 8),0,8,getHeight());
		


		// Sprites
		
		// Collision objects
		g.drawImage(piano_spr.getImage(), piano_left - xPos, THREE_BLOCKS, null);
		g.drawImage(chand1_spr.getImage(), chand1_left - xPos, SIX_BLOCKS, null);
		g.drawImage(chand2_spr.getImage(), chand2_left - xPos, SIX_BLOCKS, null);
		g.drawImage(chand3_spr.getImage(), chand3_left - xPos, SIX_BLOCKS, null);
		


		// Interactable objects
		g.drawImage(chandTop1_spr.getImage(), chand1_left - xPos + BLOCK, 0, null);
		g.drawImage(chandTop2_spr.getImage(), chand2_left - xPos + BLOCK, 0, null);
		g.drawImage(chandTop3_spr.getImage(), chand3_left - xPos + BLOCK, 0, null);
		
		
		
		// Misc objects
		g.drawImage(arch_spr.getImage(), BLOCK * 52 - xPos, SIX_BLOCKS, null);
		


		// Enemies
		g.drawImage(chas_spr.getImage(), 1537 - xPos + (int) chas_spr.getX(), TWO_BLOCKS, null);
		


		// Player
		g.drawImage(torso_spr.getImage(), indent, (yPos - 128), null);
		if (goRight) g.drawImage(legs_spr.getImage(), indent, (yPos - 64), null);
		if (attackl) g.drawImage(larm_spr.getImage(), (indent - 128), (yPos - 128), null);
		if (attackr) g.drawImage(rarm_spr.getImage(), (indent + 64), (yPos - 128), null);
	}



	public void update(long elapsed) {
		elapsed++;



		// Walking and running
		if (indent <= 8) mayGoLeft = false;
		if (indent >= rokumeikan.getPixelWidth()) mayGoRight = false;
		if (mayGoLeft) {

			if ((xPos < 0) || (indent > THREE_BLOCKS)) {
				if (goLeft && !run) 	indent = indent - WALK_SPEED;
				if (goLeft && run) 	indent = indent - RUN_SPEED;
			} else {
				if (goLeft && !run) 	xPos = xPos - WALK_SPEED;
				if (goLeft && run) 	xPos = xPos - RUN_SPEED;
			}
		}
		
		if (mayGoRight) {
			if ((indent < THREE_BLOCKS) || (xPos >= rokumeikan.getPixelWidth() - 1024)) {
				if (goRight && !run)	indent = indent + WALK_SPEED;
				if (goRight && run)	indent = indent + RUN_SPEED;
			} else {
				if (goRight && !run) 	xPos = xPos + WALK_SPEED;
				if (goRight && run) 	xPos = xPos + RUN_SPEED;
			}	
		}



		// Jumping and falling
		if ((jump) && (yPos % 64 == 0)) {
			jump_dx = 40;
			mayDescend = true;
		}
		
		if (mayDescend) {
			yPos -= jump_dx;
			jump_dx -= WEIGHT;
		} else {
			jump_dx = 0;
		}
	


		// Combat
		chas_spr.attackChassepot(indent, xPos, yPos);



		// Collisions
		if (yPos > floor) {
			yPos = floor;
			mayDescend = false;
		}
		
		// Only check for collisions within a certain distance of the player.
		if (xPos < 48 * BLOCK) { 
			collision(piano_spr);
			collision(chand1_spr);
			collision(chand2_spr);
			collision(chand3_spr);
		}

		// Precursor to the above function:
		/*if ((xPos >= piano_left + c) && (xPos <= piano_right + c)) {
			if (yPos > THREE_BLOCKS) {
				if (xPos < piano_right - 64 + c) {
					xPos = piano_left + c;
					mayGoRight = false;
				} else {
					xPos = piano_right + c;
					mayGoLeft = false;
				}
			} else {
				mayGoLeft = true;
				mayGoRight = true;
				floor = THREE_BLOCKS;
			}
		} else if ((xPos >= chand2_left + c) && (xPos <= chand2_right + c)) {
			if ((yPos > SIX_BLOCKS) && (yPos < FIVE_BLOCKS)) {
				if (xPos < chand2_right - 64 + c) {
					xPos = chand2_left + c;
					mayGoRight = false;
				} else {
					xPos = chand2_right + c;
					mayGoLeft = false;
				}
			} else {
				mayGoLeft = true;
				mayGoRight = true;
				if (yPos > SIX_BLOCKS) {
					floor = ZERO_BLOCKS;
				} else {
					floor = SIX_BLOCKS;
				}

			}
		} else { // "else if" statements would continue for every sprite with collision detection; collisions existed independently from sprites themselves.
				mayGoLeft = true;
				mayGoRight = true;
				floor = ZERO_BLOCKS;
		}
	
	*/
	}



	public void collision(Sprite spr) {

		int left = (int) spr.getX() + c;
		int right = left + (int) spr.getImage().getWidth(null) + BLOCK;
		int top = (int) spr.getY();
		int bottom = top + (int) spr.getImage().getHeight(null);

		boolean inX;
		boolean inY;

		if ((xPos > left) && (xPos < right)) {
			inX = true;
		} else {
			inX = false;
		}

		if ((yPos > top) && (yPos < bottom)) {
			inY = true;
		} else {
			inY = false;
		}



		if ((inX) && (yPos == bottom)) {
			int distL = xPos - left;
			int distR = right - xPos;

			// Which edge of the rectangle is closest to the player?
			// Move the player to whichever edge, and disable colliding further.
			if (distL < distR) {
				xPos = left;
				mayGoRight = false;
			} else if (distR < distL) {
				xPos = right;
				mayGoLeft = false;
			}
		} else if ((inX) && (yPos == top)) {
			floor = top;
			yPos = top;
			mayDescend = false;
		} else if ((inX) && (inY)) {
			//jump_dx = 0;
			int distL = xPos - left;
			int distR = right - xPos;
			int distT = yPos - top;
			int distB = bottom - yPos;

			// Which edge of the rectangle is closest to the player?
			// Move the player to whichever edge, and disable colliding further.
			if ((distL < distR) && (distL < distT) && (distL < distB)) {
				xPos = left;
				mayGoRight = false;
			} else if ((distR < distL) && (distR < distT) && (distR < distB)) {
				xPos = right;
				mayGoLeft = false;
			} else if ((distT < distL) && (distT < distR) && (distT < distB)) {
				yPos = top;
				jump_dx = 0;
				mayDescend = false;
			} else if ((distB < distL) && (distB < distR) && (distB < distT)) {
				yPos = bottom;
				mayAscend = false;
			}
		}

		if (inX && ((xPos <= left + 10) || (xPos >= right - 10))) mayDescend = true;

		// Missing the object
		if (xPos < left    || !inY) mayGoRight = true;
		if (xPos > right   || !inY) mayGoLeft = true;
		if (yPos < top    || !inX) mayDescend = true;
		if (yPos > bottom || !inX) mayAscend = true;
		
		if (!inY) floor = ZERO_BLOCKS;
	}



	public void keyPressed(KeyEvent e) { 
		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = true;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = true;
		if (e.isShiftDown()) run = true;
		if ((e.getKeyCode() == KeyEvent.VK_SPACE) && (yPos % 64 == 0)) jump = true; 
		
		// Combat
		if ((e.getKeyCode() == KeyEvent.VK_H) && (!attackl) && (!attackr)) attackl = true;
		if ((e.getKeyCode() == KeyEvent.VK_L) && (!attackl) && (!attackr)) attackr = true;
	}



	public void keyReleased(KeyEvent e) {
		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = false;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = false;
		if (!e.isShiftDown()) run = false;
		if (e.getKeyCode() == KeyEvent.VK_SPACE) jump = false;
		
		// Combat
		if (e.getKeyCode() == KeyEvent.VK_H) attackl = false;
		if (e.getKeyCode() == KeyEvent.VK_L) attackr = false;
	}
}
