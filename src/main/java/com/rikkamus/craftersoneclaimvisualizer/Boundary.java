package com.rikkamus.craftersoneclaimvisualizer;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Collection;

public class Boundary {

    private Collection<Vector2f> points;
    private float y1;
    private float y2;
    private Vector4f fillRgba;
    private Vector4f outlineRgba;

    public Boundary(Collection<Vector2f> points, float y1, float y2, Vector4f fillRgba, Vector4f outlineRgba) {
        this.points = points;
        this.y1 = y1;
        this.y2 = y2;
        this.fillRgba = fillRgba;
        this.outlineRgba = outlineRgba;
    }

    public Collection<Vector2f> getPoints() {
        return this.points;
    }

    public void setPoints(Collection<Vector2f> points) {
        this.points = points;
    }

    public float getY1() {
        return this.y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getY2() {
        return this.y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public Vector4f getFillRgba() {
        return this.fillRgba;
    }

    public void setFillRgba(Vector4f fillRgba) {
        this.fillRgba = fillRgba;
    }

    public Vector4f getOutlineRgba() {
        return this.outlineRgba;
    }

    public void setOutlineRgba(Vector4f outlineRgba) {
        this.outlineRgba = outlineRgba;
    }

}
