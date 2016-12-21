package at.msoft.commons.filetransferprotocol;

/**
 * Created by Andreas on 28.11.2016.
 */
public class Triple<T, V, W> {
    T v1 = null;
    V v2 = null;
    W v3 = null;

    public Triple(T v1, V v2, W v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public T getFirst() {
        return v1;
    }

    public V getSecond() {
        return v2;
    }

    public W getThird() {
        return v3;
    }
}
