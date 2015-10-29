package cl.flores.nicolas.spheroedu.Utils;

import com.orbotix.ConvenienceRobot;

public class RobotWrapper {
    private final ConvenienceRobot robot;
    private int x;
    private int y;
    private double charge;
    private float[] color;

    public RobotWrapper(ConvenienceRobot robot, float[] color, int x, int y, double charge) {
        this.robot = robot;
        this.x = x;
        this.y = y;
        this.charge = charge;
        this.color = color;
    }

    public ConvenienceRobot getRobot() {
        return robot;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public float[] getColor() {
        return color;
    }
}
