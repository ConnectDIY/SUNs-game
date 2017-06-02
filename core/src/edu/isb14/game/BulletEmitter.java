package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

// Класс описывающий общую работу пуль.
public class BulletEmitter {

    public class Bullet {
        private Vector2 position;
        private float speed;
        private boolean active;     // Активность пули. Летит она или нет (в запасе, в массиве с пулями)
        private boolean forwardShot;    // True - стрельба вправо, False - стрельба влево

        public Bullet(float speed, boolean forwardShot){
            this.forwardShot = forwardShot;
            position = new Vector2(0, 0);
            this.speed = speed;
            active = false;
        }

        public Bullet(float speed){
            position = new Vector2(0, 0);
            this.speed = speed;
            active = false;
            forwardShot = true;
        }

        public boolean isActive(){
            return active;
        }

        // Создаёт пулю в заданной позиции
        public void setup(float x, float y){
            position.x = x;
            position.y = y;
            active = true;
        }

        public Vector2 getPosition(){
            return position;
        }

        public void destroy(){
            active = false;
        }

        // Линейная трельба - пули летят по прямой
        public void linerShot(){
            if (forwardShot){
                position.x += speed;
                //          Обработка, когда пуля за экраном
                // !!!!!!!!!!!!!!!!!!!! НА БУДУЩЕЕ - снять хитбокс пули при достижении края экрана. Ширина минус ширина текстурки
                if (position.x > SunsGame.CONFIG_WIDTH ){
                    destroy();
                }
            } else {
                position.x -= speed;
                // Обработка, когда пуля за экраном
                if (position.x < -texBullet.getWidth()) {
                    destroy();
                }
            }
        }
    }

    private Texture texBullet;
    private int bulletsCount = 50; // Кол-во пуль, такое чтобы массив пуль никогда не был полностью заполнен
    public Bullet[] bullets;
    ShapeRenderer shapeRenderer;
    public BulletEmitter(String strTex, float bulletSpeed){

        if (texBullet == null){
            texBullet = new Texture(Gdx.files.internal(strTex)); // Загрузка текстуры пули
        }

        bullets = new Bullet[bulletsCount];
        for (int i = 0; i < bulletsCount; i++) {
            bullets[i] = new Bullet(bulletSpeed);
        }
        shapeRenderer = new ShapeRenderer();
    }

    public BulletEmitter(String strTex, float bulletSpeed, boolean forwardShot){

        if (texBullet == null){
            texBullet = new Texture(Gdx.files.internal(strTex)); // Загрузка текстуры пули
        }

        bullets = new Bullet[bulletsCount];
        for (int i = 0; i < bulletsCount; i++) {
            bullets[i] = new Bullet(bulletSpeed, forwardShot);
        }
        shapeRenderer = new ShapeRenderer();
    }

    public void setBulletsCount(int bulletsCountChange){
        bulletsCount = bulletsCountChange;
    }

    public int getBulletsCount(){
        return bulletsCount;
    }

    public void renderLinerShot(SpriteBatch batch){
        updateLinerShot();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // drawing each bullet
        for (int i = 0; i < bulletsCount; i++) {
            if (bullets[i].isActive()) {
                batch.draw(texBullet, bullets[i].position.x, bullets[i].position.y);
                shapeRenderer.rect(bullets[i].position.x, bullets[i].position.y, texBullet.getWidth(), texBullet.getHeight());
            }
        }
        shapeRenderer.end();
    }

    public void updateLinerShot(){
        // обновление пуль
        for (int i = 0; i < bulletsCount; i++) {
            bullets[i].linerShot();
        }
    }

}
