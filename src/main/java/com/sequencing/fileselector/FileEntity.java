package com.sequencing.fileselector;

/**
 * Pojo class serves as wrapper for items of server response
 */
public class FileEntity {

    private String DateAdded;
    private String Ext;
    private String FileCategory;
    private String FileSubType;
    private String FriendlyDesc1;
    private String FriendlyDesc2;
    private String Id;
    private String Name;
    private String Population;
    private String Sex;

    public String getDataAdded() {
        return DateAdded;
    }

    public void setDataAdded(String dateAdded) {
        DateAdded = dateAdded;
    }

    public String getExt() {
        return Ext;
    }

    public void setExt(String ext) {
        Ext = ext;
    }

    public String getFileCategory() {
        return FileCategory;
    }

    public void setFileCategory(String fileCategory) {
        FileCategory = fileCategory;
    }

    public String getFileSubType() {
        return FileSubType;
    }

    public void setFileSubType(String fileSubType) {
        FileSubType = fileSubType;
    }

    public String getFriendlyDesc1() {
        return FriendlyDesc1;
    }

    public void setFriendlyDesc1(String friendlyDesc1) {
        FriendlyDesc1 = friendlyDesc1;
    }

    public String getFriendlyDesc2() {
        return FriendlyDesc2;
    }

    public void setFriendlyDesc2(String friendlyDesc2) {
        FriendlyDesc2 = friendlyDesc2;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPopulation() {
        return Population;
    }

    public void setPopulation(String population) {
        Population = population;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DateAdded").append(" : ").append(DateAdded).append('\n')
                .append("Ext").append(" : ").append(Ext).append('\n')
                .append("FileCategory").append(" : ").append(FileCategory).append('\n')
                .append("FileSubType").append(" : ").append(FileSubType).append('\n')
                .append("FriendlyDesc1").append(" : ").append(FriendlyDesc1).append('\n')
                .append("FriendlyDesc2").append(" : ").append(FriendlyDesc2).append('\n')
                .append("Id").append(" : ").append(Id).append('\n')
                .append("Name").append(" : ").append(Name).append('\n')
                .append("Population").append(" : ").append(Population).append('\n')
                .append("Sex").append(" : ").append(Sex);
        return builder.toString();
    }
}
