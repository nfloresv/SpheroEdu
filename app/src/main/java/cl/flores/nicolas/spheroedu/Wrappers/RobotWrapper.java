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
        this.pos = new Vector(x, y);
        this.charge = charge;
        this.color = color;
    }

    public ConvenienceRobot getRobot() {
        return robot;
    }

    public String getColor() {
        return color;
    }

    public Vector getPos() {
        return pos;
    }

    public void setPos(float x, float y) {
        this.pos = new Vector(x, y);
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }
}
