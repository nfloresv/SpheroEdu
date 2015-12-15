package cl.flores.nicolas.spheroedu.Wrappers;

import android.util.Log;

import com.orbotix.ConvenienceRobot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.Utils.SpheroColors;
import cl.flores.nicolas.spheroedu.Utils.Vector;

public class RobotManager {
    private int independent;
    private ArrayList<RobotWrapper> robots;
    private int tolerance;
    private Vector destination;

    public RobotManager(ArrayList<ConvenienceRobot> robots, String exercise) {
        this.robots = new ArrayList<>();
        try {
            JSONObject description = new JSONObject(exercise);
            independent = description.getInt(Constants.JSON_EXERCISE_INDEPENDENT);

            JSONArray spheros = description.getJSONArray(Constants.JSON_EXERCISE_SPHEROS_ARRAY);
            for (int i = 0; i < spheros.length(); ++i) {
                JSONObject sphero = spheros.getJSONObject(i);

                ConvenienceRobot robot = null;
                if (i < robots.size()) {
                    robot = robots.get(i);
                }
                float x = (float) sphero.getDouble(Constants.JSON_EXERCISE_X);
                float y = (float) sphero.getDouble(Constants.JSON_EXERCISE_Y);
                int charge = sphero.getInt(Constants.JSON_EXERCISE_CHARGE);
                String color = SpheroColors.getColorByIndex(i);

                RobotWrapper wrapper = new RobotWrapper(robot, color, x, y, charge);
                this.robots.add(wrapper);
            }

            JSONObject objective = description.getJSONObject(Constants.JSON_EXERCISE_DESTINATION);
            tolerance = objective.getInt(Constants.JSON_EXERCISE_TOLERANCE);
            float x = (float) objective.getDouble(Constants.JSON_POSITION_X);
            float y = (float) objective.getDouble(Constants.JSON_POSITION_Y);
            destination = new Vector(x, y);
        } catch (JSONException e) {
            independent = 0;
            Log.e(Constants.LOG_TAG, "Error parsing JSON Exercise", e);
        }
    }

    // Independent Data
    public ArrayList<RobotWrapper> getIndependentWrapper() {
        List<RobotWrapper> sublist = robots.subList(0, independent);
        return new ArrayList<>(sublist);
    }

    public ArrayList<RobotWrapper> getDependentWrapper() {
        List<RobotWrapper> sublist = robots.subList(independent, robots.size());
        return new ArrayList<>(sublist);
    }

    // Dependent Data
    public RobotWrapper getWrapper(int index) {
        return robots.get(independent + index);
    }

    // General Methods
    public void sleep() {
        for (RobotWrapper wrapper : robots) {
            ConvenienceRobot robot = wrapper.getRobot();
            if (robot != null)
                robot.sleep();
        }
    }

    public boolean isInDestination(Vector pos) {
        double x = Math.pow(pos.getX() - destination.getX(), 2);
        double y = Math.pow(pos.getY() - destination.getY(), 2);
        double r = Math.pow(tolerance, 2);
        return x + y <= r;
    }
}
