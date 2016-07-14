package com.sequencing.fileselector;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Pojo class serves as wrapper for items of server response
 */
public class FileEntity implements Serializable{

    private static final long serialVersionUID = 4839878757146151033L;
    /**
     * date file was added
     */
    @SerializedName("DateAdded")
    private String dateAdded;

    /**
     * file extension
     */
    @SerializedName("Ext")
    private String ext;

    /**
     * file category: Community, Uploaded, FromApps, Altruist
     */
    @SerializedName("FileCategory")
    private String fileCategory;

    /**
     * file subtype
     */
    @SerializedName("FileSubType")
    private String fileSubType;

    /**
     * file type
     */
    @SerializedName("FileType")
    private String fileType;

    /**
     * person name for sample files
     */
    @SerializedName("FriendlyDesc1")
    private String friendlyDesc1;

    /**
     * person description for sample files
     */
    @SerializedName("FriendlyDesc2")
    private String friendlyDesc2;

    /**
     * file ID
     */
    @SerializedName("Id")
    private String id;

    /**
     * file name
     */
    @SerializedName("Name")
    private String name;

    /**
     * human population
     */
    @SerializedName("Population")
    private String population;

    /**
     * the sex
     */
    @SerializedName("Sex")
    private String sex;

    public String getDataAdded() {
        return dateAdded;
    }

    public void setDataAdded(String dateAdded) {
        dateAdded = dateAdded;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        ext = ext;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        fileCategory = fileCategory;
    }

    public String getFileSubType() {
        return fileSubType;
    }

    public void setFileSubType(String fileSubType) {
        fileSubType = fileSubType;
    }

    public String getFriendlyDesc1() {
        return friendlyDesc1;
    }

    public void setFriendlyDesc1(String friendlyDesc1) {
        friendlyDesc1 = friendlyDesc1;
    }

    public String getFriendlyDesc2() {
        return friendlyDesc2;
    }

    public void setFriendlyDesc2(String friendlyDesc2) {
        friendlyDesc2 = friendlyDesc2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        population = population;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        sex = sex;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        fileType = fileType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DateAdded").append(" : ").append(dateAdded).append('\n')
                .append("Ext").append(" : ").append(ext).append('\n')
                .append("FileCategory").append(" : ").append(fileCategory).append('\n')
                .append("FileType").append(" : ").append(fileType).append('\n')
                .append("FileSubType").append(" : ").append(fileSubType).append('\n')
                .append("FriendlyDesc1").append(" : ").append(friendlyDesc1).append('\n')
                .append("FriendlyDesc2").append(" : ").append(friendlyDesc2).append('\n')
                .append("Id").append(" : ").append(id).append('\n')
                .append("Name").append(" : ").append(name).append('\n')
                .append("Population").append(" : ").append(population).append('\n')
                .append("Sex").append(" : ").append(sex);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
