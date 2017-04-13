/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

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
        this.speed = 5.0f;
        this.fireCounter = 0;
        this.fireRate = 5;
        
        this.texture = new Texture(Gdx.files.internal(sprite));
        this.position = new Vector2(SunsGame.CONFIG_WIDTH, SunsGame.CONFIG_HEIGHT/2);
        
        
    }
    
    @Override
    public void update(){
        if (active)
            position.x -= speed;
        if (position.x < -texture.getWidth())
            active = false;
         this.fireCounter++;                                                  // мы увеличиваем какой-то счетчик
        if (this.fireCounter > this.fireRate){                                     // если этот счётчик стал больше чем
            fireCounter = 0;                                            // счётчик сбрасываем
            for (int i = 0; i < bulletEmitter.bullets.length; i++) {        // начинаем ходить по массиву пуль, котор лежит в MyGdxGame
                if(!bulletEmitter.bullets[i].isActive()){                    // как только находим в этом массиве не активную пулю,
                    bulletEmitter.bullets[i].setup(position.x + 55,position.y + 16);  // мы её создаём
                    break;                                              // и перестаем искать ещё какие то пули
                }
            }
        }
    }
    
    public void bulletRender(SpriteBatch batch){
        batch.draw(texture, position.x, position.y);
        bulletEmitter.renderLinerShot(batch);
    }
}
