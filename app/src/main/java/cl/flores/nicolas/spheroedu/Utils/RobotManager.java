package cl.flores.nicolas.spheroedu.Utils;

import android.util.Log;

import com.orbotix.ConvenienceRobot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RobotManager {
    private int independent;
    private ArrayList<RobotWrapper> robots;

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
}
