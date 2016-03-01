package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import dataclient.robotdata.arm.ArmStatus;

public class MockUtilityArm {
	private final double CONVERSION_FACTOR = 1024 / 360.0;
	private final double LENGTH_A = 17.5;
	private final double LENGTH_B = 18;
	private final double INITIAL_POT_A_POSITION = 10, INITIAL_POT_B_POSITION = 90;

	private double jointBDegree, jointADegree;
	private double jointBPosition, jointAPosition;

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
		MockUtilityArm m = new MockUtilityArm();
	}

	public void goTo(double xEndPoint, double yEndPoint, ArmStatus status) {

		writeToFile("X: " + this.x + ", Y:" + this.y);

		if (xEndPoint > LENGTH_A - 2 && yEndPoint < LENGTH_B - 2) {
			xEndPoint = LENGTH_A - 2;
			yEndPoint = LENGTH_B - 2;
		}

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

		writeToFile("\nJoint A Degree: " + jointADegree + " --Joint B Degree: " + jointBDegree);

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

		
		writeToFile("\nJoint A Degree 2: " + jointADegree + " --Joint B Degree 2: " + jointBDegree);

		jointAPosition = jointADegree * CONVERSION_FACTOR + INITIAL_POT_A_POSITION;
		jointBPosition = jointBDegree * CONVERSION_FACTOR + INITIAL_POT_B_POSITION;

		writeToFile("\nJoint A Position: " + jointAPosition + " --Joint B Position: " + jointBPosition
				+ "Initial B Pot: " + INITIAL_POT_B_POSITION);

	}
}