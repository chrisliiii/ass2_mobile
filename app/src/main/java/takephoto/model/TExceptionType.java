package takephoto.model;


public enum TExceptionType {
    TYPE_NOT_IMAGE("The selected file is not a picture."),
    TYPE_WRITE_FAIL("Failed to save selected files."),
    TYPE_URI_NULL("The Uri of the selected photo is null."),
    TYPE_URI_PARSE_FAIL("Failed to get file path from Uri."),
    TYPE_NO_MATCH_PICK_INTENT("There is no match to Intent for selecting pictures."),
    TYPE_NO_MATCH_CROP_INTENT("There is no Intent that matches the picture cut."),
    TYPE_NO_CAMERA("No camera"),
    TYPE_NO_FIND("The selected file was not found."), ;

    String stringValue;
    TExceptionType(String stringValue) {
        this.stringValue=stringValue;
    }
    public String getStringValue() {
        return stringValue;
    }
}
