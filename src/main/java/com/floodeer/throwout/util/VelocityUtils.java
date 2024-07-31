package com.floodeer.throwout.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityUtils {

    public static Vector getTrajectory(Entity entity1, Entity entity2) {
        return getTrajectory(entity1.getLocation().toVector(), entity2.getLocation().toVector());
    }

    public static Vector getTrajectory(Entity entity, Player player) {
        return getTrajectory(entity.getLocation().toVector(), player.getLocation().toVector());
    }

    public static Vector getTrajectory(Location location, Player player) {
        return getTrajectory(location.toVector(), player.getLocation().toVector());
    }

    public static Vector getTrajectory(Location location1, Location location2) {
        return getTrajectory(location1.toVector(), location2.toVector());
    }

    public static Vector getTrajectory(Vector vector1, Vector vector2) {
        return vector2.subtract(vector1).normalize();
    }

    public static Vector getTrajectory2d(Entity entity1, Entity entity2) {
        return getTrajectory2d(entity1.getLocation().toVector(), entity2.getLocation().toVector());
    }

    public static Vector getTrajectory2d(Location location1, Location location2) {
        return getTrajectory2d(location1.toVector(), location2.toVector());
    }

    public static Vector getTrajectory2d(Vector vector1, Vector vector2) {
        return vector2.subtract(vector1).setY(0).normalize();
    }

    public static float getPitch(Vector vector) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();
        double sqrt = Math.sqrt(x * x + z * z);

        double pitch = Math.toDegrees(Math.atan(sqrt / y));
        if (y <= 0.0D) {
            pitch += 90.0D;
        } else {
            pitch -= 90.0D;
        }
        return (float) pitch;
    }

    public static float getYaw(Vector vector) {
        double x = vector.getX();
        double z = vector.getZ();

        double yaw = Math.toDegrees(Math.atan(-x / z));
        if (z < 0.0D) {
            yaw += 180.0D;
        }
        return (float) yaw;
    }

    public static Vector normalize(Vector vector) {
        if (vector.length() > 0.0D) {
            vector.normalize();
        }
        return vector;
    }

    public static Vector clone(Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector getBumpVector(Entity entity, Location location, double multiplier) {
        Vector vector = entity.getLocation().toVector().subtract(location.toVector()).normalize();
        vector.multiply(multiplier);
        return vector;
    }

    public static void knockback(Player player, Location location, double multiplier, double y, boolean reverse) {
        Location playerLocation = player.getLocation();
        playerLocation.setPitch(0.0F);
        location.setPitch(0.0F);
        Vector vector = playerLocation.toVector().subtract(location.toVector()).normalize();
        if (reverse) {
            vector = location.toVector().subtract(playerLocation.toVector()).normalize();
        }

        vector.setY(y);
        player.setVelocity(vector.multiply(multiplier));
    }

    public static Vector getRandomVectorLine() {
        int min = -5;
        int max = 5;
        int rz = (int) (Math.random() * (max - min) + min);
        int rx = (int) (Math.random() * (max - min) + min);

        double miny = -5.0D;
        double maxy = -1.0D;
        double ry = Math.random() * (maxy - miny) + miny;

        return new Vector(rx, ry, rz).normalize();
    }

    public static Vector getPullVector(Entity entity, Location location, double multiplier) {
        Vector vector = location.toVector().subtract(entity.getLocation().toVector()).normalize();
        vector.multiply(multiplier);
        return vector;
    }

    public static void bumpEntity(Entity entity, Location location, double multiplier) {
        entity.setVelocity(getBumpVector(entity, location, multiplier));
    }

    public static void bumpEntity(Entity entity, Location location, double multiplier, double y) {
        Vector vector = getBumpVector(entity, location, multiplier);
        vector.setY(y);
        entity.setVelocity(vector);
    }

    public static void pullEntity(Entity entity, Location location, double multiplier) {
        entity.setVelocity(getPullVector(entity, location, multiplier));
    }

    public static void pullEntity(Entity entity, Location location, double multiplier, double y) {
        Vector vector = getPullVector(entity, location, multiplier);
        vector.setY(y);
        entity.setVelocity(vector);
    }

    public static void velocity(Entity entity, double x, double y, double z) {
        velocity(entity, entity.getLocation().getDirection(), x, false, 0.0D, y, z);
    }

    public static void velocity(Entity entity, Vector direction, double multiplier, boolean setY, double y, double addY, double maxY) {
        if ((Double.isNaN(direction.getX())) || (Double.isNaN(direction.getY()))
                || (Double.isNaN(direction.getZ())) || (direction.length() == 0.0D)) {
            return;
        }
        if (setY) {
            direction.setY(y);
        }
        direction.normalize();
        direction.multiply(multiplier);

        direction.setY(direction.getY() + addY);
        if (direction.getY() > maxY) {
            direction.setY(maxY);
        }
        entity.setFallDistance(0.0F);
        entity.setVelocity(direction);
    }

    public static final Vector rotateAroundAxisX(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = vector.getY() * cos - vector.getZ() * sin;
        double z = vector.getY() * sin + vector.getZ() * cos;
        return vector.setY(y).setZ(z);
    }

    public static final Vector rotateAroundAxisY(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;
        return vector.setX(x).setZ(z);
    }

    public static final Vector rotateAroundAxisZ(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = vector.getX() * cos - vector.getY() * sin;
        double y = vector.getX() * sin + vector.getY() * cos;
        return vector.setX(x).setY(y);
    }

    public static final Vector rotateVector(Vector vector, double angleX, double angleY, double angleZ) {
        rotateAroundAxisX(vector, angleX);
        rotateAroundAxisY(vector, angleY);
        rotateAroundAxisZ(vector, angleZ);
        return vector;
    }

    public static final double angleToXAxis(Vector vector) {
        return Math.atan2(vector.getX(), vector.getY());
    }

    public static void velocity(Entity entity, double x, double y, double z, boolean addExtraY) {
        velocity(entity, entity.getLocation().getDirection(), x, false, 0.0D, y, z, addExtraY);
    }

    public static void velocity(Entity entity, Vector direction, double multiplier, boolean setY, double y, double addY, double maxY, boolean addExtraY) {
        if ((Double.isNaN(direction.getX())) || (Double.isNaN(direction.getY()))
                || (Double.isNaN(direction.getZ())) || (direction.length() == 0.0D)) {
            return;
        }
        if (setY) {
            direction.setY(y);
        }
        direction.normalize();
        direction.multiply(multiplier);

        direction.setY(direction.getY() + addY);
        if (direction.getY() > maxY) {
            direction.setY(maxY);
        }
        if (addExtraY) {
            direction.setY(direction.getY() + 0.2D);
        }
        entity.setFallDistance(0.0F);
        entity.setVelocity(direction);
    }

    public static Vector getBackVector(Location location) {
        final float newZ = (float) (location.getZ() + (1 * Math.sin(Math.toRadians(location.getYaw() + 90))));
        final float newX = (float) (location.getX() + (1 * Math.cos(Math.toRadians(location.getYaw() + 90))));
        return new Vector(newX - location.getX(), 0, newZ - location.getZ());
    }

    public static Vector rotateX(Vector vector, double angle) {
        double y = Math.cos(angle) * vector.getY() - Math.sin(angle) * vector.getZ();
        double z = Math.sin(angle) * vector.getY() + Math.cos(angle) * vector.getZ();
        return vector.setY(y).setZ(z);
    }

    public static Vector rotateY(Vector vector, double angle) {
        double x = Math.cos(angle) * vector.getX() + Math.sin(angle) * vector.getZ();
        double z = -Math.sin(angle) * vector.getX() + Math.cos(angle) * vector.getZ();
        return vector.setX(x).setY(z);
    }

    public static final Vector rotateZ(Vector vector, double angle) {
        double x = Math.cos(angle) * vector.getX() - Math.sin(angle) * vector.getY();
        double y = Math.sin(angle) * vector.getX() + Math.cos(angle) * vector.getY();
        return vector.setX(x).setY(y);
    }
}
