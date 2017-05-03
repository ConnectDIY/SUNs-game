/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author world61
 */
public class MediumEnemy extends Enemy{
    
    BulletEmitter bulletEmitter = new BulletEmitter("bullet20.png", 19f, false);
    
    public MediumEnemy(String sprite){
        this.active = true;
        this.hp = 3;
        this.attack = 3;
        this.reward = 10;
        this.speed = 2.0f;
        this.fireCounter = 0;
        this.fireRate = 15;
        
        this.texture = new Texture(Gdx.files.internal(sprite));
        this.position = new Vector2(SunsGame.CONFIG_WIDTH, (float) (Math.random()*SunsGame.CONFIG_HEIGHT - texture.getHeight()));
        this.hitBox = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
        
    }
    
    public void recreate(){
        
        this.active = true;
        this.hp = 3;
        this.position.x = SunsGame.CONFIG_WIDTH;
        this.position.y = (float) (Math.random()*(SunsGame.CONFIG_HEIGHT - 1.5*texture.getHeight()) + texture.getHeight()/2 );
        this.hitBox.x = position.x;
        this.hitBox.y = position.y;
    }
    
    @Override
    public void update(){
        if (this.isActive()){
            position.x -= speed;
            hitBox.x -= speed;
        }

        if ( position.x == -this.texture.getWidth() )
            recreate();
        
        this.fireCounter++;                                                  // мы увеличиваем какой-то счетчик
        if (this.fireCounter > this.fireRate){                                     // если этот счётчик стал больше чем
            fireCounter = 0;                                            // счётчик сбрасываем
            for (int i = 0; i < bulletEmitter.bullets.length; i++) {        // начинаем ходить по массиву пуль, котор лежит в MyGdxGame
                if(!bulletEmitter.bullets[i].isActive()){                    // как только находим в этом массиве не активную пулю,
                    bulletEmitter.bullets[i].setup(position.x + 55,position.y + 16);  // мы её создаём
                    break;                                              // и перестаем искать ещё какие то пули
                }
                if (bulletEmitter.bullets[i].getPosition().x<0)
                    bulletEmitter.bullets[i].destroy();
            }
        }
    }
    
    public void bulletRender(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
        bulletEmitter.renderLinerShot(batch);
    }
}
