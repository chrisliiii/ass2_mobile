package takephoto.model;

import java.io.Serializable;


public class TakePhotoOptions implements Serializable {

    private boolean withOwnGallery;

    private TakePhotoOptions() {
    }

    public boolean isWithOwnGallery() {
        return withOwnGallery;
    }

    public void setWithOwnGallery(boolean withOwnGallery) {
        this.withOwnGallery = withOwnGallery;
    }

    public static class Builder {
        private TakePhotoOptions options;

        public Builder() {
            this.options = new TakePhotoOptions();
        }

        public Builder setWithOwnGallery(boolean withOwnGallery) {
            options.setWithOwnGallery(withOwnGallery);
            return this;
        }
        public TakePhotoOptions create(){
            return options;
        }
    }
}
