package src.main.java.com.space.model;

import java.io.Serializable;

public class ShipDTO implements Serializable {

    private String name;

    private String planet;

    private String shipType;

    private Long prodDate;

    private Boolean isUsed;

    private Double speed;

    private Integer crewSize;

    private Double rating;

    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public String getShipType() {
        return shipType;
    }

    public Long getProdDate() {
        return prodDate;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public Double getSpeed() {
        return speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public Double getRating() {
        return rating;
    }
}
