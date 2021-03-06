/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sandbox.scifi;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;


    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand, animWalk, animJump, animPunch;

    public PlayerControl() {
        Texture staticTexture = FXGL.getAssetLoader()
                .loadTexture("dude.png")
                .subTexture(new Rectangle2D(0, 0, 32, 42));

        animatedTexture = FXGL.getAssetLoader()
                .loadTexture("dude.png")
                .subTexture(new Rectangle2D(32, 0, 32*3, 42))
                .superTexture(staticTexture, HorizontalDirection.RIGHT)
                .toAnimatedTexture(4, Duration.seconds(0.5));

        animWalk = animatedTexture.getAnimationChannel();
        animStand = new AnimationChannel(animatedTexture.getImage(), 4, 32, 42, Duration.seconds(1), 1, 1);
        //animWalk = new AnimationChannel("dude.png", 4, 32, 42, Duration.seconds(0.5), 0, 3);

//        animStand = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(1), 0, 11);
//        animWalk = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(1), 12*2, 12*2 + 11);
//        animJump = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(1.2), 12*4, 12*4 + 11);
//        animPunch = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(1 / 8.0), 12*6, 12*6 + 11);

        this.animatedTexture.setAnimationChannel(animStand);
        this.animatedTexture.start(FXGL.getApp().getStateMachine().getPlayState());
    }

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        physics = Entities.getPhysics(entity);
        view = Entities.getView(entity);

        view.getView().addNode(animatedTexture);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        if (Math.abs(physics.getVelocityX()) == 0) {
            stopAnimate();
        } else {
            animate();
        }

        if (Math.abs(physics.getVelocityX()) < 140)
            physics.setVelocityX(0);

        if (Math.abs(physics.getVelocityY()) < 1)
            canJump = true;
    }

    private boolean canJump = true;

    public void left() {
        view.getView().setScaleX(-1);
        physics.setVelocityX(-150);
    }

    public void right() {
        view.getView().setScaleX(1);
        physics.setVelocityX(150);
    }

    private void animate() {
        animatedTexture.setAnimationChannel(animWalk);
    }

    private void stopAnimate() {
        animatedTexture.setAnimationChannel(animStand);
    }

    public void jump() {
        if (canJump) {
            physics.setVelocityY(-250);
            animatedTexture.playAnimationChannel(animJump);
            canJump = false;
        }
    }

    public void punch() {
        animatedTexture.playAnimationChannel(animPunch);
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }

    public void shoot(Point2D endPoint) {
        double x = position.getX();
        double y = position.getY();

        Point2D velocity = endPoint
                .subtract(x, y)
                .normalize()
                .multiply(500);

        ((GameWorld) getEntity().getWorld()).spawn("Arrow",
                new SpawnData(x, y)
                        .put("velocity", velocity)
                        .put("shooter", getEntity()));
    }
}
