package com.zapoc.zombie;

public enum ZombieType {

    NORMAL(1.0, 1.0, 1.0),
    RUNNER(0.8, 0.7, 2.0),
    TANK(3.0, 2.0, 0.6),
    HUNTER(1.5, 1.3, 1.2),
    BREAKER(2.0, 1.5, 0.9);

    // HP / DAMAGE / SPEED multipliers
    public final double hp;
    public final double damage;
    public final double speed;

    ZombieType(double hp, double damage, double speed) {
        this.hp = hp;
        this.damage = damage;
        this.speed = speed;
    }
}
