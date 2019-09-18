package com.google.maps.android.ui;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class Polyline {
    static double DEFAULT_CURVE_ROUTE_CURVATURE = 0.10;
    static double DEFAULT_CURVE_POINTS = 60;

    /**
     * method that given a origin, destination and curvature, draws a curved polyline.
     *
     * @param origin    the origin LatLng
     * @param dest      the destination LatLng
     * @param curvature The curvature. Keep in mind to use low values (0.5 to 0.25) for smoothness
     * @return the PolyLineOptions ready to be added to a map
     */
    public static PolylineOptions curvedPolyline(LatLng origin, LatLng dest, Double curvature) {
        double distance = SphericalUtil.computeDistanceBetween(origin, dest);
        double heading = SphericalUtil.computeHeading(origin, dest);
        double halfDistance = distance / 2;

        // Calculate midpoint position
        LatLng midPoint = SphericalUtil.computeOffset(origin, halfDistance, heading);

        if (curvature == null) {
            curvature = DEFAULT_CURVE_ROUTE_CURVATURE;
        }

        // Calculate position of the curve center point
        double sqrCurvature = curvature * curvature;
        double extraParam = distance / (4 * curvature);
        double midPerpendicularLength = (1 - sqrCurvature) * extraParam;
        double r = (1 + sqrCurvature) * extraParam;
        LatLng circleCenterPoint = SphericalUtil.computeOffset(midPoint, midPerpendicularLength, heading + 90.0);

        // Calculate heading between circle center and two points
        double headingToOrigin = SphericalUtil.computeHeading(circleCenterPoint, origin);

        // Calculate positions of points on the curve
        double step = Math.toDegrees(Math.atan(halfDistance / midPerpendicularLength)) * 2 / DEFAULT_CURVE_POINTS;
        ArrayList<LatLng> points = new ArrayList<>();

        for (int i = 0; i < DEFAULT_CURVE_POINTS; i++) {
            points.add(SphericalUtil.computeOffset(circleCenterPoint, r, headingToOrigin + i * step));
        }

        return new PolylineOptions().addAll(points).geodesic(false);
    }
}
