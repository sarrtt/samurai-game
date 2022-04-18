//package game2D;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * THE SAMURAI
 * @author 2721415
 */
@SuppressWarnings("serial")

public class Game extends GameCore {

	static int screenWidth = 1024;
	static int screenHeight = 768;

	int elapsed = 0;
	int level;


	// Positioning
	
	final int BLOCK = 64; // the width of my tiles
	
	// Heights of things from the ground
	final int SEVEN_BLOCKS 	= 192;
	final int SIX_BLOCKS 	= 256; // e.g. chandeliers
	final int FIVE_BLOCKS 	= 320;
	final int FOUR_BLOCKS   = 384;
	final int THREE_BLOCKS 	= 448; // e.g. piano
	final int TWO_BLOCKS 	= 512; // e.g. people
	final int ZERO_BLOCKS 	= 640; // e.g. floor

	// Collisions coping with relative movement
	float yPos = ZERO_BLOCKS; 	// the bottom of the player character sprite
	int indent = 448; 		// player's distance from left side of the screen
	int xOff = 0; 			// this is for the other things in the world besides the player!
	int leftmost;			// protag cannot walk left of this point, or he might get run over by a train
	int rightmost;			// protag wins by reaching this rightmost point of the stage

	// These declarations record the left-hand sides of sprites within the world.
	// They remain constant; it is the addition of xOff which lets them move around.
	int piano_left = (BLOCK * 14) + 32;
	int rifleman_left = 1537;
	int rifleman2_left = BLOCK * 58;
	int screen_left = BLOCK * 34;


	// Player attributes
	int hp = 4;
	final int WALK_SPEED = 10;
	final int RUN_SPEED = 15;
	final int WEIGHT = 3;
	int jump_dx = 0; // when jumping, the player rises by this height every time.


	// Boolean flags
	boolean cutscene = true;
	boolean win = false;
	boolean lose = false;
	boolean musicPlaying = false;
	
	// Controls
	boolean goLeft = false;		// a
	boolean goRight = false;	// d
	boolean run = false;		// shift + a or d
	boolean jump = false;		// space
	boolean attackl = false;	// h
	boolean attackr = false;	// l

	boolean mayGoLeft = true;
	boolean mayGoRight = true;
	boolean mayAscend = true;
	boolean mayDescend = false;


	// Resources
	
	// Opening scene
	Image studio;
	Image subs_enter;
	Animation sun1a;
	Animation sun2a;
	Sprite sun1;
	Sprite sun2;
	double sun1r = 0.1;
	double sun2r = 0.05;
	Animation titlea;
	Sprite title;
	Animation fadeouta;
	Sprite fadeout;

	Image bg_ballroom;
	Image bg_train;
	Image owari;
	Image select_ballroom;
	Image select_train;
	Image subs_ballroom;
	Image subs_train;
	
	// Characters
	Animation shife_anim;		// Character's torso at 4 HP,
	Animation hitonce_anim;		// 3 HP,
	Animation hittwice_anim;	// 2 HP,
	Animation hitthrice_anim;	// 1 HP,
	Animation down_anim;		// and his death animation.
	Animation legs_anim;		// Walking/running
	Animation legs_idle_anim;	// Standing, tapping foot
	Animation legs_jump_anim;
	Sprite torso_spr;
	Sprite larm_spr;
	Sprite rarm_spr;
	Sprite legs_spr;
	
	Animation rifleman_anim;
	Enemy rifleman_spr;
	Enemy rifleman2_spr;

	// Level 1: Ballroom
	TileMap ballroom_map = new TileMap();	
	TileMap ballroom_collisions = new TileMap();
	
	Animation chand_anim;
	Animation chandTop_anim;
	Animation screen_anim;
	ArrayList<Sprite> chandeliers = new ArrayList<Sprite>();
	ArrayList<Sprite> chandelierTops = new ArrayList<Sprite>();
	Sprite piano_spr;

	Image monet;

	Animation handrail_anim;
	Animation branch1_anim;
	Animation branch2_anim;
	Sprite handrail1_spr;
	Sprite handrail2_spr;
	Sprite branch1_spr;
	Sprite branch2_spr;
	Enemy screen_spr;
	
	// Level 2: Train
	TileMap train_map = new TileMap();
	TileMap tracks_map = new TileMap();
	TileMap train_collisions = new TileMap();

	Animation train_steam_anim;
	Sprite train_steam_spr;
	Animation train_semaphore_anim;
	Sprite train_semaphore_spr;
	
	boolean semaphore_incident = false;


	TileMap thisLevel;


	public static void main(String[] args) {
		Game gct = new Game();
		gct.init_load();
		gct.run(false, screenWidth, screenHeight);
	}


	
	public void debug() {
	}

	

