package cl.flores.nicolas.spheroedu.Wrappers;

import com.orbotix.ConvenienceRobot;

import cl.flores.nicolas.spheroedu.Utils.Vector;

public class RobotWrapper {
    private final ConvenienceRobot robot;
    private final String color;
    private Vector pos;
    private int charge;

    public RobotWrapper(ConvenienceRobot robot, String color, float x, float y, int charge) {
        this.robot = robot;
        this.pos=new Vector(x,y);
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
        return pos.getX();
    }

    public void setX(float x) {
        pos.setX(x);
    }

    public float getY() {
        return pos.getY();
    }

    public void setY(float y) {
        pos.setY(y);
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}
