package JavaBasics;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class BasicsPractice {
	
	    private static volatile boolean stopRequested = false;
	    

	    public static void main(String[] args) {
	        System.out.println("starting. Press ENTER in the console to stop.");

	        // Start the mover thread
	        Thread mover = new Thread(() -> {
	            try {
	                runMouseLoop();
	            } catch (AWTException | InterruptedException e) {
	                System.err.println("Mover thread error: " + e.getMessage());
	            }
	        }, "MouseMover-Thread");

	        mover.setDaemon(true);
	        mover.start();

	        // Wait for user to press ENTER to stop
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
	            br.readLine();
	            stopRequested = true;
	            System.out.println("Stop requested. Waiting for thread to finish...");
	            mover.join(2000); // wait briefly for thread to finish
	        } catch (IOException | InterruptedException e) {
	            System.err.println("Main thread error: " + ((Throwable) e).getMessage());
	        }

	       
	        System.out.println("Stopped.");
	    }

	    private static void runMouseLoop() throws AWTException, InterruptedException {
	        Robot robot = new Robot();

	        // get current mouse location as center
	        Point p = MouseInfo.getPointerInfo().getLocation();
	        int centerX = p.x;
	        int centerY = p.y;

	        // parameters you can tweak
	        final int radius = 20;       // how far from center cursor will move (pixels)
	        final int steps = 36;        // number of intermediate positions for one circle
	        final int delayMs = 150;     // delay between moves in milliseconds

	        double angle = 0.0;
	        double angleStep = 2.0 * Math.PI / steps;

	        while (!stopRequested) {
	            // compute new position on the circle
	            int x = centerX + (int) Math.round(Math.cos(angle) * radius);
	            int y = centerY + (int) Math.round(Math.sin(angle) * radius);

	            robot.mouseMove(x, y);

	            angle += angleStep;
	            if (angle > 2.0 * Math.PI) {
	                angle -= 2.0 * Math.PI;
	            }

	            // after one full circle, refresh center in case you moved the mouse manually
	            if ((int)(angle / angleStep) == 0) {
	                Point current = MouseInfo.getPointerInfo().getLocation();
	                centerX = current.x;
	                centerY = current.y;
	            }

	            Thread.sleep(delayMs);
	        }
	    }


}