	public void init_load () {
		long startLoad = System.currentTimeMillis();
		System.out.println("Loading...");

		Sprite s;

		// Opening
		studio = new ImageIcon("images/toho.png").getImage();
		subs_enter = new ImageIcon("images/subs_enter.png").getImage();
		
		titlea = new Animation();
		titlea.addFrame(loadImage("images/start_title.png"), 200);
		title = new Sprite(titlea);
		
		sun1a = new Animation();
		sun1a.addFrame(loadImage("images/start_sun1.png"), 200);
		sun2a = new Animation();
		sun2a.addFrame(loadImage("images/start_sun2.png"), 200);
		sun1 = new Sprite(sun1a);
		sun2 = new Sprite(sun2a);

		fadeouta = new Animation();
		fadeouta.addFrame(loadImage("images/fade/10.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/20.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/30.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/40.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/50.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/70.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/90.png"), 200);
		fadeouta.addFrame(loadImage("images/fade/100.png"), 500);
		fadeout = new Sprite(fadeouta);
		fadeouta.setLoop(false);

		// Player

		// Torso and head: indicates damage
		shife_anim = new Animation();
		shife_anim.loadAnimationFromSheet("images/protag_hp4.png",2,1,1200);
		hitonce_anim = new Animation();
		hitonce_anim.loadAnimationFromSheet("images/protag_hp3.png",2,1,1200);
		hittwice_anim = new Animation();
		hittwice_anim.loadAnimationFromSheet("images/protag_hp2.png",2,1,1200);
		hitthrice_anim = new Animation();
		hitthrice_anim.loadAnimationFromSheet("images/protag_hp1.png",2,1,1200);
		torso_spr = new Sprite(shife_anim);

		// Arms and sword: for combat
		Image larm_img = loadImage("images/protag_arml.png");
		Animation larm_anim = new Animation();
		larm_anim.addFrame(larm_img, 200);
		larm_spr = new Sprite(larm_anim);
		
		Image rarm_img = loadImage("images/protag_armr.png");
		Animation rarm_anim = new Animation();
		rarm_anim.addFrame(rarm_img, 200);
		rarm_spr = new Sprite(rarm_anim);

		// Legs: indicates movement.  Also sole sprite for laying KO'd	
		legs_idle_anim = new Animation();
		legs_idle_anim.loadAnimationFromSheet("images/protag_legs_idle.png",2,1,600);
		legs_anim = new Animation();
		legs_anim.loadAnimationFromSheet("images/protag_legs_run.png",4,1,200);
		legs_jump_anim = new Animation();
		legs_jump_anim.loadAnimationFromSheet("images/protag_legs_jump.png",2,1,100);
		down_anim = new Animation();
		down_anim.loadAnimationFromSheet("images/protag_legs_dead.png",2,1,1200);
		legs_spr = new Sprite(legs_idle_anim);



		rifleman_anim = new Animation();
		rifleman_spr = new Enemy(rifleman_anim, "rifleman");
		rifleman2_spr = new Enemy(rifleman_anim, "rifleman");
		


		// Level select menu
		select_ballroom = new ImageIcon("images/select_ballroom.jpg").getImage();
		select_train = new ImageIcon("images/select_train.jpg").getImage();
		subs_ballroom = new ImageIcon("images/subs_ballroom.png").getImage();
		subs_train = new ImageIcon("images/subs_train.png").getImage();

		// Ballroom
		bg_ballroom = new ImageIcon("images/bg_ballroom.jpg").getImage();	
		ballroom_map.loadMap("maps", "ballroom.txt");
		ballroom_collisions.loadMap("maps", "ballroom_collisions.txt");

		Animation chand_anim = new Animation();
		Animation chandTop_anim = new Animation();
		chand_anim.addFrame(loadImage("images/ballroom_chandelier.png"), 200);
		chandTop_anim.addFrame(loadImage("images/ballroom_chandelierCord.png"), 200);
		for (int i = 0; i < 4; i++) {	
			s = new Sprite(chand_anim);
			s.show();
			chandeliers.add(s);
		}	
		for (int i = 0; i < 4; i++) {	
			s = new Sprite(chandTop_anim);
			s.show();
			chandelierTops.add(s);	
		}

		Animation piano_anim = new Animation();
		piano_anim.addFrame(loadImage("images/ballroom_piano.png"), 200);
		piano_spr = new Sprite(piano_anim);
		
		Animation handrailin_anim = new Animation();
		Animation handrailout_anim = new Animation();
		handrailin_anim.addFrame(loadImage("images/ballroom_handrail_in.png"), 200);
		handrailout_anim.addFrame(loadImage("images/ballroom_handrail_out.png"), 200);
		handrail1_spr = new Sprite(handrailin_anim);
		handrail2_spr = new Sprite(handrailout_anim);

		Animation branch1_anim = new Animation();
		branch1_anim.addFrame(loadImage("images/garden_branch1.png"), 200);
		branch1_spr = new Sprite(branch1_anim);

		Animation branch2_anim = new Animation();
		branch2_anim.addFrame(loadImage("images/garden_branch2.png"), 200);
		branch2_spr = new Sprite(branch2_anim);
		
		screen_anim = new Animation();
		screen_spr = new Enemy (screen_anim, "screen");

		monet = new ImageIcon("images/ballroom_monet.png").getImage();
		
		// Train
		bg_train = new ImageIcon("images/bg_train.jpg").getImage();	
		train_map.loadMap("maps", "train.txt");
		tracks_map.loadMap("maps", "track.txt");
		train_collisions.loadMap("maps", "train_collisions.txt");

		train_steam_anim = new Animation();
		train_steam_anim.loadAnimationFromSheet("images/train_steam.png",2,1,600);
		train_steam_spr = new Sprite(train_steam_anim);
		

		Animation train_semaphore_anim = new Animation();
		train_semaphore_anim.addFrame(loadImage("images/train_semaphore.png"), 200);
		train_semaphore_spr = new Sprite(train_semaphore_anim);

		long endLoad = System.currentTimeMillis() - startLoad;
		System.out.println("Loaded in " + endLoad + " milliseconds!");


		opening();
	}



