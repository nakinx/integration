package org.acme.dto;

public class NominatimResponse {

    private Double latitude;
    private Double longitude;
    private String mapUrl;

    public NominatimResponse() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    @Override
    public String toString() {
        return "NominatimResponse{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", mapUrl='" + mapUrl + '\'' +
                '}';
    }
}
