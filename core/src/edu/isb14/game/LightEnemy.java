/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author world61
 */
public class LightEnemy extends Enemy{
    
    BulletEmitter bulletEmitter = new BulletEmitter("bullet20.png", 5f, false);
    ShapeRenderer shapeRenderer;
    float dateDeath = 0;
    
    public LightEnemy(String sprite){
        this.active = false;
        this.hp = 1;
        this.attack = 1;
        this.reward = 5;
        this.speed = 2.0f;
        this.fireCounter = 0;
        this.fireRate = 50;
        
        this.texture = new Texture(Gdx.files.internal(sprite));
        this.position = new Vector2(SunsGame.CONFIG_WIDTH + (float) Math.random()* 200, (float) (Math.random()*(SunsGame.CONFIG_HEIGHT - 1.5*texture.getHeight()) + texture.getHeight()/2 ));
        this.hitBox = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
        shapeRenderer = new ShapeRenderer();
    }
    
    public void recreate(){
        this.active = true;
        this.hp = 2;
//        this.position.x = SunsGame.CONFIG_WIDTH + (float) Math.random()* 200;
        this.position.x = SunsGame.CONFIG_WIDTH + (float) Math.random()* 200 -700;
        this.position.y = (float) (Math.random()*(SunsGame.CONFIG_HEIGHT - 1.5*texture.getHeight()) + texture.getHeight()/2 );
        this.hitBox.x = position.x;
        this.hitBox.y = position.y;
    }
    
    @Override
    public void update(){
        if (this.isActive()){
            this.position.x -= speed;
            this.hitBox.x -= speed;
        }
        
        if (this.position.x < -this.texture.getWidth())
            this.dead();

        this.fireCounter++;                                                  // мы увеличиваем какой-то счетчик
        if (this.fireCounter > this.fireRate){                                     // если этот счётчик стал больше чем
            fireCounter = 0;                                            // счётчик сбрасываем
            for (int i = 0; i < bulletEmitter.bullets.length; i++) {        // начинаем ходить по массиву пуль, котор лежит в MyGdxGame
                if(!bulletEmitter.bullets[i].isActive()){                    // как только находим в этом массиве не активную пулю,
                    bulletEmitter.bullets[i].setup(position.x, position.y + +this.texture.getHeight()/2);  // мы её создаём
                    break;                                              // и перестаем искать ещё какие то пули
                }
                if (bulletEmitter.bullets[i].getPosition().x<0)
                    bulletEmitter.bullets[i].destroy();
            }
        }

        if (this.hp <= 0){
            dateDeath += Gdx.graphics.getDeltaTime();
            if (dateDeath >= 10){
                this.destroy();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.draw(texture, position.x, position.y);
        bulletEmitter.renderLinerShot(batch);

        shapeRenderer.rect(position.x,position.y,texture.getWidth(),texture.getHeight());

        shapeRenderer.end();
    }

    public void bulletRender(SpriteBatch batch){
//        batch.draw(texture, position.x, position.y);

    }


    
}