	public void opening() {
		sun1.setX(screenWidth / 2);
		sun1.setY(screenHeight / 2);
		sun2.setX(screenWidth / 2);
		sun2.setY(screenHeight / 2);
		title.setX(-4096);
		title.setY(0);
	}


	
	private void init_game(int select) {
		cutscene = false;

		win = false;
		lose = false;
		hp = 4;
		
		xOff = 0;
		yPos = ZERO_BLOCKS;
		jump_dx = 0;
		indent = 448;
		
		mayGoLeft = true;
		mayGoRight = true;
		mayAscend = true;

		// Initialising levels
		if (select == 0) {
			level = 0;

			indent = (screenWidth / 2) - 32;		
		} else if (select == 1) {
			level = 1;

			int j = 448;
			for (Sprite s: chandeliers) {
				int i = chandeliers.indexOf(s);
				if (i <= 1) s.setX(192 + (i * j));
				if (i > 1) s.setX(192 + 128 + (i * j));
				s.setY(SIX_BLOCKS);
			}	
			for (Sprite s: chandelierTops) {
				int i = chandelierTops.indexOf(s);
				if (i <= 1) s.setX(192 + (i * j) + 64);
				if (i > 1) s.setX(192 + 128 + (i * j) + 64);
				s.setY(0);
			}	

			piano_spr.setX(piano_left);		piano_spr.setY(THREE_BLOCKS);
			handrail1_spr.setX((33 * BLOCK) + 16); 	handrail1_spr.setY(SEVEN_BLOCKS);
			handrail2_spr.setX((39 * BLOCK) + 16); 	handrail2_spr.setY(SEVEN_BLOCKS);
			branch1_spr.setX(BLOCK * 54); 		branch1_spr.setY(FOUR_BLOCKS);
			branch2_spr.setX(BLOCK * 56); 		branch2_spr.setY(FIVE_BLOCKS);

			rifleman_spr.setX(rifleman_left);
			rifleman_spr.rifleman_init();
			rifleman2_spr.setX(rifleman2_left);
			rifleman2_spr.rifleman_init();
			screen_spr.setX(screen_left);
			screen_spr.screen_init();

			rifleman_spr.setY(512);
			rifleman2_spr.setY(512);
			screen_spr.setY(512);

			rifleman_spr.reset();
			rifleman2_spr.reset();
			screen_spr.reset();

			rifleman_spr.setTileMap(ballroom_collisions);
			rifleman2_spr.setTileMap(ballroom_collisions);
			screen_spr.setTileMap(ballroom_collisions);

			// Starts the level's soundtrack
			try { if (!musicPlaying) soundtrack(level); } catch (Exception e) { System.out.println("MIDI error!"); }
		} else if (select == 2) {
			level = 2;

			yPos = TWO_BLOCKS;

			train_semaphore_spr.setX(-32);
			train_semaphore_spr.setY(SIX_BLOCKS);
			semaphore_incident = false;

			train_steam_spr.setX(BLOCK * 5);
			train_steam_spr.setY(0);

			rifleman_spr.setX(rifleman_left);
			rifleman_spr.rifleman_init();
			rifleman2_spr.setX(rifleman2_left);
			rifleman2_spr.rifleman_init();

			rifleman_spr.setY(448);
			rifleman2_spr.setY(448);
			
			rifleman_spr.reset();
			rifleman2_spr.reset();

			rifleman_spr.setTileMap(train_collisions);
			rifleman2_spr.setTileMap(train_collisions);

			// Starts the level's soundtrack
			try { if (!musicPlaying) soundtrack(level); } catch (Exception e) { System.out.println("MIDI error!"); }
		}
		
		// Arranging protag's body parts
		torso_spr.setX(indent);
		torso_spr.setY(yPos - 128);
		torso_spr.setAnimation(shife_anim);
		larm_spr.setX(indent - 128);
		larm_spr.setY(yPos - 128);
		rarm_spr.setX(indent + 64);
		rarm_spr.setY(yPos - 128);
		legs_spr.setX(indent);
		legs_spr.setY(yPos - 64);
	}



