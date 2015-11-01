package cl.flores.nicolas.spheroedu.Utils;

import com.orbotix.ConvenienceRobot;

public class RobotWrapper {
    private final ConvenienceRobot robot;
    private final String color;
    private int x;
    private int y;
    private double charge;

    public RobotWrapper(ConvenienceRobot robot, String color, int x, int y, double charge) {
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
}
