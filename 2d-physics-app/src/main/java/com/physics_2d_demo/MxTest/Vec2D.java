package com.physics_2d_demo.MxTest;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
public final class Vec2D {

    private static Vec2D vecTemp = new Vec2D();
    float x, y;

    public Vec2D() {
        x = y = 0.0f;
    }

    public Vec2D(float ax, float ay) {
        x = ax;
        y = ay;
    }

    public Vec2D(Vec2D aV) {
        x = aV.x;
        y = aV.y;
    }

    public static float dot(Vec2D a, Vec2D b) {
        return (a.x * b.x + a.y * b.y);
    }

    public static float dist(Vec2D a, Vec2D b) {
        vecTemp.x = a.x - b.x;
        vecTemp.y = a.y - b.y;
        return (float) Math.sqrt(vecTemp.x * vecTemp.x + vecTemp.y * vecTemp.y);
    }

    public static void sub(Vec2D a, Vec2D b, Vec2D resVec) {
        resVec.x = a.x - b.x;
        resVec.y = a.y - b.y;
    }

    public static void add(Vec2D a, Vec2D b, Vec2D resVec) {
        resVec.x = a.x + b.x;
        resVec.y = a.y + b.y;
    }

    public static float cross(Vec2D a, Vec2D b) {
        return (a.x * b.y) - (a.y * b.x);
    }

    public void reset() {
        x = 0;
        y = 0;
    }

    public void negate() {
        x = -x;
        y = -y;
    }

    public void set(float ax, float ay) {
        x = ax;
        y = ay;
    }

    public void set(Vec2D v) {
        x = v.x;
        y = v.y;
    }

    public void scale(float v) {
        x *= v;
        y *= v;
    }

    public Vec2D getScaled(float v) {
        Vec2D res = new Vec2D(x, y);
        res.x *= v;
        res.y *= v;
        return res;
    }

    public void mul(Vec2D v) {
        x *= v.x;
        y *= v.y;
    }

    public float dot(Vec2D b) {
        return (x * b.x + y * b.y);
    }

    public float lenght() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float normalise() {
        float l = (float) Math.sqrt(x * x + y * y);
        if (l == 0.0f) {
            return 0.0f;
        }
        x /= l;
        y /= l;
        return l;
    }

    public void sub(Vec2D b) {
        x -= b.x;
        y -= b.y;
    }

    public void add(Vec2D b) {
        x += b.x;
        y += b.y;
    }

    public float cross(Vec2D b) {
        return (x * b.y) - (y * b.x);
    }

    public void postMultiply(Mat2 m) {
        float xr = x * m.m11 + y * m.m12;
        float yr = x * m.m21 + y * m.m22;
        x = xr;
        y = yr;
    }

    public void predMultiply(Mat2 m) {
        float xr = x * m.m11 + y * m.m21;
        float yr = x * m.m12 + y * m.m22;
        x = xr;
        y = yr;
    }
}
