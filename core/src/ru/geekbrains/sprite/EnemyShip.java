package ru.geekbrains.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Ship;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.ExplosionPool;

public class EnemyShip extends Ship {

    private enum State {DESCENT, FIGHT}

    private State state;

    private Vector2 descentV = new Vector2(0, -0.15f);

    private MainShip mainShip;

    public EnemyShip(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds, MainShip mainShip) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        v0 = new Vector2();
        v = new Vector2();
        bulletV = new Vector2();
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        state = State.DESCENT;
        this.mainShip = mainShip;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pos.mulAdd(v, delta);
        switch (state) {
            case DESCENT:
                if (getTop() <= worldBounds.getTop()) {
                    v.set(v0);
                    state = State.FIGHT;
                }
                break;
            case FIGHT:
                reloadTimer += delta;
                if (reloadTimer >= reloadInterval) {
                    reloadTimer = 0;
                    shoot();
                }
                if (getBottom() < worldBounds.getBottom()) {
                    mainShip.damage(damage);
                    setDestroyed();
                }
                break;
        }
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v0,
            TextureRegion bulletRegion,
            float bulletHeight,
            float bulletVy,
            int damage,
            float reloadInterval,
            float height,
            int hp
    ) {
        this.regions = regions;
        this.v0.set(v0);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV.set(0, bulletVy);
        this.damage = damage;
        this.reloadInterval = reloadInterval;
        setHeightProportion(height);
        this.hp = hp;
        v.set(descentV);
        reloadTimer = reloadInterval;
        state = State.DESCENT;
    }

    public boolean isBulletCollision(Rect bullet) {
        return (
                bullet.getRight() > getLeft()
                        && bullet.getLeft() < getRight()
                        && bullet.getBottom() < getTop()
                        && bullet.getTop() > pos.y
        );
    }
}
