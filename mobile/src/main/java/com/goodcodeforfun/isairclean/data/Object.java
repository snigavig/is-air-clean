package com.goodcodeforfun.isairclean.data;

public class Object
{
    private String futureCarbon;

    private String name;

    private String locationId;

    private String futureIntensity;

    private String currentIntensity;

    private String currentCarbon;

    private String longitude;

    private String latitude;

    private String futureEnergy;

    private String currentEnergy;

    public String getFutureCarbon ()
    {
        return futureCarbon;
    }

    public void setFutureCarbon (String futureCarbon)
    {
        this.futureCarbon = futureCarbon;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getLocationId ()
    {
        return locationId;
    }

    public void setLocationId (String locationId)
    {
        this.locationId = locationId;
    }

    public String getFutureIntensity ()
    {
        return futureIntensity;
    }

    public void setFutureIntensity (String futureIntensity)
    {
        this.futureIntensity = futureIntensity;
    }

    public String getCurrentIntensity ()
    {
        return currentIntensity;
    }

    public void setCurrentIntensity (String currentIntensity)
    {
        this.currentIntensity = currentIntensity;
    }

    public String getCurrentCarbon ()
    {
        return currentCarbon;
    }

    public void setCurrentCarbon (String currentCarbon)
    {
        this.currentCarbon = currentCarbon;
    }

    public String getLongitude ()
    {
        return longitude;
    }

    public void setLongitude (String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }

    public String getFutureEnergy ()
    {
        return futureEnergy;
    }

    public void setFutureEnergy (String futureEnergy)
    {
        this.futureEnergy = futureEnergy;
    }

    public String getCurrentEnergy ()
    {
        return currentEnergy;
    }

    public void setCurrentEnergy (String currentEnergy)
    {
        this.currentEnergy = currentEnergy;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [futureCarbon = "+futureCarbon+", name = "+name+", locationId = "+locationId+", futureIntensity = "+futureIntensity+", currentIntensity = "+currentIntensity+", currentCarbon = "+currentCarbon+", longitude = "+longitude+", latitude = "+latitude+", futureEnergy = "+futureEnergy+", currentEnergy = "+currentEnergy+"]";
    }
}
