package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

// Класс игрока, объкты класса будут названы как Player 1, Player2
public class Hero {
    private Texture texture;
//    State state = new State();
    BulletEmitter bulletEmitter = new BulletEmitter("bullet20.png", 20.0f); // 1 - текстура, 2 - скорость пули
    private int hp = 100;
    private int score = 0;
    private int attack = 2;
    private float speed = 10.0f;
    private int fireRate = 5;       //
    private int fireCounter = 0;    // how many time press SPACE
    private Vector2 position;
    protected Rectangle hitBox;
    private Player player;
    public enum Player { P1, P2 }

    boolean flyRight = false;

    AnimationGame flyBigFire;
    AnimationGame flyFire;
    AnimationGame flySmoke;

    // control
    private int left, right, up, down, fire;

    public Hero(Player player){

        this.player = player;

        position = new Vector2(30,(float) SunsGame.CONFIG_HEIGHT / 2);

        flyBigFire = new AnimationGame("fire_large2.png",1,2,0.2f,true);
        flyFire = new AnimationGame("fire_normal.png",1,2,0.2f,true);
        flySmoke = new AnimationGame("smoke_sp.png",1,2,0.2f,true);

    // УПРАВЛЕНИЕ
        if ( player == Hero.Player.P1 ) {
            texture = new Texture(Gdx.files.internal("bimini.png"));
            left = Input.Keys.A;
            right = Input.Keys.D;
            up = Input.Keys.W;
            down = Input.Keys.S;
            fire = Input.Keys.SPACE;
        } else {
            texture = new Texture(Gdx.files.internal("bi2mini.png"));
            left = Input.Keys.LEFT;
            right = Input.Keys.RIGHT;
            up = Input.Keys.UP;
            down = Input.Keys.DOWN;
            fire = Input.Keys.NUMPAD_3;
        }
        
        this.hitBox = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
    }

    public void render(SpriteBatch batch){
        update();
        flyFire.render(batch);
        if (flyRight){

//            flySmoke.render(batch);
            flyBigFire.render(batch);
        }

        batch.draw(texture, position.x, position.y);
        bulletEmitter.renderLinerShot(batch);

    }

    public void update(){
        flyRight = false;
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
            hitBox.y = position.y;
//            rect.y = position.y;
            // ограничение ВЕРХ
            if (position.y > SunsGame.CONFIG_HEIGHT - texture.getHeight()){
                position.y = SunsGame.CONFIG_HEIGHT - texture.getHeight();
            }
        }
        if (Gdx.input.isKeyPressed(down) && !Gdx.input.isKeyPressed(up)){
            position.y -= speed;
            hitBox.y = position.y;

            // ограничение НИЗ
            if (position.y < 25){   // 25 - высота статус бара
                position.y = 25;
            }
        }
        if (Gdx.input.isKeyPressed(right) && !Gdx.input.isKeyPressed(left)){
            position.x += speed;
            hitBox.x = position.x;
            flyRight = true;
//            rect.x = position.x;
            // ограничение ПРАВО
            if (position.x > SunsGame.CONFIG_WIDTH - texture.getWidth()){
                position.x = SunsGame.CONFIG_WIDTH - texture.getWidth();
            }
        }
        if (Gdx.input.isKeyPressed(left) && !Gdx.input.isKeyPressed(right)){
            position.x -= speed;
            hitBox.x = position.x;
//            rect.x = position.x;
            // ограничение ЛЕВО
            if (position.x < 0){
                position.x = 0;
            }
        }

        flyBigFire.setPosition(position.x - 30, position.y + 14);
        flyFire.setPosition(position.x - 16, position.y + 17);
        flySmoke.setPosition(position.x - 100, position.y -11);
    }

    public int getAttack(){
        return attack;
    }

    public int getHp(){
        return hp;
    }

    public int getScore(){
        return score;
    }

    public void setPosition(float x, float y){
        position.set(x,y);
    }
    public Rectangle getHitBox(){
        return this.hitBox;
    }
    
    public void getDamage(int dmg){
        this.hp -= dmg;
        if (this.hp <=0)
            this.destroy();
    }
    
    public void destroy(){
        this.hitBox.set(-texture.getWidth(), -texture.getHeight(), 0, 0);
        this.position.set(-texture.getWidth(), -texture.getHeight());
        left = 0;
        right = 0;
        up = 0;
        down = 0;
        fire = 0;
    }
}






