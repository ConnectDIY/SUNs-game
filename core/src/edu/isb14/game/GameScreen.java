package edu.isb14.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ApplicationAdapter implements Screen{

	final SunsGame game;

	OrthographicCamera camera1;
	private Background background;
	// Настройка отображения под разными экранами
	private Viewport viewport;
	private Camera camera;
	// Анимация человечка
//	private AnimationGame walkAnimation;
	// Противники
        private MediumEnemy badGuy;
        private LightEnemy lEn;
        private HeavyEnemy heavy;
	// Игроки
	private Hero player1;
	private Hero player2;
	boolean onePlayers;

	private BitmapFont pauseMenu;
	private String pauseMenuItems[];
	private int currentPauseItem;

	public GameScreen (final SunsGame gam, boolean amountPlayer) {		// используем конструткор вместо метода create при работе с экранами
		this.game = gam;

		camera = new OrthographicCamera();
		background = new Background("bckgrnd.png", this.game);
                badGuy = new MediumEnemy("enemy_ufo_black.png");
                lEn = new LightEnemy("ship2_60x60.png");
                heavy = new HeavyEnemy("enemy.png");

		//camera = new PerspectiveCamera();

		viewport = new FitViewport(SunsGame.CONFIG_WIDTH, SunsGame.CONFIG_HEIGHT, camera);	// отображение экрана с чёрными линииями по краям и сохранение пропорций

//		walkAnimation = new AnimationGame("sprite-animation4.png", 5, 6, 0.03f, true);

		onePlayers = amountPlayer;
		player1 = new Hero(Hero.Player.P1);
		if (!onePlayers){
			player2 = new Hero(Hero.Player.P2);

			player1.setPosition(30, SunsGame.CONFIG_HEIGHT/2 + 100);
			player2.setPosition(30, SunsGame.CONFIG_HEIGHT/2 - 100);
		}

		pauseMenu = new BitmapFont();
		pauseMenuItems = new String[]{
				"Continue",
				"Back to menu",
				"Quit"
		};
		currentPauseItem = 0;
	}

	@Override
	public void render (float delta) {

		switch(game.state){
			case Running:
				draw();
				if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
					game.state = SunsGame.State.Paused;
				}
				break;
			case Paused:
				//don't update
				pause();
				break;
		}

	}

	public void pause(){
		game.batch.begin();
//		Gdx.gl.glClearColor(100, 0, 0, 0.7f);	// Цвет фона
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	// Очищает экран при каждом кадре.

		// draw menu
		for(int i = 0; i < pauseMenuItems.length; i++) {
			if(currentPauseItem == i) pauseMenu.setColor(Color.RED);
			else pauseMenu.setColor(Color.WHITE);
			pauseMenu.draw(
					game.batch,
					pauseMenuItems[i],
					(SunsGame.CONFIG_WIDTH)/2,
					400 - 35 * i
			);
		}

		game.batch.end();

		pauseHandleInput();
	}

	public void pauseHandleInput() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			if (currentPauseItem > 0){
				currentPauseItem--;
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			if (currentPauseItem < pauseMenuItems.length -1){
				currentPauseItem++;
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			// Continue
			if (currentPauseItem == 0) {
				game.state = SunsGame.State.Running;
			}
			// back to menu
			if (currentPauseItem == 1) {
				game.setScreen(new MainMenuScreen(game));
				game.state = SunsGame.State.Running;
				dispose();
			}
			// quit
			if (currentPauseItem == 2) {
				Gdx.app.exit();
				dispose();
			}

		}

	}

	public void draw(){
		update();
		game.batch.begin();

		game.updateTime(Gdx.graphics.getDeltaTime());

		background.render(game.batch);	// Отрисовка фона
//		walkAnimation.render(game.batch);	// Отрисовка бегущего человечка

		player1.render(game.batch);		// Отрисовка игрока
		if (!onePlayers){
			player2.render(game.batch);		// Отрисовка игрока}
		}

		if (badGuy.isActive()){
			badGuy.render(game.batch);
			badGuy.bulletRender(game.batch);
		}
                
                if (lEn.isActive()){
                    lEn.render(game.batch);
                    lEn.bulletRender(game.batch);
                }

                if (heavy.isActive()){
                    heavy.render(game.batch);
                    heavy.bulletRender(game.batch);
                }

        background.renderStatusBar(game.batch, onePlayers);
		game.batch.end();
	}

	@Override
	public void show() {

	}

	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void hide() {

	}

	private void update(){
            boolean mFl = false;
            boolean hFl = false;
		Gdx.gl.glClearColor(0, 0, 0, 1);	// Цвет фона
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	// Очищает экран при каждом кадре.
                
            if (player1.getScore() > 25 && !mFl){
                badGuy.setActive();
                mFl = true;
            }
            
            if (player1.getScore() > 50 && !hFl){
                heavy.setActive();
                hFl = true;
            }

            if (badGuy.isActive())
                badGuy.update();
            
            if (lEn.isActive())
                lEn.update();

            if (heavy.isActive())
                heavy.update();

            for(int i = 0; i < player1.bulletEmitter.getBulletsCount(); i++){
                if (player1.bulletEmitter.bullets[i].isActive()){
                    if(badGuy.getHitBox().contains(player1.bulletEmitter.bullets[i].getPosition()) ){
                        badGuy.getDamage(player1.getAttack()); //надо бы получить доступ к полю урона игрока
                        player1.bulletEmitter.bullets[i].destroy();
                        if (!badGuy.isActive())
                            player1.addScore(badGuy.getReward());
                    }
                
                    if(lEn.getHitBox().contains(player1.bulletEmitter.bullets[i].getPosition())){
                        lEn.getDamage(player1.getAttack()); //надо бы получить доступ к полю урона игрока
                        player1.bulletEmitter.bullets[i].destroy();
                        if (!lEn.isActive())
                            player1.addScore(lEn.getReward());
                    }

                    if(heavy.getHitBox().contains(player1.bulletEmitter.bullets[i].getPosition()) ){
                        heavy.getDamage(player1.getAttack()); //надо бы получить доступ к полю урона игрока
                        player1.bulletEmitter.bullets[i].destroy();
                        if (!heavy.isActive())
                            player1.addScore(heavy.getReward());
                    }
                }
                
                if(!onePlayers)
                if (player2.bulletEmitter.bullets[i].isActive()){
                    if(badGuy.getHitBox().contains(player2.bulletEmitter.bullets[i].getPosition()) ){
                        badGuy.getDamage(player2.getAttack()); //надо бы получить доступ к полю урона игрока
                        player2.bulletEmitter.bullets[i].destroy();
                        if (!badGuy.isActive())
                            player2.addScore(badGuy.getReward());
                    }
                
                    if(lEn.getHitBox().contains(player2.bulletEmitter.bullets[i].getPosition())){
                        lEn.getDamage(player2.getAttack()); //надо бы получить доступ к полю урона игрока
                        player2.bulletEmitter.bullets[i].destroy();
                        if (!lEn.isActive())
                            player2.addScore(lEn.getReward());
                    }

                    if(heavy.getHitBox().contains(player2.bulletEmitter.bullets[i].getPosition()) ){
                        heavy.getDamage(player2.getAttack()); //надо бы получить доступ к полю урона игрока
                        player2.bulletEmitter.bullets[i].destroy();
                        if (!heavy.isActive())
                            player2.addScore(heavy.getReward());
                    }
                }
            }
            
            for(int i = 0; i < badGuy.bulletEmitter.getBulletsCount(); i++){
                if (badGuy.bulletEmitter.bullets[i].isActive()){
                    if(player1.getHitBox().contains(badGuy.bulletEmitter.bullets[i].getPosition())){
                        player1.getDamage(badGuy.attack);
                        badGuy.bulletEmitter.bullets[i].destroy();
                        player1.addScore(badGuy.getReward());
                    }
                }
                
                if (lEn.bulletEmitter.bullets[i].isActive()){
                    if(player1.getHitBox().contains(lEn.bulletEmitter.bullets[i].getPosition())){
                        player1.getDamage(lEn.attack);
                        lEn.bulletEmitter.bullets[i].destroy();
                        player1.addScore(lEn.getReward());
                    }
                }
                
                if (heavy.bulletEmitter.bullets[i].isActive()){
                    if(player1.getHitBox().contains(heavy.bulletEmitter.bullets[i].getPosition())){
                        player1.getDamage(heavy.attack);
                        heavy.bulletEmitter.bullets[i].destroy();
                        player1.addScore(heavy.getReward());
                    }
                }
                
                if(!onePlayers){
                if (badGuy.bulletEmitter.bullets[i].isActive()){
                    if(player2.getHitBox().contains(badGuy.bulletEmitter.bullets[i].getPosition())){
                        player2.getDamage(badGuy.attack);
                        badGuy.bulletEmitter.bullets[i].destroy();
                        player2.addScore(badGuy.getReward());
                    }
                }
                
                if (lEn.bulletEmitter.bullets[i].isActive()){
                    if(player2.getHitBox().contains(lEn.bulletEmitter.bullets[i].getPosition())){
                        player2.getDamage(lEn.attack);
                        lEn.bulletEmitter.bullets[i].destroy();
                        player2.addScore(lEn.getReward());
                    }
                }
                
                if (heavy.bulletEmitter.bullets[i].isActive()){
                    if(player2.getHitBox().contains(heavy.bulletEmitter.bullets[i].getPosition())){
                        player2.getDamage(heavy.attack);
                        heavy.bulletEmitter.bullets[i].destroy();
                        player2.addScore(heavy.getReward());
                        }
                    }
                }
            }
            
            if ( badGuy.getHitBox().contains(player1.getHitBox())){
                player1.getDamage(badGuy.hp);
                badGuy.getDamage(player1.getHp());
            }
            
            if (player1.getHitBox().contains(lEn.position)){
                player1.getDamage(lEn.hp);
                lEn.getDamage(player1.getHp());
            }
            
            if ( heavy.getHitBox().contains(player1.getHitBox())){
                player1.getDamage(heavy.hp);
                heavy.getDamage(player1.getHp());
            }
            
            if(!onePlayers){
                if ( badGuy.getHitBox().contains(player2.getHitBox())){
                    player2.getDamage(badGuy.hp);
                    badGuy.getDamage(player2.getHp());
                }
            
                if ( player2.getHitBox().contains(lEn.position)){
                    player2.getDamage(lEn.hp);
                    lEn.getDamage(player2.getHp());
                }
            
                if ( heavy.getHitBox().contains(player2.getHitBox())){
                    player2.getDamage(heavy.hp);
                    heavy.getDamage(player2.getHp());
                }
            }
	}

	public Hero getPlayer1(){
        return player1;
    }

    public Hero getPlayer2(){
        return player2;
    }

	@Override
	public void dispose () {
		game.timeDispose();
		pauseMenu.dispose();
	}
}