	private void soundtrack (int select) throws InvalidMidiDataException, IOException, MidiUnavailableException {
		Sequence debussy = MidiSystem.getSequence(new File("sound/debussy.mid")); // Debussy's Suite Bergmasque #4
		Sequence saintsaens = MidiSystem.getSequence(new File("sound/saintsaens.mid"));  // Saint-Saens' Concerto in G Minor #3
		
		Sequencer sqr = MidiSystem.getSequencer();
		sqr.open();
		
		if (select == 1) sqr.setSequence(debussy);
		if (select == 2) sqr.setSequence(saintsaens);

		sqr.start();
		musicPlaying = true;
	}



	public void draw(Graphics2D g) {
		Color black = new Color(0,0,0);
		g.setColor(black);
		g.fillRect(0,0,getWidth(),getHeight());
		
		if (cutscene) {
			sun2.setRotation(sun2.getRotation() + sun2r);
			sun2.setX(0);
			sun2.setY(-192);
			sun2.drawTransformed(g);
			sun1.setRotation(sun1.getRotation() + sun1r);
			sun1.setX(0);
			sun1.setY(-192);
			sun1.drawTransformed(g);
			g.drawImage(studio, 258, 134, null);
			g.drawImage(title.getImage(), (int) title.getX(), (int) title.getY(), null);

			if (title.getX() >= 0) {
				g.drawImage(subs_enter, 0, 0, null);
			}

			return;
		} 

		// Backgrounds
		if (level == 0) { // Level select menu
			// A picture representing each level
			g.drawImage(select_ballroom, 0, 0, null);
			g.drawImage(select_train, 512, 0, null);

			if (torso_spr.getX() <= ((screenWidth / 2) - 64)) g.drawImage(subs_ballroom, 0, 0, null);
			if (torso_spr.getX() >= ((screenWidth / 2) + 64)) g.drawImage(subs_train, 0, 0, null);


		} else if (level == 1) { // Ballroom
			g.drawImage(bg_ballroom, (-xOff / 32), 0, null);
			g.fillRect((-xOff - 8),0,8,getHeight());

			// Tilemaps
			ballroom_collisions.draw(g,-xOff,0);
			ballroom_map.draw(g,-xOff,0);
			
			// Obstacles			
			for (Sprite s: chandeliers) g.drawImage(s.getImage(), (int) s.getX() - xOff, (int) s.getY(), null);
			for (Sprite s: chandelierTops) g.drawImage(s.getImage(), (int) s.getX() - xOff, (int) s.getY(), null);

			g.drawImage(monet, (piano_left + 32) - xOff, SIX_BLOCKS - 32, null);
			g.drawImage(piano_spr.getImage(), piano_left - xOff, THREE_BLOCKS, null);
			g.drawImage(handrail1_spr.getImage(), (int) handrail1_spr.getX() - xOff, (int) handrail1_spr.getY(), null);
			g.drawImage(handrail2_spr.getImage(), (int) handrail2_spr.getX() - xOff, (int) handrail2_spr.getY(), null);
			g.drawImage(branch1_spr.getImage(), (int) branch1_spr.getX() - xOff, (int) branch1_spr.getY(), null);
			g.drawImage(branch2_spr.getImage(), (int) branch2_spr.getX() - xOff, (int) branch2_spr.getY(), null);
		} else if (level == 2) { // Train
			g.drawImage(bg_train, -(xOff / 64), 0, null);

			g.drawImage(train_semaphore_spr.getImage(), (int) train_semaphore_spr.getX(), (int) train_semaphore_spr.getY(), null); // a rare sprite behind the tilemap
			
			g.drawImage(train_steam_spr.getImage(), (int) train_steam_spr.getX() - xOff, (int) train_steam_spr.getY(), null);

			train_collisions.draw(g,-xOff, 0);
			train_map.draw(g,-xOff, 0);
			tracks_map.draw(g,0,0);

		}


		// Player
		if (!lose) {
			torso_spr.setY(yPos - 128);
			larm_spr.setY(yPos - 128);
			rarm_spr.setY(yPos - 128);

			g.drawImage(torso_spr.getImage(), (int) torso_spr.getX(), (int) torso_spr.getY(), null);
			if (attackl && mayGoLeft) g.drawImage(larm_spr.getImage(), (int) larm_spr.getX(), (int) larm_spr.getY(), null);
			if (attackr && mayGoRight) g.drawImage(rarm_spr.getImage(), (int) rarm_spr.getX(), (int) rarm_spr.getY(), null);
			
			// Legs are the most complicated to animate: the movements convey speed, heading, jumping and game over statuses.	
			if (run) { 
				legs_anim.setAnimationSpeed(2);
			} else {
				legs_anim.setAnimationSpeed(1);
			}
			
			if (jump_dx > 0 && mayDescend) {
				legs_spr.setAnimation(legs_jump_anim);
				if (goLeft) {
					legs_spr.setScale(-1.0f, 1.0f);
					legs_spr.setY(yPos - 64);
					legs_spr.drawTransformed(g);
				} else {
					legs_spr.setScale(1.0f, 1.0f);
					legs_spr.setY(yPos - 64);
					legs_spr.drawTransformed(g);	
				}
			} else if (goLeft) {
				legs_spr.setAnimation(legs_anim);
				legs_spr.setScale(-1.0f, 1.0f);
				legs_spr.setY(yPos - 64);
				legs_spr.drawTransformed(g);
			} else if (goRight) {
				legs_spr.setAnimation(legs_anim);
				legs_spr.setScale(1.0f, 1.0f);
				legs_spr.setY(yPos - 64);
				legs_spr.drawTransformed(g);
			} else {
				legs_spr.setAnimation(legs_idle_anim);
				g.drawImage(legs_spr.getImage(), (int) legs_spr.getX(), (int) (yPos - 64), null);
			}
		} else {
			legs_spr.setAnimation(down_anim);
			g.drawImage(legs_spr.getImage(), (int) legs_spr.getX(), (int) (yPos - 64), null);
		}



		// Foregrounds i.e. things to stick your sword into
		if (level == 1) {	
			if (!screen_spr.getStatus()) {
				g.drawImage(screen_spr.getImage(), screen_left - xOff, TWO_BLOCKS, null);
			}	
		}

		if ((level == 1) || (level == 2)) {
			if (!rifleman_spr.getStatus()) {
				rifleman_spr.setX(rifleman_left - xOff + rifleman_spr.getOwnMovement());
				if (torso_spr.getX() < rifleman_spr.getX()) {
					g.drawImage(rifleman_spr.getImage(), (int) rifleman_spr.getX(), (int) rifleman_spr.getY(), null);
				} else {
					rifleman_spr.setScale(-1.0f, 1.0f);
					rifleman_spr.drawTransformed(g);
				}
			}
			
			if (!rifleman2_spr.getStatus()) {
				rifleman2_spr.setX(rifleman2_left - xOff + rifleman2_spr.getOwnMovement());
				if (torso_spr.getX() < rifleman2_spr.getX()) {
					g.drawImage(rifleman2_spr.getImage(), (int) rifleman2_spr.getX(), (int) rifleman2_spr.getY(), null);
				} else {
					rifleman2_spr.setScale(-1.0f, 1.0f);
					rifleman2_spr.drawTransformed(g);
				}
			}
		}
		


		// Front: overlays
		if (win || lose) {
			Image owari = new ImageIcon("images/sub_owari.png").getImage();
			g.drawImage(fadeout.getImage(), 0, 0, null);
			g.drawImage(owari, 0, 0, null);
		}
	}



