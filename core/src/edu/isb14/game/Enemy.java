/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isb14.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author world61
 */
public abstract class Enemy {
    
    protected Texture texture;
    protected int hp;
    protected int reward;
    protected int attack;
    protected float speed;
    protected int fireRate;       //
    protected int fireCounter;    // how many time press SPACE
    protected Vector2 position;
    protected Rectangle hitBox;
    protected boolean active;

    
    public boolean isActive(){
        return active;
    }

    public void setActive(boolean act){
        active = act;
    }
    
    public void destroy(){
        this.active = false;
//        this.recreate();
    }
    
    public abstract void recreate(); //надо обдумать использование
    
    public void getDamage(int dmg){
        this.hp -= dmg;
        if (this.hp <= 0)
            this.dead();
//        this.destroy();
    }
    
    public Rectangle getHitBox(){
        return hitBox;
    }
        
    public abstract void update();

    public void dead(){
        this.position.set(48, 48);
        this.speed = 0;
        this.fireCounter = 0;
        this.hitBox.setPosition(position);
        this.hitBox.setSize(0,0);
//        this.fireRate = 1024;
    }
    public int getHp(){
        return hp;
    }
    
    public abstract void render(SpriteBatch batch);
    
}
