package com.zapoc.horde;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class HordeGroup {

    private static final String LEADER_MARK_TAG = "ZapocHordeLeaderMark";

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

    public void setLeader(Mob newLeader) {

        if (leader != null) {
            clearLeaderMark(leader);
        }

        leader = newLeader;

        if (leader != null) {
            markLeader(leader);
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

        if (mob.isRemoved())
            return;

        if (!mob.isAlive())
            return;

        if (!zombies.contains(mob)) {
            zombies.add(mob);
        }

        if (leader == null || leader.isRemoved() || !leader.isAlive()) {
            setLeader(mob);
        }
    }

    public void removeZombie(Mob mob) {

        if (mob == null)
            return;

        zombies.remove(mob);

        if (leader == mob) {

            clearLeaderMark(mob);

            Mob newLeader = findNewLeader();

            setLeader(newLeader);
        }
    }

    public int size() {
        return zombies.size();
    }

    public void clearMarks() {

        for (Mob mob : zombies) {

            if (mob == null)
                continue;

            clearLeaderMark(mob);
        }

        leader = null;
    }

    private Mob findNewLeader() {

        for (Mob mob : zombies) {

            if (mob == null)
                continue;

            if (mob.isRemoved())
                continue;

            if (!mob.isAlive())
                continue;

            return mob;
        }

        return null;
    }

    private void markLeader(Mob mob) {

        mob.getPersistentData().putBoolean(LEADER_MARK_TAG, true);

        mob.setGlowingTag(true);
        mob.setCustomName(new TextComponent("HORDE LEADER G" + id));
        mob.setCustomNameVisible(true);
    }

    private void clearLeaderMark(Mob mob) {

        if (mob == null)
            return;

        if (!mob.getPersistentData().getBoolean(LEADER_MARK_TAG))
            return;

        mob.getPersistentData().remove(LEADER_MARK_TAG);

        mob.setGlowingTag(false);
        mob.setCustomName(null);
        mob.setCustomNameVisible(false);
    }
}
