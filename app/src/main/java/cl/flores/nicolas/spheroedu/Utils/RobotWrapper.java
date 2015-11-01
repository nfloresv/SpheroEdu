package cl.flores.nicolas.spheroedu.Utils;

import com.orbotix.ConvenienceRobot;

public class RobotWrapper {
    private final ConvenienceRobot robot;
    private final String color;
    private float x;
    private float y;
    private int charge;

    public RobotWrapper(ConvenienceRobot robot, String color, float x, float y, int charge) {
        this.robot = robot;
        this.x = x;
        this.y = y;
        this.charge = charge;
        this.color = color;
    }

    public ConvenienceRobot getRobot() {
        return robot;
    }

    public String getColor() {
        return color;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}