	public void update(long elapsed) {
		elapsed++;
		debug();

		if (cutscene) {
			if (title.getX() < 0) title.shiftX(25);
			if (title.getX() > 0) title.setX(0);
			return;
		}

		if (level == 1) {
			thisLevel = ballroom_map;
			leftmost = 64;
		} else if (level == 2) {
			thisLevel = train_map;
			leftmost = 256;
		}


		// Movement

		// You can't move when the game is over (primitive cutscenes) and movement works differently on level 0, the single-screen menu.
		if (!isTheGameOver() && level != 0) {
			rightmost = thisLevel.getPixelWidth() - screenWidth;

			// Reaching the extremes of the level.  Of course, the start treats you differently compared to the goal.
			if (torso_spr.getX() <= leftmost) {
				mayGoLeft = false;
			} else {
				mayGoLeft = true;
			}
			if (xOff >= rightmost) win();

			if (mayGoLeft) {
				if ((xOff < 0) || (torso_spr.getX() > THREE_BLOCKS)) { // The protag is approaching the left side of the screen; he will move, not scroll the world.
					if (goLeft && !run) {
						torso_spr.shiftX(-WALK_SPEED);
						larm_spr.shiftX(-WALK_SPEED);
						rarm_spr.shiftX(-WALK_SPEED);
						legs_spr.shiftX(-WALK_SPEED);
					}
					if (goLeft && run) {
						torso_spr.shiftX(-RUN_SPEED);
						larm_spr.shiftX(-RUN_SPEED);
						rarm_spr.shiftX(-RUN_SPEED);
						legs_spr.shiftX(-RUN_SPEED);
					}
				} else { // Ordinary world scrolling.
					if (goLeft && !run) xOff -= WALK_SPEED;
					if (goLeft && run)  xOff -= RUN_SPEED;
				}
			}

			if (mayGoRight) {
				if (torso_spr.getX() < THREE_BLOCKS) {
					if (goRight && !run) {
						torso_spr.shiftX(WALK_SPEED);
						larm_spr.shiftX(WALK_SPEED);
						rarm_spr.shiftX(WALK_SPEED);
						legs_spr.shiftX(WALK_SPEED);
					}
					if (goRight && run) {
						torso_spr.shiftX(RUN_SPEED);
						larm_spr.shiftX(RUN_SPEED);
						rarm_spr.shiftX(RUN_SPEED);
						legs_spr.shiftX(RUN_SPEED);
					}
				} else { 
					if (goRight && !run) xOff += WALK_SPEED;
					if (goRight && run)  xOff += RUN_SPEED;
				}
			}
	
			if ((jump) && (yPos % 64 == 0)) { // Not a safe way of doing things..
				jump_dx = 40;
				mayDescend = true;
			}	
		}

		
		// Movement within the level selection screen is limited.
		if (level == 0) {
			if (mayGoLeft) {
				if (goLeft && !run) {
					torso_spr.shiftX(-WALK_SPEED);
					legs_spr.shiftX(-WALK_SPEED);
					larm_spr.shiftX(-WALK_SPEED);
					rarm_spr.shiftX(-WALK_SPEED);
				}
				if (goLeft && run) {
					torso_spr.shiftX(-RUN_SPEED);
					legs_spr.shiftX(-RUN_SPEED);
					larm_spr.shiftX(-RUN_SPEED);
					rarm_spr.shiftX(-RUN_SPEED);
				}
			}
			
			if (mayGoRight) {
				if (goRight && !run) {
					torso_spr.shiftX(WALK_SPEED);
					legs_spr.shiftX(WALK_SPEED);
					larm_spr.shiftX(WALK_SPEED);
					rarm_spr.shiftX(WALK_SPEED);
				}
				if (goRight && run) {	
					torso_spr.shiftX(RUN_SPEED);
					legs_spr.shiftX(RUN_SPEED);
					larm_spr.shiftX(RUN_SPEED);
					rarm_spr.shiftX(RUN_SPEED);
				}
			}

			if (torso_spr.getX() <= 192) init_game(1);
			if (torso_spr.getX() >= (screenWidth - 192)) init_game(2);
		}


		if (mayDescend && level != 0) {
			yPos -= jump_dx;
			torso_spr.shiftY(-jump_dx);
			larm_spr.shiftY(-jump_dx);
			rarm_spr.shiftY(-jump_dx);
			legs_spr.shiftY(-jump_dx);
			jump_dx -= WEIGHT;
		} else {
			jump_dx = 0;
		}


		// When you win, the protag runs off the screen on his own.
		if (win) {
			torso_spr.shiftX(RUN_SPEED);
			larm_spr.shiftX(RUN_SPEED);
			rarm_spr.shiftX(RUN_SPEED);
			legs_spr.shiftX(RUN_SPEED);
		}

	

		// Enemy AI
		
		if (level == 1) {
			screen_spr.getGot(xOff + torso_spr.getX(), yPos, attackl, attackr);
		}


		if ((!lose) && ((level == 1) || (level == 2))) {
			// The enemy attacks when he's within two screen widths of the player, whether approaching from the left or the right.
			if ((Math.abs(torso_spr.getX() - rifleman_spr.getX())) < (screenWidth * 2)) rifleman_spr.attackChassepot(torso_spr.getX(), (float) yPos);
			if ((Math.abs(torso_spr.getX() - rifleman2_spr.getX())) < (screenWidth * 2)) rifleman2_spr.attackChassepot(torso_spr.getX(), (float) yPos);
		
			// Checks for attacks by the player: the player's position and whether the attack was from the left or the right.
			rifleman_spr.getGot(torso_spr.getX(), yPos, attackl, attackr);
			rifleman2_spr.getGot(torso_spr.getX(), yPos, attackl, attackr);

			// Checks for the protag having been attacked.
			if (rifleman_spr.didYouHitMe()) {
				rifleman_spr.acknowledgeHit();
				takeHit();
			}
			
			if (rifleman2_spr.didYouHitMe()) {
				rifleman2_spr.acknowledgeHit();
				takeHit();
			}
		}



		// Collisions
		
		// No point checking collisions in a different level!
		if (level == 1) {
			for (Sprite s: chandeliers) {
				collision(s, true);	
			}

			collision(piano_spr, false);
			collision(handrail1_spr, false);
			collision(handrail2_spr, false);
			if (!screen_spr.getStatus()) collision(screen_spr, false);
			collision(branch1_spr, false);
			collision(branch2_spr, false);

			checkTileCollision(legs_spr, ballroom_collisions);
		}

		if (level == 2) {
			checkTileCollision(legs_spr, train_collisions);
			
			if (semaphore_incident) {
				float sem_left;
				float sem_right;
		
				train_semaphore_spr.shiftX(20);
			
				sem_left = train_semaphore_spr.getX();
				sem_right = sem_left + 32;

				// This sprite needs its own collision detection because it uses a more prosaic system, "where on the screen am i"	
				if ((torso_spr.getX() > sem_left) && (torso_spr.getX() < sem_right) && (yPos < THREE_BLOCKS) && yPos > FIVE_BLOCKS) lose();
				if ((train_semaphore_spr.getX() > screenWidth + 2048) && !lose) semaphore_incident = false;
			}
		}
	
		// Update animations
		torso_spr.getAnimation().update(elapsed);
		legs_spr.getAnimation().update(elapsed);
		
		if ((level == 1) || (level == 2)) {
			if (!rifleman_spr.getStatus()) rifleman_spr.update(elapsed);
			if (!rifleman2_spr.getStatus()) rifleman2_spr.update(elapsed);
		}

		if (level == 2) train_steam_anim.update(elapsed);
		if (win || lose) fadeouta.update(elapsed);
	}



