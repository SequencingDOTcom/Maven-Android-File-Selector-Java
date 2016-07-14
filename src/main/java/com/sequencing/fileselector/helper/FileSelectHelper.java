package com.sequencing.fileselector.helper;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.sequencing.fileselector.FileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Class defines methods for handling server response
 */
public class FileSelectHelper {

    public static final String ATTR_SAMPLE = "Community";
    public static final String ATTR_MY_FILES = "My Files";

    public static final String ATTR_SAMPLE_ALL = "ALL";
    public static final String ATTR_SAMPLE_MALE = "Male";
    public static final String ATTR_SAMPLE_FEMALE = "Female";

    public static final String ATTR_MY_FILES_FROMAPPS = "FromApps";
    public static final String ATTR_MY_FILES_UPLOADED = "Uploaded";
    public static final String ATTR_MY_FILES_ALTRUIST = "Altruist";
    public static final String ATTR_MY_FILES_SHARED = "SharedToM";

    public static Map<String, String> myFilesCategoriesMap = new TreeMap<String, String>();
    public static Map<String, String> sampleCategoriesMap = new TreeMap<String, String>();

    public static FileEntity[] getFileEntities(String json){
        return JsonHelper.<FileEntity[]>convertToJavaObject(json, FileEntity[].class);
    }

    /**
     * @return subcategories set of Sample files
     */
    public static List<String> getSampleFilesSubCategories(){
        sampleCategoriesMap.put(ATTR_SAMPLE_ALL, "All");
        sampleCategoriesMap.put(ATTR_SAMPLE_FEMALE, "Women");
        sampleCategoriesMap.put(ATTR_SAMPLE_MALE, "Men");
        return new ArrayList<>(sampleCategoriesMap.keySet());
    }

    /**
     * @param entities all entities that are own files of user
     * @return subcategories set of My files
     */
    public static List<String> getMyFilesSubCategories(FileEntity[] entities) {
        for (FileEntity entity : entities) {
            switch (entity.getFileCategory()) {
                case ATTR_MY_FILES_UPLOADED:
                    myFilesCategoriesMap.put(ATTR_MY_FILES_UPLOADED, "Uploaded");
                    break;
                case ATTR_MY_FILES_FROMAPPS:
                    myFilesCategoriesMap.put(ATTR_MY_FILES_FROMAPPS, "From Apps");
                    break;
                case ATTR_MY_FILES_ALTRUIST:
                    myFilesCategoriesMap.put(ATTR_MY_FILES_ALTRUIST, "Altruist");
                    break;
                case ATTR_MY_FILES_SHARED:
                    myFilesCategoriesMap.put(ATTR_MY_FILES_SHARED, "Shared");
                    break;
            }
        }

        List<String> myFilesCategories =  new ArrayList<>(myFilesCategoriesMap.keySet());
        Collections.sort(myFilesCategories, new CategoriesComparator());

        return myFilesCategories;
    }

    private static class CategoriesComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2) * (-1);
        }
    }

    /**
     * @param json server response
     * @return true if user don't has owb files, else - otherwise.
     */
    public static boolean isEmptyMyFiles(String json){
        return getMyFilesSubCategories(getFileEntities(json)).size() == 0;
    }

    /**
     * Return list of file entities by category that are own files of user
     * @param entities all own files
     * @param fileCategory indicated category
     * @return list of file entities
     */
    public static List<FileEntity> getMyFileEntitiesByCategory(FileEntity[] entities, String fileCategory){
        List<FileEntity> list = new ArrayList<>();

        for(FileEntity entity: entities)
            if(entity.getFileCategory().equals(fileCategory))
                list.add(entity);

        return list;
    }

    /**
     * Return list of file names by category that are own files of user
     * @param entities all own files
     * @param fileCategory indicated category
     * @return list of files name
     */
    public static List<String> getMyFileEntitiesNameByCategory(FileEntity[] entities, String fileCategory){
        List<FileEntity> fileEntityList = getMyFileEntitiesByCategory(entities, fileCategory);
        List<String> list = new ArrayList<>();

        for(FileEntity entity: fileEntityList)
                list.add(entity.getName());

        return list;
    }

    /**
     * @param entities all file entities from server response
     * @return all file entities that are sample files
     */
    public static List<FileEntity> getAllSampleFileEntities(FileEntity[] entities){
        List<FileEntity> list = new ArrayList<>();

        for(FileEntity entity: entities) {
            if(entity.getFileCategory().equals(ATTR_SAMPLE))
                list.add(entity);
        }

        return list;
    }

    /**
     * @param entities all file entities from server response
     * @return all file names that are sample files
     */
    public static List<SpannableString> getAllSampleFileEntitiesName(FileEntity[] entities){
        List<FileEntity> listEntities = getAllSampleFileEntities(entities);

        return fromSpannableStringsList(listEntities);
    }

    /**
     * @param entities all file entities from server response
     * @param sex sex of variant
     * @return file entities list that grouped by defined sex
     */
    public static List<FileEntity> getSampleFileEntitiesBySex(FileEntity[] entities, String sex){
        List<FileEntity> listEntities = getAllSampleFileEntities(entities);
        List<FileEntity> listBySex = new ArrayList<>();

        for(FileEntity entity: listEntities)
            if(entity.getSex().equals(sex))
                listBySex.add(entity);

        return listBySex;
    }

    /**
     * @param entities all file entities from server response
     * @param sex sex of variant
     * @return list of file entities name that grouped by defined category
     */
    public static List<SpannableString> getSampleFileEntitiesNameBySex(FileEntity[] entities, String sex){
        List<FileEntity> listEntities = getSampleFileEntitiesBySex(entities, sex);

        return fromSpannableStringsList(listEntities);
    }

    public static FileEntity getFileEntityByFileId(FileEntity[] entities, String fileId){
        if(fileId == null)
            return null;

        for(FileEntity fileEntity: entities)
            if(fileEntity.getId().equals(fileId))
                return fileEntity;

        return null;
    }

    /**
     * @param listEntities list of sample file entities
     * @return list of sample file names special formatted
     */
    private static List<SpannableString> fromSpannableStringsList(List<FileEntity> listEntities){
        List<SpannableString> list = new ArrayList<>();

        for(FileEntity entity: listEntities) {
            String topStr = entity.getFriendlyDesc1();
            String underStr = entity.getFriendlyDesc2();
            SpannableString spannableString = new SpannableString(topStr + "\n" + underStr);
            spannableString.setSpan(new RelativeSizeSpan(1f), 0, topStr.length(), 0);
            spannableString.setSpan(new RelativeSizeSpan(0.80f), topStr.length()+1, topStr.length() + underStr.length()+1, 0);
            list.add(spannableString);
        }

        return list;
    }
}
