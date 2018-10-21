package takephoto.model;

import java.io.Serializable;


public class LubanOptions implements Serializable {

    private int maxSize;
    private int maxHeight;
    private int maxWidth;
    private int gear=121;
    private LubanOptions(){}

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public static class Builder{
        private LubanOptions options;

        public Builder() {
            options=new LubanOptions();
        }

        public Builder setMaxSize(int maxSize) {
            options.setMaxSize(maxSize);
            return this;
        }

        public Builder setMaxHeight(int maxHeight) {
            options.setMaxHeight(maxHeight);
            return this;
        }

        public Builder setMaxWidth(int maxWidth) {
            options.setMaxWidth(maxWidth);
            return this;
        }

        public Builder setGear(int gear) {
            options.setGear(gear);
            return this;
        }
        public LubanOptions create(){
            return options;
        }
    }
}
