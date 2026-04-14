package com.lesadrax.registrationclient.from.utils;

/**
 * Created by G513563 on 29/08/2016.
 */
public class SimpleImageData {

    private String _extension;
    private String _path;
    private byte[] _pixels;
    private int _width;
    private int _height;
    private float _resolution;
    private String _colorSpace;
    private String _templateQuality;
    private String _templateEyesDistance;

    public SimpleImageData(String extension, String path, byte[] pixels, int width, int height, float resolution, String colorSpace) {
        _extension = extension;
        _path = path;
        _pixels = pixels.clone();
        _width = width;
        _height = height;
        _resolution = resolution;
        _colorSpace = colorSpace;
        _templateQuality = "-";
        _templateEyesDistance = "-";
    }

    public String getExtension() {
        return _extension;
    }
    public String getPath() {
        return _path;
    }
    public byte[] getPixels() {
        return _pixels;
    }
    public int getWidth() {
        return _width;
    }
    public int getHeight() {
        return _height;
    }
    public float getResolution() {
        return _resolution;
    }
    public String getColorSpace() {
        return _colorSpace;
    }
    public void setTemplateQuality(String quality) {
        _templateQuality = quality;
    }
    public String getTemplateQuality() {
        return _templateQuality;
    }
    public void setTemplateEyesDistance(String eyes_distance) {
        _templateEyesDistance = eyes_distance;
    }
    public String getTemplateEyesDistance() {
        return _templateEyesDistance;
    }
}
