package com.zapoc.horde;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class HordeGroup {

    private final int id;
    private final HordeAttackPoint attackPoint;
    private final List<Mob> zombies = new ArrayList<>();

    private Mob leader;
    private HordeGroupRole role;

    public HordeGroup(int id, HordeAttackPoint attackPoint, HordeGroupRole role) {
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

        if (this.leader != null) {
            clearLeaderMark(this.leader);
        }

        this.leader = leader;

        if (this.leader != null) {
            markLeader(this.leader);
        }
    }

    public HordeGroupRole getRole() {
        return role;
    }

    public void setRole(HordeGroupRole role) {
        this.role = role;
    }

    public void addZombie(Mob mob) {

        if (mob == null)
            return;

        if (!zombies.contains(mob)) {
            zombies.add(mob);
        }

        if (leader == null) {
            setLeader(mob);
        }
    }

    public void removeZombie(Mob mob) {

        if (mob == null)
            return;

        zombies.remove(mob);

        if (leader == mob) {

            clearLeaderMark(mob);

            if (zombies.isEmpty()) {

                leader = null;

            } else {

                setLeader(zombies.get(0));
            }
        }
    }

    public int size() {
        return zombies.size();
    }

    private void markLeader(Mob mob) {

        mob.setGlowingTag(true);
        mob.setCustomName(new TextComponent("HORDE LEADER G" + id));
        mob.setCustomNameVisible(true);
    }

    private void clearLeaderMark(Mob mob) {

        mob.setGlowingTag(false);
        mob.setCustomName(null);
        mob.setCustomNameVisible(false);
    }
}