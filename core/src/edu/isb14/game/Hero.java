package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

// Класс игрока, объкты класса будут названы как Player 1, Player2
public class Hero {
    private Texture texture;
//    State state = new State();
    BulletEmitter bulletEmitter = new BulletEmitter("bullet20.png", 20.0f); // 1 - текстура, 2 - скорость пули
    private int hp = 100;
    private int score = 0;
    private int attack = 1;
    private float speed = 10.0f;
    private int fireRate = 5;       //
    private int fireCounter = 0;    // how many time press SPACE
    private Vector2 position;
    private Player player;
    public enum Player { P1, P2 }

    // control
    private int left, right, up, down, fire;

    public Hero(Player player){

        this.player = player;

        position = new Vector2(0,(float)SunsGame.CONFIG_HEIGHT / 2);

//        state.setHp(100);
//        state.setAttack(20);
//        state.setPosition(0,(float)SunsGame.CONFIG_HEIGHT / 2);
//        state.setSpeed(10.0f);
//        state.setFireRate(5);

        if ( player == Hero.Player.P1 ) {
            texture = new Texture(Gdx.files.internal("ship1_80x52.png"));
            left = Input.Keys.A;
            right = Input.Keys.D;
            up = Input.Keys.W;
            down = Input.Keys.S;
            fire = Input.Keys.SPACE;
        } else {
            texture = new Texture(Gdx.files.internal("ship2_60x60.png"));
            left = Input.Keys.LEFT;
            right = Input.Keys.RIGHT;
            up = Input.Keys.UP;
            down = Input.Keys.DOWN;
            fire = Input.Keys.NUMPAD_3;
        }
    }

    public void render(SpriteBatch batch){
        update();
//        batch.draw(texture, state.getPosition().x, state.getPosition().y);
        batch.draw(texture, position.x, position.y);
        bulletEmitter.renderLinerShot(batch);
    }

    public void update(){

// логика стельбы
        if (Gdx.input.isKeyPressed(fire)){                        // при нажатии пробел
            fireCounter++;                                                  // мы увеличиваем какой-то счетчик
            if (fireCounter > fireRate){                                     // если этот счётчик стал больше чем
                fireCounter = 0;                                            // счётчик сбрасываем
                for (int i = 0; i < bulletEmitter.bullets.length; i++) {        // начинаем ходить по массиву пуль
                    if(!bulletEmitter.bullets[i].isActive()){                    // как только находим в этом массиве не активную пулю,
                        bulletEmitter.bullets[i].setup(position.x + 55,position.y + 16);  // мы её создаём
                        break;                                              // и перестаем искать ещё какие то пули
                    }
                }
            }
        }
// CONTROL


        if (Gdx.input.isKeyPressed(up) && !Gdx.input.isKeyPressed(down)){
            position.y += speed;
//            rect.y = position.y;
            // ограничение ВЕРХ
            if (position.y > SunsGame.CONFIG_HEIGHT - texture.getHeight()){
                position.y = SunsGame.CONFIG_HEIGHT - texture.getHeight();
            }
        }
        if (Gdx.input.isKeyPressed(down) && !Gdx.input.isKeyPressed(up)){
            position.y -= speed;
//            rect.y = position.y;
            // ограничение НИЗ
            if (position.y < 25){   // 25 - высота статус бара
                position.y = 25;
            }
        }
        if (Gdx.input.isKeyPressed(right) && !Gdx.input.isKeyPressed(left)){
            position.x += speed;
//            rect.x = position.x;
            // ограничение ПРАВО
            if (position.x > SunsGame.CONFIG_WIDTH - texture.getWidth()){
                position.x = SunsGame.CONFIG_WIDTH - texture.getWidth();
            }
        }
        if (Gdx.input.isKeyPressed(left) && !Gdx.input.isKeyPressed(right)){
            position.x -= speed;
//            rect.x = position.x;
            // ограничение ЛЕВО
            if (position.x < 0){
                position.x = 0;
            }
        }

//        for (int i = 0; i < Asteroid.AST_COUNT; i++) {
//            if (MyGdxGame.asteroids[i].getRect().contains(rect)){
//                MyGdxGame.asteroids[i].recreate();
//            }
//
//        }

//        rect.x = position.x;
//        rect.y = position.y;

    }
}






