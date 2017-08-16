package ru.nivanov.jrbuilder.utils;

import java.io.Serializable;

/**
 * @author nivanov
 *         on 14.08.17.
 */
public class DataSource implements Serializable{
    private String name, url;

    public DataSource(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
