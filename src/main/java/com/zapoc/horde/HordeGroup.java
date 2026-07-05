package com.zapoc.horde;

import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class HordeGroup {

    private final int id;
    private final HordeAttackPoint attackPoint;
    private final List<Mob> zombies = new ArrayList<>();

    private Mob leader;
    private HordeGroupRole role;

    public HordeGroup(int id,
                      HordeAttackPoint attackPoint,
                      HordeGroupRole role) {

        this.id = id;
        this.attackPoint = attackPoint;
        this.role = role;

    }

    public int getId() {
        return id;
    }

    public HordeAttackPoint getAttackPoint() {
        return attackPoint;
    }

    public List<Mob> getZombies() {
        return zombies;
    }

    public Mob getLeader() {
        return leader;
    }

    public void setLeader(Mob leader) {
        this.leader = leader;
    }

    public HordeGroupRole getRole() {
        return role;
    }

    public void setRole(HordeGroupRole role) {
        this.role = role;
    }

    public void addZombie(Mob mob) {

        if (!zombies.contains(mob)) {
            zombies.add(mob);
        }

        if (leader == null) {
            leader = mob;
        }

    }

    public void removeZombie(Mob mob) {

        zombies.remove(mob);

        if (leader == mob) {

            if (zombies.isEmpty()) {
                leader = null;
            } else {
                leader = zombies.get(0);
            }

        }

    }

    public int size() {
        return zombies.size();
    }

}