	public boolean collision(Sprite spr, boolean hasBottom) {

		float left = spr.getX() - (torso_spr.getX() + BLOCK); // getX() matches xOff when the sprite is on the left edge of the screen.  torso, etc. brings it to the player's location.
		float right = left + spr.getImage().getWidth(null) + BLOCK; // the extra block is the protag's breadth; checks his left side, not his right.
		float top = spr.getY();
		float bottom = top + spr.getImage().getHeight(null) + (BLOCK * 2);

		boolean inX;
		boolean inY;
		boolean collided = false;

		if ((xOff > left) && (xOff < right)) {
			inX = true;
		} else {
			inX = false;
		}

		if ((yPos > top) && (yPos < bottom)) {
			inY = true;
		} else {
			inY = false;
		}
		
		// If the object is completely missed, freedom and a hasty return
		if (xOff < left    || !inY) mayGoRight = true;
		if (xOff > right   || !inY) mayGoLeft = true;
		if (yPos < top    || !inX) mayDescend = true;
		if ((yPos > bottom || !inX) || !hasBottom) mayAscend = true;
		if (!inX && !inY) return false;

		if ((inX) && (yPos > bottom - 48) && (yPos <= bottom) && hasBottom) { // bumping the bottom with your head a la a ? block
			collided = true;

			float distL = (float) xOff - left;
			float distR = right - (float) xOff;

			// Which edge of the rectangle is closest to the player?
			// Move the player to whichever edge, and disable colliding further.
			if ((distL > 32) && (distR > 32)) {
				yPos = bottom + BLOCK + 1;
				torso_spr.setY(bottom - BLOCK + 1);
				larm_spr.setY(bottom - BLOCK + 1);
				rarm_spr.setY(bottom - BLOCK + 1);
				jump = false;
				jump_dx = 0;
				mayAscend = false;
			} else if (distL < distR) {
				if (xOff >= 0) xOff = (int) left;
				mayGoRight = false;
			} else if (distR < distL) {
				xOff = (int) right;
				mayGoLeft = false;
			}
		} else if ((inX) && (yPos == top)) { // landing on top or walking onto the top
			yPos = top;
			jump = false;
			torso_spr.setY(top - (BLOCK * 2));
			larm_spr.setY(top - (BLOCK * 2));
			rarm_spr.setY(top - (BLOCK * 2));
			jump_dx = 0;
			mayDescend = false;
		} else if ((inX) && (inY)) { // simply ending up inside somehow
			collided = true;

			//jump_dx = 0;
			float distL = (float) xOff - left;
			float distR = right - (float) xOff;
			float distT = yPos - top;
			float distB = bottom - yPos;

			// Which edge of the rectangle is closest to the player?
			// Move the player to whichever edge, and disable colliding further.
			if ((distL < distR) && (distL < distT) && (distL < distB)) {  // towards the left
				if (xOff >= 0) xOff = (int) left;
				mayGoRight = false;
			} else if ((distR < distL) && (distR < distT) && (distR < distB)) { // towards the right
				xOff = (int) right;
				mayGoLeft = false;
			} else if ((distT < distL) && (distT < distR) && (distT < distB)) { // towards the top
				yPos = top;
				jump = false;
				jump_dx = 0;
				mayDescend = false;

				Sound land = new Sound("sound/land.wav");
				land.start();
			} else if ((distB < distL) && (distB < distR) && (distB < distT) && hasBottom) { // towards the bottom
				yPos = bottom;
				jump = false;
				jump_dx = 0;
				mayAscend = false;
			}
		}

		if ((xOff <= left + 10) || (xOff >= right - 10)) { // allows for sliding down the edge of the sprite
			mayDescend = true;
		}

		return collided;
	}


