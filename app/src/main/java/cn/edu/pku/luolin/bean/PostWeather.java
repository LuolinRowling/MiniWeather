package cn.edu.pku.luolin.bean;

/**
 * Created by luolin on 2016/12/3.
 */
public class PostWeather {

    private String date;
    private String high;
    private String low;
    private String type;
    private String fengli;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
