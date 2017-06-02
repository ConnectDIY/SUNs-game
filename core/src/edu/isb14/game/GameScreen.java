package edu.isb14.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ApplicationAdapter implements Screen{

	final SunsGame game;

	OrthographicCamera camera1;
	private Background background;
	// Настройка отображения под разными экранами
	private Viewport viewport;
	private Camera camera;
	// Противники
        private MediumEnemy badGuy;
        private LightEnemy[] lightEn;
        private final int LIGHT_ENEMY_COUNTS = 5;
        private HeavyEnemy heavyEn;
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
        lightEn = new LightEnemy[LIGHT_ENEMY_COUNTS];
        for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
            lightEn[i] = new LightEnemy("ship2_60x60.png");
        }

//                heavyEn = new HeavyEnemy("enemy.png");

		viewport = new FitViewport(SunsGame.CONFIG_WIDTH, SunsGame.CONFIG_HEIGHT, camera);	// отображение экрана с чёрными линииями по краям и сохранение пропорций

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

		player1.render(game.batch);		// Отрисовка игрока

		if (!onePlayers){
			player2.render(game.batch);		// Отрисовка игрока}
		}

//		if (badGuy.isActive()){
//			badGuy.render(game.batch);
//			badGuy.bulletRender(game.batch);
//		}

        lvlTiming(); // метод в котором описаны тайминги уровня

        for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
            if (lightEn[i].isActive()){
                lightEn[i].render(game.batch);
            }
        }

        background.renderStatusBar(game.batch, onePlayers);
		game.batch.end();
	}



	private void update(){
		Gdx.gl.glClearColor(0, 0, 0, 1);	// Цвет фона
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	// Очищает экран при каждом кадре.

            if (badGuy.isActive())
                badGuy.update();
        for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
            if (lightEn[i].isActive())
                lightEn[i].update();
        }



