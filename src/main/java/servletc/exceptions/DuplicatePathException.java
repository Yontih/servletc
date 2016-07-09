package servletc.exceptions;

/**
 * Created by Yonti on 09/07/2016.
 */
public class DuplicatePathException extends Exception {

    private String mPath;

    public DuplicatePathException(String path) {
        super(String.format("%s already in use", path));

        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

}
