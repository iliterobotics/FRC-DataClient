package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import dataclient.robotdata.arm.ArmStatus;

public class MockUtilityArm {
    private final double LENGTH_A = 17.5;
    private final double LENGTH_B = 18;
    private final double INITIAL_POT_A_POSITION = 10, INITIAL_POT_B_POSITION = 90;
    public static final double CONVERSION_FACTOR = 1024 / 360.0; // multiplier
                                                                    // to
                                                                    // convert
                                                                    // from
                                                                    // degrees
                                                                    // to ticks
    private static final double FRAME_LENGTH = 5;
    private final double BOUNDARY = 13;
    private final double X_MAX_BACK_REACH = 9;
    private final double Y_MAX_UP_REACH = 33;
    private final double Y_MAX_DOWN_REACH = -10;
    private double jointAPosition; // storage for updating the A angle
    private double jointBPosition; // storage for updating the B angle
    private double jointADegree;
    private double jointBDegree;
    private double aP, aI, aD; // values for the PID to move joint A
    private double bP, bI, bD; // values for the PID to move joint B

    private double xCoord; // Current x Coordinate
    private double yCoord; // Current y Coordinate

    double aAngleVal = 0;
    double bAngleVal = 0;

    private double x, y;

    private File f = new File("src/Values.txt");

    public MockUtilityArm() {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToFile(String pLine) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true)));
            bw.write(pLine + "\n");
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        MockUtilityArm m = new MockUtilityArm();
//        m.goTo(-10, 10);
    }

    public void goTo(double xEndPoint, double yEndPoint, ArmStatus status) {
        /*
         * Explanation of if-statements:
         * https://docs.google.com/drawings/d/1dR8t4AcUfh1KOq0hK-
         * Sh3xFV05fgMqhl_l8BH47n6JQ/edit?usp=sharing
         */

        if (xEndPoint < -BOUNDARY - FRAME_LENGTH) {
            xEndPoint = -BOUNDARY - FRAME_LENGTH;
        }
        if (xEndPoint > X_MAX_BACK_REACH) {
            xEndPoint = X_MAX_BACK_REACH;
        }
        if (yEndPoint > Y_MAX_UP_REACH) {
            yEndPoint = Y_MAX_UP_REACH;
        }
        if (yEndPoint < Y_MAX_DOWN_REACH) {
            yEndPoint = Y_MAX_DOWN_REACH;
        }
        if (yEndPoint < 3 && xEndPoint > -FRAME_LENGTH) {
            xEndPoint = -5;
        }

        if (xEndPoint < 1 && xEndPoint > -1 && yEndPoint < 6) {
            yEndPoint = 6;
        }

        if (xEndPoint >= 1 / 45.0 && yEndPoint < 22 && yEndPoint < Math.sqrt(45 * (xEndPoint + 4.0 / 45)) + 1) {
            xEndPoint = Math.pow((yEndPoint - 1), 2) / 45.0 - 4.0 / 45;
        }

        if (yEndPoint > 28 && (yEndPoint) > Math
                .sqrt((1 - (xEndPoint * xEndPoint) / (Math.pow((BOUNDARY + FRAME_LENGTH), 2))) * (5 * 5)) + 28) {
            yEndPoint = (Math
                    .sqrt((1 - (xEndPoint * xEndPoint) / ((Math.pow((BOUNDARY + FRAME_LENGTH), 2)))) * (5 * 5))) + 28;
        }

        xCoord = xEndPoint;
        yCoord = yEndPoint;

        double p = Math.sqrt((xEndPoint * xEndPoint) + (yEndPoint * yEndPoint));
        double k = ((p * p) + LENGTH_A * LENGTH_A - LENGTH_B * LENGTH_B) / (2 * p);

        double x1 = (xEndPoint * k) / p + (yEndPoint / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);
        double y1 = (yEndPoint * k) / p - (xEndPoint / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);

        double x2 = (xEndPoint * k) / p - (yEndPoint / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);
        double y2 = (yEndPoint * k) / p + (xEndPoint / p) * Math.sqrt(LENGTH_A * LENGTH_A - k * k);

        double finaly = 0;
        double finalx = 0;
        if (y1 < 0) {
            finaly = y2;
            finalx = x2;
        } else {
            finaly = y1;
            finalx = x1;
        }

        jointADegree = Math.toDegrees(Math.atan2(finaly, finalx));

        double transformedX = (xEndPoint - finalx);
        double transformedY = (yEndPoint - finaly);
        jointBDegree = Math.toDegrees(Math.atan2(transformedY, transformedX));

        // DriverStation.reportError("\nJoint A Degree: " + jointADegree
        // + " --Joint B Degree: " + jointBDegree, false);

        jointBDegree = 180 - jointBDegree;

        if (jointBDegree > 350 - jointADegree) {
            jointBDegree = jointBDegree % 360 - 360;
        }

        if (jointBDegree < -jointADegree + 10) {
            jointBDegree = -jointADegree + 10;
        }

        jointBDegree += jointADegree;
        
        status.setAlpha(jointADegree);
        status.setBeta(jointBDegree);
        status.setDestX(xEndPoint);
        status.setDestY(yEndPoint);
        
        // DriverStation.reportError("\nJoint A Degree 2: " + jointADegree
        // + " --Joint B Degree 2: " + jointBDegree, false);

        jointAPosition = jointADegree * CONVERSION_FACTOR + INITIAL_POT_A_POSITION;
        jointBPosition = jointBDegree * CONVERSION_FACTOR + INITIAL_POT_B_POSITION;

        // DriverStation
        // .reportError(
        // "\nJoint A Position: " + jointAPosition
        // + " --Joint B Position: " + jointBPosition
        // + "Initial B Pot: "
        // + SensorInputControlSRX
        // .getInstance().INITIAL_POT_B_POSITION,
        // false);

    }
}