//            if (heavyEn.isActive())
//                heavyEn.update();
        // ------------------------------------------------------------------------------------------------------------
        // Столкновения пуль первого игрока и врагов
            for(int i = 0; i < player1.bulletEmitter.getBulletsCount(); i++){
                if (player1.bulletEmitter.bullets[i].isActive()){
                    if(badGuy.getHitBox().contains(player1.bulletEmitter.bullets[i].getPosition()) ){
                        badGuy.getDamage(player1.getAttack());
                        player1.bulletEmitter.bullets[i].destroy();
                    }
                    for (int j = 0; j < LIGHT_ENEMY_COUNTS; j++) {
                        if(lightEn[j].getHitBox().contains(player1.bulletEmitter.bullets[i].getPosition())){
                            lightEn[j].getDamage(player1.getAttack()); //надо бы получить доступ к полю урона игрока
                            player1.bulletEmitter.bullets[i].destroy();
                        }
                    }


//                    if(heavyEn.getHitBox().contains(player1.bulletEmitter.bullets[i].getPosition()) ){
//                        heavyEn.getDamage(player1.getAttack()); //надо бы получить доступ к полю урона игрока
//                        player1.bulletEmitter.bullets[i].destroy();
//                    }
                }

                // Столкновения пуль второго игрока и врагов
                if(!onePlayers)
                if (player2.bulletEmitter.bullets[i].isActive()){
                    if(badGuy.getHitBox().contains(player2.bulletEmitter.bullets[i].getPosition()) ){
                        badGuy.getDamage(player2.getAttack()); //надо бы получить доступ к полю урона игрока
                        player2.bulletEmitter.bullets[i].destroy();
                    }
                    for (int j = 0; j < LIGHT_ENEMY_COUNTS; j++) {
                        if(lightEn[j].getHitBox().contains(player2.bulletEmitter.bullets[i].getPosition())){
                            lightEn[j].getDamage(player2.getAttack()); //надо бы получить доступ к полю урона игрока
                            player2.bulletEmitter.bullets[i].destroy();
                        }
                    }

//
//                    if(heavyEn.getHitBox().contains(player2.bulletEmitter.bullets[i].getPosition()) ){
//                        heavyEn.getDamage(player2.getAttack()); //надо бы получить доступ к полю урона игрока
//                        player2.bulletEmitter.bullets[i].destroy();
//                    }
                }
            }
        // ------------------------------------------------------------------------------------------------------------
        // Столкновения пуль ВРАГОВ и игрока1
            for(int i = 0; i < badGuy.bulletEmitter.getBulletsCount(); i++){
                if (badGuy.bulletEmitter.bullets[i].isActive()){
                    if(player1.getHitBox().contains(badGuy.bulletEmitter.bullets[i].getPosition())){
                        player1.getDamage(badGuy.attack);
                        badGuy.bulletEmitter.bullets[i].destroy();
                    }
                }
                for (int j = 0; j < LIGHT_ENEMY_COUNTS; j++) {
                    if ( lightEn[j].isActive() && lightEn[j].bulletEmitter.bullets[i].isActive()){
                        if(player1.getHitBox().contains(lightEn[j].bulletEmitter.bullets[i].getPosition())){
                            player1.getDamage(lightEn[j].attack);
                            lightEn[j].bulletEmitter.bullets[i].destroy();
                        }
                    }
                }

                
//                if (heavyEn.bulletEmitter.bullets[i].isActive()){
//                    if(player1.getHitBox().contains(heavyEn.bulletEmitter.bullets[i].getPosition())){
//                        player1.getDamage(heavyEn.attack);
//                        heavyEn.bulletEmitter.bullets[i].destroy();
//                    }
//                }
                // Столкновения пуль ВРАГОВ и игрока2
                if(!onePlayers){
                if (badGuy.bulletEmitter.bullets[i].isActive()){
                    if(player2.getHitBox().contains(badGuy.bulletEmitter.bullets[i].getPosition())){
                        player2.getDamage(badGuy.attack);
                        badGuy.bulletEmitter.bullets[i].destroy();
                    }
                }
                    for (int j = 0; j < LIGHT_ENEMY_COUNTS; j++) {
                        if (lightEn[j].bulletEmitter.bullets[i].isActive()){
                            if(player2.getHitBox().contains(lightEn[j].bulletEmitter.bullets[i].getPosition())){
                                player2.getDamage(lightEn[j].attack);
                                lightEn[j].bulletEmitter.bullets[i].destroy();
                            }
                        }
                    }

                
//                if (heavyEn.bulletEmitter.bullets[i].isActive()){
//                    if(player2.getHitBox().contains(heavyEn.bulletEmitter.bullets[i].getPosition())){
//                        player2.getDamage(heavyEn.attack);
//                        heavyEn.bulletEmitter.bullets[i].destroy();
//                        }
//                    }
                }
            }
        // ------------------------------------------------------------------------------------------------------------
        // Столкновения врагов и игроков
            if ( badGuy.getHitBox().overlaps(player1.getHitBox())){
                player1.getDamage(badGuy.hp);
                badGuy.getDamage(player1.getHp());
            }
        for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
            if ( lightEn[i].isActive() && lightEn[i].getHitBox().overlaps( player1.getHitBox() )){      //!!!!!!!!!!!!!!1
                player1.getDamage(lightEn[i].hp);
                lightEn[i].getDamage(player1.getHp());
            }
        }

            
//            if ( heavyEn.getHitBox().contains(player1.getHitBox())){
//                player1.getDamage(heavyEn.hp);
//                heavyEn.getDamage(player1.getHp());
//            }
            
            if(!onePlayers){
                if ( badGuy.getHitBox().contains(player2.getHitBox())){
                    player2.getDamage(badGuy.hp);
                    badGuy.getDamage(player2.getHp());
                }
                for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
                    if ( player2.getHitBox().contains(lightEn[i].position)){
                        player2.getDamage(lightEn[i].hp);
                        lightEn[i].getDamage(player2.getHp());
                    }
                }

            
//                if ( heavyEn.getHitBox().contains(player2.getHitBox())){
//                    player2.getDamage(heavyEn.hp);
//                    heavyEn.getDamage(player2.getHp());
//                }
            }
        // ------------------------------------------------------------------------------------------------------------
//        // Dead enemy
//        for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
//            if (lightEn[i].getHp() <= 0){
//                lightEn[i].dead();
//            }
//        }

	}

	public Hero getPlayer1(){
        return player1;
    }

    public Hero getPlayer2(){
        return player2;
    }

    private void lvlTiming(){
        if (SunsGame.minute == 0 && SunsGame.sec == 1){
            for (int i = 0; i < LIGHT_ENEMY_COUNTS; i++) {
                lightEn[i].setActive(true);
            }

        }

    }

	@Override
	public void dispose () {
		game.timeDispose();
		pauseMenu.dispose();
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
}

