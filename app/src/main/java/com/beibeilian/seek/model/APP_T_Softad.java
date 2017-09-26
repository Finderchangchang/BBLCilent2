package com.beibeilian.seek.model;

public class APP_T_Softad {
    private Integer id;

    private String logo;

    private String appname;

    private String appcontent;

    private String appsize;

    private String modtime;

    private String appapk;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo == null ? null : logo.trim();
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname == null ? null : appname.trim();
    }

    public String getAppcontent() {
        return appcontent;
    }

    public void setAppcontent(String appcontent) {
        this.appcontent = appcontent == null ? null : appcontent.trim();
    }

    public String getAppsize() {
        return appsize;
    }

    public void setAppsize(String appsize) {
        this.appsize = appsize == null ? null : appsize.trim();
    }

    public String getModtime() {
        return modtime;
    }

    public void setModtime(String modtime) {
        this.modtime = modtime == null ? null : modtime.trim();
    }

    public String getAppapk() {
        return appapk;
    }

    public void setAppapk(String appapk) {
        this.appapk = appapk == null ? null : appapk.trim();
    }
}