	/**
	 *  Checks tile collisions, corner by corner, and responds to them.
	 *  @param Sprite s.  Designed to suit both the player character and enemies.
	 *  @param TileMap tmap.  The special version of the level with collision data.
	 */
	public void checkTileCollision(Sprite s, TileMap tmap) {
       	 	// Find out how wide and how tall a tile is
		int tileWidth = tmap.getTileWidth();
       		int tileHeight = tmap.getTileHeight(); 

		// Edges of the body
		int left = (xOff + (int) s.getX());
		int bottom = (int) yPos;

		int left_tile = (left / tileWidth);
		int right_tile = left_tile + 1;
		int bottom_tile = (bottom / tileHeight);
		int top_tile = bottom_tile - 2;

		char tl = tmap.getTileChar(left_tile, top_tile);
		char tr = tmap.getTileChar(right_tile, top_tile);
		char bl = tmap.getTileChar(right_tile, bottom_tile);
		char br = tmap.getTileChar(right_tile, bottom_tile);
        
		if ((tl == '.') && (tr == '.') && (bl == '.') && (br == '.')) {
			mayGoRight = true;
			mayGoLeft = true;
			mayAscend = true;
			mayDescend = true;
			return;
		}

		if ((tl == '?') || (tr == '?') || (bl == '?') || (br == '?')) { // don't want us to get stuck above the screen
			mayDescend = true;
			return;
		}
		
		if ((tl == 's') || (tr == 's') || (bl == 's') || (br == 's')) { // spawn a girder.  Won't prevent movement
			if (!semaphore_incident) semaphoreIncident();
			tmap.setTileChar('.', top_tile, bottom_tile);
		}
		
		if (((tr == 'y') && (br == 'y')) || ((tr == 'Y') || (br == 'Y'))) { // at the left
			if (goRight) {
				if (run) xOff -= RUN_SPEED;
				if (!run) xOff -= WALK_SPEED;
			}
			mayGoRight = false;
			mayDescend = true;
		} else mayGoRight = true;

		if (((tl == 'y') && (bl == 'y')) || ((tl == 'Y') || (bl == 'Y'))) { // at the right
			if (goLeft) {
				if (run) xOff += RUN_SPEED;
				if (!run) xOff += WALK_SPEED;
			}
			mayGoLeft = false;
			mayDescend = true;
		} else mayGoLeft = true;

		if (((tl == 'y') && (tr == 'y')) || ((tl == 'Y') || (tr == 'Y'))) { // at the bottom
			yPos = (top_tile + 3) * 64;
			jump = false;
			jump_dx = 0;
			mayAscend = false;
		} else mayAscend = true;


		if (((bl == 'y') && (br == 'y')) || ((bl == 'Y') || (br == 'Y'))) { // at the top
			yPos = bottom_tile * 64;
			mayDescend = false;
			jump = false;
		} else mayDescend = true;
   	 }



