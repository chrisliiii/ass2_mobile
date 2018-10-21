package takephoto.compress;





import java.io.Serializable;

import takephoto.model.LubanOptions;


public class CompressConfig implements Serializable {


    private int maxPixel=1200;


    private int maxSize=100*1024;

    private boolean enablePixelCompress=true;

    private boolean enableQualityCompress=true;
    private LubanOptions lubanOptions;
    public static CompressConfig ofDefaultConfig(){
        return new CompressConfig();
    }
    public static CompressConfig ofLuban(LubanOptions options){
        return new CompressConfig(options);
    }
    private CompressConfig(){}
    private CompressConfig(LubanOptions options){
        this.lubanOptions=options;
    }

    public LubanOptions getLubanOptions() {
        return lubanOptions;
    }

    public int getMaxPixel() {
        return maxPixel;
    }
    public CompressConfig setMaxPixel(int maxPixel) {
        this.maxPixel = maxPixel;
        return this;
    }
    public int getMaxSize() {
        return maxSize;
    }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isEnablePixelCompress() {
        return enablePixelCompress;
    }

    public void enablePixelCompress(boolean enablePixelCompress) {
        this.enablePixelCompress = enablePixelCompress;
    }

    public boolean isEnableQualityCompress() {
        return enableQualityCompress;
    }

    public void enableQualityCompress(boolean enableQualityCompress) {
        this.enableQualityCompress = enableQualityCompress;
    }
    public static class Builder{
        private CompressConfig config;
        public Builder() {
            config=new CompressConfig();
        }
        public Builder setMaxSize(int maxSize) {
            config.setMaxSize( maxSize);
            return this;
        }
        public Builder setMaxPixel(int maxPixel) {
            config.setMaxPixel(maxPixel);
            return this;
        }
        public Builder enablePixelCompress(boolean enablePixelCompress) {
            config.enablePixelCompress(enablePixelCompress);
            return this;
        }
        public Builder enableQualityCompress(boolean enableQualityCompress) {
            config.enableQualityCompress(enableQualityCompress);
            return this;
        }
        public CompressConfig create(){
            return config;
        }
    }
}

