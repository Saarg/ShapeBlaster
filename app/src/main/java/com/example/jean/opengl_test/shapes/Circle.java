package com.example.jean.opengl_test.shapes;

/**
 * Created by jean on 04/10/16.
 */

public class Circle extends Shape {
    public Circle(int quality) {
        super();

        // Init coords and drawOrder
        float[] circleCoords = new float[(quality+1) * 3];
        short[] drawOrder = new short[3*quality];

        // Center
        circleCoords[0] = 0.0f;
        circleCoords[1] = 0.0f;
        circleCoords[2] = 0.0f;

        for (int i = 3 ; i < circleCoords.length ; i += 3) {
            double angle = (i/3) * (2*Math.PI/quality);
            circleCoords[i] = (float)(0.5f * Math.cos(angle));
            circleCoords[i+1] = (float)(0.5f * Math.sin(angle));
            circleCoords[i+2] = 0.0f;
        }

        for (int i = 0 ; i < quality-1 ; i++) {
            drawOrder[i*3] = (short)(0);
            drawOrder[i*3+1] = (short) (i+1);
            drawOrder[i*3+2] = (short) (i+2);
        }

        drawOrder[(quality-1)*3] = (short)(0);
        drawOrder[(quality-1)*3+1] = (short) quality;
        drawOrder[(quality-1)*3+2] = (short)(1);

        super.init(circleCoords, drawOrder);
    }
}