	/**
	 * A little extra scene of the protag getting bonked with a girder
	 */
	private void semaphoreIncident() {
		train_semaphore_spr.setX(-32);
		semaphore_incident = true;
	}



	public void keyPressed(KeyEvent e) { 

		if (e.getKeyCode() == KeyEvent.VK_ENTER) init_game(0);

		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = true;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = true;
		if (e.isShiftDown()) run = true;
		if (e.getKeyCode() == KeyEvent.VK_SPACE) jump = true;
	
		// Combat
		if ((e.getKeyCode() == KeyEvent.VK_H) && (!attackl) && (!attackr)) attackl = true;
		if ((e.getKeyCode() == KeyEvent.VK_L) && (!attackl) && (!attackr)) attackr = true;
	
		// Debug
		if (e.getKeyCode() == KeyEvent.VK_R) {
			init_game(level);
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (level == 0) {
				System.out.println("Thanks for playing!");
				stop();
			} else {
				init_game(0);
			}
		}
	}



	public void keyReleased(KeyEvent e) {
		// Movement
		if (e.getKeyCode() == KeyEvent.VK_A) goLeft = false;
		if (e.getKeyCode() == KeyEvent.VK_D) goRight = false;
		if (!e.isShiftDown()) run = false;
		
		// Combat
		if (e.getKeyCode() == KeyEvent.VK_H) attackl = false;
		if (e.getKeyCode() == KeyEvent.VK_L) attackr = false;
	}



	public void takeHit() {
		hp--;

		Sound shot = new Sound("sound/shot.wav");
		shot.start();

		if (hp == 3) torso_spr.setAnimation(hitonce_anim);
		if (hp == 2) torso_spr.setAnimation(hittwice_anim);
		if (hp == 1) torso_spr.setAnimation(hitthrice_anim);

		if (hp < 1) {
			lose();
		}
	}


	public void win() {
		win = true;
	
		fadeout = new Sprite(fadeouta);
		fadeouta.start();
		fadeouta.setLoop(false);

		mayGoLeft = false;
		mayGoRight = false;
		mayDescend = true;
		rifleman_spr.kill();
		rifleman2_spr.kill();
	}

	public void lose() {
		lose = true;

		Sound shot = new Sound("sound/shot.wav");
		shot.start();
		
		fadeout = new Sprite(fadeouta);
		fadeouta.start();
		fadeouta.setLoop(false);
		
		mayGoRight = false;
		mayGoLeft = false;
		mayAscend = false;
		mayDescend = true;

		torso_spr.hide();
		larm_spr.hide();
		rarm_spr.hide();
	}

	public boolean isTheGameOver() {
		if (win || lose) {
			return true;
		} else {
			return false;
		}
	}
}
