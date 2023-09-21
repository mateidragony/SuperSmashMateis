package SSMCode.PlayerAttacks;

import SSMCode.Player;
import SSMEngines.old.PlayerOld;

import java.awt.*;
import java.util.ArrayList;

public class StickyProjectile extends Projectile {

    private double explodeTimer;

    public StickyProjectile(int x, int y, int w, int h, int direction, String team_, int shooter, boolean bossMode) {
        super(x, y, w, h, direction, team_, shooter, bossMode, false);
    }

    @Override
    public void animateDamage(ArrayList<Player> players) {
        for (Player target : players) {
            //if the projectile hits the target, and wasn't shot by
            //a fellow team member and the target isn't untargetable
            if (this.getHitBox().intersects(target.getHitBox())
                    && !getTeam().equals(target.getTeam())
                    && !target.isUntargetable()) {

                target.setPercentage(target.getPercentage() + 3.5);
                setIsNull(true);

                target.setDamageXVel(.75 * (1.5 + 3 * target.getPercentage() / 25) * getDirection());
                target.setYVel(.85 * (target.getYVel() - 2.5));
                target.setNumStickies(target.getNumStickies() + 1);
                target.addStickyTimer(1.0);
                target.addStickyLoc(new Point((int) (getX() - target.getX()) + getDirection()*10, (int) (getY() - target.getY())));
                target.addStickyTeam(getTeam());
            }
        }
    }


}
